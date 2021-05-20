object AppConfig {
    const val applicationIdPrefix = "com.telematics"
    private const val name = "zenroad"

    const val applicationId = "$applicationIdPrefix.$name"
    const val compileSdk = 30
    const val minSdk = 21
    const val targetSdk = 30
    const val versionName = "1.0.0"
    const val versionCode = 1
    const val dimension = name

    const val DASHBOARD_DISTANCE_LIMIT = "10" //in km

    const val APP_ID = "\"d0a14a5d-4942-4e0c-8afc-5e8dd6bad424\""
    const val INSTANCE_ID_PROD = "\"F7832F3A-F936-4E97-B570-D8B640E4F244\""
    const val INSTANCE_KEY_PROD = "\"C70A300F-CD9A-4541-90F3-73B5B660743E\""
    const val INSTANCE_ID_DEV = "\"da48d3b8-9dee-4d70-802d-108be3cb1d52\""
    const val INSTANCE_KEY_DEV = "\"be1c097b-bf88-44a2-92ca-53210be240da\""

    const val USER_SERVICE_URL = "\"https://user.telematicssdk.com/\""
    const val USER_SERVICE_URL_DEV = "\"https://user.telematicssdk.com/\""

    const val DRIVE_COINS_URL = "\"https://mobilesdk.telematicssdk.com/api/rewarding/\""
    const val DRIVE_COINS_URL_DEV = "\"https://mobilesdk.telematicssdk.com/api/rewarding/\""

    const val USER_STATISTICS = "\"https://api.telematicssdk.com/\""
    const val USER_STATISTICS_DEV = "\"https://api.telematicssdk.com/\""
}