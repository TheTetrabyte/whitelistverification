# Whitelist Verification

## What is required

- API Server (Later in this document)
- Database server (Not 100% needed but is better than flat file storage)
- Spigot server running atleast 1.13.1
- Servers built in whitelist disabled

## How it works

When a user joins that is not in the whitelist file, and if the whitelist is set to enabled in the plugin config, it will send a get request to see if the user is supposed to be whitelisted.

If the user is not supposed to be whitelisted they will be kicked and given a code, this code and expiry time will need to come from a custom api. Which I will put on git as well in the coming days, unless you wanna make your own with the route explanation below.

If the user is supposed to be whitelisted it will add them to the whitelist and connect them to the server.

All the custom join verification api calls are ignored if the player is already in the whitelist

## Route Definition

### On join - GET (/api/verification/:UUID)

Sends an authorization header with the token from the config.yml

Returns status code 200 with json type and looks like below

```json
{
    "success": true,
    "code": 5000,
    "data": {
        "uuid": "PlayerUUID"
    }
}
```

### On join - POST (/api/verification)

If the check request about returns anything other then 5000 in the json

JSON body that is sent to the api

```json
{
    "uuid": "PlayerUUID",
    "name": "PlayerName"
}
```

Sends an authorization header with the token from the config.yml

Returns status code 200 with json type and looks like below

```json
{
    "success": true,
    "code": 5002,
    "data": {
        "code": "CodeGeneratedByAPI",
        "expires": "DateExpires",
        "expiresIn": "?? hours, ?? minutes, ??, seconds"
    }
}
```

### On successful join - POST (/api/stats/join/:UUID)

This is basically used so you can track who joined and when, also sends the api the player count of the server.

JSON body that is sent to the api

```json
{
    "uuid": "PlayerUUID",
    "name": "PlayerName",
    "max": 20,
    "players": 2
}
```

Sends an authorization header with the token from the config.yml

Returns status code 200 with json type and looks like below

```json
{
    "success": true,
    "code": 10000
}
```

### On successful leave - POST (/api/stats/leave/:UUID)

This is basically used so you can track who left, also sends the api the player count of the server.

JSON body that is sent to the api

```json
{
    "uuid": "PlayerUUID",
    "name": "PlayerName",
    "max": 20,
    "players": 1
}
```

Sends an authorization header with the token from the config.yml

Returns status code 200 with json type and looks like below

```json
{
    "success": true,
    "code": 10000
}
```