import kotlin.meta.Node

annotation class HelloWorld {
    operator fun invoke(node: Node.Decl.Structured): Node {
        val method = qd"fun helloWorld() = println(\"Hello, world!\")"
        val members = node.members.toMutableList().apply { add(method) }
        return node.copy(members = members)
    }
}