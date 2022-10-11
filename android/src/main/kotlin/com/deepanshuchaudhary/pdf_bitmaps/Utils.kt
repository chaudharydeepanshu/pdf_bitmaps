package com.deepanshuchaudhary.pdf_bitmaps

import android.net.Uri
import java.io.*

class Utils {
    fun getURI(uri: String): Uri {
        val parsed: Uri = Uri.parse(uri)
        val parsedScheme: String? = parsed.scheme
        return if ((parsedScheme == null) || parsedScheme.isEmpty()) {
            Uri.fromFile(File(uri))
        } else parsed
    }
}