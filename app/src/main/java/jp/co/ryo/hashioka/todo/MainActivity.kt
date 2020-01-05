package jp.co.ryo.hashioka.todo

import android.content.Context
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
import jp.co.ryo.hashioka.todo.ui.config.CategoryModel
import jp.co.ryo.hashioka.todo.ui.todo.*
import android.view.inputmethod.InputMethodManager
import androidx.coordinatorlayout.widget.CoordinatorLayout

class MainActivity : AppCompatActivity(),
    TodoListFragment.OnTodoListFragmentListener,
    AddCategoryFragment.OnAddCategoryFragmentListener,
    InputTodoFragment.OnAddTodoListener{

    // キーボード表示を制御するためのオブジェクト
    private lateinit var inputMethodManager: InputMethodManager
    // 背景のレイアウト
    private lateinit var mainLayout: CoordinatorLayout

    private lateinit var categoryModel: CategoryModel
    private lateinit var todoModel: TodoModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mainLayout = findViewById(R.id.main_layout)

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
        categoryModel = CategoryModel(user)
        todoModel = TodoModel(user)

        // TODO入力の生成
        val inputTodoFragment = InputTodoFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.todoInputLayout, inputTodoFragment)
        transaction.commit()

        // TODOの取得
        todoModel.getTodoList({
            TodoObject.list = ArrayList(it)

            // カテゴリの取得
            categoryModel.getCategoryList({

                // タブの生成
                val sectionsPagerAdapter = SectionsPagerAdapter(this, user, it, supportFragmentManager)
                val viewPager: ViewPager = findViewById(R.id.view_pager)
                viewPager.adapter = sectionsPagerAdapter
                val tabs: TabLayout = findViewById(R.id.tabs)
                tabs.setupWithViewPager(viewPager)

                tabs.setOnClickListener {
                    Log.d(TAG, "click tab : ${it.findViewById<TabLayout>(R.id.tabs).selectedTabPosition}")
                }
            }, {
                // TODO: カテゴリ取得失敗時のエラー処理を記述
            })

        }, {
            // TODO: todolist 取得失敗時のエラー処理を記述
        })

        // TODO: FloatingActionButton は設定ボタンとして扱いたい。
        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "未実装です", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onAddTodo(todo: TodoObject.Todo?) {
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
            Log.i(TAG, "Firestore にデータを追加しました。")
        }, {
            Toast.makeText(
                this, getText(R.string.error_add_todo_failed),
                Toast.LENGTH_SHORT
            ).show()
        })

        // 追加処理完了後、キーボードを隠す
        inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(),
            InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun onChangeTodo(item: TodoObject.Todo?) {
        Log.d(TAG, "list is clicked!")
        if(item!=null) {
            todoModel.update(
                item,{
                    Log.i(TAG, "Firestore を更新しました。")
                }, {
                    Toast.makeText(
                        this, getText(R.string.error_set_todo_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                })
        }
    }

    override fun onAddedCategory(category: CategoryModel.Category?) {
//        TODO("カテゴリ追加のため UI を更新する")
    }

    companion object {
        private const val TAG = "MainActivity"

        const val KEY_USER = "user"
    }
}