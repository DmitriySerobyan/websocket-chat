import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.http.ContentType
import io.ktor.http.cio.websocket.*
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import java.time.Duration
import java.util.*
import kotlin.collections.LinkedHashSet

fun main() {
    val server = embeddedServer(Netty, port = 8080) {
        module()
    }
    server.start(wait = true)
}

fun Application.module() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val wsConnections = Collections.synchronizedSet(LinkedHashSet<DefaultWebSocketSession>())

    routing {
        get("/") {
            call.respondText("root", contentType = ContentType.Text.Plain)
        }

        webSocket("/ws/echo") {
            send(Frame.Text("Hi from server"))
            while (true) {
                val frame = incoming.receive()
                if (frame is Frame.Text) {
                    send(Frame.Text("You said: " + frame.readText()))
                }
            }
        }

        webSocket("/ws/chat") {
            wsConnections += this
            try {
                send(Frame.Text("you joined"))
                while (true) {
                    when (val frame = incoming.receive()) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            for (wsConnection in wsConnections) {
                                if (wsConnection != this) {
                                    wsConnection.outgoing.send(Frame.Text(text))
                                }
                            }
                        }
                    }
                }
            } finally {
                wsConnections -= this
            }
        }
    }
}
