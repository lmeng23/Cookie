package com.example.jd

import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.TextView
import android.widget.Toast
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initWebView()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeCookie()
    }

    private fun initWebView() {
        val url = "https://plogin.m.jd.com/login/login"
        val wb = findViewById<View>(R.id.homeweb) as WebView
        val settings: WebSettings = wb.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        wb.loadUrl(url)
        wb.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return if (url.startsWith("http") || url.startsWith("https")) {
                    view.loadUrl(url)
                    getCookie(url)
                    true
                } else {
                    false
                }
            }
        }
    }


    private fun getCookie(url: String) {
        val cookieManager = CookieManager.getInstance()
        var cookieStr = cookieManager.getCookie(url)
        if (cookieStr.contains("pt_key") && cookieStr.contains("pt_pin")) {
            cookieStr = cookieStr.substring(cookieStr.indexOf("pt_key"))
            val key = cookieStr.substring(0, cookieStr.indexOf("pt_pin"))
            cookieStr = cookieStr.substring(cookieStr.indexOf("pt_pin"))
            val pin = cookieStr.substring(0, cookieStr.indexOf(";") + 1)
            val cookie = key + pin
            showCookie(cookie)
            copyClipboard(cookie)
        }
    }

    private fun showCookie(cookie: String) {
        val textView = findViewById<View>(R.id.textView) as TextView
        textView.text = cookie

    }


    private fun copyClipboard(content: String?) {
        try {
            val myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val primaryClip = ClipData.newPlainText("text", content)
            myClipboard.setPrimaryClip(primaryClip)
            Toast.makeText(this, "已复制CK到粘贴板", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeCookie() {
        try {
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}