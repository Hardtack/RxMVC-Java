package kr.geonu.example.github.controller

import kr.geonu.example.github.api.GitHubService
import kr.geonu.example.github.model.*
import kr.geonu.example.github.view.GitHubSearchView
import kr.geonu.mvc.ControllerMixin
import kr.geonu.mvc.Event
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import java.util.concurrent.TimeUnit

class GitHubSearchController(val delegate: Delegate) : ControllerMixin<GitHubSearch> {
    /* Delegate */
    interface Delegate {
        fun openUrl(url: String)
    }

    /* Events */
    class Search(val query: String) : Event

    object EmptyQuery : Event

    class SearchStart(val query: String) : Event
    class SearchError(val error: Throwable) : Event
    class SearchComplete(val items: List<Repo>) : Event

    /* Members */
    val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    val github = retrofit.create(GitHubService::class.java)

    override fun observeEvent(eventStream: Observable<Event>): Observable<GitHubSearch> = eventStream
            // Filter QueryChange event and subscribe with throttling
            .mergeWith(
                    eventStream.filter { x -> x is GitHubSearchView.QueryChange }
                            .throttleLast(300, TimeUnit.MILLISECONDS)
                            .map { e -> Search((e as GitHubSearchView.QueryChange).query) }
            )
            // Handle asynchronous events
            .switchMap { e ->
                when (e) {
                    is Search -> e.query.trim().let { query ->
                        when (query.length) {
                        // Ignore empty query
                            0 -> Observable.just<Event>(EmptyQuery)
                            else -> searchRepo(query)
                        }
                    }
                    else -> Observable.just(e)
                }
            }
            .scan(GitHubSearch(false, null, Empty)) { state, event ->
                when (event) {
                    is EmptyQuery -> state.copy(loading = false, query = "", state = None)
                    is SearchStart -> state.copy(loading = true, query = event.query)
                    is SearchComplete -> state.copy(loading = false, state = Some(event.items))
                    is SearchError -> state.copy(loading = false, state = Error(event.error))
                    is GitHubSearchView.ClickItem -> {
                        // Delegate opens URL.
                        delegate.openUrl(event.item.svnUrl)
                        state
                    }
                    else -> state
                }
            }


    fun searchRepo(query: String) = Observable
            // Start with SearchStart event
            .just<Event>(SearchStart(query))
            // And then call API
            .concatWith(github.searchRepo(query)
                    .map(fun(result: SearchResult): Event = SearchComplete(result.items))
                    .onErrorResumeNext { error -> Observable.just(SearchError(error)) })

}
