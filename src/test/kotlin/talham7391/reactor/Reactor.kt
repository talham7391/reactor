/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package talham7391.reactor

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlin.test.Test

data class TestState(
    var counter: Int = 0
) : State {
    val mutex = Mutex()
}

data class TestEvent(override val name: String) : Event {
    override suspend fun process(state: State) {
        if (state !is TestState) return
        state.mutex.lock()
        state.counter += 1
        state.mutex.unlock()
    }
}

data class ComplexEvent(val count: Int) : Event {
    override val name: String = "complex"
    override suspend fun process(state: State) {

    }
}

class ComplexTransformer : EventTransformer {
    override fun deserialize(rawEvent: String): Event {
        val mapper = jacksonObjectMapper()
        return mapper.readValue<ComplexEvent>(rawEvent)
    }
}

class TestReactor {
    @Test fun testReactor() {
        val testState = TestState()
        val reactor = Reactor(testState)
        val times = 100
        runBlocking {
            repeat(times) {
                reactor.processEvent(TestEvent(""))
            }
            reactor.stop()
        }
        assert(testState.counter == times)
    }

    @Test fun testSerialization() {
        EventSerializer.registerTransformer("complex", ComplexTransformer())

        var same = false

        val complexEvent1 = ComplexEvent(1)
        val complexEvent2 = ComplexEvent(2)
        val str1 = EventSerializer.serialize(complexEvent1)
        val str2 = EventSerializer.serialize(complexEvent2)
        if (str1 != null && str2 != null) {
            println(str1)
            val sameComplexEvent1 = EventSerializer.deserialize(str1)
            val sameComplexEvent2 = EventSerializer.deserialize(str2)
            if (sameComplexEvent1 != null && sameComplexEvent2 != null) {
                if (complexEvent1 == sameComplexEvent1 && complexEvent2 == sameComplexEvent2) {
                    same = true
                }
            }
        }

        assert(same)
    }
}