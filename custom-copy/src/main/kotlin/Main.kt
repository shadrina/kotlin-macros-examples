import kotlin.meta.Node
import kotlin.meta.Node.*
import kotlin.meta.Node.Expr.Name
import kotlin.meta.Node.Decl.*
import kotlin.meta.Node.Decl.Func.*

annotation class CustomCopy {
    operator fun invoke(klass: Structured): Node {
        val primaryParams = klass.primaryConstructor?.params
            ?.map { createFuncParam(it.name, it.type!!) }
            ?: listOf()
        val propertiesAsParams = klass.members.filterIsInstance<Property>()
            .filter { !it.readOnly && !it.isPrivate() }
            .mapNotNull {
                val v = it.vars.singleOrNull()
                if (v?.type == null) null else createFuncParam(v.name, v.type!!)
            }

        val constructorArgs = primaryParams.map { createValueArg(it.name) }
        val constructorCall = qe"${klass.name}()".copy(args = constructorArgs)
        val resultDecl = qd"val result = $constructorCall".toStmt()
        val setters = propertiesAsParams.map { qe"result.${it.name} = ${it.name}".toStmt() }
        val returnExpr = qe"return result".toStmt()
        val stmts = listOf<Stmt>() + resultDecl + setters + returnExpr

        val copy = qd"fun copy() {}".copy(
            params = primaryParams + propertiesAsParams,
            type = typeFromName(klass.name),
            body = Body.Block(Block(stmts))
        )
        return klass.copy(members = klass.members + copy)
    }

    private fun Expr.toStmt() = Stmt.Expr(this)
    private fun Decl.toStmt() = Stmt.Decl(this)

    private fun Property.isPrivate() = mods.any { it is Modifier.Lit && it.keyword == Modifier.Keyword.PRIVATE }

    private fun createFuncParam(name: Name, type: Type) = Param(
        mods = listOf(),
        readOnly = null,
        name = name,
        type = type,
        default = qe"this.$name"
    )
    private fun createValueArg(name: Name) = ValueArg(
        name = null,
        asterisk = false,
        expr = name
    )

    private fun typeFromName(name: Name) = Type(
        mods = listOf(),
        ref = TypeRef.Simple(
            pieces = listOf(
                TypeRef.Simple.Piece(
                    name = name,
                    typeParams = listOf()
                )
            )
        )
    )
}