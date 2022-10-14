package com.deepanshuchaudhary.pdf_bitmaps

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import kotlin.math.floor


// https://developer.android.com/reference/android/graphics/pdf/PdfRenderer
// https://stackoverflow.com/questions/2883355/how-to-render-pdf-in-android

data class PageInfo(
    var pageNumber: Int,
    val rotationAngle: Int,
    val scale: Double,
    val backgroundColor: String?
)

// For getting pdf file page count.
suspend fun getPdfBitmap(
    pdfPath: String?,
    context: Activity,
    pagesInfo: List<PageInfo>,
): List<ByteArray>? {

    var byteArrayList: MutableList<ByteArray> = mutableListOf()

    withContext(Dispatchers.IO) {

        val begin = System.nanoTime()

        val contentResolver = context.contentResolver

        suspend fun renderPage(
            parcelFileDescriptor: ParcelFileDescriptor?,
            pagesInfo: List<PageInfo>
        ) {
            try {
                yield()
                val pdfRenderer: PdfRenderer?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && parcelFileDescriptor != null) {
                    pdfRenderer = PdfRenderer(parcelFileDescriptor)

//                    var pageCount: Int = pdfRenderer.pageCount

                    pagesInfo.forEach { pageInfo ->
                        yield()

                        val pdfBackgroundColor = try {
                            if (pageInfo.backgroundColor != null)
                                Color.parseColor(pageInfo.backgroundColor)
                            else
                                Color.TRANSPARENT
                        } catch (e: Exception) {
                            Log.d(PdfBitmapsPlugin.LOG_TAG, "createPdfBitmaps - IN")
                            Log.e("Parse", "Error parsing ${pageInfo.backgroundColor}. $e")
                            Color.TRANSPARENT
                        }

                        val page: PdfRenderer.Page = pdfRenderer.openPage(pageInfo.pageNumber - 1)

//                      val width: Int = context.resources.displayMetrics.densityDpi / 72 * page.width

                        val width: Int = floor(page.width * pageInfo.scale.toFloat()).toInt()
                        val height: Int = floor(page.height * pageInfo.scale.toFloat()).toInt()
                        val bitmap: Bitmap = Bitmap.createBitmap(
                            width, height,
                            Bitmap.Config.ARGB_8888
                        )

                        bitmap.eraseColor(pdfBackgroundColor)

                        val matrix = Matrix()
                        matrix.postTranslate((-0).toFloat(), (-0).toFloat())

                        if (pageInfo.scale.toFloat() != 1.0f)
                            matrix.postScale(pageInfo.scale.toFloat(), pageInfo.scale.toFloat())

                        page.render(
                            bitmap,
                            Rect(
                                0,
                                0,
                                floor(page.width * pageInfo.scale.toFloat()).toInt(),
                                floor(page.height * pageInfo.scale.toFloat()).toInt()
                            ),
                            matrix,
                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                        )
                        page.close()

                        val stream = ByteArrayOutputStream()

                        val rotatedBitmap =
                            Utils().rotateBitmap(bitmap, pageInfo.rotationAngle.toFloat())
                        bitmap.recycle()
                        rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        var byteArray: ByteArray = stream.toByteArray()
                        byteArrayList.add(byteArray)
                        rotatedBitmap.recycle()
                        stream.close()
                    }

                    pdfRenderer.close()
                    parcelFileDescriptor.close()

                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

        val parcelFileDescriptor: ParcelFileDescriptor? =
            contentResolver.openFileDescriptor(Utils().getURI(pdfPath!!), "r")

        renderPage(parcelFileDescriptor, pagesInfo)

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

    }

    return byteArrayList
}