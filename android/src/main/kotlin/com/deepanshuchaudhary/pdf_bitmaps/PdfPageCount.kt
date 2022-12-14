package com.deepanshuchaudhary.pdf_bitmaps

import android.app.Activity
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.FileNotFoundException

// For getting pdf file page count.
suspend fun getPdfPageCount(
    pdfPath: String?,
    context: Activity,
): Int? {

    var pageCount: Int? = null

    withContext(Dispatchers.IO) {

        val begin = System.nanoTime()

        val contentResolver = context.contentResolver

        suspend fun countPages(parcelFileDescriptor: ParcelFileDescriptor?) {
            try {
                yield()
                //https://developer.android.com/training/data-storage/shared/documents-files#open
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

        val parcelFileDescriptor: ParcelFileDescriptor? =
            contentResolver.openFileDescriptor(Utils().getURI(pdfPath!!), "r")

        countPages(parcelFileDescriptor)

        println(pageCount)

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

    }

    return pageCount
}