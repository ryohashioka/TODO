package jp.co.ryo.hashioka.todo.ui.config

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.util.*

/**
 * カテゴリの操作機能
 * TODO: カテゴリの削除機能を追加
 * TODO: カテゴリの移動機能を追加
 */
class CategoryModel (
    private val user: FirebaseUser
) {
    var categoryList: ArrayList<Category>? = null

    var db = FirebaseFirestore.getInstance()

    init {
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.firestoreSettings = settings
    }

    data class Category(
        val category : String,
        val createDateTime : Timestamp?,
        val updateDateTime : Timestamp?
    )

    /**
     * カテゴリを追加する
     */
    fun add(
        category : Category,
        success : (() -> Unit)?,
        error : ((e:Exception) -> Unit)?
    ) {
        categoryList?.add(category)
        val categoryListMap = mapOf(
            FIRESTORE_FIELD to categoryList?.map { categoryTomap(it) }
        )

        // firestore に追加
        // 一旦複数端末同時接続等は考えない。
        // ローカルで追加したものを淡々と追加するだけ。
        db.collection(FIRESTORE_COLLECTION).document(user.uid)
            .set(categoryListMap)
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
    }

    /**
     * CategoryList を Firebase から取得して返却する。
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
                    val categoryList = document.data!![FIRESTORE_FIELD]
                    if(categoryList == null) {
                        success?.invoke(listOf())
                        return@addOnSuccessListener
                    }
                    if(categoryList is List<*>) {
                        success?.invoke(categoryList as List<Map<String, Any>>)
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

    /**
     * カテゴリ一覧を返却
     */
    fun getCategoryList(
        success: ((data: List<Category>) -> Unit)?,
        error: ((e: Exception) -> Unit)?
    ) {
        getList({
            val list = it.map { mapToCategory(it) }
            categoryList = ArrayList(list)
            success?.invoke(list)
        }, {
            error?.invoke(it)
        })
    }

    /**
     * convert [Category] to [Map]
     */
    private fun categoryTomap(category: Category): Map<String, Any> {
        return mapOf(
            KEY_CATEGORY to category.category,
            KEY_CREATE_AT to Date(),
            KEY_UPDATE_AT to Date()
        )
    }

    /**
     * convert [Map] to [Category]
     */
    private fun mapToCategory(map: Map<String, Any>): Category {
        return Category(
            map[KEY_CATEGORY] as String,
            map[KEY_CREATE_AT] as Timestamp?,
            map[KEY_UPDATE_AT] as Timestamp?
        )
    }

    companion object {
        private const val TAG = "CategoryModel"

        private const val FIRESTORE_COLLECTION = "category"
        private const val FIRESTORE_FIELD = "categorylist"
        private const val KEY_CATEGORY = "category"
        private const val KEY_CREATE_AT = "createAt"
        private const val KEY_UPDATE_AT = "updateAt"
    }

}