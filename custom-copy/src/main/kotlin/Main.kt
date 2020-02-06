import kotlin.meta.Node
import kotlin.meta.Node.*
import kotlin.meta.Node.Expr.This
import kotlin.meta.Node.Expr.Name
import kotlin.meta.Node.Expr.BinaryOp
import kotlin.meta.Node.Expr.BinaryOp.Oper.*
import kotlin.meta.Node.Expr.BinaryOp.Token.*
import kotlin.meta.Node.Decl.*
import kotlin.meta.Node.Decl.Func.*

annotation class CustomCopy {
    operator fun invoke(klass: Structured): Node {
        val primaryParams = klass.primaryConstructor?.params?.map { toFuncParam(it.name, it.type!!) } ?: listOf()
        val propertiesAsParams = klass.members.filterIsInstance<Property>().mapNotNull {
            val v = it.vars.singleOrNull()
            if (v?.type == null) null else toFuncParam(v.name, v.type!!)
        }
        val params = primaryParams + propertiesAsParams

        val constructorCall = qe"${klass.name}()".copy(args = primaryParams.map { toValueArg(it.name) })
        val resultDecl = qd"val result = $constructorCall"
        val stmts = mutableListOf<Stmt>(Stmt.Decl(resultDecl))
        propertiesAsParams.forEach {
            val valueSetter = qe"result.${it.name} = ${it.name}"
            stmts.add(Stmt.Expr(valueSetter))
        }
        val returnExpr = qe"return result"
        stmts.add(Stmt.Expr(returnExpr))

        val copy = qd"fun copy() {}".copy(params = params, body = bodyFromStmts(stmts), type = typeFromName(klass.name))
        return klass.copy(members = klass.members + copy)
    }

    private fun toFuncParam(name: Name, type: Type) = Param(
        mods = listOf(),
        readOnly = null,
        name = name,
        type = type,
        default = BinaryOp(
            lhs = This(label = null),
            oper = Token(token = DOT),
            rhs = name
        )
    )

    private fun toValueArg(name: Name) = ValueArg(name = null, asterisk = false, expr = name)

    private fun bodyFromStmts(stmts: List<Stmt>) = Body.Block(Block(stmts))

    private fun typeFromName(name: Name) = Type(
        mods = listOf(),
        ref = TypeRef.Simple(pieces = listOf(TypeRef.Simple.Piece(name = name, typeParams = listOf())))
    )
}