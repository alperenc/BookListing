package com.alperencan.booklisting.android.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.alperencan.booklisting.android.R;
import com.alperencan.booklisting.android.adapter.VolumeAdapter;
import com.alperencan.booklisting.android.model.Volume;
import com.alperencan.booklisting.android.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;

public class BookListingActivity extends AppCompatActivity {

    /**
     * URL for volume data from the Google Books API
     */
    private static final String GOOGLE_BOOKS_API_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";

    /**
     * RecyclerView to view the volumes
     */
    private RecyclerView recyclerView;

    /**
     * TextView for empty state.
     */
    private TextView emptyView;

    /**
     * Adapter for the list of volumes
     */
    private VolumeAdapter volumeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_listing);
        handleIntent(getIntent());

        // Find a reference to the {@link RecyclerView} in the layout
        recyclerView = (RecyclerView) findViewById(R.id.list);

        // Find a reference to the {@link TextView} in the layout
        emptyView = (TextView) findViewById(R.id.empty_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<Volume> volumes = new ArrayList<>();
//        volumes.add(new Volume("Android in The Attic", new String[]{"Nicholas Allan"}, "http://books.google.com/books/content?id=MoXpe6H2B5gC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"));
//        volumes.add(new Volume("Voice Application Development for Android", new String[]{"Michael F. McTear", "Zoraida Callejas"}, "http://books.google.com/books/content?id=V-gtAgAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"));

        if (volumes.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            volumeAdapter = new VolumeAdapter(volumes);
            recyclerView.setAdapter(volumeAdapter);
        }

//        // Start the AsyncTask to fetch the volume data
//        VolumeAsyncTask task = new VolumeAsyncTask();
//        task.execute(GOOGLE_BOOKS_API_BASE_URL, "android");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        getMenuInflater().inflate(R.menu.options_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            // Start the AsyncTask to fetch the volume data for search term
            VolumeAsyncTask task = new VolumeAsyncTask();
            task.execute(GOOGLE_BOOKS_API_BASE_URL, query);

        }
    }

    private class VolumeAsyncTask extends AsyncTask<String, Void, List<Volume>> {

        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link Volume}s as the result.
         */
        @Override
        protected List<Volume> doInBackground(String... params) {
            // Don't perform the request if there are no URLs, or the first URL is null
            if (params.length < 1 || params[0] == null) {
                return null;
            }

            return QueryUtils.fetchVolumeData(params[0], params[1]);
        }

        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. We update the adapter with the new list of volumes, which will trigger
         * the RecyclerView to re-populate its items.
         */
        @Override
        protected void onPostExecute(List<Volume> volumes) {
            if (volumes != null && !volumes.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);

                volumeAdapter = new VolumeAdapter(volumes);
                recyclerView.setAdapter(volumeAdapter);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    }
}
