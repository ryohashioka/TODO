package jp.co.ryo.hashioka.todo.ui.todo

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreSettings

class TodoModel(
    private val user : FirebaseUser
) {
    var db = FirebaseFirestore.getInstance()

    init {
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.firestoreSettings = settings
    }

    /**
     * TODOを追加する
     */
    fun add(
        todo : TodoObject.Todo,
        success : (() -> Unit)?,
        error : ((e:Exception) -> Unit)?
    ) {
        // 新しくTodoを追加
        TodoObject.list.add(
            TodoObject.Todo(
                createId(),
                todo.text,
                todo.category,
                todo.timeLimit,
                false,
                Timestamp(Date()),
                Timestamp(Date())
            )
        )

        set(success, error)
    }

    /**
     * TODOを更新する
     */
    fun update(
        todo : TodoObject.Todo,
        success : (() -> Unit)?,
        error : ((e:Exception) -> Unit)?
    ) {
        todo.updateDateTime = Timestamp(Date())
        // 対象のtodoを取得
        val index: Int = TodoObject.list.indexOfFirst {
            todo.id.equals(it.id)
        }
        TodoObject.list[index] = todo

        set(success, error)
    }

    /**
     * Firestore にセットする
     */
    private fun set(
        success : (() -> Unit)?,
        error : ((e:Exception) -> Unit)?
    ) {
        val todoListMap = mapOf(
            FIRESTORE_FIELD to TodoObject.list.map { todoToMap(it) }
        )

        // firestore に追加
        db.collection(FIRESTORE_COLLECTION).document(user.uid)
            .set(todoListMap)
            .addOnSuccessListener {
                Log.d(TAG,"DocumentSnapshot added.")
                success?.invoke()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                error?.invoke(e)
            }
    }

    /**
     * TodoList を返却する。（success の callback で）
     */
    fun getList(
        success: ((data: List<Map<String, Any>>) -> Unit)?,
        error: ((e: Exception) -> Unit)?
    ) {
        db.collection(FIRESTORE_COLLECTION).document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    // データが正常に取得できなかった場合、空配列を返却
                    if(document.data == null) {
                        success?.invoke(listOf())
                        return@addOnSuccessListener
                    }
                    val todoList = document.data!![FIRESTORE_FIELD]
                    if(todoList == null) {
                        success?.invoke(listOf())
                        return@addOnSuccessListener
                    }
                    if(todoList is List<*>) {
                        success?.invoke(todoList as List<Map<String, Any>>)
                        return@addOnSuccessListener
                    } else {
                        success?.invoke(listOf())
                        return@addOnSuccessListener
                    }
                } else {
                    Log.d(TAG, "No such document")
                    // TODO: Firebase の document が取得できなかった時のエラー処理を記述する。
                    error?.invoke(Exception("ドキュメントが存在しません。"))
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                error?.invoke(exception)
            }
    }

    fun getTodoList(
        success: ((data: List<TodoObject.Todo>) -> Unit)?,
        error: ((e: Exception) -> Unit)?
    ) {
        getList({
            success?.invoke(it.map { mapToTodo(it) })
        }, {
            error?.invoke(it)
        })
    }

    private fun mapToTodo(map: Map<String, Any>): TodoObject.Todo {
        return TodoObject.Todo(
            map[KEY_ID] as String?,
            map[KEY_TEXT] as String,
            map[KEY_CATEGORY] as String,
            map[KEY_TIMELIMIT] as Timestamp?,
            map[KEY_IS_COMPLETED] as Boolean,
            map[KEY_CREATE_AT] as Timestamp?,
            map[KEY_UPDATE_AT] as Timestamp?
        )
    }

    private fun todoToMap(todo: TodoObject.Todo): Map<String, Any?> {
        return mapOf(
            KEY_ID to todo.id,
            KEY_TEXT to todo.text,
            KEY_CATEGORY to todo.category,
            KEY_IS_COMPLETED to todo.isCompleted,
            KEY_CREATE_AT to todo.createDateTime,
            KEY_UPDATE_AT to todo.updateDateTime
        )
    }

    /**
     * todoのID を生成する。
     * TODO: 一時的に todoid をランダム uuid で生成している。そのうちほんとのユニークIDに切り替える。
     */
    private fun createId() : String {
        return UUID.randomUUID().toString()
    }

    companion object {
        private const val TAG = "TodoModel"

        private const val FIRESTORE_COLLECTION = "todo"
        private const val FIRESTORE_FIELD = "todolist"
        private const val KEY_ID = "id"
        private const val KEY_TEXT = "text"
        private const val KEY_CATEGORY = "category"
        private const val KEY_TIMELIMIT = "timeLimit"
        private const val KEY_IS_COMPLETED = "isCompleted"
        private const val KEY_CREATE_AT = "createAt"
        private const val KEY_UPDATE_AT = "updateAt"
    }

}