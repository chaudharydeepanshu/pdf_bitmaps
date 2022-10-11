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
        pdfPath: String?,
    ) {
        Log.d(
            LOG_TAG,
            "pdfPageCount - IN, pdfPath=$pdfPath"
        )

        if (!setPendingResult(result)) {
            finishWithAlreadyActiveError(result)
            return
        }

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val pageCount: Int? = getPdfPageCount( pdfPath, activity)

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

    // For getting pdf file page bitmap.
    fun pdfBitmap(
        result: MethodChannel.Result,
        pdfPath: String?,
        pageIndex: Int?,
        scale: Double?,
        backgroundColor: String?
    ) {
        Log.d(
            LOG_TAG,
            "pdfBitmap - IN, pdfPath=$pdfPath"
        )

        if (!setPendingResult(result)) {
            finishWithAlreadyActiveError(result)
            return
        }

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val bitmap: ByteArray? =
                    getPdfBitmap(pdfPath, activity, listOf(pageIndex!!), scale!!, backgroundColor!!)?.get(0)

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

    // For getting pdf file pages bitmaps.
    fun pdfBitmaps(
        result: MethodChannel.Result,
        pdfPath: String?,
        pagesIndexes: List<Int>?,
        scale: Double?,
        backgroundColor: String?
    ) {
        Log.d(
            LOG_TAG,
            "pdfBitmap - IN, pdfPath=$pdfPath"
        )

        if (!setPendingResult(result)) {
            finishWithAlreadyActiveError(result)
            return
        }

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val bitmaps: List<ByteArray>? =
                    getPdfBitmap(pdfPath, activity, pagesIndexes!!, scale!!, backgroundColor!!)

                if (bitmaps != null && bitmaps.isEmpty()) {
                    finishPdfBitmapsSuccessfully(null)
                } else {
                    finishPdfBitmapsSuccessfully(bitmaps)
                }
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

    fun cancelBitmaps(
    ) {
        job?.cancel()
        Log.d(LOG_TAG, "Canceled Bitmaps")
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

    private fun finishPdfBitmapsSuccessfully(result: List<ByteArray>?) {
        pendingResult?.success(result)
        clearPendingResult()
    }

    private fun finishWithError(errorCode: String, errorMessage: String?, errorDetails: String?) {
        pendingResult?.error(errorCode, errorMessage, errorDetails)
        clearPendingResult()
    }
}
