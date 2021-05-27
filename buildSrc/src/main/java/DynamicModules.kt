object Modules {

    const val app = ":app"
    const val data = ":data"
    const val domain = ":domain"
    const val authentication = ":authentication"
    const val content = ":content"
    const val tracking = ":tracking_api"
    const val account = ":features:account"
    const val dashboard = ":features:dashboard"
    const val settings = ":features:settings"
}

object DynamicModules {

    val modules = mutableSetOf(Modules.account)
}