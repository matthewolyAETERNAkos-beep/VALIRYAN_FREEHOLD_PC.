package com.valiryan.freehold

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import android.content.SharedPreferences
import android.provider.OpenableColumns
import android.text.InputType
import java.net.URL

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private lateinit var address: EditText
    private lateinit var web: WebView
    private lateinit var prefs: SharedPreferences
    private var privateMode = false
    private var currentUrl = "https://search.brave.com/"
    private var dark = true
    private val bg get() = if (dark) Color.rgb(7,9,13) else Color.rgb(244,241,233)
    private val panel get() = if (dark) Color.rgb(18,22,30) else Color.rgb(255,255,255)
    private val fg get() = if (dark) Color.rgb(237,232,220) else Color.rgb(26,28,33)
    private val gold = Color.rgb(214,179,106)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("valiryan", MODE_PRIVATE)
        dark = prefs.getBoolean("dark", true)
        buildUi()
        showHome()
    }

    private fun buildUi() {
        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(bg) }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 18, 16, 10)
            setBackgroundColor(panel)
        }
        val brand = TextView(this).apply {
            text = "VALIRYAN FREEHOLD"
            textSize = 20f; setTextColor(gold); gravity = Gravity.CENTER_VERTICAL
            letterSpacing = .12f
        }
        val subtitle = TextView(this).apply {
            text = "PRIVATE BROWSING • YOUR OWN DOMAIN"
            textSize = 10f; setTextColor(fg); letterSpacing = .12f
        }
        header.addView(brand); header.addView(subtitle)
        address = EditText(this).apply {
            hint = "Search or enter address"; textSize = 15f
            setSingleLine(true); inputType = InputType.TYPE_TEXT_VARIATION_URI
            setTextColor(fg); setHintTextColor(Color.GRAY)
            setPadding(14, 2, 14, 2)
            background = rounded(panel, gold)
            setOnEditorActionListener { _, _, _ -> loadAddress(text.toString()); true }
        }
        val addressRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(12, 8, 12, 8); setBackgroundColor(panel)
        }
        val go = makeButton("GO") { loadAddress(address.text.toString()) }
        addressRow.addView(address, LinearLayout.LayoutParams(0, 48, 1f))
        addressRow.addView(go)
        header.addView(addressRow)
        root.addView(header)
        web = WebView(this)
        web.setBackgroundColor(bg)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.loadsImagesAutomatically = true
        web.settings.javaScriptCanOpenWindowsAutomatically = false
        web.settings.allowFileAccess = false
        web.settings.allowContentAccess = true
        web.settings.mediaPlaybackRequiresUserGesture = true
        web.webChromeClient = WebChromeClient()
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val u = request.url
                if (u.scheme == "http" || u.scheme == "https") return false
                return try { startActivity(Intent(Intent.ACTION_VIEW, u)); true } catch (_: Exception) { true }
            }
            override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                currentUrl = url
                if (!privateMode) address.setText(url)
            }
        }
        web.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            enqueueDownload(url, userAgent, contentDisposition, mimeType)
        })
        root.addView(web, LinearLayout.LayoutParams(-1, 0, 1f))
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER
            setPadding(4, 6, 4, 6); setBackgroundColor(panel)
        }
        listOf(
            "←" to { if (web.canGoBack()) web.goBack() },
            "→" to { if (web.canGoForward()) web.goForward() },
            "⌂" to { showHome() },
            "＋ Tab" to { newTab() },
            "Private" to { togglePrivate() },
            "Tools" to { toolsMenu() }
        ).forEach { (label, action) ->
            nav.addView(makeButton(label, action), LinearLayout.LayoutParams(0, 44, 1f))
        }
        root.addView(nav)
        setContentView(root)
    }

    private fun rounded(color: Int, stroke: Int): android.graphics.drawable.Drawable {
        return android.graphics.drawable.GradientDrawable().apply {
            setColor(color); cornerRadius = 24f; setStroke(1, stroke)
        }
    }
    private fun makeButton(label: String, action: () -> Unit): Button =
        Button(this).apply {
            text = label; textSize = 11f; isAllCaps = false
            setTextColor(gold); background = rounded(panel, Color.rgb(65,57,43))
            setPadding(5, 0, 5, 0); setOnClickListener { action() }
        }

    private fun showHome() {
        val html = """
            <!doctype html><html><head><meta name="viewport" content="width=device-width,initial-scale=1">
            <style>
            body{margin:0;background:${hex(bg)};color:${hex(fg)};font:16px system-ui;padding:28px 18px}
            .crest{font-size:12px;letter-spacing:4px;color:#d6b36a;margin-top:22px}
            h1{font-size:32px;line-height:1.08;letter-spacing:1px;margin:10px 0}
            p{color:${hex(if(dark) Color.LTGRAY else Color.DKGRAY)};line-height:1.6}
            .grid{display:grid;grid-template-columns:repeat(2,1fr);gap:12px;margin-top:28px}
            a{display:block;text-decoration:none;color:inherit;background:${hex(panel)};border:1px solid #51452d;border-radius:14px;padding:17px}
            small{display:block;color:#d6b36a;margin-top:7px}
            </style></head><body>
            <div class="crest">THE FREEHOLD BROWSER</div><h1>Knowledge.<br>Without chains.</h1>
            <p>A private-minded browser with a sovereign, customizable interface.</p>
            <div class="grid">
            <a href="https://search.brave.com/">Search<small>Brave Search</small></a>
            <a href="https://www.wikipedia.org/">Explore<small>Wikipedia</small></a>
            <a href="https://www.youtube.com/">Watch<small>YouTube</small></a>
            <a href="https://github.com/">Build<small>GitHub</small></a>
            </div><p style="margin-top:32px;font-size:12px">Use Tools for downloads, PDFs, bookmarks, themes and privacy controls.</p>
            </body></html>
        """.trimIndent()
        address.setText("")
        web.loadDataWithBaseURL("https://valiryan.local/", html, "text/html", "UTF-8", null)
    }
    private fun hex(c: Int) = String.format("#%06X", 0xFFFFFF and c)

    private fun loadAddress(raw: String) {
        var s = raw.trim()
        if (s.isEmpty()) return
        if (!s.contains("://") && s.contains(" ") || (!s.contains(".") && !s.contains("://"))) {
            s = "https://search.brave.com/search?q=" + Uri.encode(s)
        } else if (!s.contains("://")) s = "https://$s"
        try {
            val uri = Uri.parse(s)
            if (uri.scheme != "https" && uri.scheme != "http") {
                Toast.makeText(this, "Only HTTP and HTTPS pages are supported", Toast.LENGTH_SHORT).show(); return
            }
            address.setText(s); web.loadUrl(s)
        } catch (_: Exception) { Toast.makeText(this, "Invalid address", Toast.LENGTH_SHORT).show() }
    }

    private fun newTab() {
        // This starter uses one visible WebView. A real tab manager needs a WebView collection.
        showHome()
        Toast.makeText(this, "Starter mode: new-tab home opened. Full multi-tab stack is not implemented yet.", Toast.LENGTH_LONG).show()
    }
    private fun togglePrivate() {
        privateMode = !privateMode
        if (privateMode) {
            web.clearHistory(); web.clearCache(true); CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
            address.setText("")
            Toast.makeText(this, "Private session enabled. Cookies/cache cleared; use Tools > Exit private session to return.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Private session flag disabled. Existing pages may still be open.", Toast.LENGTH_LONG).show()
        }
        root.findViewWithTag<View>("privateBadge")?.visibility = if (privateMode) View.VISIBLE else View.GONE
    }

    private fun toolsMenu() {
        val options = arrayOf(
            "Downloads manager", "Open PDF / document", "Import bookmarks (HTML)",
            "Themes", "DIY module catalog", "Privacy & tracking protection",
            "Exit private session", "Clear browsing data"
        )
        AlertDialog.Builder(this).setTitle("VALIRYAN TOOLS").setItems(options) { _, which ->
            when (which) {
                0 -> openDownloads()
                1 -> openDocument()
                2 -> importBookmarks()
                3 -> themeMenu()
                4 -> diyMenu()
                5 -> privacyInfo()
                6 -> { privateMode = false; web.clearHistory(); web.clearCache(true); CookieManager.getInstance().removeAllCookies(null); showHome() }
                7 -> clearData()
            }
        }.show()
    }
    private fun enqueueDownload(url: String, userAgent: String, disposition: String, mime: String) {
        try {
            val req = DownloadManager.Request(Uri.parse(url))
                .setTitle(disposition.ifBlank { URL(url).path.substringAfterLast('/').ifBlank { "Download" } })
                .setDescription("Downloaded by VALIRYAN FREEHOLD")
                .setMimeType(mime)
                .addRequestHeader("User-Agent", userAgent)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URL(url).path.substringAfterLast('/').ifBlank { "download" })
            if (url.startsWith("https://")) req.setAllowedOverMetered(true)
            val id = (getSystemService(DOWNLOAD_SERVICE) as DownloadManager).enqueue(req)
            Toast.makeText(this, "Download queued • ID $id", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Could not download this link: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    private fun openDownloads() {
        try { startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)) }
        catch (_: Exception) { Toast.makeText(this, "Open the Downloads app to manage files.", Toast.LENGTH_LONG).show() }
    }
    private val PICK_PDF = 101
    private val PICK_BOOKMARKS = 102
    private fun openDocument() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE); type = "application/pdf"
        }
        startActivityForResult(i, PICK_PDF)
    }
    private fun importBookmarks() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE); type = "text/html"
        }
        startActivityForResult(i, PICK_BOOKMARKS)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data?.data == null) return
        val uri = data.data!!
        when (requestCode) {
            PICK_PDF -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/pdf"); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(intent)
                } catch (_: Exception) {
                    Toast.makeText(this, "No PDF viewer found. Install a PDF reader.", Toast.LENGTH_LONG).show()
                }
            }
            PICK_BOOKMARKS -> {
                Toast.makeText(this, "Bookmark file selected. HTML parsing/import into a bookmark database is a TODO in this starter.", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun themeMenu() {
        val items = arrayOf("Obsidian & gold", "Ivory & bronze")
        AlertDialog.Builder(this).setTitle("Choose theme").setItems(items) { _, which ->
            dark = which == 0
            prefs.edit().putBoolean("dark", dark).apply()
            buildUi(); showHome()
        }.show()
    }
    private fun diyMenu() {
        AlertDialog.Builder(this).setTitle("DIY modules")
            .setMessage("Starter module catalog. Future modules can be added as JSON theme/module packs without replacing the main HTML. Arbitrary downloaded JavaScript is not executed for security.")
            .setPositiveButton("Open module folder guide") { _, _ ->
                AlertDialog.Builder(this).setTitle("DIY pack format")
                    .setMessage("Place JSON packs in app-specific storage or bundle trusted packs in assets. Suggested fields: id, name, description, accentColor, backgroundColor, homeCards. Dynamic native features require Kotlin code.")
                    .setPositiveButton("OK", null).show()
            }.setNegativeButton("Close", null).show()
    }
    private fun privacyInfo() {
        AlertDialog.Builder(this).setTitle("Privacy & protection")
            .setMessage("This starter does not yet include a network request interceptor or filter lists, so it does NOT block all ads or trackers. WebView has limited browser-engine controls. For strong protection, integrate a maintained filter engine or use GeckoView/Chromium-based engine with a verified content-blocking implementation. Private mode here is a basic session toggle, not a guaranteed forensic-erasure system.")
            .setPositiveButton("OK", null).show()
    }
    private fun clearData() {
        AlertDialog.Builder(this).setTitle("Clear browsing data?")
            .setMessage("This clears WebView history, cache and cookies.")
            .setNegativeButton("Cancel", null).setPositiveButton("Clear") { _, _ ->
                web.clearHistory(); web.clearCache(true)
                CookieManager.getInstance().removeAllCookies { }
                CookieManager.getInstance().flush()
                Toast.makeText(this, "Browsing data cleared", Toast.LENGTH_SHORT).show()
            }.show()
    }
    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else super.onBackPressed()
    }
    override fun onDestroy() {
        if (privateMode) {
            web.clearHistory(); web.clearCache(true)
            CookieManager.getInstance().removeAllCookies(null)
        }
        web.destroy()
        super.onDestroy()
    }
}
