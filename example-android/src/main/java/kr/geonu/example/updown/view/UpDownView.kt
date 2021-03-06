package kr.geonu.example.updown.view

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.jakewharton.rxbinding.view.RxView
import com.jakewharton.rxbinding.widget.RxTextView
import kr.geonu.example.R
import kr.geonu.example.updown.model.UpDown
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.PublishSubject

class UpDownView : LinearLayout, kr.geonu.mvc.ViewMixin<UpDown> {
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
    }

    class ClickUp : kr.geonu.mvc.Event
    class ClickDown : kr.geonu.mvc.Event
    class ClickDelayedReset : kr.geonu.mvc.Event

    override fun observeModel(modelStream: Observable<UpDown>): Observable<kr.geonu.mvc.Event> {
        val eventStream = PublishSubject<kr.geonu.mvc.Event>()

        // Find subviews
        val upButton = findViewById(R.id.button_up) as Button
        val downButton = findViewById(R.id.button_down) as Button
        val delayedResetButton = findViewById(R.id.button_delayed_reset) as Button
        val valueTextView = findViewById(R.id.textview_value) as TextView

        // Update view using model stream
        modelStream.map { x -> x.value.toString() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(RxTextView.text(valueTextView))

        // Send events to stream
        RxView.clicks(delayedResetButton)
                .subscribe { eventStream.onNext(ClickDelayedReset()) }
        RxView.clicks(upButton)
                .subscribe { eventStream.onNext(ClickUp()) }
        RxView.clicks(downButton)
                .subscribe { eventStream.onNext(ClickDown()) }

        return eventStream
    }
}
