package com.deepanshuchaudhary.pdf_bitmaps

import android.app.Activity
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import com.deepanshuchaudhary.pdf_bitmaps.PdfBitmapsPlugin.Companion.LOG_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

// For checking pdf validity and encryption.
suspend fun getPdfValidityAndProtection(
    pdfPath: String,
    context: Activity,
): List<Boolean?> {

    var isPDFValid: Boolean? = null
    var isOpenPasswordProtected: Boolean? = null // Means that this pdf is user password protected.

    withContext(Dispatchers.IO) {

        val begin = System.nanoTime()

        val contentResolver = context.contentResolver

        val parcelFileDescriptor: ParcelFileDescriptor? =
            contentResolver.openFileDescriptor(Utils().getURI(pdfPath), "r")

        try {
            yield()
            val pdfRenderer: PdfRenderer?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && parcelFileDescriptor != null) {
                pdfRenderer = PdfRenderer(parcelFileDescriptor)

                pdfRenderer.close()
                parcelFileDescriptor.close()

                isPDFValid = true
                isOpenPasswordProtected = false

            }
        } catch (e: SecurityException) {
            isPDFValid = true
            isOpenPasswordProtected = true
            Log.d(
                LOG_TAG,
                e.stackTraceToString(),
            )
        } catch (e: Exception) {
            isPDFValid = false
            Log.d(
                LOG_TAG,
                e.stackTraceToString(),
            )
        }

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

    }

    return listOf(
        isPDFValid,
        isOpenPasswordProtected,
    )
}