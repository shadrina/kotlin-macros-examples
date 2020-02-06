@macro:CustomCopy
open class NeedCopy(val c: String) {
    var a: Int = 1
    var b: Int = 2

    override fun toString(): String = "a=$a, b=$b, c=$c"
}

fun main() {
    val needCopy = NeedCopy("abc")
    println(needCopy.copy(a = 42))
}