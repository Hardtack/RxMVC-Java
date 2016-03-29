package kr.geonu.example.github.controller

import android.app.Activity
import android.content.Intent
import android.net.Uri
import kr.geonu.example.github.api.GitHubService
import kr.geonu.example.github.model.*
import kr.geonu.example.github.view.GitHubSearchView
import kr.geonu.mvc.ControllerMixin
import kr.geonu.mvc.Event
import kr.geonu.util.rx.mergeLatest
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import java.util.concurrent.TimeUnit

class GitHubSearchController(val activity: Activity) : ControllerMixin<GitHubSearch> {
    class Search(val query: String) : Event

    object EmptyQuery : Event

    class SearchStart(val query: String) : Event
    class SearchError(val error: Throwable) : Event
    class SearchComplete(val items: List<Repo>) : Event

    override fun observeEvent(eventStream: Observable<Event>): Observable<GitHubSearch> {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val github = retrofit.create(GitHubService::class.java)
        return Observable.just(GitHubSearch(false, null, Empty))
                .mergeLatest(eventStream
                        // Filter QueryChange event and subscribe with throttling
                        .mergeWith(
                                eventStream.filter { x -> x is GitHubSearchView.QueryChange }
                                        .throttleLast(300, TimeUnit.MILLISECONDS)
                                        .map { e -> Search((e as GitHubSearchView.QueryChange).query) }
                        )
                        // Handle asynchronous events
                        .switchMap { e ->
                            when (e) {
                                is Search -> {
                                    e.query.trim().let { query ->
                                        if (query.length == 0) {
                                            Observable.just<Event>(EmptyQuery)
                                        } else {
                                            Observable
                                                    // Start with SearchStart event
                                                    .just<Event>(SearchStart(query))
                                                    // And then call API
                                                    .concatWith(github.searchRepo(query).map(
                                                            fun(result: SearchResult): Event = SearchComplete(result.items)
                                                    ).onErrorResumeNext { error ->
                                                        Observable.just(SearchError(error))
                                                    })
                                        }
                                    }
                                }
                                else -> Observable.just(e)
                            }
                        }
                ) { state, event ->
                    // Handle synchronous events
                    when (event) {
                        is EmptyQuery -> state.copy(loading = false, query = "", state = None)
                        is SearchStart -> state.copy(loading = true, query = event.query)
                        is SearchComplete -> state.copy(loading = false, state = Some(event.items))
                        is SearchError -> state.copy(loading = false, state = Error(event.error))
                        is GitHubSearchView.ClickItem -> {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.item.svnUrl));
                            activity.startActivity(intent);
                            state
                        }
                        else -> state
                    }
                }
    }
}
