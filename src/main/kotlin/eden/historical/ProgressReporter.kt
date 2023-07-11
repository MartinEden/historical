package eden.historical

import java.time.Duration
import java.time.Instant

class ProgressReporter(private val reportFrequency: Duration) {
    private var timeLastUpdatedUser = Instant.now()

    fun update(currentState: String) {
        val now = Instant.now()
        if (Duration.between(timeLastUpdatedUser, now) > reportFrequency) {
            timeLastUpdatedUser = now
            println(currentState)
        }
    }
}