package com.example.vxsound.activity

import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.example.vxsound.R
import com.example.vxsound.databinding.ActivityChangePasswordBinding
import com.example.vxsound.model.User
import com.example.vxsound.prefs.DataStoreManager
import com.example.vxsound.utils.StringUtil

class ChangePasswordActivity : BaseActivity() {
    private var mActivityChangePasswordBinding: ActivityChangePasswordBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityChangePasswordBinding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(mActivityChangePasswordBinding?.root)
        mActivityChangePasswordBinding?.imgBack?.setOnClickListener { onBackPressed() }
        mActivityChangePasswordBinding?.btnChangePassword?.setOnClickListener { onClickValidateChangePassword() }
    }

    private fun onClickValidateChangePassword() {
        val strOldPassword =
            mActivityChangePasswordBinding?.edtOldPassword?.text.toString().trim { it <= ' ' }
        val strNewPassword =
            mActivityChangePasswordBinding?.edtNewPassword?.text.toString().trim { it <= ' ' }
        val strConfirmPassword =
            mActivityChangePasswordBinding?.edtConfirmPassword?.text.toString().trim { it <= ' ' }
        if (StringUtil.isEmpty(strOldPassword)) {
            Toast.makeText(
                this@ChangePasswordActivity,
                getString(R.string.msg_old_password_require),
                Toast.LENGTH_SHORT
            ).show()
        } else if (StringUtil.isEmpty(strNewPassword)) {
            Toast.makeText(
                this@ChangePasswordActivity,
                getString(R.string.msg_new_password_require),
                Toast.LENGTH_SHORT
            ).show()
        } else if (StringUtil.isEmpty(strConfirmPassword)) {
            Toast.makeText(
                this@ChangePasswordActivity,
                getString(R.string.msg_confirm_password_require),
                Toast.LENGTH_SHORT
            ).show()
        } else if (DataStoreManager.user?.password != strOldPassword) {
            Toast.makeText(
                this@ChangePasswordActivity,
                getString(R.string.msg_old_password_invalid),
                Toast.LENGTH_SHORT
            ).show()
        } else if (strNewPassword != strConfirmPassword) {
            Toast.makeText(
                this@ChangePasswordActivity,
                getString(R.string.msg_confirm_password_invalid),
                Toast.LENGTH_SHORT
            ).show()
        } else if (strOldPassword == strNewPassword) {
            Toast.makeText(
                this@ChangePasswordActivity,
                getString(R.string.msg_new_password_invalid),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            changePassword(strNewPassword)
        }
    }

    private fun changePassword(newPassword: String) {
        showProgressDialog(true)
        val user = FirebaseAuth.getInstance().currentUser ?: return
        user.updatePassword(newPassword)
            .addOnCompleteListener { task: Task<Void?> ->
                showProgressDialog(false)
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@ChangePasswordActivity,
                        getString(R.string.msg_change_password_successfully), Toast.LENGTH_SHORT
                    ).show()
                    val userLogin: User? = DataStoreManager.user
                    userLogin?.password = newPassword
                    DataStoreManager.user = userLogin
                    mActivityChangePasswordBinding?.edtOldPassword?.setText("")
                    mActivityChangePasswordBinding?.edtNewPassword?.setText("")
                    mActivityChangePasswordBinding?.edtConfirmPassword?.setText("")
                }
            }
    }
}