package com.example.vxsound.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.vxsound.MyApplication
import com.example.vxsound.adapter.AdminFeedbackAdapter
import com.example.vxsound.databinding.FragmentAdminFeedbackBinding
import com.example.vxsound.model.Feedback

class AdminFeedbackFragment : Fragment() {
    private var mFragmentAdminFeedbackBinding: FragmentAdminFeedbackBinding? = null
    private var mListFeedback: MutableList<Feedback>? = null
    private var mFeedbackAdapter: AdminFeedbackAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragmentAdminFeedbackBinding =
            FragmentAdminFeedbackBinding.inflate(inflater, container, false)
        initView()
        loadListFeedback()
        return mFragmentAdminFeedbackBinding?.root
    }

    private fun initView() {
        if (activity == null) return
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentAdminFeedbackBinding?.rcvFeedback?.layoutManager = linearLayoutManager
    }

    private fun loadListFeedback() {
        if (activity == null) return
        MyApplication[activity!!].feedbackDatabaseReference()
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (mListFeedback != null) {
                        mListFeedback!!.clear()
                    } else {
                        mListFeedback = ArrayList()
                    }
                    for (dataSnapshot in snapshot.children) {
                        val feedback = dataSnapshot.getValue(Feedback::class.java)
                        if (feedback != null) {
                            mListFeedback!!.add(0, feedback)
                        }
                    }
                    mFeedbackAdapter = AdminFeedbackAdapter(mListFeedback)
                    mFragmentAdminFeedbackBinding?.rcvFeedback?.adapter = mFeedbackAdapter
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}