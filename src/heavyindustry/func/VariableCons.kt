package heavyindustry.func

@FunctionalInterface
interface VariableCons<P> {
	fun get(vararg params: P)
}