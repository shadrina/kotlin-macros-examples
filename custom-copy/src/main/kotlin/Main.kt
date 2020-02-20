import kotlin.meta.Node
import kotlin.meta.Node.*
import kotlin.meta.Node.Decl.*
import kotlin.meta.Node.Decl.Func.*

annotation class CustomCopy {
    operator fun invoke(klass: Structured): Node {
        val primaryParams = klass.primaryConstructor?.params
            ?.map { Param.fromNameAndType(it.name, it.type!!) }
            ?: listOf()
        val propertiesAsParams = klass.members.filterIsInstance<Property>()
            .filter { !it.readOnly && !it.isPrivate() }
            .mapNotNull { Param.fromProperty(it) }

        val constructorArgs = primaryParams.map { ValueArg.fromExpr(it.name) }
        val constructorCall = qe"${klass.name}()".copy(args = constructorArgs)
        val resultDecl = qd"val result = $constructorCall".toStmt()
        val setters = propertiesAsParams.map { qe"result.${it.name} = ${it.name}".toStmt() }
        val returnExpr = qe"return result".toStmt()
        val stmts = listOf<Stmt>() + resultDecl + setters + returnExpr

        val copy = qd"fun copy() {}".copy(
            params = primaryParams + propertiesAsParams,
            type = Type.fromName(klass.name),
            body = Body.Block(Block(stmts))
        )
        return klass.copy(members = klass.members + copy)
    }

    private fun Expr.toStmt() = Stmt.Expr(this)
    private fun Decl.toStmt() = Stmt.Decl(this)
}