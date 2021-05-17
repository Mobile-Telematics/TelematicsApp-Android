object Modules {

    const val app = ":app"
    const val data = ":data"
    const val domain = ":domain"
    const val account = ":features:account"
    const val dashboard = ":features:dashboard"
}

object DynamicModules {

    val modules = mutableSetOf(Modules.account)
}