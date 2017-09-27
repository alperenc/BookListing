package com.alperencan.booklisting.android.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alperencan.booklisting.android.R;
import com.alperencan.booklisting.android.model.Volume;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * {@link VolumeAdapter} creates VolumeViewHolder classes as needed and binds them to their data.
 */

public class VolumeAdapter extends RecyclerView.Adapter<VolumeAdapter.VolumeViewHolder> {

    private List<Volume> volumes;

    public static class VolumeViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView authorsTextView;
        public ImageView coverImageView;
        public View layout;
        public AsyncTask<?, ?, ?> task;


        public VolumeViewHolder(View itemView) {
            super(itemView);
            layout = itemView;

            titleTextView = itemView.findViewById(R.id.title_text);
            authorsTextView = itemView.findViewById(R.id.authors_text);
            coverImageView = itemView.findViewById(R.id.cover_image);

        }
    }

    public void add(int position, Volume item) {
        volumes.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        volumes.remove(position);
        notifyItemRemoved(position);
    }

    public VolumeAdapter(List<Volume> volumes) {
        this.volumes = volumes;
    }

    @Override
    public VolumeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new VolumeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VolumeViewHolder holder, int position) {
        Volume volume = volumes.get(position);
        holder.titleTextView.setText(volume.getTitle());

        String authors = "";
        for (String author : volume.getAuthors()) {
            authors += author;
            if (author != volume.getAuthors()[volume.getAuthors().length - 1]) {
                authors += ", ";
            }
        }
        holder.authorsTextView.setText(authors);

        // Don't show any image before the correct one is downloaded
        holder.coverImageView.setImageBitmap(null);

        if (holder.task != null) { // Existing task may be executing
            holder.task.cancel(true);
            holder.task = null;
        }

        if (volume.getCoverImage() == null) { // Image not downloaded already
            DownloadImageTask task = new DownloadImageTask(holder.coverImageView);
            holder.task = task;
            task.execute(volume);
        } else {
            holder.coverImageView.setImageBitmap(volume.getCoverImage());
        }

    }

    @Override
    public int getItemCount() {
        return volumes.size();
    }

    private class DownloadImageTask extends AsyncTask<Volume, Void, Volume> {

        private ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Volume doInBackground(Volume... volumes) {
            Volume volume = volumes[0];
            volume.setCoverImage(downloadBitmap(volume.getCoverImageUrl()));
            return volume;
        }

        @Override
        protected void onPostExecute(Volume volume) {
            // Update the ImageView with the downloaded image
            imageView.setImageBitmap(volume.getCoverImage());
        }

        /*
        I have found the following code snippet from StackOverflow (https://stackoverflow.com/a/32983958)
         */
        private Bitmap downloadBitmap(String url) {
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();

                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (Exception e) {
                Log.d("URLCONNECTIONERROR", e.toString());
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                Log.w("ImageDownloader", "Error downloading image from " + url);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();

                }
            }
            return null;
        }
    }
}