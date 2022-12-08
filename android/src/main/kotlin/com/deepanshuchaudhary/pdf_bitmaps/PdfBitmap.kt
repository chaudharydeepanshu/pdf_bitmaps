package com.deepanshuchaudhary.pdf_bitmaps

import android.app.Activity
import android.content.ContentResolver
import android.graphics.*
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.net.toUri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.ImageType
import com.tom_roush.pdfbox.rendering.PDFRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.floor

// https://developer.android.com/reference/android/graphics/pdf/PdfRenderer
// https://stackoverflow.com/questions/2883355/how-to-render-pdf-in-android

data class PageInfo(
    var pageNumber: Int, val rotationAngle: Int, val scale: Double, val backgroundColor: String?
)

enum class PdfRendererType {
    AndroidPdfRenderer, PdfBoxPdfRenderer
}

// For getting pdf file page count.
suspend fun getPdfBitmap(
    pdfPath: String,
    context: Activity,
    pagesInfo: List<PageInfo>,
    pdfRendererType: PdfRendererType,
): List<String> {

    val utils = Utils()

    var imageFilesPathsList: List<String>

    withContext(Dispatchers.IO) {

        val begin = System.nanoTime()

        val contentResolver = context.contentResolver

        val uri = utils.getURI(pdfPath)

        imageFilesPathsList = if (pdfRendererType == PdfRendererType.PdfBoxPdfRenderer) {

            PDFBoxResourceLoader.init(context)

            val pdfTempFile: File = File.createTempFile("writerTempFile", ".pdf")

            utils.copyDataFromSourceToDestDocument(
                sourceFileUri = uri,
                destinationFileUri = pdfTempFile.toUri(),
                contentResolver = contentResolver
            )

            renderFile(pdfTempFile, pagesInfo)
        } else {
            renderPage(uri, contentResolver, pagesInfo)
        }

        val end = System.nanoTime()
        println("Elapsed time in nanoseconds: ${end - begin}")

    }

    return imageFilesPathsList
}

//https://github.com/TomRoush/PdfBox-Android/blob/master/sample/src/main/java/com/tom_roush/pdfbox/sample/MainActivity.java
suspend fun renderFile(sourcePdfFile: File, pagesInfo: List<PageInfo>): List<String> {

    val imageRenderFilesPaths: MutableList<String> = mutableListOf()
    // Render the pages and save them to image files
    try {
        // Load in an already created PDF
        val document: PDDocument = PDDocument.load(sourcePdfFile)
        // Create a renderer for the document
        val renderer = PDFRenderer(document)

        pagesInfo.forEach { pageInfo ->
            yield()

            // Getting image background color
            val pdfImagesBackgroundColor = try {
                if (pageInfo.backgroundColor != null) Color.parseColor(pageInfo.backgroundColor)
                else Color.TRANSPARENT
            } catch (e: Exception) {
                Log.d(PdfBitmapsPlugin.LOG_TAG, "createPdfBitmaps - IN")
                Log.e("Parse", "Error parsing ${pageInfo.backgroundColor}. $e")
                Color.TRANSPARENT
            }

            val page = document.getPage(pageInfo.pageNumber - 1)

            // Create new empty bitmap for a colored background
            val width: Int = floor(page.mediaBox.width * pageInfo.scale.toFloat()).toInt()
            val height: Int = floor(page.mediaBox.height * pageInfo.scale.toFloat()).toInt()
            val newBitmap = Bitmap.createBitmap(
                width, height, Bitmap.Config.ARGB_8888
            )

            // Filling empty bitmap background color
            newBitmap.eraseColor(pdfImagesBackgroundColor)

            // Render the image to an ARGB Bitmap
            val pageImage = renderer.renderImage(
                pageInfo.pageNumber - 1, pageInfo.scale.toFloat(), ImageType.ARGB
            )

            // Overlaying image bitmap over empty bitmap
            val canvas = Canvas(newBitmap)
            canvas.drawBitmap(newBitmap, Matrix(), null)
            canvas.drawBitmap(pageImage, Matrix(), null)

            // Image rotation
            val rotatedBitmap = Utils().rotateBitmap(pageImage, pageInfo.rotationAngle.toFloat())
            pageImage.recycle()

            // Save the render result to an image
            val imageRenderFile: File = File.createTempFile("imageRenderFile", ".png")
            val fileOut = FileOutputStream(imageRenderFile)
            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOut)
            fileOut.close()
            rotatedBitmap.recycle()
            imageRenderFilesPaths.add(imageRenderFile.path)
        }
        document.close()
    } catch (e: IOException) {
        Log.e("PdfBox-Android", "Exception thrown while rendering file", e)
    } finally {
        sourcePdfFile.delete()
    }
    return imageRenderFilesPaths
}

suspend fun renderPage(
    uri: Uri, contentResolver: ContentResolver, pagesInfo: List<PageInfo>
): List<String> {
    val imageRenderFilesPaths: MutableList<String> = mutableListOf()
    // Render the pages and save them to image files
    try {
        yield()
        val parcelFileDescriptor: ParcelFileDescriptor? =
            contentResolver.openFileDescriptor(uri, "r")

        val pdfRenderer: PdfRenderer?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && parcelFileDescriptor != null) {
            // Load in an already created PDF
            pdfRenderer = PdfRenderer(parcelFileDescriptor)

            // var pageCount: Int = pdfRenderer.pageCount

            pagesInfo.forEach { pageInfo ->
                yield()

                val pdfBackgroundColor = try {
                    if (pageInfo.backgroundColor != null) Color.parseColor(pageInfo.backgroundColor)
                    else Color.TRANSPARENT
                } catch (e: Exception) {
                    Log.d(PdfBitmapsPlugin.LOG_TAG, "createPdfBitmaps - IN")
                    Log.e("Parse", "Error parsing ${pageInfo.backgroundColor}. $e")
                    Color.TRANSPARENT
                }

                val page: PdfRenderer.Page = pdfRenderer.openPage(pageInfo.pageNumber - 1)

                //  val width: Int = context.resources.displayMetrics.densityDpi / 72 * page.width

                val width: Int = floor(page.width * pageInfo.scale.toFloat()).toInt()
                val height: Int = floor(page.height * pageInfo.scale.toFloat()).toInt()
                val bitmap: Bitmap = Bitmap.createBitmap(
                    width, height, Bitmap.Config.ARGB_8888
                )

                bitmap.eraseColor(pdfBackgroundColor)

//                       Not using custom transform because it is leading to wrong rendering for rotated pdf on Android 7.0(Verified by me)
//                       More info at: https://stackoverflow.com/a/41421216
//                        val matrix = Matrix()
//                        matrix.postTranslate((-0).toFloat(), (-0).toFloat())
//
//                        if (pageInfo.scale.toFloat() != 1.0f) matrix.postScale(
//                            pageInfo.scale.toFloat(),
//                            pageInfo.scale.toFloat()
//                        )

                page.render(
                    bitmap, Rect(
                        0,
                        0,
                        floor(page.width * pageInfo.scale).toInt(),
                        floor(page.height * pageInfo.scale).toInt(),
                    ),
//                            matrix,
                    null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )
                page.close()

                val rotatedBitmap = Utils().rotateBitmap(bitmap, pageInfo.rotationAngle.toFloat())
                bitmap.recycle()

                // Save the render result to an image
                val imageRenderFile: File = File.createTempFile("imageRenderFile", ".png")
                val fileOut = FileOutputStream(imageRenderFile)
                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOut)
                fileOut.close()
                rotatedBitmap.recycle()
                imageRenderFilesPaths.add(imageRenderFile.path)
            }

            pdfRenderer.close()
            parcelFileDescriptor.close()

        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    return imageRenderFilesPaths
}