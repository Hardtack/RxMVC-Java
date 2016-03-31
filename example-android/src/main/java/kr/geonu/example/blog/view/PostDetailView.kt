package kr.geonu.example.blog.view

import kr.geonu.example.blog.model.detail.State
import kr.geonu.mvc.Event
import kr.geonu.mvc.ViewMixin
import rx.Observable

class PostDetailView : ViewMixin<State> {
    override fun observeModel(modelStream: Observable<State>): Observable<Event> {
        throw UnsupportedOperationException()
    }
}
