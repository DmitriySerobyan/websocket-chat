import io.kotest.core.spec.style.StringSpec
import org.slf4j.LoggerFactory

class LogTest : StringSpec({

    val logger = LoggerFactory.getLogger(LogTest::class.java)

    "!error log" {
        logger.error("error log")
    }

})