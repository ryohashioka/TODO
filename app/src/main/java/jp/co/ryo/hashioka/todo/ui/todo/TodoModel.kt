package jp.co.ryo.hashioka.todo.ui.todo

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot


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

    data class Todo(
        val id : String?,
        val text : String,
        val category : String,
        val timeLimit : Date?,
        val isCompleted : Boolean?,
        val createDateTime : Date?,
        val updateDateTime : Date?
        )

    /**
     * TODOを追加する
     */
    fun add(
        todo : Todo,
        success : (() -> Unit)?,
        error : ((e:Exception) -> Unit)?
    ) {
        // Map を生成
        val todoMap = hashMapOf(
            KEY_ID to createId(),
            KEY_TEXT to todo.text,
            KEY_CATEGORY to todo.category,
            KEY_IS_COMPLETED to false,
            KEY_CREATE_AT to Date(),
            KEY_UPDATE_AT to Date()
        )
        if(todo.timeLimit!=null) {
            todoMap[KEY_TIMELIMIT] = todo.timeLimit
        }

        // todolist を取得
        getList({
            val todoList = ArrayList(it)
            todoList.add(todoMap)
            val todoListMap = mapOf(
                FIRESTORE_FIELD to todoList
            )

            // firestore に追加
            db.collection(FIRESTORE_COLLECTION).document(user.uid)
                .set(todoListMap)
                .addOnSuccessListener {
                    Log.d(
                        TAG,
                        "DocumentSnapshot added."
                    )
                    success?.invoke()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    error?.invoke(e)
                }
        },{
            // TODO: エラー処理を記述
        })
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
                    }
                    val todoList = document.data!![FIRESTORE_FIELD]
                    if(todoList == null) {
                        success?.invoke(listOf())
                    }
                    if(todoList is List<*>) {
                        success?.invoke(todoList as List<Map<String, Any>>)
                    } else {
                        success?.invoke(listOf())
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