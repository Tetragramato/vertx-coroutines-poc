package com.tetragramato.vertx

import java.util.*
import java.util.Collections.emptyMap


/**
 * @author vivienbrissat
 * Date: 30/09/2019
 */
data class Message(val id: UUID, val header: Header, val body: Body)

data class Header(val type: String)

data class Body(val values: Map<String, String> = emptyMap())

