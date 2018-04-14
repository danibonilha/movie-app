package com.dbonilha.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;






public class ReviewDescription extends AppCompatActivity {

    RatingBar reviewRatingBar;
    TextView summaryText;
    TextView reviewSumTextView;
    TextView urlText;
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review_description);

        //inicializa sdk do facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //criação do Up button

        intent = getIntent();
        String summary = intent.getStringExtra(("summary"));
        final String urlReview = intent.getStringExtra(("link"));

        summaryText = (TextView) findViewById(R.id.summaryTextView);
        reviewSumTextView = (TextView) findViewById(R.id.reviewSumTextView);
        reviewRatingBar = (RatingBar) findViewById(R.id.reviewRatingBar);

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

        //verifica se contem summary_short, se não houver mostra uma snackbar

        if (!summary.isEmpty()) {
            summaryText.setText(summary);
        }
        else {
            reviewSumTextView.setVisibility(View.INVISIBLE);     //esconde "Review Summary" r
            reviewRatingBar.setVisibility(View.INVISIBLE);      // e a ratingbar
            Snackbar.make(findViewById(R.id.reviewLayout),
                    R.string.no_summary, Snackbar.LENGTH_LONG).show();

        }

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}


