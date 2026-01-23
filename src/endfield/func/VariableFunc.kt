package endfield.func

@FunctionalInterface
interface VariableFunc<P, R> {
	fun get(vararg param: P): R
}