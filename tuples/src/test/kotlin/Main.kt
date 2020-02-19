@macro:CreateTuple(2)
class Tuple2

@macro:CreateTuple(4)
class Tuple4

fun main() {
    val tuple = Tuple4("a", true, Pair("abc", 123), Tuple2("abc", 123))
    println(tuple)
}