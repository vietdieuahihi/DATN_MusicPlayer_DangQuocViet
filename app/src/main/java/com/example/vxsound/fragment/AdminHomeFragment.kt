package com.example.vxsound.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.example.vxsound.MyApplication
import com.example.vxsound.R
import com.example.vxsound.activity.AddSongActivity
import com.example.vxsound.activity.PlayMusicActivity
import com.example.vxsound.adapter.AdminSongAdapter
import com.example.vxsound.constant.Constant
import com.example.vxsound.constant.GlobalFunction
import com.example.vxsound.databinding.FragmentAdminHomeBinding
import com.example.vxsound.listener.IOnManagerSongListener
import com.example.vxsound.model.Song
import com.example.vxsound.service.MusicService
import com.example.vxsound.utils.StringUtil
import java.util.Locale

class AdminHomeFragment : Fragment() {
    private var mFragmentAdminHomeBinding: FragmentAdminHomeBinding? = null
    private var mListSong: MutableList<Song>? = null
    private var mAdminSongAdapter: AdminSongAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragmentAdminHomeBinding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        initView()
        initListener()
        loadListSong("")
        return mFragmentAdminHomeBinding?.root
    }

    private fun initView() {
        if (activity == null) return
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentAdminHomeBinding?.rcvSong?.layoutManager = linearLayoutManager
        mListSong = ArrayList()
        mAdminSongAdapter = AdminSongAdapter(mListSong, object : IOnManagerSongListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }

            override fun onClickUpdateSong(song: Song) {
                onClickEditSong(song)
            }

            override fun onClickDeleteSong(song: Song) {
                deleteSongItem(song)
            }
        })
        mFragmentAdminHomeBinding?.rcvSong?.adapter = mAdminSongAdapter
    }

    private fun initListener() {
        mFragmentAdminHomeBinding?.btnAddSong?.setOnClickListener { onClickAddSong() }
        mFragmentAdminHomeBinding?.imgSearch?.setOnClickListener { searchSong() }
        mFragmentAdminHomeBinding?.edtSearchName?.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchSong()
                return@setOnEditorActionListener true
            }
            false
        }
        mFragmentAdminHomeBinding?.edtSearchName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val strKey = s.toString().trim { it <= ' ' }
                if (strKey == "" || strKey.isEmpty()) {
                    searchSong()
                }
            }
        })
    }

    private fun goToSongDetail(song: Song) {
        MusicService.clearListSongPlaying()
        MusicService.mListSongPlaying!!.add(song)
        MusicService.isPlaying = false
        GlobalFunction.startMusicService(activity, Constant.PLAY, 0)
        GlobalFunction.startActivity(activity, PlayMusicActivity::class.java)
    }

    private fun onClickAddSong() {
        GlobalFunction.startActivity(activity, AddSongActivity::class.java)
    }

    private fun onClickEditSong(song: Song) {
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_INTENT_SONG_OBJECT, song)
        GlobalFunction.startActivity(activity, AddSongActivity::class.java, bundle)
    }

    private fun deleteSongItem(song: Song) {
        AlertDialog.Builder(activity)
            .setTitle(getString(R.string.msg_delete_title))
            .setMessage(getString(R.string.msg_confirm_delete))
            .setPositiveButton(getString(R.string.action_ok)) { _: DialogInterface?, _: Int ->
                if (activity == null) {
                    return@setPositiveButton
                }
                MyApplication[activity].songsDatabaseReference()
                    ?.child(song.id.toString())
                    ?.removeValue { _: DatabaseError?, _: DatabaseReference? ->
                        Toast.makeText(
                            activity,
                            getString(R.string.msg_delete_movie_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .setNegativeButton(getString(R.string.action_cancel), null)
            .show()
    }

    private fun searchSong() {
        val strKey = mFragmentAdminHomeBinding?.edtSearchName?.text.toString().trim { it <= ' ' }
        if (mListSong != null) {
            mListSong!!.clear()
        } else {
            mListSong = ArrayList()
        }
        loadListSong(strKey)
        GlobalFunction.hideSoftKeyboard(activity)
    }

    private fun loadListSong(keyword: String?) {
        if (activity == null) return
        MyApplication[activity].songsDatabaseReference()
            ?.addChildEventListener(object : ChildEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val song = dataSnapshot.getValue(Song::class.java)
                    if (song == null || mListSong == null) return
                    if (StringUtil.isEmpty(keyword)) {
                        mListSong!!.add(0, song)
                    } else {
                        if (GlobalFunction.getTextSearch(song.title)
                                .toLowerCase(Locale.getDefault()).trim { it <= ' ' }
                                .contains(
                                    GlobalFunction.getTextSearch(keyword)
                                        .toLowerCase(Locale.getDefault()).trim { it <= ' ' })
                        ) {
                            mListSong!!.add(0, song)
                        }
                    }
                    if (mAdminSongAdapter != null) mAdminSongAdapter!!.notifyDataSetChanged()
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                    val song = dataSnapshot.getValue(Song::class.java)
                    if (song == null || mListSong == null || mListSong!!.isEmpty()) return
                    for (i in mListSong!!.indices) {
                        if (song.id == mListSong!![i].id) {
                            mListSong!![i] = song
                            break
                        }
                    }
                    if (mAdminSongAdapter != null) mAdminSongAdapter!!.notifyDataSetChanged()
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    val song = dataSnapshot.getValue(Song::class.java)
                    if (song == null || mListSong == null || mListSong!!.isEmpty()) return
                    for (songObject in mListSong!!) {
                        if (song.id == songObject.id) {
                            mListSong!!.remove(songObject)
                            break
                        }
                    }
                    if (mAdminSongAdapter != null) mAdminSongAdapter!!.notifyDataSetChanged()
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }
}