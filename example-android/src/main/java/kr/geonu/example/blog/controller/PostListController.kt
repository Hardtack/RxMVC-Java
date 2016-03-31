package kr.geonu.example.blog.controller

import kr.geonu.mvc.ControllerMixin
import kr.geonu.example.blog.model.list.State
import kr.geonu.mvc.Event
import rx.Observable

class PostListController: ControllerMixin<State> {
    override fun observeEvent(eventStream: Observable<Event>): Observable<State> {
        throw UnsupportedOperationException()
    }
}
