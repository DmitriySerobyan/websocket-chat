import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication

class ApplicationTest : StringSpec({

    "root" {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                response.status() shouldBe HttpStatusCode.OK
                response.content shouldBe "HELLO WORLD!"
            }
        }
    }

    "ws echo" {
        withTestApplication({ module() }) {
            handleWebSocketConversation("/myws/echo") { incoming, outgoing ->
                val textMessages = listOf("111", "222")
                (incoming.receive() as Frame.Text).readText() shouldBe "Hi from server"
                for (msg in textMessages) {
                    outgoing.send(Frame.Text(msg))
                    (incoming.receive() as Frame.Text).readText() shouldBe "Client said: $msg"
                }
            }
        }
    }

})