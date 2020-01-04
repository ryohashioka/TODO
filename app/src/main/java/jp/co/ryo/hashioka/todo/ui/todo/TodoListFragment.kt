package jp.co.ryo.hashioka.todo.ui.todo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseUser
import jp.co.ryo.hashioka.todo.R

import jp.co.ryo.hashioka.todo.ui.todo.dummy.DummyContent
import jp.co.ryo.hashioka.todo.ui.todo.dummy.DummyContent.DummyItem

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [TodoListFragment.OnListFragmentInteractionListener] interface.
 */
class TodoListFragment : Fragment() {

    private var user: FirebaseUser? = null
    private var todoModel: TodoModel? = null

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = it.getParcelable(ARG_USER)
        }
        if(user==null) {
            Log.w(TAG, "ユーザが指定されていません。")
            return
        }
        todoModel = TodoModel(user!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todolist_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
//                when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
                todoModel?.getTodoList({
                    adapter = MyTodoListRecyclerViewAdapter(it, listener)
                }, {
                    Log.w(TAG, "TodoList の取得に失敗しました。")
                })
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: TodoModel.Todo?)
    }

    companion object {

        private const val TAG = "TodoListFragment"

        private const val ARG_USER = "user"

        @JvmStatic
        fun newInstance(user: FirebaseUser) =
            TodoListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER, user)
                }
            }
    }
}
