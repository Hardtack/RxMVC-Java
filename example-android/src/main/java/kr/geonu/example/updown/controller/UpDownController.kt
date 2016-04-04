package kr.geonu.example.updown.controller

import kr.geonu.example.updown.model.UpDown
import kr.geonu.example.updown.view.UpDownView
import rx.Observable
import java.util.concurrent.TimeUnit

class UpDownController : kr.geonu.mvc.ControllerMixin<UpDown> {
    internal class Reset : kr.geonu.mvc.Event

    override fun observeEvent(eventStream: Observable<kr.geonu.mvc.Event>): Observable<UpDown> =
            eventStream.flatMap { event ->
                // Handle asynchronous event
                when (event) {
                    is UpDownView.ClickDelayedReset -> Observable.just(Reset()).delay(3, TimeUnit.SECONDS)
                    else -> Observable.just(event)
                }
            }.scan(UpDown(0)) { state, event ->
                when (event) {
                    is UpDownView.ClickUp -> state.copy(state.value + 1)
                    is UpDownView.ClickDown -> state.copy(state.value - 1)
                    is Reset -> UpDown(0)
                    else -> state
                }
            }
}
