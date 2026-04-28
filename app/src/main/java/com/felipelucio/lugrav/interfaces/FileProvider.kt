package com.felipelucio.lugrav.interfaces

import java.io.File

interface FileProvider {
    fun getCacheDir(): File
    fun getExternalFilesDir(): File?
}
