package com.example.vxsound.activity

import android.os.Bundle
import android.view.View
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.example.vxsound.MyApplication
import com.example.vxsound.R
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.ActivityFeedbackBinding
import com.example.vxsound.model.Feedback
import com.example.vxsound.prefs.DataStoreManager
import com.example.vxsound.utils.StringUtil

class FeedbackActivity : BaseActivity() {
    private var mActivityFeedbackBinding: ActivityFeedbackBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityFeedbackBinding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(mActivityFeedbackBinding?.root)
        setupToolbar()
        initListener()
    }

    private fun setupToolbar() {
        mActivityFeedbackBinding?.header?.imgBack?.visibility = View.VISIBLE
        mActivityFeedbackBinding?.header?.tvTitle?.text = getString(R.string.menu_feedback)
        mActivityFeedbackBinding?.header?.imgBack?.setOnClickListener { onBackPressed() }
    }

    private fun initListener() {
        mActivityFeedbackBinding?.edtEmail?.setText(DataStoreManager.user?.email)
        mActivityFeedbackBinding?.tvSendFeedback?.setOnClickListener { onClickSendFeedback() }
    }

    private fun onClickSendFeedback() {
        val strName = mActivityFeedbackBinding?.edtName?.text.toString()
        val strPhone = mActivityFeedbackBinding?.edtPhone?.text.toString()
        val strEmail = mActivityFeedbackBinding?.edtEmail?.text.toString()
        val strComment = mActivityFeedbackBinding?.edtComment?.text.toString()
        if (StringUtil.isEmpty(strName)) {
            GlobalFunction.showToastMessage(this, getString(R.string.name_require))
        } else if (StringUtil.isEmpty(strComment)) {
            GlobalFunction.showToastMessage(this, getString(R.string.comment_require))
        } else {
            showProgressDialog(true)
            val feedback = Feedback(strName, strPhone, strEmail, strComment)
            MyApplication.Companion[this].feedbackDatabaseReference()
                    ?.child(System.currentTimeMillis().toString())
                    ?.setValue(feedback) { _: DatabaseError?, _: DatabaseReference? ->
                        showProgressDialog(false)
                        sendFeedbackSuccess()
                    }
        }
    }

    private fun sendFeedbackSuccess() {
        GlobalFunction.hideSoftKeyboard(this)
        GlobalFunction.showToastMessage(this, getString(R.string.msg_send_feedback_success))
        mActivityFeedbackBinding?.edtName?.setText("")
        mActivityFeedbackBinding?.edtPhone?.setText("")
        mActivityFeedbackBinding?.edtComment?.setText("")
    }
}