import com.google.gson.Gson

@macro:ConstructFromJson("""{
  "a": 1,
  "b": true,
  "c": {
    "d": "value"
  }
}""")
class Example1

@macro:ConstructFromJson("""{
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
}""")
class Example2

fun main() {
    val gson = Gson()

    val json1 = object {}.javaClass.getResource("example1.json").readText(Charsets.UTF_8)
    val message1 = gson.fromJson(json1, Example1::class.java)
    println(message1)

    val json2 = object {}.javaClass.getResource("example2.json").readText(Charsets.UTF_8)
    val message2 = gson.fromJson(json2, Example2::class.java)
    println(message2)
}