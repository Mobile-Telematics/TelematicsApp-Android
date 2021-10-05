object AppConfig {
    const val applicationIdPrefix = "com.your_application_prefix"
    private const val name = "your_application_name"

    const val applicationId = "$applicationIdPrefix.$name"
    const val compileSdk = 30
    const val minSdk = 21
    const val targetSdk = 30
    const val versionName = "1.0.0"
    const val versionCode = 1

    const val SOURCE = "\"Zenroad\""

    const val PRIVACY_POLICY = "\"YOUR_PRIVACY_POLICY_LINK\"" //for example "\"https://www.telematicssdk.com/privacy-policy/\""
    const val TERMS_OF_USE = "\"YOUR_TERMS_OF_USE_LINK\"" //for example"\"https://www.telematicssdk.com/privacy-policy/\""

    const val DASHBOARD_DISTANCE_LIMIT = "10" //in km

    const val INSTANCE_ID = "\"YOUR_INSTANCE_ID\""
    const val INSTANCE_KEY = "\"YOUR_INSTANCE_KEY\""

    const val HERE_API_KEY = "\"YOUR_HERE_API_KEY\""

    const val HERE_LICENCE_KEY = "\"YOUR_HERE_LICENCE_KEY\""
    const val HERE_APP_ID = "\"YOUR_HERE_APP_ID\""
    const val HERE_APP_CODE = "\"YOUR_HERE_APP_CODE\""

    const val USER_SERVICE_URL = "\"https://user.telematicssdk.com/\""
    const val USER_SERVICE_URL_DEV = "\"https://user.telematicssdk.com/\""

    const val DRIVE_COINS_URL = "\"https://mobilesdk.telematicssdk.com/api/rewarding/\""
    const val DRIVE_COINS_URL_DEV = "\"https://mobilesdk.telematicssdk.com/api/rewarding/\""

    const val USER_STATISTICS = "\"https://api.telematicssdk.com/\""
    const val USER_STATISTICS_DEV = "\"https://api.telematicssdk.com/\""

    const val LEADERBOARD_URL = "\"http://leaderboard.telematicssdk.com/\""
    const val LEADERBOARD_URL_DEV = "\"http://leaderboard.telematicssdk.com/\""

    const val TRIP_EVENT_TYPE_URL = "\"https://mobilesdk.telematicssdk.com/mobilesdk/stage/\""
    const val TRIP_EVENT_TYPE_URL_DEV = "\"https://mobilesdk.telematicssdk.com/mobilesdk/stage/\""

    const val OBD_API_ENDPOINT = "\"https://services.telematicssdk.com/api/carservice/\""
    const val OBD_API_ENDPOINT_DEV = "\"https://services.telematicssdk.com/api/carservice/\""
}