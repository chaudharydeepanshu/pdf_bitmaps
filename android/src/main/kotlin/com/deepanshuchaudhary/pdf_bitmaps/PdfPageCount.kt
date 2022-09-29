package com.deepanshuchaudhary.pdf_bitmaps

import android.app.Activity
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.FileNotFoundException

// For getting pdf file page count.
suspend fun getPdfPageCount(
    pdfUri: String,
    context: Activity,
): Int? {

    var pageCount: Int? = null

    withContext(Dispatchers.IO) {

        val begin = System.nanoTime()

        val contentResolver = context.contentResolver

        suspend fun countPages(uri: Uri) {
            try {
                yield()

                //https://developer.android.com/training/data-storage/shared/documents-files#open
                val parcelFileDescriptor: ParcelFileDescriptor? =
                    contentResolver.openFileDescriptor(uri, "r")
                val pdfRenderer: PdfRenderer?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && parcelFileDescriptor != null) {
                    pdfRenderer = PdfRenderer(parcelFileDescriptor)
                    pageCount = pdfRenderer.pageCount
                    pdfRenderer.close()
                    parcelFileDescriptor.close()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

        countPages(Uri.parse(pdfUri))

        println(pageCount)

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

    }

    return pageCount
}