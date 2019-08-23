package talham7391.reactor

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

class Reactor(val state: State) {
    private val worker = worker()
    private lateinit var parentJob: Job

    suspend fun processEvent(event: Event) {
        worker.send(event)
    }

    suspend fun stop() {
        worker.close()
        parentJob.join()
    }

    private fun worker(): SendChannel<Event> {
        val channel = Channel<Event>()
        parentJob = GlobalScope.launch {
            for (event in channel) {
                launch { event.process(state) }
            }
        }
        return channel
    }
}