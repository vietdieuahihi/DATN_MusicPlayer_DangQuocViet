package com.example.vxsound.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vxsound.R
import com.example.vxsound.adapter.ContactAdapter
import com.example.vxsound.constant.AboutUsConfig
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.ActivityContactBinding
import com.example.vxsound.model.Contact

class ContactActivity : BaseActivity() {
    private var mActivityContactBinding: ActivityContactBinding? = null
    private var mContactAdapter: ContactAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityContactBinding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(mActivityContactBinding?.root)
        setupToolbar()
        initUi()
        initListener()
    }

    private fun setupToolbar() {
        mActivityContactBinding?.header?.imgBack?.visibility = View.VISIBLE
        mActivityContactBinding?.header?.tvTitle?.text = getString(R.string.menu_contact)
        mActivityContactBinding?.header?.imgBack?.setOnClickListener { onBackPressed() }
    }

    private fun initUi() {
        mActivityContactBinding?.tvAboutUsTitle?.text = AboutUsConfig.ABOUT_US_TITLE
        mActivityContactBinding?.tvAboutUsContent?.text = AboutUsConfig.ABOUT_US_CONTENT
        mActivityContactBinding?.tvAboutUsWebsite?.text = AboutUsConfig.ABOUT_US_WEBSITE_TITLE
        mContactAdapter = ContactAdapter(this, listContact(), object : ContactAdapter.ICallPhone {
            override fun onClickCallPhone() {
                GlobalFunction.callPhoneNumber(this@ContactActivity)
            }
        })
        val layoutManager = GridLayoutManager(this, 3)
        mActivityContactBinding?.rcvData?.isNestedScrollingEnabled = false
        mActivityContactBinding?.rcvData?.isFocusable = false
        mActivityContactBinding?.rcvData?.layoutManager = layoutManager
        mActivityContactBinding?.rcvData?.adapter = mContactAdapter
    }

    private fun initListener() {
        mActivityContactBinding?.layoutWebsite?.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(AboutUsConfig.WEBSITE)
                )
            )
        }
    }

    private fun listContact(): List<Contact> {
        val contactArrayList: MutableList<Contact> = ArrayList()
        contactArrayList.add(Contact(Contact.FACEBOOK, R.drawable.ic_facebook))
        contactArrayList.add(Contact(Contact.HOTLINE, R.drawable.ic_hotline))
        contactArrayList.add(Contact(Contact.GMAIL, R.drawable.ic_gmail))
//        contactArrayList.add(Contact(Contact.SKYPE, R.drawable.ic_skype))
//        contactArrayList.add(Contact(Contact.YOUTUBE, R.drawable.ic_youtube))
//        contactArrayList.add(Contact(Contact.ZALO, R.drawable.ic_zalo))
        return contactArrayList
    }

    public override fun onDestroy() {
        super.onDestroy()
        mContactAdapter!!.release()
    }
}