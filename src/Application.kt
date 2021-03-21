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
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.time.Duration
import java.util.*
import java.util.concurrent.CancellationException
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

    val clients = Collections.synchronizedSet(LinkedHashSet<DefaultWebSocketSession>())

    routing {
        get("/") {
            call.respondText("root", contentType = ContentType.Text.Plain)
        }

        webSocket("/ws/echo") {
            outgoing.send(Frame.Text("Hi from server"))
            while (true) {
                val frame = incoming.receive()
                if (frame is Frame.Text) {
                    outgoing.send(Frame.Text("You said: ${frame.readText()}"))
                }
            }
        }

        webSocket("/ws/chat") {
            clients += this
            try {
                println("Client joined the chat")
                outgoing.send(Frame.Text("You joined the chat"))
                for (client in clients.toList()) {
                    if (client != this) {
                        client.outgoing.send(Frame.Text("Client joined the chat"))
                    }
                }
                while (true) {
                    when (val frame = incoming.receive()) {
                        is Frame.Text -> {
                            val message = frame.readText()
                            println("Receive: $message")
                            for (wsConnection in clients.toList()) {
                                if (wsConnection != this) {
                                    wsConnection.outgoing.send(Frame.Text(message))
                                }
                            }
                        }
                    }
                }
            } catch (_: CancellationException) {
            } catch (_: ClosedReceiveChannelException) {
            } finally {
                clients -= this
                println("Client left the chat")
                for (wsConnection in clients.toList()) {
                    wsConnection.outgoing.send(Frame.Text("Client left the chat"))
                }
            }
        }
    }
}
