package com.telematics.authentication.exception

enum class AuthErrorCode {
    INVALID_PASSWORD,
    USER_NOT_EXIST,
    LOGIN_TIMEOUT,
    EMPTY_VERIFICATION_CODE,
    INVALID_VERIFICATION_CODE,
    EMPTY_SESSION,
    EMPTY_DEVICE_TOKEN,
    NONE,
    EMPTY,
    NEED_VERIFY_CODE
}