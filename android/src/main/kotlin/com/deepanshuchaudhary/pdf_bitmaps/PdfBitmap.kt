package com.deepanshuchaudhary.pdf_bitmaps

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException


// https://developer.android.com/reference/android/graphics/pdf/PdfRenderer
// https://stackoverflow.com/questions/2883355/how-to-render-pdf-in-android

// For getting pdf file page count.
suspend fun getPdfBitmap(
    pdfUri: String,
    context: Activity,
    pageIndex: Int,
    quality: Int,
): ByteArray? {

    var byteArray: ByteArray? = null

    withContext(Dispatchers.IO) {

        val begin = System.nanoTime()

        val contentResolver = context.contentResolver

        suspend fun renderPage(uri: Uri, pageIndex: Int) {
            try {
                yield()
                val parcelFileDescriptor: ParcelFileDescriptor? =
                    contentResolver.openFileDescriptor(uri, "r")
                val pdfRenderer: PdfRenderer?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && parcelFileDescriptor != null) {
                    pdfRenderer = PdfRenderer(parcelFileDescriptor)
//                    var pageCount: Int = pdfRenderer.pageCount
                    val page: PdfRenderer.Page = pdfRenderer.openPage(pageIndex)
                    val width: Int =
                        context.resources.displayMetrics.densityDpi / 72 * page.width * quality / 100
                    val height: Int =
                        context.resources.displayMetrics.densityDpi / 72 * page.height * quality / 100
                    val bitmap: Bitmap = Bitmap.createBitmap(
                        width, height,
                        Bitmap.Config.ARGB_8888
                    )

                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    pdfRenderer.close()
                    parcelFileDescriptor.close()
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    byteArray = stream.toByteArray()
                    bitmap.recycle()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

        renderPage(Uri.parse(pdfUri), pageIndex)

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

    }

    return byteArray
}