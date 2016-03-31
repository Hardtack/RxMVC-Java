package kr.geonu.example.blog.controller

import kr.geonu.example.blog.model.State
import kr.geonu.mvc.ControllerMixin
import kr.geonu.mvc.Event
import rx.Observable

class BlogController(
        val detailController: PostDetailController,
        val listController: PostListController): ControllerMixin<State> {
    override fun observeEvent(eventStream: Observable<Event>): Observable<State> {
        throw UnsupportedOperationException()
    }
}
