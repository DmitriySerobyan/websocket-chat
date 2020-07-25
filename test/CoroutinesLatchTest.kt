import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.random.Random

class CoroutinesLatchTest : StringSpec({

    "launch" {
        runBlocking {
            val childrenCount = 5
            val latch = CoroutinesLatch(childrenCount)
            val events = Collections.synchronizedList(mutableListOf<String>())
            val parentJob = launch {
                events.add("parent job start")
                repeat(childrenCount) { childNumber ->
                    launch {
                        events.add("child job №$childNumber start")
                        delay(Random.nextLong(200L, 500L))
                        latch.joinAndWaitOther()
                        delay(Random.nextLong(200L, 500L))
                        events.add("child job №$childNumber finish")
                    }
                }
            }
            events.add("parent job join")
            parentJob.join()
            events.add("parent job finish")
            events.forEach { event ->
                println(event)
            }
        }
    }


})