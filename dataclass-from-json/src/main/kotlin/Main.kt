import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import kotlin.meta.Node
import kotlin.meta.Node.Decl.*
import kotlin.meta.Node.Decl.Func.*
import kotlin.meta.quote

annotation class ConstructFromJson(private val json: String) {
    operator fun invoke(node: Structured): Node {
        val jsonObject = JsonParser().parse(json).asJsonObject
        val klass = qd"data class ${node.name}()"
        return klass.build(jsonObject)
    }

    private fun Structured.build(bodyObject: JsonObject): Structured {
        val constructor = primaryConstructor ?: return this
        val params = constructor.params.toMutableList()
        val members = members.toMutableList()
        for (entry in bodyObject.entrySet()) {
            val key = entry.key
            val obj = bodyObject.get(key)
            val type = when {
                obj.isJsonObject -> {
                    val name = key.capitalize()
                    val klass = qd"data class ${name.quote()}()"
                    members.add(klass.build(obj.asJsonObject))
                    name
                }
                obj.isJsonPrimitive -> obj.asJsonPrimitive.toKotlinType()
                obj.isJsonArray -> TODO()
                obj.isJsonNull -> TODO()
                else -> "Any"
            }
            val property = qd"val ${key.decapitalize().quote()}: ${type.quote()}"
            params.add(Param.fromProperty(property)!!)
        }
        return copy(
            primaryConstructor = constructor.copy(params = params),
            members = members
        )
    }

    private fun JsonPrimitive.toKotlinType() = when {
        isString -> "String"
        isNumber -> "Int"
        isBoolean -> "Boolean"
        else -> "Any"
    }
}