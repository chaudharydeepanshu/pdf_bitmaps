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
    private val utils = Utils()

    private var job: Job? = null

    // For getting pdf file page count.
    fun pdfPageCount(
        result: MethodChannel.Result,
        pdfPath: String?,
    ) {
        Log.d(
            LOG_TAG, "pdfPageCount - IN, pdfPath=$pdfPath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val pageCount: Int? = getPdfPageCount(pdfPath, activity)

                utils.finishPageCountSuccessfully(pageCount, result)
            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfPageCount_exception", e.stackTraceToString(), null, result
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfPageCount_OutOfMemoryError", e.stackTraceToString(), null, result
                )
            }
        }
        Log.d(LOG_TAG, "pdfPageCount - OUT")
    }

    // For getting pdf file page size info.
    fun pdfPageSizeInfo(
        result: MethodChannel.Result,
        pdfPath: String?,
        pageNumber: Int?,
    ) {
        Log.d(
            LOG_TAG, "pdfPageSizeInfo - IN, pdfPath=$pdfPath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val pdfPageSizeInfo: List<Int> = getPdfPageSize(pdfPath!!, pageNumber!!, activity)

                if (pdfPageSizeInfo.isEmpty()) {
                    utils.finishSplitSuccessfullyWithListOfInt(null, result)
                } else {
                    utils.finishSplitSuccessfullyWithListOfInt(pdfPageSizeInfo, result)
                }
            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfPageSizeInfo_exception", e.stackTraceToString(), null, result
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfPageSizeInfo_OutOfMemoryError", e.stackTraceToString(), null, result
                )
            }
        }
        Log.d(LOG_TAG, "pdfPageSizeInfo - OUT")
    }

    // For getting pdf file page bitmap.
    fun pdfBitmap(
        result: MethodChannel.Result, pdfPath: String?, pageInfo: PageInfo?
    ) {
        Log.d(
            LOG_TAG, "pdfBitmap - IN, pdfPath=$pdfPath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val bitmap: ByteArray? = getPdfBitmap(
                    pdfPath, activity, listOf(pageInfo!!)
                ).get(0)

                utils.finishPdfBitmapSuccessfully(bitmap, result)
            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfBitmap_exception", e.stackTraceToString(), null, result
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfBitmap_OutOfMemoryError", e.stackTraceToString(), null, result
                )
            }
        }
        Log.d(LOG_TAG, "pdfBitmap - OUT")
    }

    // For getting pdf file pages bitmaps.
    fun pdfBitmaps(
        result: MethodChannel.Result, pdfPath: String?, pagesInfo: List<PageInfo>?
    ) {
        Log.d(
            LOG_TAG, "pdfBitmap - IN, pdfPath=$pdfPath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val bitmaps: List<ByteArray>? = getPdfBitmap(pdfPath, activity, pagesInfo!!)

                if (bitmaps != null && bitmaps.isEmpty()) {
                    utils.finishPdfBitmapsSuccessfully(null, result)
                } else {
                    utils.finishPdfBitmapsSuccessfully(bitmaps, result)
                }
            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfBitmap_exception", e.stackTraceToString(), null, result
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfBitmap_OutOfMemoryError", e.stackTraceToString(), null, result
                )
            }
        }
        Log.d(LOG_TAG, "pdfBitmap - OUT")
    }

    // For getting pdf validity and protection info.
    fun pdfValidityAndProtection(
        resultCallback: MethodChannel.Result,
        pdfPath: String?,
    ) {
        Log.d(
            LOG_TAG, "pdfValidityAndProtection - IN, pdfPath=$pdfPath"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val result: List<Boolean?>? = getPdfValidityAndProtection(pdfPath!!, activity)

                utils.finishSplitSuccessfullyWithListOfBoolean(result, resultCallback)

            } catch (e: Exception) {
                utils.finishWithError(
                    "pdfValidityAndProtection_exception",
                    e.stackTraceToString(),
                    null,
                    resultCallback
                )
            } catch (e: OutOfMemoryError) {
                utils.finishWithError(
                    "pdfValidityAndProtection_OutOfMemoryError",
                    e.stackTraceToString(),
                    null,
                    resultCallback
                )
            }
        }
        Log.d(LOG_TAG, "pdfValidityAndProtection - OUT")
    }

    fun cancelBitmaps(
    ) {
        job?.cancel()
        Log.d(LOG_TAG, "Canceled Bitmaps")
    }
}
