package jp.co.ryo.hashioka.todo.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import jp.co.ryo.hashioka.todo.R
import jp.co.ryo.hashioka.todo.data.LoginRepository
import java.lang.Exception

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var user : FirebaseUser? = mAuth.currentUser

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    /**
     * ログイン済なら true
     * 未ログインなら false
     */
    fun isLoggedIn(): Boolean {
        return user != null
    }

    fun signIn(
        email: String,
        password: String,
        success: ((user : FirebaseUser) -> Unit)?,
        error: ((e : Exception) -> Unit)?
    ) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // ログイン成功
                    Log.d(TAG, "signInWithEmail:success")
                    user = mAuth.currentUser
                    // ユーザが正常に取得できていなければ登録処理を行う
                    if(user == null) {
                        signUp(email, password, success, error)
                        return@addOnCompleteListener
                    }
                    success?.invoke(user!!)
                } else {
                    // ログイン失敗でサインアップする。
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    signUp(email, password, success, error)
                }
            }
    }

    fun signUp(
        email: String,
        password: String,
        success: ((user : FirebaseUser) -> Unit)?,
        error: ((e : Exception) -> Unit)?
    ) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 登録成功
                    Log.d(TAG, "createUserWithEmail:success")
                    user = mAuth.currentUser
                    // ユーザが正常に取得できていなければ登録処理を行う
                    if(user == null) {
                        // 何らかのエラーが発生（基本あり得ない）
                        error?.invoke(Exception())
                        return@addOnCompleteListener
                    }
                    success?.invoke(user!!)
                } else {
                    // 登録失敗
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    error?.invoke(task.exception!!)
                }
            }
    }

    fun signInGoogle(
        success: ((user : FirebaseUser) -> Unit)?,
        error: ((e : Exception) -> Unit)?
    ) {
        // TODO: ここに Google でログインする処理を追加
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}
