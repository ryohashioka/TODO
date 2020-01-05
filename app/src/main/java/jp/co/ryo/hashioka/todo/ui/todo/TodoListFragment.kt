package jp.co.ryo.hashioka.todo.ui.todo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseUser
import jp.co.ryo.hashioka.todo.R

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [TodoListFragment.OnListFragmentInteractionListener] interface.
 */
class TodoListFragment : Fragment() {

    private var listener: OnTodoListFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todolist_list, container, false)
        val view2 = inflater.inflate(R.layout.fragment_todolist, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = MyTodoListRecyclerViewAdapter(TodoObject.list, listener)
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTodoListFragmentListener) {
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
    interface OnTodoListFragmentListener {
        fun onChangeTodo(item: TodoObject.Todo?)
    }

    companion object {

        private const val TAG = "TodoListFragment"

        @JvmStatic
        fun newInstance() =
            TodoListFragment().apply {
                arguments = Bundle()
            }
    }
}
