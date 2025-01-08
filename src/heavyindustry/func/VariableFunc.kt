package heavyindustry.func

interface VariableFunc<T, R> {
    fun apply(vararg args: T): R
}
