package kr.geonu.example.main.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.jakewharton.rxbinding.view.RxView
import kr.geonu.example.R
import kr.geonu.example.main.model.ActivityList
import kr.geonu.mvc.Event
import kr.geonu.mvc.ViewMixin
import kr.geonu.util.rx.rxBind
import kr.geonu.util.view.DividerItemDecoration
import rx.Observable
import rx.lang.kotlin.PublishSubject

class MainView : RelativeLayout, ViewMixin<ActivityList> {
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
    }

    class ClickItem(val item: Pair<String, Class<*>>, val index: Int) : Event

    override fun observeModel(modelStream: Observable<ActivityList>): Observable<Event> {
        val eventStream = PublishSubject<Event>()

        val recyclerView = findViewById(R.id.recyclerview_items) as RecyclerView
        // Add divider
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);

        // Set layout manager
        recyclerView.layoutManager = LinearLayoutManager(context).let { layoutManager ->
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager
        }

        // Bind with data
        recyclerView.rxBind(modelStream.map { x ->
            x.items
        }.distinctUntilChanged(), { parent, position, item ->
            val view = LayoutInflater.from(context).inflate(R.layout.itemview_main, parent, false)
            ViewHolder(view, view.findViewById(R.id.textview_title) as TextView)
        }, { viewHolder, position, item ->
            val (name, activityClass) = item
            viewHolder.titleTextView.text = name
            RxView.clicks(viewHolder.itemView)
                    .subscribe { eventStream.onNext(ClickItem(item, position)) }
        })

        return eventStream
    }


    inner class ViewHolder(
            itemView: View,
            val titleTextView: TextView
    ) : RecyclerView.ViewHolder(itemView)
}
