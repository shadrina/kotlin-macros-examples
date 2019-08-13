import kotlin.meta.Node

annotation class HelloWorld {
    operator fun invoke(node: Node): Node {
        node as Node.Decl.Structured
        val method = qd"fun helloWorld() = println(\"Hello, world!\")"
        val members = node.members.toMutableList().apply { add(method) }
        return node.copy(members = members)
    }
}