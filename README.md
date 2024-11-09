ReflectionExpose
======================
A utility for simplify reflection method call

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

## Field access also supported

```kotlin
interface WindowLayoutParamsExpose : ExposeOf<WindowManager.LayoutParams> {
  @set:Field
  @get:Field
  var privateFlags: Int
}

fun main() {
  val windowLayoutParamsExpose = ReflectionExpose.wrap<WindowLayoutParamsExpose>(lparams)
  // set
  windowLayoutParamsExpose.privateFlags = 1
  // get 
  val flags = windowLayoutParamsExpose.privateFlags
}

```

## call static method

```kotlin

fun main() {
  fun main() {
    // just call non args wrap function, there you go!
    val viewRootExpose = ReflectionExpose.wrap<ViewExpose>().getViewRootImpl()
  }
}

```