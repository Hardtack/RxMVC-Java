package kr.geonu.example.blog.model.list

import kr.geonu.example.blog.model.common.Post

/* States for list of posts */
interface State

// No posts
object Empty: State
// Has some posts
data class Some(val items: List<Post>): State
