package kr.geonu.example.blog.model.common

import java.util.Date

data class Post(val id: Int, val title: String, val content: String, val createdAt: Date)
