package kr.geonu.example.blog.controller

import kr.geonu.example.blog.model.list.Empty
import kr.geonu.example.blog.model.list.State
import kr.geonu.mvc.ControllerMixin
import kr.geonu.mvc.Event
import rx.Observable
import rx.functions.Func2

class PostListController : ControllerMixin<State> {
    override fun observeEvent(eventStream: Observable<Event>): Observable<State> = eventStream
            .scan<State>(Empty, Func2<State, Event, State> { state, event ->
                when (event) {
                    else -> state
                }
            })
}
