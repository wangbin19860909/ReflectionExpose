package com.benny.library.expose

@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.PROPERTY_SETTER,
  AnnotationTarget.PROPERTY_GETTER
)
annotation class Field()
