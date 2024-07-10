package com.example.vxsound.activity

import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.example.vxsound.R
import com.example.vxsound.constant.Constant
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.ActivitySignInBinding
import com.example.vxsound.model.User
import com.example.vxsound.prefs.DataStoreManager
import com.example.vxsound.utils.StringUtil

class SignInActivity : BaseActivity() {

    private var mActivitySignInBinding: ActivitySignInBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivitySignInBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(mActivitySignInBinding?.root)
        mActivitySignInBinding?.rdbUser?.isChecked = true
        mActivitySignInBinding?.layoutSignUp?.setOnClickListener {
            GlobalFunction.startActivity(
                this@SignInActivity,
                SignUpActivity::class.java
            )
        }
        mActivitySignInBinding?.btnSignIn?.setOnClickListener { onClickValidateSignIn() }
        mActivitySignInBinding?.tvForgotPassword?.setOnClickListener { onClickForgotPassword() }
    }

    private fun onClickForgotPassword() {
        GlobalFunction.startActivity(this, ForgotPasswordActivity::class.java)
    }

    private fun onClickValidateSignIn() {
        val strEmail = mActivitySignInBinding?.edtEmail?.text.toString().trim { it <= ' ' }
        val strPassword = mActivitySignInBinding?.edtPassword?.text.toString().trim { it <= ' ' }
        if (StringUtil.isEmpty(strEmail)) {
            Toast.makeText(
                this@SignInActivity,
                getString(R.string.msg_email_require),
                Toast.LENGTH_SHORT
            ).show()
        } else if (StringUtil.isEmpty(strPassword)) {
            Toast.makeText(
                this@SignInActivity,
                getString(R.string.msg_password_require),
                Toast.LENGTH_SHORT
            ).show()
        } else if (!StringUtil.isValidEmail(strEmail)) {
            Toast.makeText(
                this@SignInActivity,
                getString(R.string.msg_email_invalid),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (mActivitySignInBinding?.rdbAdmin?.isChecked == true) {
                if (!strEmail.contains(Constant.ADMIN_EMAIL_FORMAT)) {
                    Toast.makeText(
                        this@SignInActivity,
                        getString(R.string.msg_email_invalid_admin),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    signInUser(strEmail, strPassword)
                }
                return
            }
            if (strEmail.contains(Constant.ADMIN_EMAIL_FORMAT)) {
                Toast.makeText(
                    this@SignInActivity,
                    getString(R.string.msg_email_invalid_user),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                signInUser(strEmail, strPassword)
            }
        }
    }

    private fun signInUser(email: String, password: String) {
        showProgressDialog(true)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                showProgressDialog(false)
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        val userObject = User(user.email, password)
                        if (user.email != null && user.email!!.contains(Constant.ADMIN_EMAIL_FORMAT)) {
                            userObject.isAdmin = true
                        }
                        DataStoreManager.user = userObject
                        if (DataStoreManager.user?.isAdmin == true) {
                            GlobalFunction.startActivity(
                                this@SignInActivity,
                                AdminMainActivity::class.java
                            )
                        } else {
                            GlobalFunction.startActivity(
                                this@SignInActivity,
                                MainActivity::class.java
                            )
                        }
                        finishAffinity()
                    }
                } else {
                    Toast.makeText(
                        this@SignInActivity, getString(R.string.msg_sign_in_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}