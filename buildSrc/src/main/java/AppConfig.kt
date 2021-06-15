object AppConfig {
    const val applicationIdPrefix = "com.your_application_prefix"
    private const val name = "your_application_name"

    const val applicationId = "$applicationIdPrefix.$name"
    const val compileSdk = 30
    const val minSdk = 21
    const val targetSdk = 30
    const val versionName = "1.0.0"
    const val versionCode = 1
    const val dimension = name

    const val DASHBOARD_DISTANCE_LIMIT = "10" //in km

    const val APP_ID = "\"YOUR_APP_ID\""
    const val INSTANCE_ID = "\"YOUR_INSTANCE_ID\""
    const val INSTANCE_KEY = "\"YOUR_INSTANCE_KEY\""

    const val USER_SERVICE_URL = "\"https://user.telematicssdk.com/\""
    const val USER_SERVICE_URL_DEV = "\"https://user.telematicssdk.com/\""

    const val DRIVE_COINS_URL = "\"https://mobilesdk.telematicssdk.com/api/rewarding/\""
    const val DRIVE_COINS_URL_DEV = "\"https://mobilesdk.telematicssdk.com/api/rewarding/\""

    const val USER_STATISTICS = "\"https://api.telematicssdk.com/\""
    const val USER_STATISTICS_DEV = "\"https://api.telematicssdk.com/\""
}