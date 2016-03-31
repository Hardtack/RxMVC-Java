package kr.geonu.example.blog.model.detail

import kr.geonu.example.blog.model.common.Post

/* States for post detail */
interface State

// No such post
object NotExists: State
// Post exists
data class Some(val post: Post): State
