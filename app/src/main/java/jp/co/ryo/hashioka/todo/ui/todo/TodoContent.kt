package jp.co.ryo.hashioka.todo.ui.todo

import java.util.ArrayList
import java.util.HashMap

object TodoContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<TodoModel.Todo> = ArrayList()

    private val COUNT = 25

    init {
        // Add some sample items.
//        for (i in 1..COUNT) {
//            addItem(createDummyItem(i))
//        }
    }

    private fun addItem(item: TodoModel.Todo) {
        ITEMS.add(item)
    }

    /**
     * TodoList に表示するアイテム
     */
    data class TodoItem(val id: String, val text: String) {
        override fun toString(): String = text
    }
}