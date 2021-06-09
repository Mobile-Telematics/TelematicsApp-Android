package com.telematics.authentication.exception

import java.io.IOException

class AuthException(val errorCode: AuthErrorCode) : IOException()