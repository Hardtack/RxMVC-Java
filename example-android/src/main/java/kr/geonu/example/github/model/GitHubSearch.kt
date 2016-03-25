package kr.geonu.example.github.model

data class Repo(val name: String, val url: String)

data class SearchResult(val items: List<Repo>)

interface State

object None : State;
object Empty : State;
data class Some(val items: List<Repo>) : State;
data class Error(val error: Throwable) : State;

data class GitHubSearch(val loading: Boolean, val query: String?, val state: State)
