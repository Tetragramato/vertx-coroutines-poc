package com.tetragramato.vertx

import io.vertx.core.Vertx
import io.vertx.kotlin.core.deployVerticleAwait

/**
 * @author Brissat
 */
suspend fun main() {
  val vertx = Vertx.vertx()
  try {
    vertx.deployVerticleAwait("com.tetragramato.vertx.MainVerticle")
    println("Application started")
  } catch (exception: Throwable) {
    println("Could not start application")
    exception.printStackTrace()
  }
}
