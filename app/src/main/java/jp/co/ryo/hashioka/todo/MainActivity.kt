package jp.co.ryo.hashioka.todo

import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import jp.co.ryo.hashioka.todo.ui.config.AddCategoryFragment
import jp.co.ryo.hashioka.todo.ui.main.SectionsPagerAdapter
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast
import jp.co.ryo.hashioka.todo.ui.todo.*

class MainActivity : AppCompatActivity(),
    TodoListFragment.OnListFragmentInteractionListener,
    AddCategoryFragment.OnFragmentInteractionListener,
    InputTodoFragment.OnAddTodoListener{

    private lateinit var todoModel: TodoModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // LoginActivity から渡されるユーザを取得
        // ユーザが取得できなければ何もしない。
        val user = intent.getParcelableExtra<FirebaseUser>(KEY_USER)
        if(user==null) {
            Toast.makeText(
                this, getText(R.string.error_get_user_failed),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        todoModel = TodoModel(user)

        // TODO入力の生成
        val inputTodoFragment = InputTodoFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.todoInputLayout, inputTodoFragment)
        transaction.commit()

        // タブの生成
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        tabs.setOnClickListener {
            Log.d(TAG, "click tab : ${it.findViewById<TabLayout>(R.id.tabs).selectedTabPosition}")
        }

        // TODO: FloatingActionButton は設定ボタンとして扱いたい。
        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onAddTodo(todo: TodoModel.Todo?) {
        // todoが空なら登録処理を行わない。
        if(todo==null) {
            Toast.makeText(
                this, getText(R.string.msg_todo_is_empty),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // 表示中のタブの todolist に追加
        todoModel.add(todo, {
            Log.d(TAG, "TODO を追加しました。UI の再描画を行います。")
            // TODO: ここに UI 再描画処理を記述する
        }, {
            Toast.makeText(
                this, getText(R.string.error_add_todo_failed),
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    override fun onListFragmentInteraction(item: TodoContent.TodoItem?) {
        Log.d(TAG, "list is clicked!")
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private const val TAG = "MainActivity"

        const val KEY_USER = "user"
    }
}