package kr.geonu.util.rx

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

class RecyclerViewAdapter<T, VH : RecyclerView.ViewHolder>(
        var items: List<T>,
        val viewHolderCreator: (parent: ViewGroup, position: Int, item: T) -> VH,
        val viewBinder: (viewHolder: VH, position: Int, item: T) -> Unit
) : RecyclerView.Adapter<VH>() {
    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, position: Int) = viewHolderCreator(parent, position, items[position])

    override fun onBindViewHolder(viewHolder: VH, position: Int) = viewBinder(viewHolder, position, items[position])
}

fun <T, VH : RecyclerView.ViewHolder> RecyclerView.rxBind(
        itemsStream: Observable<List<T>>,
        viewHolderCreator: (parent: ViewGroup, position: Int, item: T) -> VH,
        viewBinder: (viewHolder: VH, position: Int, item: T) -> Unit
) {
    val recyclerView = this
    val (first, rest) = itemsStream.observeOn(AndroidSchedulers.mainThread()).firstAndRest()
    first.subscribe { items ->
        val adapter = RecyclerViewAdapter(items, viewHolderCreator, viewBinder)
        recyclerView.adapter = adapter
        rest.subscribe { items ->
            adapter.items = items
            adapter.notifyDataSetChanged()
        }
    }
}
