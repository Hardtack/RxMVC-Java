package kr.geonu.example.github.api

import kr.geonu.example.github.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface GitHubService {
    @GET("/search/repositories")
    fun searchRepo(@Query("q") query: String): Observable<SearchResult>
}
