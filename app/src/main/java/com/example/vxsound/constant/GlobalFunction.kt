package com.example.vxsound.constant

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.vxsound.MyApplication
import com.example.vxsound.model.Song
import com.example.vxsound.model.UserInfor
import com.example.vxsound.prefs.DataStoreManager
import com.example.vxsound.service.MusicReceiver
import com.example.vxsound.service.MusicService
import java.text.Normalizer
import java.util.regex.Pattern

object GlobalFunction {
    fun startActivity(context: Context?, clz: Class<*>?) {
        val intent = Intent(context, clz)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

    fun startActivity(context: Context?, clz: Class<*>?, bundle: Bundle?) {
        val intent = Intent(context, clz)
        intent.putExtras(bundle!!)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

    fun hideSoftKeyboard(activity: Activity?) {
        try {
            val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
    }

    fun onClickOpenGmail(context: Context?) {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", AboutUsConfig.GMAIL, null))
        context?.startActivity(Intent.createChooser(emailIntent, "Send Email"))
    }

    fun onClickOpenSkype(context: Context?) {
        try {
            val skypeUri = Uri.parse("skype:" + AboutUsConfig.SKYPE_ID + "?chat")
            context?.packageManager?.getPackageInfo("com.skype.raider", 0)
            val skypeIntent = Intent(Intent.ACTION_VIEW, skypeUri)
            skypeIntent.component = ComponentName("com.skype.raider", "com.skype.raider.Main")
            context?.startActivity(skypeIntent)
        } catch (e: Exception) {
            openSkypeWebView(context)
        }
    }

    private fun openSkypeWebView(context: Context?) {
        try {
            context?.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("skype:" + AboutUsConfig.SKYPE_ID + "?chat")))
        } catch (exception: Exception) {
            val skypePackageName = "com.skype.raider"
            try {
                context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$skypePackageName")))
            } catch (anfe: ActivityNotFoundException) {
                context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$skypePackageName")))
            }
        }
    }

    fun onClickOpenFacebook(context: Context?) {
        var intent: Intent
        try {
            var urlFacebook: String = AboutUsConfig.PAGE_FACEBOOK
            val packageManager = context?.packageManager
            val versionCode = packageManager?.getPackageInfo("com.facebook.katana", 0)?.versionCode
            if (versionCode!! >= 3002850) { //newer versions of fb app
                urlFacebook = "fb://facewebmodal/f?href=" + AboutUsConfig.LINK_FACEBOOK
            }
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlFacebook))
        } catch (e: Exception) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.LINK_FACEBOOK))
        }
        context?.startActivity(intent)
    }

    fun onClickOpenYoutubeChannel(context: Context?) {
        context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.LINK_YOUTUBE)))
    }

    fun onClickOpenZalo(context: Context?) {
        context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.ZALO_LINK)))
    }

    fun callPhoneNumber(activity: Activity) {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), 101)
                    return
                }
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:" + AboutUsConfig.PHONE_NUMBER)
                activity.startActivity(callIntent)
            } else {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:" + AboutUsConfig.PHONE_NUMBER)
                activity.startActivity(callIntent)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun showToastMessage(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getTextSearch(input: String?): String {
        val nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("")
    }

    fun startMusicService(ctx: Context?, action: Int, songPosition: Int) {
        val musicService = Intent(ctx, MusicService::class.java)
        musicService.putExtra(Constant.MUSIC_ACTION, action)
        musicService.putExtra(Constant.SONG_POSITION, songPosition)
        ctx?.startService(musicService)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun openMusicReceiver(ctx: Context, action: Int): PendingIntent {
        val intent = Intent(ctx, MusicReceiver::class.java)
        intent.putExtra(Constant.MUSIC_ACTION, action)
        val pendingFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getBroadcast(ctx.applicationContext, action, intent, pendingFlag)
    }

    fun isFavoriteSong(song: Song): Boolean {
        if (song.favorite == null || song.favorite!!.isEmpty()) return false
        val listUsersFavorite: List<UserInfor> = ArrayList<UserInfor>(song.favorite!!.values)
        if (listUsersFavorite.isEmpty()) return false
        for (userInfo in listUsersFavorite) {
            if (DataStoreManager.user?.email == userInfo.emailUser) {
                return true
            }
        }
        return false
    }

    fun onClickFavoriteSong(context: Context?, song: Song, isFavorite: Boolean) {
        if (context == null) return
        if (isFavorite) {
            val userEmail: String? = DataStoreManager.user?.email
            val userInfo = UserInfor(System.currentTimeMillis(), userEmail)
            MyApplication[context].songsDatabaseReference()
                    ?.child(song.id.toString())
                    ?.child("favorite")
                    ?.child(userInfo.id.toString())
                    ?.setValue(userInfo)
        } else {
            val userInfo: UserInfor? = getUserFavoriteSong(song)
            if (userInfo != null) {
                MyApplication[context].songsDatabaseReference()
                        ?.child(song.id.toString())
                        ?.child("favorite")
                        ?.child(userInfo.id.toString())
                        ?.removeValue()
            }
        }
    }

    private fun getUserFavoriteSong(song: Song): UserInfor? {
        var userInfor: UserInfor? = null
        if (song.favorite == null || song.favorite!!.isEmpty()) return null
        val listUsersFavorite: List<UserInfor> = ArrayList<UserInfor>(song.favorite!!.values)
        if (listUsersFavorite.isEmpty()) return null
        for (userObject in listUsersFavorite) {
            if (DataStoreManager.user?.email == userObject.emailUser) {
                userInfor = userObject
                break
            }
        }
        return userInfor
    }
}