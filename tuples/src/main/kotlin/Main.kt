import kotlin.meta.Node
import kotlin.meta.Node.*
import kotlin.meta.Node.Modifier.*
import kotlin.meta.Node.Decl.*
import kotlin.meta.Node.Decl.Func.*
import kotlin.meta.quote

/*
 * data class TupleN<out T0, out T1, ...>(val t0: T0, val t1: T1, ...) {
 *     override fun toString() = "(" + toList().toString().removeSurrounding("[", "]") + ")"
 *     fun toList() = listOf(t0, t1, ...)
 * }
 */

annotation class CreateTuple(private val n: Int) {
    operator fun invoke(node: Structured): Node {
        val klass = qd"data class ${node.name}()"
        val params = (0 until n).map(::createParam)
        val typeParams = (0 until n).map(::createTypeParam)
        return klass.copy(
            typeParams = typeParams,
            primaryConstructor = klass.primaryConstructor?.copy(params = params),
            members = klass.members + createToString() + createToList()
        )
    }

    private fun createParam(i: Int) = Param
        .fromNameAndType("t$i".quote(), Type.fromName("T$i".quote()))
        .copy(readOnly = true)
    private fun createTypeParam(i: Int) = TypeParam
        .fromName("T$i".quote())
        .withModifier(Keyword.OUT)

    private fun createToString(): Decl =
        qd"""override fun toString() = "(" + toList().toString().removeSurrounding("[", "]") + ")" """

    private fun createToList(): Decl {
        val args = (0 until n).map { i -> ValueArg.fromExpr("t$i".quote()) }
        val impl = qe"listOf()".copy(args = args)
        val toList = qd"fun toList() = $impl"
        return toList
    }
}