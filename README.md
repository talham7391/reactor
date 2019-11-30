# Reactor
Run code in response to user defined events. I originally created this pattern to organize server-client communication in multiplayer games.

# Usage

Define a state to operate on:

```kotlin
import talham7391.reactor.State

data class GlobalState(var count: Int) : State
```

Define some events that will operate on the state:

```kotlin
import talham7391.reactor.Event

data class MyEvent(val localState: Int) : Event {
	override val name: String = "my-event"
	
	override suspend fun process(state: State) {
		if (state !is GlobalState) return
		state.count += localState
	}
}
```

Setup the Reactor with the global state. Any local state must be passed through the events.

```kotlin
fun main() = runBlocking<Unit> {
	val globalState = GlobalState()
	val reactor = Reactor(globalState)
	
	val e1 = MyEvent(1)
	val e2 = MyEvent(2)
	
	reactor.processEvent(e1)
	reactor.processEvent(e2)
	reactor.stop()
	
	println(globalState.count) // Expected output: 3
}
```

The `name` field on `Event` is meant to be used for serialization/deserialization. Examples of serialization/deserialization can be found in the test files. 