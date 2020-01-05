package jp.co.ryo.hashioka.todo.ui.todo

import android.graphics.Color
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView

import kotlinx.android.synthetic.main.fragment_todolist.view.*
import android.graphics.Paint
import jp.co.ryo.hashioka.todo.R


class MyTodoListRecyclerViewAdapter(
    private val mValues: List<TodoObject.Todo>,
    private val mListener: TodoListFragment.OnTodoListFragmentListener?
) : RecyclerView.Adapter<MyTodoListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_todolist, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mContentView.text = item.text

        with(holder.mView) {
            tag = item
        }

        // textview の処理
        with(holder.mContentView) {
            if(item.isCompleted != null && item.isCompleted!!) {
                this.setTextColor(Color.LTGRAY)
                val textPaint = this.paint
                textPaint.flags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                textPaint.isAntiAlias = true
            }
        }

        // checkbox の処理
        with(holder.mCheckBoxView) {
            if(item.isCompleted != null && item.isCompleted!!) {
                this.isChecked = true
            }
            setOnCheckedChangeListener { _, checked ->
                val textView = holder.mContentView
                if (checked) {
                    textView.setTextColor(Color.LTGRAY)
                    val textPaint = textView.paint
                    textPaint.flags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    textPaint.isAntiAlias = true

                    item.isCompleted = true
                } else {
                    textView.setTextColor(Color.BLACK)
                    val textPaint = textView.paint
                    textPaint.flags = 0

                    item.isCompleted = false
                }
                mListener?.onChangeTodo(item)
            }
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mCheckBoxView: CheckBox = mView.checkBox
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }

    companion object {
        private const val TAG = "MyTodoListRecyclerView"
    }
}
