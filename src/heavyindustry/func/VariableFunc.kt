package heavyindustry.func

interface VariableFunc<P, R> {
	fun apply(vararg args: P): R
}
