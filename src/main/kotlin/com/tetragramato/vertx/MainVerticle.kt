package com.tetragramato.vertx

import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch

class MainVerticle : CoroutineVerticle() {

  override suspend fun start() {

    val router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.get("/").coroutineHandler { ctx -> getMessage(ctx) }
    router.post("/").coroutineHandler { ctx -> postMessage(ctx) }

    vertx.createHttpServer()
      .requestHandler(router)
      .listenAwait(config.getInteger("http.port", 8888))

  }

  private fun getMessage(ctx: RoutingContext) {
    println("GET")
    ctx.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .setStatusCode(200)
      .end(json { obj("message" to "Welcome!").encode() })
  }

  private fun postMessage(ctx: RoutingContext) {
    val body = ctx.bodyAsJson
    println("POST : ${body.encode()}")
    ctx.response().setStatusCode(201)
      .putHeader("content-type", "application/json; charset=utf-8")
      .end(body.encode())
  }

  /**
   * An extension method for simplifying coroutines usage with Vert.x Web routers.
   */
  private fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit): Route {
    return handler { ctx ->
      launch(ctx.vertx().dispatcher()) {
        try {
          fn(ctx)
        } catch (e: Exception) {
          println(e.message)
          ctx.fail(e)
        }
      }
    }
  }
}
