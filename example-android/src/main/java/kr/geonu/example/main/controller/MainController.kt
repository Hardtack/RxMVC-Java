package kr.geonu.example.main.controller

import kr.geonu.example.activity.GitHubSearchActivity
import kr.geonu.example.activity.PostListActivity
import kr.geonu.example.activity.UpDownActivity
import kr.geonu.example.main.model.ActivityList
import kr.geonu.example.main.view.MainView
import kr.geonu.mvc.ControllerMixin
import kr.geonu.mvc.Event
import rx.Observable

class MainController(val delegate: Delegate) : ControllerMixin<ActivityList> {
    /* Delegate */
    interface Delegate {
        fun startActivity(activityClass: Class<*>)
    }
    override fun observeEvent(eventStream: Observable<Event>): Observable<ActivityList> {
        // Handle event
        eventStream.subscribe { e ->
            when (e) {
                is MainView.ClickItem -> {
                    val (name, activityClass) = e.item
                    delegate.startActivity(activityClass)
                }
            }
        }
        // Return static state
        return Observable.just(ActivityList(listOf(
                Pair("Up & Down", UpDownActivity::class.java),
                Pair("GitHub Search", GitHubSearchActivity::class.java),
                Pair("Local Blog", PostListActivity::class.java)
        )))
    }
}
