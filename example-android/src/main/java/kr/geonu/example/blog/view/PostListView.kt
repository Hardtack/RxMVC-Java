package kr.geonu.example.blog.view

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import kr.geonu.example.R
import kr.geonu.example.blog.model.list.State
import kr.geonu.mvc.Event
import kr.geonu.mvc.ViewMixin
import rx.Observable
import rx.lang.kotlin.PublishSubject

class PostListView : CoordinatorLayout, ViewMixin<State> {
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
    }

    override fun observeModel(modelStream: Observable<State>): Observable<Event> {
        val eventStream = PublishSubject<Event>()


        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(
                    view,
                    "Replace with your own action",
                    Snackbar.LENGTH_LONG
            ).setAction("Action", null).show()
        }

        return eventStream
    }
}
