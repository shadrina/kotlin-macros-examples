import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import kotlin.meta.Node
import kotlin.meta.Node.Decl.*
import kotlin.meta.Node.Decl.Func.*
import kotlin.meta.Writer
import kotlin.meta.quote

annotation class ConstructFromJson(val json: String) {
    operator fun invoke(node: Node): Node {
        node as Structured
        val parsed = JsonParser().parse(json)
        val jsonObject = if (parsed.isJsonObject) parsed.asJsonObject else return node
        val klass = qd"data class ${node.name}()"
        return klass.build(jsonObject)
    }

    fun Structured.build(bodyObject: JsonObject): Structured {
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
                obj.isJsonPrimitive -> obj.asJsonPrimitive.typeToString()
                obj.isJsonArray -> TODO()
                obj.isJsonNull -> TODO()
                else -> "Any"
            }
            val property = qd"val ${key.decapitalize().quote()}: ${type.quote()}"
            params.add(property.convertToParam())
        }
        return copy(
            primaryConstructor = constructor.copy(params = params),
            members = members
        )
    }

    fun Property.convertToParam(): Param {
        val variable = vars.single()!!
        return Param(
            mods = mods,
            readOnly = readOnly,
            name = variable.name,
            type = variable.type,
            default = expr
        )
    }

    fun JsonPrimitive.typeToString() = when {
        isString -> "String"
        isNumber -> "Int"
        isBoolean -> "Boolean"
        else -> "Any"
    }
}

fun main() {
    val test = qd"class Message"
    println(
        Writer.write(ConstructFromJson("""{
        "title": "Hello",
        "author": {
          "name": "Alice",
          "age": 23,
          "online": false
        },
        "date": "01.02.2006",
        "additional": {
          "a": true,
          "b": 123,
          "c": {
            "d": "abc"
          }
        }
    }""")(test)))
}