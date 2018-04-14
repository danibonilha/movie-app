package com.dbonilha.movieapp;
import com.dbonilha.movieapp.R;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dani on 24/07/2016.
 * Esta classe é  o Adapter
 */
public class ReviewArrayAdapter extends ArrayAdapter<Review> {


    private static class ViewHolder {
        ImageView movieImageView;
        TextView  titleTextView;
        TextView  authorTextView;
        TextView  dateTextView;

    }

    private Map<String, Bitmap> bitmaps = new HashMap<>();


    public ReviewArrayAdapter(Context context, List<Review> reviews) {
        super(context, -1, reviews);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Review movie = getItem(position);

        ViewHolder viewHolder;


        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView =
                    inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.movieImageView =
                    (ImageView) convertView.findViewById(R.id.movieImageView);
            viewHolder.titleTextView =
                    (TextView) convertView.findViewById(R.id.titleTextView);
            viewHolder.dateTextView =
                    (TextView) convertView.findViewById(R.id.dateTextView);
            viewHolder.authorTextView =
                    (TextView) convertView.findViewById(R.id.authorTextView);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (bitmaps.containsKey(movie.iconURL)) {
            viewHolder.movieImageView.setImageBitmap(
                    bitmaps.get(movie.iconURL));
        }
        else {
            if (movie.iconURL.contains("jpg")) {                       //verifica se contem icone
                new LoadImageTask(viewHolder.movieImageView).execute(  //pois td url de icone desta
                        movie.iconURL);                                //API contem jpg no fim da string
            }
            else{                                            //se não tiver adiciona um icone padrão
                int icon = Integer.parseInt(movie.iconURL);  //o movie.iconURL é convertido para int
                viewHolder.movieImageView.setImageResource(icon); //pq setImageRes só recebe int
            }
        }


        Context context = getContext();
        viewHolder.titleTextView.setText(context.getString(
                R.string.movie_title, movie.movieTitle));
        viewHolder.dateTextView.setText(
                context.getString(R.string.publication_date, movie.convertedDate));
        viewHolder.authorTextView.setText(
                context.getString(R.string.review_author, movie.reviewAuthor));

        return convertView;
    }


    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;


        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }


        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(params[0]);


                connection = (HttpURLConnection) url.openConnection();

                try (InputStream inputStream = connection.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.put(params[0], bitmap);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                connection.disconnect(); // close the HttpURLConnection
            }

            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
