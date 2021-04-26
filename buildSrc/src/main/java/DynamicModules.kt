object Modules {

    const val data = ":data"
    const val account = ":features:account"
    const val dashboard = ":features:dashboard"
}

object DynamicModules {

    private const val HOME = ":features:home"

    val modules = mutableSetOf(HOME)
}