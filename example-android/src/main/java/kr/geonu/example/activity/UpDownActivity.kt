package kr.geonu.example.activity

import android.os.Bundle
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kr.geonu.example.R
import kr.geonu.example.updown.controller.UpDownController
import kr.geonu.example.updown.view.UpDownView
import kr.geonu.mvc.android.AndroidMVC

class UpDownActivity : RxAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updown)

        // Get a view from content view
        val view = findViewById(R.id.layout_content) as UpDownView
        // Make a controller
        val controller = UpDownController()
        // Start a MVC chain
        AndroidMVC.combine(view, controller, this)
    }
}
