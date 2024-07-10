package com.example.vxsound.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.vxsound.MyApplication
import com.example.vxsound.R
import com.example.vxsound.activity.PlayMusicActivity
import com.example.vxsound.adapter.SongAdapter
import com.example.vxsound.constant.Constant
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.FragmentSearchBinding
import com.example.vxsound.listener.IOnClickSongItemListener
import com.example.vxsound.model.Song
import com.example.vxsound.service.MusicService
import com.example.vxsound.utils.GlideUtils
import com.example.vxsound.utils.StringUtil
import java.util.Locale

class SearchFragment : Fragment() {
    private var mFragmentSearchBinding: FragmentSearchBinding? = null
    private var mListSong: MutableList<Song>? = null
    private var mSongAdapter: SongAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false)
        initUi()
        initListener()
        loadListSongFromFirebase("")
        return mFragmentSearchBinding?.root
    }

    private fun initUi() {
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentSearchBinding?.rcvData?.layoutManager = linearLayoutManager
        mListSong = ArrayList()
        mSongAdapter = SongAdapter(mListSong, object : IOnClickSongItemListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }

            override fun onClickFavoriteSong(song: Song, favorite: Boolean) {
                GlobalFunction.onClickFavoriteSong(activity, song, favorite)
            }
        })
        mFragmentSearchBinding?.rcvData?.adapter = mSongAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadListSongFromFirebase(key: String) {
        if (activity == null) return
        MyApplication[activity].songsDatabaseReference()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                resetListData()
                for (dataSnapshot in snapshot.children) {
                    val song = dataSnapshot.getValue(Song::class.java) ?: return
                    if (StringUtil.isEmpty(key)) {
                        mListSong!!.add(0, song)
                    } else {
                        if (GlobalFunction.getTextSearch(song.title).toLowerCase(Locale.getDefault()).trim { it <= ' ' }
                                        .contains(GlobalFunction.getTextSearch(key).toLowerCase(Locale.getDefault()).trim { it <= ' ' })) {
                            mListSong!!.add(0, song)
                        }
                    }
                }
                val isHasData = mListSong != null && mListSong!!.size > 1
                displayLayoutPlayAll(isHasData)
                if (mSongAdapter != null) mSongAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                GlobalFunction.showToastMessage(activity, getString(R.string.msg_get_date_error))
            }
        })
    }

    private fun resetListData() {
        if (mListSong == null) {
            mListSong = ArrayList()
        } else {
            mListSong!!.clear()
        }
    }

    private fun goToSongDetail(song: Song) {
        MusicService.clearListSongPlaying()
        MusicService.mListSongPlaying!!.add(song)
        MusicService.isPlaying = false
        GlobalFunction.startMusicService(activity, Constant.PLAY, 0)
        GlobalFunction.startActivity(activity, PlayMusicActivity::class.java)
    }

    private fun initListener() {
        mFragmentSearchBinding?.edtSearchName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable) {
                val strKey = s.toString().trim { it <= ' ' }
                if (strKey == "" || strKey.isEmpty()) {
                    if (mListSong != null) mListSong!!.clear()
                    loadListSongFromFirebase("")
                }
            }
        })
        mFragmentSearchBinding?.imgSearch?.setOnClickListener { searchSong() }
        mFragmentSearchBinding?.edtSearchName?.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchSong()
                return@setOnEditorActionListener true
            }
            false
        }
        mFragmentSearchBinding?.layoutPlayAll?.setOnClickListener {
            if (mListSong == null || mListSong!!.isEmpty()) return@setOnClickListener
            MusicService.clearListSongPlaying()
            MusicService.mListSongPlaying!!.addAll(mListSong!!)
            MusicService.isPlaying = false
            GlobalFunction.startMusicService(activity, Constant.PLAY, 0)
            GlobalFunction.startActivity(activity, PlayMusicActivity::class.java)
        }
    }

    private fun searchSong() {
        val strKey = mFragmentSearchBinding?.edtSearchName?.text.toString().trim { it <= ' ' }
        loadListSongFromFirebase(strKey)
        GlobalFunction.hideSoftKeyboard(activity)
    }

    private fun displayLayoutPlayAll(isShow: Boolean) {
        if (isShow) {
            mFragmentSearchBinding?.layoutPlayAll?.visibility = View.VISIBLE
            GlideUtils.loadUrl(mListSong!![0].image, mFragmentSearchBinding?.imgPlayAll!!)
            mFragmentSearchBinding?.notiSearch?.visibility = View.GONE
        } else {
            mFragmentSearchBinding?.layoutPlayAll?.visibility = View.GONE
            mFragmentSearchBinding?.notiSearch?.visibility = View.VISIBLE
        }
    }
}