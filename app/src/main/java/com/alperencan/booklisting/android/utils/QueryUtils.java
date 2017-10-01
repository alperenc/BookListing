package com.alperencan.booklisting.android.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.alperencan.booklisting.android.model.Volume;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving volume data from Google Books API.
 * Obtained from Udacity's Android Basics Nanodegree: Networking lessons.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google Books API dataset and return a list of {@link Volume} objects.
     */
    public static List<Volume> fetchVolumeData(String requestUrl, String query) {
        // Create URL object
        URL url = createUrl(requestUrl, query);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response, create a list of {@link Volume}s and return.
        return extractVolumesFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl, String query) {
        Uri builtUri = Uri.parse(stringUrl)
                .buildUpon()
                .appendQueryParameter("q", query)
                .appendQueryParameter("maxResults", "20")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Volume} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Volume> extractVolumesFromJson(String volumeJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(volumeJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding volumes to
        List<Volume> volumes = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(volumeJSON);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of volumes (or books).
            JSONArray volumeArray = baseJsonResponse.getJSONArray("items");

            // For each volume in the volumeArray, create an {@link Volume} object
            for (int i = 0; i < volumeArray.length(); i++) {

                // Get a single volume at position i within the list of volumes
                JSONObject currentVolume = volumeArray.getJSONObject(i);

                // For a given volume, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of all information
                // for that volume.
                JSONObject volumeInfo = currentVolume.getJSONObject("volumeInfo");

                // Extract the value for the key called "title"
                String title = volumeInfo.getString("title");

                // Extract the value for the key called "authors" if there are any.
                String[] authorsArray;
                if (volumeInfo.has("authors")) {
                    JSONArray authorsJSONArray = volumeInfo.getJSONArray("authors");
                    authorsArray = new String[authorsJSONArray.length()];
                    for (int j = 0; j < authorsJSONArray.length(); j++) {
                        authorsArray[j] = authorsJSONArray.getString(j);
                    }
                } else {
                    authorsArray = new String[]{};
                }

                // Extract the value for the key called "thumbnail" if there are any images.
                String thumbnail = "";
                if (volumeInfo.has("imageLinks")) {
                    thumbnail = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
                }


                // Create a new {@link Volume} object with the title, authors, and thumbnail,
                // from the JSON response.
                Volume volume = new Volume(title, authorsArray, thumbnail);

                // Add the new {@link Volume} to the list of volumes.
                volumes.add(volume);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the volume JSON results", e);
        }

        // Return the list of volumes
        return volumes;
    }

}