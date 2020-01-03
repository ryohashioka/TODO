package jp.co.ryo.hashioka.todo

import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.get
import jp.co.ryo.hashioka.todo.ui.config.AddCategoryFragment
import jp.co.ryo.hashioka.todo.ui.main.SectionsPagerAdapter
import jp.co.ryo.hashioka.todo.ui.todo.InputTodoFragment
import jp.co.ryo.hashioka.todo.ui.todo.TodoContent
import jp.co.ryo.hashioka.todo.ui.todo.TodoEntity
import jp.co.ryo.hashioka.todo.ui.todo.TodoListFragment
import jp.co.ryo.hashioka.todo.ui.todo.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_main.view.*
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.firebase.auth.FirebaseUser
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T

class MainActivity : AppCompatActivity(),
    TodoListFragment.OnListFragmentInteractionListener,
    AddCategoryFragment.OnFragmentInteractionListener,
    InputTodoFragment.OnAddTodoListener{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    override fun onAddTodo(todo: TodoEntity?) {
        if(todo==null) {
            Snackbar.make(findViewById(R.id.view_pager), "TODO を入力してください。", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            return
        }
        Log.d(TAG, "TODO を追加！「${todo.text}」")

        // 表示中のタブの todolist に追加
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs[tabs.selectedTabPosition]
    }

    override fun onListFragmentInteraction(item: TodoContent.TodoItem?) {
        Log.d(TAG, "list is clicked!")
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}