import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val client = HttpClient(CIO).config {
            install(WebSockets)
        }
        client.ws(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/ws/chat") {
            val sendLoop = launch {
                while (true) {
                    val message = readLine()
                    if (message != null) {
                        println("Send: $message")
                        outgoing.send(Frame.Text(message))
                    }
                }
            }
            val receiveLoop = launch {
                while (true) {
                    when (val frame = incoming.receive()) {
                        is Frame.Text -> {
                            val message = frame.readText()
                            println("Receive: $message")
                        }
                    }
                }
            }
            sendLoop.join()
            receiveLoop.join()
        }
    }
}