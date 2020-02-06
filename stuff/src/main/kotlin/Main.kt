import kotlin.meta.Node

annotation class SimpleFunctionAnnotation {
    operator fun invoke(node: Node.Decl.Func): Node {
        return qd"fun helloWorld() = println(\"Hello, world!\")";
    }
}