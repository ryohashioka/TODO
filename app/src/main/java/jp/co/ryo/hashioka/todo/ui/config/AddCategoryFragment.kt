package jp.co.ryo.hashioka.todo.ui.config

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
import com.google.firebase.auth.FirebaseUser

import jp.co.ryo.hashioka.todo.R

/**
 * カテゴリを追加するフラグメントクラス
 */
class AddCategoryFragment : Fragment(), View.OnClickListener  {

    private var user: FirebaseUser? = null
    private var categoryModel: CategoryModel? = null

    private var listener: OnAddCategoryFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable(ARG_USER)
        }
        if(user==null) {
            Log.w(TAG, "ユーザが指定されていません。")
            return
        }
        categoryModel = CategoryModel(user!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_category, container, false)
        // イベントのセット
        view.findViewById<Button>(R.id.addCategoryButton).setOnClickListener(this)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // mainActivity に Listener が登録されていなければエラー
        if (context is OnAddCategoryFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onClick(view: View?) {
        if(view==null) return

        when (view.id) {
            R.id.addCategoryButton -> {
                // TODO追加ボタンクリック処理
                // 入力した値を取得して返却
                val text = getCategoryEditText()
                if(text == "") {
                    Log.i(TAG, "カテゴリが入力されていません。")
                    listener?.onAddedCategory(null)
                } else {
                    // Firestore にカテゴリを追加
                    val category = CategoryModel.Category(
                        getCategoryEditText(),
                        null,
                        null
                    )
                    categoryModel?.add(
                        category, {
                            /* do nothing... */
                        }, {
                            TODO("エラー処理を追加")
                        }
                    )
                    // Firestore への登録に失敗してもローカルにデータは残ってるはずなので callback 呼ぶ
                    listener?.onAddedCategory(category)
                }
            }
        }
    }

    /**
     * カテゴリ入力テキストを取得
     */
    private fun getCategoryEditText(): String {
        return if(view==null) {
            ""
        } else {
            view!!.findViewById<EditText>(R.id.categoryEditText).text.toString()
        }
    }

    interface OnAddCategoryFragmentListener {
        fun onAddedCategory(category: CategoryModel.Category?)
    }
    companion object {

        private const val TAG = "AddCategoryFragment"

        private const val ARG_USER = "user"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param user FirebaseUser.
         * @return A new instance of fragment AddCategoryFragment.
         */
        @JvmStatic
        fun newInstance(user: FirebaseUser) =
            AddCategoryFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER, user)
                }
            }
    }
}
