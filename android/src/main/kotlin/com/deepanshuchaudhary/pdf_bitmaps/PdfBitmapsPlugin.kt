package com.deepanshuchaudhary.pdf_bitmaps

import android.util.Log

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** PdfBitmapsPlugin */
class PdfBitmapsPlugin : FlutterPlugin, ActivityAware, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    private var pdfBitmaps: PdfBitmaps? = null
    private var pluginBinding: FlutterPlugin.FlutterPluginBinding? = null
    private var activityBinding: ActivityPluginBinding? = null

    companion object {
        const val LOG_TAG = "PdfBitmapsPlugin"
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        Log.d(LOG_TAG, "onAttachedToEngine - IN")

        if (pluginBinding != null) {
            Log.w(LOG_TAG, "onAttachedToEngine - already attached")
        }

        pluginBinding = flutterPluginBinding

        val messenger = pluginBinding?.binaryMessenger
        doOnAttachedToEngine(messenger!!)

        Log.d(LOG_TAG, "onAttachedToEngine - OUT")
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        Log.d(LOG_TAG, "onDetachedFromEngine")
        doOnDetachedFromEngine()
    }

    // Note: This may be called multiple times on app startup.
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        Log.d(LOG_TAG, "onAttachedToActivity")
        doOnAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        Log.d(LOG_TAG, "onDetachedFromActivity")
        doOnDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        Log.d(LOG_TAG, "onReattachedToActivityForConfigChanges")
        doOnAttachedToActivity(binding)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        Log.d(LOG_TAG, "onDetachedFromActivityForConfigChanges")
        doOnDetachedFromActivity()
    }

    private fun doOnAttachedToEngine(messenger: BinaryMessenger) {
        Log.d(LOG_TAG, "doOnAttachedToEngine - IN")

        this.channel = MethodChannel(messenger, "pdf_bitmaps")
        this.channel.setMethodCallHandler(this)

        Log.d(LOG_TAG, "doOnAttachedToEngine - OUT")
    }

    private fun doOnDetachedFromEngine() {
        Log.d(LOG_TAG, "doOnDetachedFromEngine - IN")

        if (pluginBinding == null) {
            Log.w(LOG_TAG, "doOnDetachedFromEngine - already detached")
        }
        pluginBinding = null

        this.channel.setMethodCallHandler(null)

        Log.d(LOG_TAG, "doOnDetachedFromEngine - OUT")
    }

    private fun doOnAttachedToActivity(activityBinding: ActivityPluginBinding?) {
        Log.d(LOG_TAG, "doOnAttachedToActivity - IN")

        this.activityBinding = activityBinding

        Log.d(LOG_TAG, "doOnAttachedToActivity - OUT")
    }

    private fun doOnDetachedFromActivity() {
        Log.d(LOG_TAG, "doOnDetachedFromActivity - IN")

        if (pdfBitmaps != null) {
            pdfBitmaps = null
        }
        activityBinding = null

        Log.d(LOG_TAG, "doOnDetachedFromActivity - OUT")
    }


    override fun onMethodCall(call: MethodCall, result: Result) {
        Log.d(LOG_TAG, "onMethodCall - IN , method=${call.method}")
        if (pdfBitmaps == null) {
            if (!createPdfBitmaps()) {
                result.error("init_failed", "Not attached", null)
                return
            }
        }
        when (call.method) {
            "pdfPageCount" -> pdfBitmaps!!.pdfPageCount(
                result,
                pdfPath = call.argument("pdfPath"),
            )
            "pdfBitmap" -> pdfBitmaps!!.pdfBitmap(
                result,
                pdfPath = call.argument("pdfPath"),
                pageInfo = PageInfo(
                    pageNumber = call.argument<Map<String, Int>>("pageInfo")!!["pageNumber"]!!,
                    rotationAngle = call.argument<Map<String, Int>>("pageInfo")!!["rotationAngle"]!!,
                    scale = call.argument<Map<String, Double>>("pageInfo")!!["scale"]!!,
                    backgroundColor = call.argument<Map<String, String>>("pageInfo")!!["backgroundColor"]!!
                ),
                pdfRendererType = parseMethodCallPdfRendererTypeArgument(call)
                    ?: PdfRendererType.AndroidPdfRenderer,
            )
            "pdfBitmaps" -> pdfBitmaps!!.pdfBitmaps(
                result,
                pdfPath = call.argument("pdfPath"),
                pagesInfo = parseMethodCallArrayOfPageInfoArgument(call, "pagesInfo") ?: listOf(),
                pdfRendererType = parseMethodCallPdfRendererTypeArgument(call)
                    ?: PdfRendererType.AndroidPdfRenderer,
            )
            "pdfPageSize" -> pdfBitmaps!!.pdfPageSizeInfo(
                result,
                pdfPath = call.argument("pdfPath"),
                pageNumber = call.argument("pageNumber"),
            )
            "pdfValidityAndProtection" -> pdfBitmaps!!.pdfValidityAndProtection(
                result,
                pdfPath = call.argument("pdfPath"),
            )
            "cancelBitmaps" -> pdfBitmaps!!.cancelBitmaps()
            else -> result.notImplemented()
        }
    }

    private fun createPdfBitmaps(): Boolean {
        Log.d(LOG_TAG, "createPdfBitmaps - IN")

        var pdfBitmaps: PdfBitmaps? = null
        if (activityBinding != null) {
            pdfBitmaps = PdfBitmaps(
                activity = activityBinding!!.activity
            )
        }
        this.pdfBitmaps = pdfBitmaps

        Log.d(LOG_TAG, "createPdfBitmaps - OUT")

        return pdfBitmaps != null
    }

    private fun parseMethodCallArrayOfPageInfoArgument(
        call: MethodCall, arg: String
    ): List<PageInfo>? {
        if (call.hasArgument(arg)) {
            val tempArrayOfMap = call.argument<ArrayList<Map<String, Any>>>(arg)?.toList()
            val pagesInfo: MutableList<PageInfo> = mutableListOf()
            tempArrayOfMap!!.forEach {
                val temp = PageInfo(
                    pageNumber = it["pageNumber"]!! as Int,
                    rotationAngle = it["rotationAngle"]!! as Int,
                    scale = it["scale"]!! as Double,
                    backgroundColor = it["backgroundColor"]!! as String?,
                )
                pagesInfo.add(temp)
            }
            return pagesInfo
        }
        return null
    }

    private fun parseMethodCallPdfRendererTypeArgument(call: MethodCall): PdfRendererType? {
        val arg = "pdfRendererType"

        if (call.hasArgument(arg)) {
            return if (call.argument<String>(arg)
                    ?.toString() == "PdfRendererType.pdfBoxPdfRenderer"
            ) {
                PdfRendererType.PdfBoxPdfRenderer
            } else {
                PdfRendererType.AndroidPdfRenderer
            }
        }
        return null
    }
}
