import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.random.Random

class CoroutinesLatchTest : StringSpec({

    "launch" {
        runBlocking(Dispatchers.Default) {
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

            events.forEach { event -> println(event) }

            events shouldHaveSize 13

            val childrenEvents = events.filter { "child job" in it }
            val eventsChildJobStartOrder = childrenEvents.withIndex()
                .filter { "start" in it.value }
                .map { it.index }
            val eventsChildJobFinishOrder = childrenEvents.withIndex()
                .filter { "finish" in it.value }
                .map { it.index }

            eventsChildJobStartOrder.forEach { childEventJobStartOrder ->
                eventsChildJobFinishOrder.forEach { childEventJobFinishOrder ->
                    childEventJobFinishOrder shouldBeGreaterThan childEventJobStartOrder
                }
            }
        }
    }

})