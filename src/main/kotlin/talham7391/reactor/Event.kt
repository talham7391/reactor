package talham7391.reactor

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

interface Event {
    val name: String
    suspend fun process(state: State)
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class BasicEvent(override val name: String) : Event {
    override suspend fun process(state: State) {}
}

interface EventTransformer {
    fun serialize(event: Event): String {
        val mapper = jacksonObjectMapper()
        return mapper.writeValueAsString(event)
    }
    fun deserialize(rawEvent: String): Event
}

object EventSerializer {
    private val transformers = mutableMapOf<String, EventTransformer>()

    fun registerTransformer(name: String, transformer: EventTransformer) {
        transformers[name] = transformer
    }

    fun serialize(event: Event): String? {
        transformers[event.name]?.let { return it.serialize(event) }
        return null
    }

    fun deserialize(rawEvent: String): Event? {
        val mapper = jacksonObjectMapper()
        val event = mapper.readValue<BasicEvent>(rawEvent)
        transformers[event.name]?.let { return it.deserialize(rawEvent) }
        return null
    }
}