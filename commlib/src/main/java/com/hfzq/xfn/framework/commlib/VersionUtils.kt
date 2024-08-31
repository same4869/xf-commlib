package com.hfzq.xfn.framework.commlib

import android.os.Build
import android.provider.MediaStore

class VersionUtils {

    companion object {

        @JvmStatic
        fun isAtLeastTarget30() : Boolean{
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        }

        fun test() {
            val contentResolver = APPLICATION.contentResolver
            contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
        }
    }
}