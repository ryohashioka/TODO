package jp.co.ryo.hashioka.todo.ui.main

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.firebase.auth.FirebaseUser
import jp.co.ryo.hashioka.todo.ui.config.AddCategoryFragment
import jp.co.ryo.hashioka.todo.ui.todo.TodoListFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(
    private val context: Context,
    private val user: FirebaseUser,
    fm: FragmentManager
) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val testCategoryList = listOf("Category1", "Category2", "Category3")
    var tabItems: ArrayList<String> = ArrayList()

    init {
        // "すべて" と "+" は必ず追加
        tabItems.add("すべて")

        // TODO: どこかから現在のタブ一覧をすべて取得する
        tabItems.addAll(testCategoryList)

        tabItems.add("＋")
    }

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Log.d(TAG, "item position : $position")

        // 最後以外は todolist 画面を表示
        // 最後はカテゴリ追加画面を表示
        return if(position == count-1) {
            AddCategoryFragment.newInstance("param1", "param2")
        } else {
            TodoListFragment.newInstance(user)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabItems[position]
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return tabItems.size
    }

    companion object {
        private const val TAG = "SectionPagerAdapter"
    }
}