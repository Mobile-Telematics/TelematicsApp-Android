object AppConfig {
    const val applicationIdPrefix = "com.test"
    private const val name = "app"

    const val applicationId = "$applicationIdPrefix.$name"
    const val compileSdk = 32
    const val minSdk = 21
    const val targetSdk = 32
    const val versionName = "1.0.0"
    const val versionCode = 1

    const val SOURCE = "\"Zenroad\""

    const val PRIVACY_POLICY = "\"YOUR_PRIVACY_POLICY_LINK\"" //for example "\"https://www.telematicssdk.com/privacy-policy/\""
    const val TERMS_OF_USE = "\"YOUR_TERMS_OF_USE_LINK\"" //for example"\"https://www.telematicssdk.com/privacy-policy/\""

    const val DASHBOARD_DISTANCE_LIMIT = "10" //in km

    const val INSTANCE_ID = "\"377ea380-b4df-40f7-8d07-a03012217b7c\""
    const val INSTANCE_KEY = "\"62590e73-cc7e-4fef-94ee-a8f4dacf2862\""

    //const val HERE_API_KEY = "\"9w8uIImxU4kBFFqNm0PXMg-IGlMDyiN3XfNnZt1E3LU\""
    const val GOOGLE_MAP_API = "YOUR_GOOGLE_MAP_API_KEY"

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