package kr.geonu.example.blog.controller

import kr.geonu.example.blog.model.common.Post
import kr.geonu.example.blog.view.PostView
import kr.geonu.mvc.ControllerMixin
import kr.geonu.mvc.Event
import rx.Observable

class PostController : ControllerMixin<Map<Int, Post>> {
    override fun observeEvent(eventStream: Observable<Event>): Observable<Map<Int, Post>> =
            eventStream.scan(mapOf<Int, Post>()) { entities, event ->
                when (event) {
                    is PostView.SetEntity -> {
                        val id = event.id
                        val entity = event.entity

                        val prevEntity = entities[id]
                        if (prevEntity != null && entity.equals(prevEntity)) {
                            entities
                        } else {
                            entities + Pair(id, entity)
                        }
                    }
                    is PostView.InvalidateEntity -> entities.filterNot { entry -> entry.key == event.id }
                    else -> entities
                }
            }
}
