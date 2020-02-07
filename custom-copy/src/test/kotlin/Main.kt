import kotlin.meta.quote

@macro:CustomCopy
open class NeedCopy(val a: String) {
    var b: Int = 1
    var c: Int = 2
    private var p: Int = 3

    override fun toString(): String = "a=$a, b=$b, c=$c, p=$p"
}

fun main() {
    val needCopy = NeedCopy("original")
    println(needCopy.copy(a = "copy"))
}