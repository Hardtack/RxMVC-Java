package kr.geonu.example.blog.controller

import com.github.andrewoma.dexx.kollection.ImmutableMap
import com.github.andrewoma.dexx.kollection.immutableMapOf
import kr.geonu.example.blog.model.common.Post
import kr.geonu.example.blog.view.PostView
import kr.geonu.mvc.ControllerMixin
import kr.geonu.mvc.Event
import rx.Observable

class PostController : ControllerMixin<ImmutableMap<Int, Post>> {
    override fun observeEvent(eventStream: Observable<Event>): Observable<ImmutableMap<Int, Post>> =
            eventStream.scan(immutableMapOf<Int, Post>()) { entities, event ->
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
                    is PostView.InvalidateEntity -> entities - event.id
                    else -> entities
                }
            }
}
