package com.dbonilha.movieapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Review> reviewList = new ArrayList<>();


    private ReviewArrayAdapter reviewArrayAdapter;
    private ListView movieListView;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        
        movieListView = (ListView) findViewById(R.id.movieListView);
        reviewArrayAdapter = new ReviewArrayAdapter(this, reviewList);
        movieListView.setAdapter(reviewArrayAdapter);



        FloatingActionButton fab =
                (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText movieEditText =
                        (EditText) findViewById(R.id.movieEditText);
                URL url = createURL(movieEditText.getText().toString());

                if (url != null) {
                    dismissKeyboard(movieEditText);
                    GetReviewTask getLocalReviewTask = new GetReviewTask();
                    getLocalReviewTask.execute(url);
                }
                else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout),
                            R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        /*
         *  Torna a lista clicavel para abrir um nova activity e passa o summary da review
         *  para ser exibido nessa nova activity.
         */
        movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String summary = reviewList.get(position).getReviewDescription();
                String link = reviewList.get(position).getLinkReview();
                String linkTitle = reviewList.get(position).getLinkTitle();


                Intent intent = new Intent(MainActivity.this, ReviewDescription.class);
                intent.putExtra("summary", summary);
                intent.putExtra("link", link);
                intent.putExtra("linkTitle",linkTitle);
                startActivity(intent);
            }
        });

    }


    private void dismissKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private URL createURL(String movie) {
        String apiKey = getString(R.string.api_key);
        String baseUrl = getString(R.string.web_service_url);

        try {
            String urlString = baseUrl + apiKey + "&query=" + URLEncoder.encode(movie, "UTF-8");
            return new URL(urlString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private class GetReviewTask
            extends AsyncTask<URL, Void, JSONObject> {

        private ProgressDialog progressBar;

           protected void onPreExecute() {
               progressBar = new ProgressDialog(MainActivity.this); //criação da pop-up de progresso
               progressBar.setMessage(getString(R.string.load));
               progressBar.show();
           }
        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) params[0].openConnection();
                int response = connection.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();

                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {

                        String line;

                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    catch (IOException e) {
                        Snackbar.make(findViewById(R.id.coordinatorLayout),
                                R.string.read_error, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    return new JSONObject(builder.toString());
                }
                else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout),
                            R.string.connect_error, Snackbar.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.connect_error, Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            }
            finally {
                connection.disconnect();
            }

            return null;
        }


        @Override
        protected void onPostExecute(JSONObject review) {
            convertJSONtoArrayList(review);
            reviewArrayAdapter.notifyDataSetChanged();
            movieListView.smoothScrollToPosition(0);
            progressBar.dismiss();

        }
    }


    private void convertJSONtoArrayList(JSONObject reviews) {
        reviewList.clear();

        try {

            JSONArray list = reviews.getJSONArray("results");

            //verifica se ha resultados, se não houver exibe uma snack bar

            if(list.length() == 0) {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.no_results, Snackbar.LENGTH_LONG).show();
            }


            for (int i = 0; i < list.length(); ++i) {

                String movieIcon;
                JSONObject movie = list.getJSONObject(i);

                if(!movie.isNull("multimedia")) {                       //verifica multimedia
                    JSONObject icon = movie.getJSONObject("multimedia"); //pega o objeto
                    movieIcon = icon.getString("src");                  //atribui a string
                }

                /*
                    se estiver vazio, não tem icone, cria "caminho" de  um icone padrao,
                    transforma em string, para ser passado como parametro
                 */
                else {
                    Integer defaultIcon = R.drawable.default_icon;
                    movieIcon = defaultIcon.toString();
                }
                JSONObject link = movie.getJSONObject("link");

                reviewList.add(new Review(

                        movie.getString("publication_date"),
                        movie.getString("display_title"),
                        movie.getString("byline"),
                        movieIcon,
                        movie.getString("summary_short"),
                        link.getString("url"),
                        link.getString("suggested_link_text")));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}



