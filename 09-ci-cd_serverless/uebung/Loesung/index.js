const AWS = require("aws-sdk");

const dynamo = new AWS.DynamoDB.DocumentClient();

// Beispielloesung für die FaaS Uebung zu AWS Lambda
exports.handler = async (event, context) => {
    let body;
    let statusCode = 200;
    const headers = {
        "Content-Type": "application/json"
    };

    try {
        switch (event.routeKey) {
            case "GET /books":
                body = await dynamo.scan({ TableName: "cc-2021-lambda-table" }).promise();
                break;
            case "PUT /books":
                let requestBody = JSON.parse(event.body);
                await dynamo
                    .put({
                        TableName: "cc-2021-lambda-table",
                        Item: {
                            id: requestBody.id,
                            title: requestBody.title,
                            author: requestBody.author
                        }
                    })
                    .promise();
                body = `Added ${requestBody.id}`;
                break;
            case "GET /books/{id}":
                body = await dynamo
                    .get({
                        TableName: "cc-2021-lambda-table",
                        Key: {
                            id: event.pathParameters.id
                        }
                    })
                    .promise();
                break;
            case "DELETE /books/{id}":
                await dynamo
                    .delete({
                        TableName: "cc-2021-lambda-table",
                        Key: {
                            id: event.pathParameters.id
                        }
                    })
                    .promise();
                body = `Deleted ${event.pathParameters.id}`;
                break;
            default:
                throw new Error(`Route not supported: "${event.routeKey}"`);
        }
    } catch (err) {
        body = err.message;
        statusCode = 400;
    } finally {
        body = JSON.stringify(body);
    }

    return {
        statusCode,
        body,
        headers
    };
};