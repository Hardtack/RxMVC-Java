package kr.geonu.util.rx

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1

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
    itemsStream.observeOn(AndroidSchedulers.mainThread()).subscribe(object: Action1<List<T>> {
        var adapter: RecyclerViewAdapter<T, VH>? = null
        override fun call(items: List<T>) {
            val currentAdapter = adapter
            if (currentAdapter != null) {
                currentAdapter.items = items
                currentAdapter.notifyDataSetChanged()
            } else {
                adapter = RecyclerViewAdapter(items, viewHolderCreator, viewBinder)
                recyclerView.adapter = adapter
            }
        }

    })
}
