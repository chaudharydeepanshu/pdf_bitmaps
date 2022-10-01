package com.deepanshuchaudhary.pdf_bitmaps

import android.app.Activity
import android.util.Log
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val LOG_TAG = "PdfViewer"

class PdfBitmaps(
    private val activity: Activity
) {

    private var pendingResult: MethodChannel.Result? = null

    private var job: Job? = null

    // For getting pdf file page count.
    fun pdfPageCount(
        result: MethodChannel.Result,
        pdfUri: String?,
        pdfPath: String?,
    ) {
        Log.d(
            LOG_TAG,
            "pdfPageCount - IN, pdfUri=$pdfUri"
        )

        if (!setPendingResult(result)) {
            finishWithAlreadyActiveError(result)
            return
        }

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val pageCount: Int? = getPdfPageCount(pdfUri, pdfPath, activity)

                finishPageCountSuccessfully(pageCount)
            } catch (e: Exception) {
                finishWithError(
                    "pdfPageCount_exception",
                    e.stackTraceToString(),
                    null
                )
            } catch (e: OutOfMemoryError) {
                finishWithError(
                    "pdfPageCount_OutOfMemoryError",
                    e.stackTraceToString(),
                    null
                )
            }
        }
        Log.d(LOG_TAG, "pdfPageCount - OUT")
    }

    // For getting pdf file page count.
    fun pdfBitmap(
        result: MethodChannel.Result,
        pdfUri: String?,
        pdfPath: String?,
        pageIndex: Int?,
        quality: Int?
    ) {
        Log.d(
            LOG_TAG,
            "pdfBitmap - IN, pdfUri=$pdfUri"
        )

        if (!setPendingResult(result)) {
            finishWithAlreadyActiveError(result)
            return
        }

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val bitmap: ByteArray? =
                    getPdfBitmap(pdfUri, pdfPath, activity, pageIndex!!, quality!!)

                finishPdfBitmapSuccessfully(bitmap)
            } catch (e: Exception) {
                finishWithError(
                    "pdfBitmap_exception",
                    e.stackTraceToString(),
                    null
                )
            } catch (e: OutOfMemoryError) {
                finishWithError(
                    "pdfBitmap_OutOfMemoryError",
                    e.stackTraceToString(),
                    null
                )
            }
        }
        Log.d(LOG_TAG, "pdfBitmap - OUT")
    }

    private fun setPendingResult(
        result: MethodChannel.Result
    ): Boolean {
        pendingResult = result
        return true
    }

    private fun finishWithAlreadyActiveError(result: MethodChannel.Result) {
        result.error("already_active", "Already active", null)
    }

    private fun clearPendingResult() {
        pendingResult = null
    }

    private fun finishPageCountSuccessfully(result: Int?) {
        pendingResult?.success(result)
        clearPendingResult()
    }

    private fun finishPdfBitmapSuccessfully(result: ByteArray?) {
        pendingResult?.success(result)
        clearPendingResult()
    }

    private fun finishWithError(errorCode: String, errorMessage: String?, errorDetails: String?) {
        pendingResult?.error(errorCode, errorMessage, errorDetails)
        clearPendingResult()
    }
}
