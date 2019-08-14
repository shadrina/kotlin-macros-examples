import kotlin.meta.*

annotation class ConstructFromJson(val json: String) {
    operator fun invoke(node: Node): Node= node
}

fun main() {

}