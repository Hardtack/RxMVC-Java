package kr.geonu.example.blog.model

import kr.geonu.example.blog.model.common.Post
import kr.geonu.example.blog.model.list.State as ListState
import kr.geonu.example.blog.model.detail.State as DetailState

import java.util.HashMap

data class State(val entities: HashMap<Int, Post>, val listState: ListState, val detailState: DetailState)
