package com.example.webapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MainActivity : AppCompatActivity() {
    private val websiteUrl = "https://www.rizvicollege.edu.in/"
    private lateinit var webView: WebView
    private lateinit var mySwipeRefreshLayout: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (CheckNet.isNetThere(this)) {
            setContentView(R.layout.activity_main)

            webView = findViewById(R.id.web)
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.overScrollMode = WebView.OVER_SCROLL_NEVER
            webView.loadUrl(websiteUrl)
            webView.webViewClient = WebViewClientDemo()

            mySwipeRefreshLayout = findViewById(R.id.swipeContainer)
            mySwipeRefreshLayout.setOnRefreshListener{
                webView.reload()
                mySwipeRefreshLayout.isRefreshing = false
            }
        } else {
            AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection")
                .setPositiveButton("Turn on") { _, _ -> internetstart(this) }
                .setNegativeButton("OK"){ _ , _ -> finish()}
                .show()
        }

    }
    private inner class WebViewClientDemo:WebViewClient(){
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return if (Uri.parse(url).host == Uri.parse(websiteUrl).host) {
                false
            } else {
                // Redirect to Chrome browser
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                true
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            mySwipeRefreshLayout.isRefreshing = false
        }
    }
    override fun onBackPressed() {
        if (webView.isFocused && webView.canGoBack()) {
            webView.goBack()
        } else {
            AlertDialog.Builder(this)
                .setTitle("EXIT")
                .setMessage("Are you sure you want to close this app?")
                .setPositiveButton("Yes") { _, _ -> finish() }
                .setNegativeButton("No", null)
                .show()
        }
    }
    fun internetstart(context: Context){
        val intent = Intent()
        intent.action = android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS
        context.startActivity(intent)
        if (CheckNet.isNetThere(this)){
            webView.reload()
        }
        else{
            finish()
        }
    }
}
object CheckNet{
    private const val TAG = "CheckNet"
    fun isNetThere(context: Context):Boolean{
        val connectivityManager = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val networkInfo = connectivityManager.activeNetwork
        return if (networkInfo == null) {
            Log.d(TAG, "No internet connection")
            false
        } else {
            Log.d(TAG, "Internet connection available")
            true
        }
    }
}
