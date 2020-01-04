package jp.co.ryo.hashioka.todo.ui.todo

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import jp.co.ryo.hashioka.todo.R

/**
 * TODOを追加するフラグメント
 */
class InputTodoFragment : Fragment(), View.OnClickListener {

    private var listener: OnAddTodoListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_input_todo, container, false)

        // イベントのセット
        view.findViewById<Button>(R.id.addButton).setOnClickListener(this)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAddTodoListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnAddTodoListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * UI のクリックイベント
     */
    override fun onClick(view: View?) {
        if(view==null) return

        when (view.id) {
            R.id.addButton -> {
                // TODO追加ボタンクリック処理
                // 入力した値を取得して返却
                val text = getTodoEditText()
                if(text == "") {
                    Log.i(TAG, "TODO が入力されていません。")
                    listener?.onAddTodo(null)
                } else {
                    listener?.onAddTodo(
                        TodoModel.Todo(
                            null,
                            getTodoEditText(),
                            "",
                            null,
                            null,
                            null,
                            null
                        ))
                }
            }
        }
    }

    /**
     * TODO入力テキストを取得
     */
    private fun getTodoEditText(): String {
        return if(view==null) {
            ""
        } else {
            view!!.findViewById<EditText>(R.id.todoEditText).text.toString()
        }
    }

    interface OnAddTodoListener {
        fun onAddTodo(todo: TodoModel.Todo?)
    }

    companion object {

        private const val TAG = "InputTodoFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment InputTodoFragment.
         */
        @JvmStatic
        fun newInstance() =
            InputTodoFragment().apply {
                arguments = Bundle()
            }
    }
}
