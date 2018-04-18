package com.dbonilha.movieapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;


public class ReviewDescription extends AppCompatActivity {

    RatingBar reviewRatingBar;
    TextView summaryText;
    TextView reviewSumTextView;
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    Intent intent;
    WebView webView;
    Button fullReviewBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review_description);

        summaryText = findViewById(R.id.summaryTextView);
        reviewSumTextView = findViewById(R.id.reviewSumTextView);
        reviewRatingBar = findViewById(R.id.reviewRatingBar);
        fullReviewBtn = findViewById(R.id.fullRevBtn);
        webView =  findViewById(R.id.fullReviewPage);
        progressBar = findViewById(R.id.progressBarWeb);
        progressBar.setVisibility(View.GONE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //criação do Up button

        //inicializa sdk do facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        intent = getIntent();
        String summary = intent.getStringExtra(("summary"));
        final String urlReview = intent.getStringExtra(("link"));

        setSummary(summary);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebClient());

        fullReviewBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadWebView(urlReview);
            }
        });

        //controla a interação com a Rating Bar
        reviewRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            public void onRatingChanged(RatingBar ratingBar, float rating,boolean fromUser) {

                String linkTitle  = intent.getStringExtra(("linkTitle"));

                //após a seleção das estrelas é aberta uma janela para que o usuario compartilhe
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(getString(R.string.gave) + " " + rating + " "
                                    + getString(R.string.stars))
                            .setContentDescription(linkTitle)
                            .setContentUrl(Uri.parse(urlReview))
                            .build();

                    shareDialog.show(linkContent);
                }
            }
        });

    }

    private class MyWebClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(view.GONE);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;

        }
    }

    private void setSummary(String summary){
        //verifica se contem summary_short, se não houver mostra uma snackbar

        if (!summary.isEmpty()) {
            summaryText.setText(summary);
        }
        else {
            reviewSumTextView.setVisibility(View.INVISIBLE);     //esconde "Review Summary" r
            reviewRatingBar.setVisibility(View.INVISIBLE);      // e a ratingbar
            fullReviewBtn.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.reviewLayout),
                    R.string.no_summary, Snackbar.LENGTH_LONG).show();

        }
    }

    private void loadWebView(String url){
        webView.loadUrl(url);
        progressBar.setVisibility(View.VISIBLE);
        reviewSumTextView.setVisibility(View.GONE);
        fullReviewBtn.setVisibility(View.GONE);
        summaryText.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}


