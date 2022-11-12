package com.deepanshuchaudhary.pdf_bitmaps

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import io.flutter.plugin.common.MethodChannel
import java.io.File

class Utils {
    fun getURI(uri: String): Uri {
        val parsed: Uri = Uri.parse(uri)
        val parsedScheme: String? = parsed.scheme
        return if ((parsedScheme == null) || parsedScheme.isEmpty() || "${uri[0]}" == "/") {
            // Using "${uri[0]}" == "/" in condition above because if uri is an absolute file path without any scheme starting with "/"
            // and if its filename contains ":" then the parsed scheme will be wrong.
            Uri.fromFile(File(uri))
        } else parsed
    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun finishPageCountSuccessfully(result: Int?, resultCallback: MethodChannel.Result?) {
        resultCallback?.success(result)
    }

    fun finishPdfBitmapSuccessfully(
        result: ByteArray?, resultCallback: MethodChannel.Result?
    ) {
        resultCallback?.success(result)
    }

    fun finishPdfBitmapsSuccessfully(
        result: List<ByteArray>?, resultCallback: MethodChannel.Result?
    ) {
        resultCallback?.success(result)
    }

    fun finishSplitSuccessfullyWithListOfBoolean(
        result: List<Boolean?>?, resultCallback: MethodChannel.Result?
    ) {
        resultCallback?.success(result)
    }

    fun finishSplitSuccessfullyWithListOfInt(
        result: List<Int>?, resultCallback: MethodChannel.Result?
    ) {
        resultCallback?.success(result)
    }

    fun finishWithError(
        errorCode: String,
        errorMessage: String?,
        errorDetails: String?,
        resultCallback: MethodChannel.Result?
    ) {
        resultCallback?.error(errorCode, errorMessage, errorDetails)
    }
}