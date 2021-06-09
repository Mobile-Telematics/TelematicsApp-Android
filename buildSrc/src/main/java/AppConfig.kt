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
    const val INSTANCE_ID_PROD = "\"400f4c9d-da1a-4d6e-9358-99b8aeb72584\""
    const val INSTANCE_KEY_PROD = "\"8e4e55c1-eac7-430a-9499-cb5036d2627b\""
    /*const val INSTANCE_ID_DEV = "\"da48d3b8-9dee-4d70-802d-108be3cb1d52\""
    const val INSTANCE_KEY_DEV = "\"be1c097b-bf88-44a2-92ca-53210be240da\""*/

    const val USER_SERVICE_URL = "\"https://user.telematicssdk.com/\""
    const val USER_SERVICE_URL_DEV = "\"https://user.telematicssdk.com/\""

    const val DRIVE_COINS_URL = "\"https://mobilesdk.telematicssdk.com/api/rewarding/\""
    const val DRIVE_COINS_URL_DEV = "\"https://mobilesdk.telematicssdk.com/api/rewarding/\""

    const val USER_STATISTICS = "\"https://api.telematicssdk.com/\""
    const val USER_STATISTICS_DEV = "\"https://api.telematicssdk.com/\""
}