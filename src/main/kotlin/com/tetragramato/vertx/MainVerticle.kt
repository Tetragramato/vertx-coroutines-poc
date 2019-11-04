package com.tetragramato.vertx

import io.vertx.core.json.Json
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.*

class MainVerticle : CoroutineVerticle() {

    val channel = Channel<Message>()

    override suspend fun start() {

        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.get("/").coroutineHandler { ctx -> getMessage(ctx) }
        router.post("/").coroutineHandler { ctx -> postMessage(ctx) }

        vertx.createHttpServer()
            .requestHandler(router)
            .listenAwait(config.getInteger("http.port", 8888))

    }

    private suspend fun getMessage(ctx: RoutingContext) {
        val message: Message = channel.receive()
        ctx.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .setStatusCode(200)
            .end(Json.encode(message))
    }

    private suspend fun postMessage(ctx: RoutingContext) {
        val body = ctx.bodyAsJson
        channel.send(Message(UUID.randomUUID(), Header("toto"), Body(mapOf(Pair("Message", body.encode())))))
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
