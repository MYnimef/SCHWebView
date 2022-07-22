package com.mynimef.schwebview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity: AppCompatActivity(R.layout.activity_main) {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = findViewById(R.id.webView)
        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        webView.webViewClient = object: WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url.toString()
                if (url.contains("yandex.ru/maps")) {
                    val uri = Uri.parse("yandexmaps://$url")
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        view.context.startActivity(intent)
                    } catch (exc: Exception) {
                        val intent = Intent(Intent.ACTION_VIEW, request.url)
                        view.context.startActivity(intent)
                    }
                } else if (url.contains("yandex.ru/pogoda")) {
                    val uri = Uri.parse("yandexweather://$url")
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        view.context.startActivity(intent)
                    } catch (exc: Exception) {
                        val intent = Intent(Intent.ACTION_VIEW, request.url)
                        view.context.startActivity(intent)
                    }
                } else {
                    view.loadUrl(url)
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                with (sharedPref.edit()) {
                    putString("url", webView.url)
                    apply()
                }
            }
        }
        webView.settings.javaScriptEnabled = true

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        webView.loadUrl(sharedPref.getString("url", "https://yandex.ru/")!!)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Exit the app?")
                .setPositiveButton("Yes") { _, _ -> finishAndRemoveTask() }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }
    }
}