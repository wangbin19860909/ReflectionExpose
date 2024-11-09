import com.benny.library.expose.ExposeOf
import com.benny.library.expose.Field
import com.benny.library.expose.ReflectionExpose
import org.junit.Assert.assertEquals
import org.junit.Test


interface SampleObjectExpose : ExposeOf<SampleObject> {
    @set:Field
    @get:Field
    var privateFlags: Int

    fun sayHello(): String

    fun sayWorld(): String
}

interface SampleKtObjectExpose : ExposeOf<SampleKtObject> {
    var privateFlags: Int

    fun sayHello(): String
}

class ReflectionExposeTest {

    @Test
    fun test_java_expose_invoke() {
        val testObject = SampleObject()
        val objectExpose = ReflectionExpose.wrap<SampleObjectExpose>(testObject)

        val hello = objectExpose.sayHello()
        assertEquals(hello, "Hello")

        objectExpose.privateFlags = 100
        assertEquals(objectExpose.privateFlags, 100)
    }

    @Test
    fun test_kotlin_expose_invoke() {
        val testObject = SampleKtObject()
        val objectExpose = ReflectionExpose.wrap<SampleKtObjectExpose>(testObject)

        val hello = objectExpose.sayHello()
        assertEquals(hello, "Hello")

        objectExpose.privateFlags = 100
        assertEquals(objectExpose.privateFlags, 100)
    }

    @Test
    fun test_java_static_invoke() {
        val classExpose = ReflectionExpose.wrap<SampleObjectExpose>()
        val world = classExpose.sayWorld()
        assertEquals(world, "World")
    }
}