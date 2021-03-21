import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

class CoroutinesLatch(
    private val count: Int
) {
    private val joinedCount = AtomicInteger(0)
    private val joiningNotifications = Channel<Unit>()
    private val permissionToContinue = Channel<Unit>()

    init {
        require(count > 0) { "Count must be greater than 0" }
        GlobalScope.launch {
            waitUntilAllJoin()
            allowAllContinue()
        }
    }

    private suspend fun waitUntilAllJoin() {
        for(joiningNotification in joiningNotifications) {
            if(joinedCount.incrementAndGet() >= count) break
        }
        joiningNotifications.close()
    }

    private suspend fun allowAllContinue() {
        repeat(count) {
            permissionToContinue.send(Unit)
        }
        permissionToContinue.close()
    }

    suspend fun joinAndWaitOther() {
        notifyAboutJoin()
        waitPermission()
    }

    private suspend fun notifyAboutJoin() {
        joiningNotifications.send(Unit)
    }

    private suspend fun waitPermission() {
        permissionToContinue.receive()
    }
}