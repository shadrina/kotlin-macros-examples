import org.junit.jupiter.api.Test

@macro:HelloWorld
class Example

class Test {
    @Test
    fun simple() {
        Example().helloWorld()
    }
}