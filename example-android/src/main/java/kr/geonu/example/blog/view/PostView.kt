package kr.geonu.example.blog.view

import com.github.andrewoma.dexx.kollection.ImmutableMap
import kr.geonu.example.blog.model.common.Post
import kr.geonu.mvc.Event
import kr.geonu.mvc.ViewMixin
import rx.Observable
import rx.Observer
import rx.lang.kotlin.PublishSubject

/**
 * Post view is interface of post entities store.
 * The events are map mutations, and interfaces ares entity observables.
 */
class PostView : ViewMixin<ImmutableMap<Int, Post>> {
    /* Events */
    class SetEntity(val id: Int, val entity: Post) : Event

    class InvalidateEntity(val id: Int) : Event

    /* Members */
    val eventStream = PublishSubject<Event>()
    val entitiesStream = PublishSubject<Map<Int, Post>>()

    override fun observeModel(modelStream: Observable<ImmutableMap<Int, Post>>): Observable<Event> {
        modelStream.distinctUntilChanged().subscribe(object : Observer<Map<Int, Post>> {
            override fun onCompleted() {
                entitiesStream.onCompleted()
                eventStream.onCompleted()
            }

            override fun onError(e: Throwable?) {
                entitiesStream.onError(e)
                eventStream.onCompleted()
            }

            override fun onNext(t: Map<Int, Post>) {
                entitiesStream.onNext(t)
            }

        })
        return eventStream
    }

    fun entitiesObservable(): Observable<Map<Int, Post>> = entitiesStream
    fun entityObservable(id: Int): Observable<Post?> = entitiesObservable().map { x -> x[id] }.distinctUntilChanged()

    /* Public methods that make events */
    fun setEntity(id: Int, entity: Post) {
        eventStream.onNext(SetEntity(id, entity))
    }

    fun setEntities(entities: Map<Int, Post>) {
        for ((id, entity) in entities) {
            this.setEntity(id, entity)
        }
    }
}
