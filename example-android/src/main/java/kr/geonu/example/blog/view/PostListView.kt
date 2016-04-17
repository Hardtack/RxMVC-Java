package kr.geonu.example.blog.view

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import kr.geonu.example.R
import kr.geonu.example.blog.model.common.Post
import kr.geonu.example.blog.model.list.Some
import kr.geonu.example.blog.model.list.State
import kr.geonu.mvc.Event
import kr.geonu.mvc.ViewMixin
import kr.geonu.util.rx.rxBind
import rx.Observable
import rx.lang.kotlin.PublishSubject

class PostListView : CoordinatorLayout, ViewMixin<State> {
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
    }

    override fun observeModel(modelStream: Observable<State>): Observable<Event> {
        val eventStream = PublishSubject<Event>()

        val writeButton = findViewById(R.id.button_write) as FloatingActionButton
        val listRecyclerView = findViewById(R.id.recyclerview_items) as RecyclerView

        // Bind with data
        listRecyclerView.rxBind(modelStream.distinctUntilChanged().map { state ->
            when (state) {
                is Some -> state.items
                else -> listOf<Post>()

            }
        }, { parent, index, item ->
            val view = LayoutInflater.from(context).inflate(R.layout.post_list_content, parent, false)
            ViewHolder(view,
                    view.findViewById(R.id.textview_title) as TextView,
                    view.findViewById(R.id.textview_content) as TextView)
        }, { viewHolder, position, item ->
            viewHolder.titleTextView.text = item.title
            viewHolder.contentTextView.text = item.content
        })


        // Bind event
        writeButton.setOnClickListener { view ->
            Snackbar.make(
                    view,
                    "Replace with your own action",
                    Snackbar.LENGTH_LONG
            ).setAction("Action", null).show()
        }

        return eventStream
    }

    inner class ViewHolder(
            itemView: View,
            val titleTextView: TextView,
            val contentTextView: TextView
    ) : RecyclerView.ViewHolder(itemView)
}
