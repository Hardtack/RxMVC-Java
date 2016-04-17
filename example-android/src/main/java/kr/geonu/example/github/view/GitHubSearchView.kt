package kr.geonu.example.github.view

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.rxbinding.view.RxView
import com.jakewharton.rxbinding.widget.RxTextView
import kr.geonu.example.R
import kr.geonu.example.github.model.*
import kr.geonu.mvc.Event
import kr.geonu.mvc.ViewMixin
import kr.geonu.util.rx.rxBind
import rx.Observable
import rx.lang.kotlin.PublishSubject

class GitHubSearchView : LinearLayout, ViewMixin<GitHubSearch> {
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
    }

    class ClickItem(val item: Repo, val index: Int) : Event
    class QueryChange(val query: String) : Event

    override fun observeModel(modelStream: Observable<GitHubSearch>): Observable<Event> {
        val eventStream = PublishSubject<Event>()

        val recyclerView = findViewById(R.id.recyclerview_items) as RecyclerView
        val searchTextInput = findViewById(R.id.edittext_search) as TextInputEditText

        // Bind with data

        // Loading
        modelStream.map { x -> x.loading }
                .distinctUntilChanged()
                .subscribe { loading ->
                    // Set loading state
                }

        // None-Some state
        modelStream.map { x -> x.state }
                .distinctUntilChanged()
                .subscribe { state ->
                    when (state) {
                        is None -> {
                        }
                        is Empty -> {
                        }
                        is Error -> {
                        }
                    }
                }

        // Error
        modelStream.map { x -> x.state }.map { x ->
            when (x) {
                is Error -> x.error
                else -> null
            }
        }.distinctUntilChanged().subscribe { error ->
            if (error != null) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        }

        // Text Input
        RxTextView.textChanges(searchTextInput)
                .map { x -> x.toString() }
                .subscribe { text ->
                    eventStream.onNext(QueryChange(text))
                }
        modelStream.map { x -> x.query }
                .distinctUntilChanged()
                .subscribe { query ->
                    if (!searchTextInput.text.toString().equals(query)) {
                        searchTextInput.setText(query ?: "")
                    }
                }

        // Repo List
        val itemsStream = modelStream.map { x -> x.state }
                .distinctUntilChanged()
                .map { state ->
                    when (state) {
                        is Some -> state.items
                        else -> listOf<Repo>()
                    }
                }
        recyclerView.rxBind(itemsStream, { parent, position, item ->
            val view = LayoutInflater.from(context).inflate(R.layout.itemview_repo, parent, false)
            ViewHolder(view,
                    view.findViewById(R.id.cardview_content) as CardView,
                    view.findViewById(R.id.textview_name) as TextView,
                    view.findViewById(R.id.textview_url) as TextView)
        }, { viewHolder, position, item ->
            viewHolder.nameTextView.text = item.name
            viewHolder.urlTextView.text = item.svnUrl
            RxView.clicks(viewHolder.contentCardView).subscribe {
                eventStream.onNext(ClickItem(item, position))
            }
        })

        return eventStream
    }


    inner class ViewHolder(
            itemView: View,
            val contentCardView: CardView,
            val nameTextView: TextView,
            val urlTextView: TextView
    ) : RecyclerView.ViewHolder(itemView)
}
