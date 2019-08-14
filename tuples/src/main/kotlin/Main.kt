import kotlin.meta.Node
import kotlin.meta.Node.*
import kotlin.meta.Node.Modifier.*
import kotlin.meta.Node.Decl.*
import kotlin.meta.Node.Decl.Func.*
import kotlin.meta.quote

annotation class CreateTuple(val n: Int) {
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

    fun createParam(i: Int) = qd"val ${"t$i".quote()}: ${"T$i".quote()}".toParam()
    fun createTypeParam(i: Int) = TypeParam(listOf(Lit(Keyword.OUT)), "T$i", null)
    fun createValueArg(i: Int) = ValueArg(null, false, qe"${"t$i".quote()}")

    fun createToString(): Decl = qd"""fun toString() = "(" + toList().toString().removeSurrounding("[", "]") + ")" """
    fun createToList(): Decl {
        val args = (0 until n).map(::createValueArg)
        val impl = qe"listOf()".copy(args = args)
        val toList = qd"fun toList() = $impl"
        return toList
    }

    fun Property.toParam() = with (vars.single()!!) {
        Param(
            mods = mods,
            readOnly = readOnly,
            name = name,
            type = type,
            default = expr
        )
    }
}