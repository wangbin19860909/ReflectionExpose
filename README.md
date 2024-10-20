ReflectionExpose - a utility for simplify reflection method call
======================

# How to use

```kotlin

// 1. define a interface extends ExposeOf, the generic type is which class you want to reflection
// 2. define same method you want call in generic type
// 3. wrap return type. for example:
//    fun getViewRootImpl(): ViewRootImplExpose
//    the actual return type is ViewRootImpl which can not be reference, so we can define ViewRootImplExpose and as return type
interface ViewExpose : ExposeOf<View> {
    fun getBoundsOnScreen(outRect: Rect)
    
    fun getViewRootImpl(): ViewRootImplExpose
}

interface ViewRootImplExpose : ExposeOf<Any> {
    fun getSurfaceControl(): SurfaceControl
}

fun main() {
    val viewRootExpose = ReflectionExpose.wrap<ViewExpose>(view).getViewRootImpl()
    val surfaceControl = viewRootExpose.getSurfaceControl()
}

```