import kotlin.meta.Node
import kotlin.meta.Node.*
import kotlin.meta.Node.Modifier.*
import kotlin.meta.Node.Decl.*
import kotlin.meta.Node.Decl.Func.*
import kotlin.meta.quote

annotation class CreateTuple(private val n: Int) {
    operator fun invoke(node: Node): Node {
        node as Structured
        val klass = qd"data class ${node.name}()"
        val params = (0 until n).map(::createParam)
        val typeParams = (0 until n).map(::createTypeParam)
        return klass.copy(
            typeParams = typeParams,
            primaryConstructor = klass.primaryConstructor?.copy(params = params),
            members = klass.members.toMutableList().apply { add(createToString()); add(createToList()) }
        )
    }

    private fun createParam(i: Int) = qd"val ${"t$i".quote()}: ${"T$i".quote()}".toParam()
    private fun createTypeParam(i: Int) = TypeParam(listOf(Lit(Keyword.OUT)), "T$i", null)
    private fun createValueArg(i: Int) = ValueArg(null, false, "t$i".quote())

    private fun createToString(): Decl = qd"""override fun toString() = "(" + toList().toString().removeSurrounding("[", "]") + ")" """
    private fun createToList(): Decl {
        val args = (0 until n).map(::createValueArg)
        val impl = qe"listOf()".copy(args = args)
        val toList = qd"fun toList() = $impl"
        return toList
    }

    private fun Property.toParam() = with (vars.single()!!) {
        Param(
            mods = mods,
            readOnly = readOnly,
            name = name,
            type = type,
            default = expr
        )
    }
}