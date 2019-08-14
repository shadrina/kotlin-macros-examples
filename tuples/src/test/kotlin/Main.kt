@macro:CreateTuple(2)
class Tuple2

@macro:CreateTuple(5)
class Tuple5

fun main() {
    val tuple = Tuple5("a", 1, true, Pair("abc", 123), Tuple2("abc", 123))
    println(tuple)
}