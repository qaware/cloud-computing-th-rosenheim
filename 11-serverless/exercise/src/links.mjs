// The AWS SDK v3 is part of the Node.js lambda runtime - no npm install or bundling required.
import { DynamoDBClient, ConditionalCheckFailedException } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient, PutCommand, UpdateCommand } from "@aws-sdk/lib-dynamodb";
import { randomBytes } from "node:crypto";

// Created once per lambda instance (outside the handler) and reused across invocations.
// This is fine for clients and connections - but NOT for application state like counters!
const db = DynamoDBDocumentClient.from(new DynamoDBClient({}));
const TABLE_NAME = process.env.TABLE_NAME;

const json = (statusCode, body) => ({
    statusCode,
    headers: { "content-type": "application/json" },
    body: JSON.stringify(body),
});

// POST /links  with body {"url": "https://..."}
async function createLink(event) {
    const rawBody = event.isBase64Encoded ? Buffer.from(event.body ?? "", "base64").toString() : event.body;

    let url;
    try {
        url = new URL(JSON.parse(rawBody ?? "{}").url);
    } catch {
        return json(400, { error: 'Expected a JSON body like {"url": "https://example.com"}' });
    }
    if (!["http:", "https:"].includes(url.protocol)) {
        return json(400, { error: "Only http and https URLs are allowed" });
    }

    const code = randomBytes(4).toString("base64url"); // e.g. "aB3x-9"

    try {
        await db.send(new PutCommand({
            TableName: TABLE_NAME,
            Item: { code, url: url.href, clicks: 0, createdAt: new Date().toISOString() },
            // Never overwrite an existing link if we happen to generate the same code twice
            ConditionExpression: "attribute_not_exists(code)",
        }));
    } catch (err) {
        if (err instanceof ConditionalCheckFailedException) {
            return json(409, { error: "Code collision, please try again" });
        }
        throw err;
    }

    return json(201, {
        code,
        url: url.href,
        shortUrl: `https://${event.requestContext.domainName}/${code}`,
    });
}

// GET /{code}  redirects to the stored URL and counts the click
async function followLink(event) {
    const code = event.pathParameters?.code;

    try {
        // Reads the item and increments the counter in ONE atomic operation.
        // A read-modify-write in our code (get item, clicks + 1, put item) would lose clicks
        // when several lambda instances handle requests for the same link at the same time.
        const { Attributes: link } = await db.send(new UpdateCommand({
            TableName: TABLE_NAME,
            Key: { code },
            UpdateExpression: "ADD clicks :one",
            ConditionExpression: "attribute_exists(code)", // otherwise ADD would create a new item
            ExpressionAttributeValues: { ":one": 1 },
            ReturnValues: "ALL_NEW",
        }));

        return { statusCode: 301, headers: { location: link.url } };
    } catch (err) {
        if (err instanceof ConditionalCheckFailedException) {
            return json(404, { error: `No link with code '${code}'` });
        }
        throw err;
    }
}

export const handler = async (event) => {
    switch (event.routeKey) {
        case "POST /links":
            return createLink(event);
        case "GET /{code}":
            return followLink(event);
        // TODO (stretch goal): add a route GET /links/{code}/stats that returns the click count
        default:
            return json(404, { error: `Unknown route ${event.routeKey}` });
    }
};
