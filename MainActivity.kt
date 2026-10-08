package com.yuanbao.timetable

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader

/**
 * 课表 App —— WebView 外壳
 *
 * 核心设计：用 WebViewAssetLoader 把 APK 内置 assets 映射成
 *   https://local.ck/assets/...
 *
 * 为什么必须这样做：
 *   识别引擎依赖 Web Worker，而 Web Worker 无法在 file:// 下创建
 *   （file 页面源被视为 null，会被 CORS 拦截）。给它一个正常的 https
 *   同源域后 Worker 可以正常创建，同时所有资源都来自本地闪存 —— 完全离线。
 *
 * 将来的 GPS 动态提醒通过 AppBridge 注入，届时只改那一个类即可。
 */
class MainActivity : Activity() {

    private lateinit var web: WebView
    private var assetLoader: WebViewAssetLoader? = null

    /** 相册选图回调 —— 课表里的「从相册选择课表截图」依赖它 */
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private val REQ_PICK_IMAGE = 1001

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        web = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        setContentView(web)

        assetLoader = WebViewAssetLoader.Builder()
            .setDomain(LOCAL_DOMAIN)
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        val ws = web.settings
        ws.javaScriptEnabled = true
        ws.domStorageEnabled = true          // 识别引擎缓存 + 课表本地存储
        ws.databaseEnabled = true
        ws.allowContentAccess = true
        ws.useWideViewPort = true
        ws.builtInZoomControls = false
        ws.setSupportZoom(false)
        ws.mediaPlaybackRequiresUserGesture = false

        // 关掉强制深色：课表是浅色设计，被反色会破坏可读性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ws.forceDark = WebSettings.FORCE_DARK_OFF
        }

        web.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? = assetLoader?.shouldInterceptRequest(request.url)
        }

        web.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView,
                callback: ValueCallback<Array<Uri>>,
                params: FileChooserParams
            ): Boolean {
                filePathCallback = callback
                val i = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(Intent.EXTRA_MIME_TYPES,
                        arrayOf("image/jpeg", "image/png", "image/webp"))
                }
                return try {
                    startActivityForResult(Intent.createChooser(i, "选择课表截图"), REQ_PICK_IMAGE)
                    true
                } catch (e: Exception) {
                    filePathCallback = null
                    false
                }
            }
        }

        // 将来 GPS / 导航的 JS 调用入口（当前为占位实现，不影响现有功能）
        web.addJavascriptInterface(AppBridge(this), "AppBridge")

        web.loadUrl("https://$LOCAL_DOMAIN/assets/index.html")
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_PICK_IMAGE) {
            val cb = filePathCallback ?: return
            val uris: Array<Uri>? =
                if (resultCode == RESULT_OK && data?.data != null) arrayOf(data.data!!) else null
            cb.onReceiveValue(uris)
            filePathCallback = null
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        web.destroy()
        super.onDestroy()
    }

    companion object {
        private const val LOCAL_DOMAIN = "local.ck"
    }
}
