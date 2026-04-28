package com.felipelucio.lugrav.interfaces

import android.content.Context
import java.io.File

class FileProviderImpl(private val context: Context) : FileProvider {
    override fun getCacheDir(): File = context.cacheDir
    override fun getExternalFilesDir(): File? = context.getExternalFilesDir(null)
}
