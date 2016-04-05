package kr.geonu.example.blog.util

import kr.geonu.example.blog.controller.PostController
import kr.geonu.example.blog.model.common.Post
import kr.geonu.example.blog.view.PostView
import kr.geonu.mvc.Event
import kr.geonu.mvc.MVC
import kr.geonu.util.ReferenceCounter
import rx.Observable
import rx.Observer
import rx.lang.kotlin.PublishSubject

private val PostVCL = ReferenceCounter({
    val view = PostView()
    val controller = PostController()

    val modelLifecycleSubject = PublishSubject<Map<Int, Post>>()
    val eventLifecycleSubject = PublishSubject<Event>()
    MVC.combine(view, controller, Observable.Transformer<Map<Int, Post>, Map<Int, Post>> { t ->
        t.subscribe(object: Observer<Map<Int, Post>> {
            override fun onError(e: Throwable?) {
                modelLifecycleSubject.onError(e)
            }

            override fun onCompleted() {
                modelLifecycleSubject.onCompleted()
            }

            override fun onNext(t: Map<Int, Post>?) {
                modelLifecycleSubject.onNext(t)
            }

        })
        modelLifecycleSubject
    }, Observable.Transformer<Event, Event> { t ->
        t.subscribe(object: Observer<Event> {
            override fun onError(e: Throwable?) {
                eventLifecycleSubject.onError(e)
            }

            override fun onNext(t: Event?) {
                eventLifecycleSubject.onNext(t)
            }

            override fun onCompleted() {
                eventLifecycleSubject.onCompleted()
            }

        })
        eventLifecycleSubject
    })

    Triple(view, controller, {
        if (!modelLifecycleSubject.hasCompleted()) {
            modelLifecycleSubject.onCompleted()
        }
        if (!eventLifecycleSubject.hasCompleted()) {
            eventLifecycleSubject.onCompleted()
        }
    })
}, { pair ->

})

val PostVC = ReferenceCounter({
    val vcl = PostVCL.retain()
    Pair(vcl.first, vcl.second)
}, { PostVCL.release() })
