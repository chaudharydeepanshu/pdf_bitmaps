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
suspend fun getPdfPageSize(
    pdfPath: String,
    pageNumber: Int,
    context: Activity,
): List<Int> {

    val pageSizeList: MutableList<Int> = mutableListOf()

    withContext(Dispatchers.IO) {

        val begin = System.nanoTime()

        val contentResolver = context.contentResolver

        suspend fun pageSizeInfo(parcelFileDescriptor: ParcelFileDescriptor?) {
            try {
                yield()
                //https://developer.android.com/training/data-storage/shared/documents-files#open
                val pdfRenderer: PdfRenderer?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && parcelFileDescriptor != null) {
                    pdfRenderer = PdfRenderer(parcelFileDescriptor)
                    val page: PdfRenderer.Page = pdfRenderer.openPage(pageNumber - 1)
                    pageSizeList.add(page.width)
                    pageSizeList.add(page.height)
                    page.close()
                    pdfRenderer.close()
                    parcelFileDescriptor.close()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

        val parcelFileDescriptor: ParcelFileDescriptor? =
            contentResolver.openFileDescriptor(Utils().getURI(pdfPath), "r")

        pageSizeInfo(parcelFileDescriptor)

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

    }

    return pageSizeList
}