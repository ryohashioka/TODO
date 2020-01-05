package jp.co.ryo.hashioka.todo.ui.todo

import com.google.firebase.Timestamp

object TodoObject {

    data class Todo(
        val id : String?,
        val text : String,
        val category : String,
        val timeLimit : Timestamp?,
        var isCompleted : Boolean?,
        val createDateTime : Timestamp?,
        var updateDateTime : Timestamp?
    )

    var list: ArrayList<Todo> = arrayListOf()
}