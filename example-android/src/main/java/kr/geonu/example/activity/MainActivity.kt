package kr.geonu.example.activity

import android.os.Bundle
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kr.geonu.example.R
import kr.geonu.example.main.controller.MainController
import kr.geonu.example.main.view.MainView
import kr.geonu.mvc.android.AndroidMVC

class MainActivity : RxAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get a view from content view
        val view = findViewById(R.id.layout_content) as MainView
        // Make a controller
        val controller = MainController(this)
        // Start a MVC chain
        AndroidMVC.combine(view, controller, this)
    }
}
