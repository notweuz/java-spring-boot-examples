# Spring Boot Auth Example
This is a simple example of a Spring Boot application that uses JWT for authentication and authorization.

## Requirements
- Java 21
- Gradle
- PostgresSQL

# API Endpoints
## Authentication
1. **Sign Up**
    - Path: `/auth/signup`
    - Method: `POST`
    - Request Body: `SignUpDto`
    - Response: `AuthTokenDto`
    - Description: Registers a new user and returns an authentication token

2. **Sign In**
    - Path: `/auth/signin`
    - Method: `POST`
    - Request Body: `SignInDto`
    - Response: `AuthTokenDto`
    - Description: Authenticates an existing user and returns an authentication token

### User Controller (`/users`)
1. **Get Current User Info**
    - Path: `/users/@me`
    - Method: `GET`
    - Response: `UserDto`
    - Description: Returns information about the currently authenticated user

# Data Transfer Objects (DTOs)
## Request DTOs
### SignUpDto
```json
{
  "username": "string",
  "password": "string"
}
```
### SignInDto
```json
{
  "username": "string",
  "password": "string"
}
```
### ChangePasswordDto
```json
{
  "old_password": "string",
  "new_password": "string"
}
```
### UpdateUserDto
```json
{
  "username": "string"
}
```

## Response DTOs
### AuthToken
```json
{
    "token": "string"
}
```
### UserDto
```json
{
  "id": "string",
  "username": "string"
}
```
### ApiError
```json
{
  "errors": ["string"],
  "message": "string",
  "status": "string",
  "reason": "string",
  "timestamp": "2023-10-01T00:00:00Z"
}
```

# To Do
- [ ] Implement tests