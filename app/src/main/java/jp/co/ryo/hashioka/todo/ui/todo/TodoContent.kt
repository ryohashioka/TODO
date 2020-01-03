package jp.co.ryo.hashioka.todo.ui.todo

import java.util.ArrayList
import java.util.HashMap

object TodoContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<TodoItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, TodoItem> = HashMap()

    private val COUNT = 25

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createDummyItem(i))
        }
    }

    private fun addItem(item: TodoItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createDummyItem(position: Int): TodoItem {
        return TodoItem(position.toString(), "Item " + position)
    }

    /**
     * TodoList に表示するアイテム
     */
    data class TodoItem(val id: String, val text: String) {
        override fun toString(): String = text
    }
}