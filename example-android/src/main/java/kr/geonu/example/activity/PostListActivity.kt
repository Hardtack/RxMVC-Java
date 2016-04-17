package kr.geonu.example.activity

import android.os.Bundle
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kr.geonu.example.R
import kr.geonu.example.blog.controller.PostListController
import kr.geonu.example.blog.util.PostVC
import kr.geonu.example.blog.view.PostListView
import kr.geonu.mvc.android.AndroidMVC

/**
 * An activity representing a list of Posts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [PostDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class PostListActivity : RxAppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var mTwoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (postView, postController) = PostVC.retain()

        setContentView(R.layout.activity_post_list)

        val view = findViewById(R.id.layout_content) as PostListView
        val controller = PostListController()

        AndroidMVC.combine(view, controller, this)

        if (findViewById(R.id.post_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true
        }
    }

    override fun onDestroy() {
        PostVC.release()
        super.onDestroy()
    }
}
