package br.com.mjfmarmitaria.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.PermissionRequest;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private static final int FILE_REQUEST = 4102;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(android.graphics.Color.rgb(16,13,11));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(16,13,11));
        webView = new WebView(this);
        webView.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("https".equals(uri.getScheme()) && uri.getHost() != null &&
                    (uri.getHost().equals("edujao523-sketch.github.io") || uri.getHost().equals("cdnjs.cloudflare.com") || uri.getHost().equals("fonts.googleapis.com") || uri.getHost().equals("fonts.gstatic.com") || uri.getHost().equals("images.unsplash.com"))) return false;
                if ("https".equals(uri.getScheme()) || "http".equals(uri.getScheme())) {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return true;
                }
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                try { startActivityForResult(params.createIntent(), FILE_REQUEST); }
                catch (Exception e) { fileCallback = null; return false; }
                return true;
            }
        });
        setContentView(webView);
        webView.loadUrl("https://edujao523-sketch.github.io/mjf-marmitaria-app/");
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST && fileCallback != null) {
            Uri[] result = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
            fileCallback.onReceiveValue(result);
            fileCallback = null;
        }
    }
    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
