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

                utils.finishSuccessfully(pageCount, result)
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
            LOG_TAG, "pdfPageSizeInfo - IN, pdfPath=$pdfPath, pageNumber=$pageNumber"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val pdfPageSizeInfo: List<Int> = getPdfPageSize(pdfPath!!, pageNumber!!, activity)

                if (pdfPageSizeInfo.isEmpty()) {
                    utils.finishSuccessfully(null, result)
                } else {
                    utils.finishSuccessfully(pdfPageSizeInfo, result)
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
        result: MethodChannel.Result,
        pdfPath: String?,
        pageInfo: PageInfo?,
        pdfRendererType: PdfRendererType,
    ) {
        Log.d(
            LOG_TAG,
            "pdfBitmap - IN, pdfPath=$pdfPath, pageInfo=$pageInfo, pdfRendererType=$pdfRendererType"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val imageFilesPaths: List<String> = getPdfBitmap(
                    pdfPath!!, activity, listOf(pageInfo!!), pdfRendererType
                )

                if (imageFilesPaths.isEmpty()) {
                    utils.finishSuccessfully(null, result)
                } else {
                    utils.finishSuccessfully(imageFilesPaths[0], result)
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

    // For getting pdf file pages bitmaps.
    fun pdfBitmaps(
        result: MethodChannel.Result,
        pdfPath: String?,
        pagesInfo: List<PageInfo>?,
        pdfRendererType: PdfRendererType,
    ) {
        Log.d(
            LOG_TAG,
            "pdfBitmap - IN, pdfPath=$pdfPath, pagesInfo=$pagesInfo, pdfRendererType=$pdfRendererType"
        )

        val uiScope = CoroutineScope(Dispatchers.Main)
        job = uiScope.launch {
            try {
                val imageFilesPaths: List<String> =
                    getPdfBitmap(pdfPath!!, activity, pagesInfo!!, pdfRendererType)

                if (imageFilesPaths.isEmpty()) {
                    utils.finishSuccessfully(null, result)
                } else {
                    utils.finishSuccessfully(imageFilesPaths, result)
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
                val result: List<Boolean?> = getPdfValidityAndProtection(pdfPath!!, activity)

                utils.finishSuccessfully(result, resultCallback)

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
