import kotlin.meta.Node
import kotlin.meta.Node.*
import kotlin.meta.Node.Modifier.*
import kotlin.meta.Node.Decl.*
import kotlin.meta.Node.Decl.Func.*
import kotlin.meta.quote

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

    private fun createParam(i: Int): Param {
        val property = qd"val ${"t$i".quote()}: ${"T$i".quote()}"
        return Param.fromProperty(property)!!
    }
    private fun createTypeParam(i: Int) = TypeParam.fromName("T$i".quote()).withModifier(Keyword.OUT)

    private fun createToString(): Decl =
        qd"""override fun toString() = "(" + toList().toString().removeSurrounding("[", "]") + ")" """

    private fun createToList(): Decl {
        val args = (0 until n).map { i -> ValueArg.fromExpr("t$i".quote()) }
        val impl = qe"listOf()".copy(args = args)
        val toList = qd"fun toList() = $impl"
        return toList
    }
}