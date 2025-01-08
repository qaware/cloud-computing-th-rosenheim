export const handler = async (event) => {
    // TODO implement
    const response = {
        statusCode: 200,
        body: JSON.stringify('Hello from terraform Lambda!'),
    };
    return response;
};