import com.google.gson.JsonParser
import kotlin.meta.*

annotation class ConstructFromJson(val json: String) {
    operator fun invoke(node: Node): Node {
        val jsonParser = JsonParser()
        return node
    }
}

fun main() {

}