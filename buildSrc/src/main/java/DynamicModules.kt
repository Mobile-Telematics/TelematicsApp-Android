object Modules {

    const val app = ":app"
    const val data = ":data"
    const val domain = ":domain"
    const val authentication = ":authentication"
    const val content = ":content"
    const val account = ":features:account"
    const val dashboard = ":features:dashboard"
    const val feed = ":features:feed"
    const val leaderboard = ":features:leaderboard"
    const val reward = ":features:reward"
    const val obd = ":features:obd"
    const val HERE_SDK = ":HERE-sdk"
}

object DynamicModules {

    val modules = mutableSetOf(Modules.account)
}