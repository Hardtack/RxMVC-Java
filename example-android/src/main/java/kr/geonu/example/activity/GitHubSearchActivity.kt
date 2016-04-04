package kr.geonu.example.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kr.geonu.example.R
import kr.geonu.example.github.controller.GitHubSearchController
import kr.geonu.example.github.view.GitHubSearchView
import kr.geonu.mvc.android.AndroidMVC

class GitHubSearchActivity : RxAppCompatActivity(), GitHubSearchController.Delegate {
    override fun openUrl(url: String) {
        // Open URL by starting activity.
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_search)

        // Get a view from content view
        val view = findViewById(R.id.layout_content) as GitHubSearchView
        // Make a controller
        val controller = GitHubSearchController(this)
        // Start a MVC chain
        AndroidMVC.combine(view, controller, this)
    }
}
