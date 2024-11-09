package com.benny.library.expose

import com.benny.library.expose.ReflectionExpose.wrap
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Proxy

/**
 * a utility for simplify call method by reflect
 * sample code:
 *
 * define interface with method that you want call by reflect
 * if return type is ExposeOf, it will wrap return value internal, simplify chain call of wrap
 *
 * interface ViewExpose: ExposeOf<View> {
 *     fun getViewRootImpl(): ViewRootImplExpose
 * }
 *
 * interface ViewRootImplExpose: ExposeOf<Any> {
 *     fun getSurfaceControl() : SurfaceControl
 * }
 *
 * fun main() {
 *    val viewRootExpose = ReflectionExpose.wrap<ViewExpose>(view).getViewRootImpl()
 *    val surfaceControl = viewRootExpose.getSurfaceControl()
 * }
 *
 */


interface ExposeOf<Target>

private interface ExposeTarget<T> {
  fun target(): T
}

private enum class InvokeType {
  METHOD, FIELD
}

class ExposeInvocationHandler (
  private val targetClass: Class<*>,
  private val target: Any?
) : InvocationHandler {
  companion object {
    private val exposeMethodMap = mutableMapOf<String, Method>()

    private val exposeFieldMap = mutableMapOf<String, java.lang.reflect.Field>()

    private val invokeTypeMap = mutableMapOf<String, InvokeType>()
  }

  override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
    if (method.declaringClass == ExposeTarget::class.java) {
      return target
    }

    return when(getInvokeType(method)) {
      InvokeType.FIELD -> invokeField(method, args)
      InvokeType.METHOD -> invokeMethod(method, args)
    }
  }

  private fun invokeMethod(method: Method, args: Array<out Any>?): Any? {
    val exposeMethod = exposeMethodMap.getOrPut(method.toString()) {
      targetClass.getMethod(method.name, * method.parameterTypes)
    }

    val result = if (args != null) {
      exposeMethod.invoke(target, *args)
    } else {
      exposeMethod.invoke(target)
    }

    return if (ExposeOf::class.java.isAssignableFrom(method.returnType)) {
      wrap(exposeMethod.returnType as Class<*>, result, method.returnType)
    } else result
  }

  private fun invokeField(method: Method, args: Array<out Any>?): Any? {
    val operation = method.name.substring(0, 3)
    val fieldName = method.name.substring(3).replaceFirstChar { it.lowercaseChar() }
    val signature = "${targetClass.canonicalName}.$fieldName"

    val exposeFiled = exposeFieldMap.getOrPut(signature) {
      targetClass.getField(fieldName)
    }

    return if (operation == "set") {
      exposeFiled.set(target, args?.first())
    } else {
      exposeFiled.get(target)
    }
  }

  private fun getInvokeType(method: Method): InvokeType {
    return invokeTypeMap.getOrPut(method.toString()) {
      if (method.declaringClass
        .getMethod(method.name, * method.parameterTypes)
        .getAnnotation(Field::class.java) != null
      ) {
        InvokeType.FIELD
      } else {
        InvokeType.METHOD
      }
    }
  }
}

object ReflectionExpose {
  inline fun <reified T> wrap(target: Any): T where T : ExposeOf<*> {
    return wrap(target.javaClass, target, T::class.java)
  }

  /**
   * for static method call
   */
  inline fun <reified T> wrap(): T where T : ExposeOf<*> {
    return wrap(
      (T::class.java.genericInterfaces.first() as ParameterizedType).actualTypeArguments[0] as Class<*>,
      null, T::class.java
    )
  }

  fun <T> wrap(targetClass: Class<*>, target: Any?, returnClass: Class<T>): T {
    val handler = ExposeInvocationHandler(targetClass, target)

    return Proxy.newProxyInstance(
      returnClass.classLoader,
      arrayOf(returnClass, ExposeTarget::class.java),
      handler
    ) as T
  }

  fun <T> unwrap(expose: ExposeOf<T>): T {
    return (expose as ExposeTarget<T>).target()
  }
}
