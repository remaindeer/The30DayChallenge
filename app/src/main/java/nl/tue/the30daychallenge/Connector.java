package nl.tue.the30daychallenge;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * An object which is a gateway to the back-end.
 *
 * Created by Daan and Kevin on 3/10/15.
 */
public class Connector {

    // the endpoint of the back-end
    private static String endpoint = "http://challenge.ovoweb.net/";

    // sorting fields
    public static enum SortField {
        LIKES,          // sort on the number of likes
        COMPLETIONS,    // sort on the number of completions
        DOWNLOADS,      // sort on the number of download
        CREATED_AT,     // sort on when the challenges are created
        UPDATED_AT,     // sort on when the challenges were modified
        RANDOM,         // sort in a random manner
        TITLE,          // sort on the challenge title
        DESCRIPTION     // sort on the description of the challenges
    }

    // a class which contains response information from a HTTP request
    public static class Response {
        // the HTTP status code
        public int statusCode = -1;
        // the reader which can read data from the given URL
        public BufferedReader reader = null;
    }

    /**
     * A filter is an abstract class which can be converted to a query string to query the back-end.
     */
    public abstract static class Filter {

        /**
         * Retrieve a query string for the filter.
         *
         * @return a query string which can be appended to the back-end url
         */
        public abstract String getQueryString();

    }

    /**
     * A sorter for back-end results.
     */
    public static class Sort extends Filter {

        // sort field
        public SortField sortBy = SortField.LIKES;
        // whether or not to reverse the results
        public boolean reverse = false;

        /**
         * Sort back-end results.
         *
         * @param sortBy the field to sort by
         * @param reverse whether or not to reverse the ordering
         */
        public Sort(SortField sortBy, boolean reverse) {
            this.sortBy = sortBy;
            this.reverse = reverse;
        }

        /**
         * Sort back-end results.
         *
         * @param sortBy the field to sort by
         */
        public Sort(SortField sortBy) {
            this.sortBy = sortBy;
        }

        // get the query string
        public String getQueryString() {
            String query = "";
            String field = "";
            switch (sortBy) {
                case COMPLETIONS:
                    field = "completions";
                    break;
                case DOWNLOADS:
                    field = "downloads";
                    break;
                case CREATED_AT:
                    field = "created_at";
                    break;
                case UPDATED_AT:
                    field = "updated_at";
                    break;
                case TITLE:
                    field = "title";
                    break;
                case DESCRIPTION:
                    field = "description";
                    break;
                case RANDOM:
                    field = "random";
                    break;
            }
            query += "sort=" + field;
            query += "&reverse=" + reverse;
            return query;
        }
    }

    /**
     * A search filter for the back-end
     */
    public static class Search extends Filter {

        // the query to search for
        public String query = "";

        /**
         * Filter results given a query to search on.
         *
         * @param query query to search on
         */
        public Search(String query) {
            this.query = query;
        }

        // get the query string
        public String getQueryString() {
            return "query=" + URLEncoder.encode(query);
        }

    }

    /**
     * A pagination filter, to retrieve challenges given an offset and a length.
     */
    public static class Pagination extends Filter {

        // the page to start from
        public int page = 0;
        // items per page
        public int itemsPerPage = 15;

        /**
         * Paginate back-end results.
         *
         * @param page the page to start from
         */
        public Pagination(int page) {
            this.page = page;
        }

        /**
         * Paginate back-end results.
         *
         * @param page the page to start from
         * @param itemsPerPage the number of items per page
         */
        public Pagination(int page, int itemsPerPage) {
            this.page = page;
            this.itemsPerPage = itemsPerPage;
        }

        // retrieve the query string
        public String getQueryString() {
            return "page=" + page + "&itemsPerPage=" + itemsPerPage;
        }

    }

    /**
     * Retrieve editors picks from the back-end.
     */
    public static class EditorsPicks extends Filter {

        // whether or not to only use editors picks
        public boolean enabled = true;

        // retrieve the query string
        public String getQueryString() {
            if (enabled) {
                return "editorsPicks=true";
            }
            return "";
        }

    }

    /**
     * Send a back-end request.
     *
     * @param path path to go for
     * @param method the method to use (POST, GET, ...)
     * @return a response
     */
    public static Response sendRequest(String path, String method) {
        Response result = new Response();
        try {
            URL url = new URL(endpoint + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.connect();
            result.statusCode = connection.getResponseCode();
            result.reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Retrieve a list of challenges.
     *
     * @param filters filters to use
     * @return a list of challenges
     */
    public static List<Challenge> getChallenges(Filter... filters) {
        Gson gson = new Gson();
        Log.d("Connector", "getChallenges");
        String path = "challenge?";
        for (Filter filter: filters) {
            path += filter.getQueryString() + "&";
        }
        Response response = Connector.sendRequest(path, "GET");
        Log.d("Connector", "Response code: " + response.statusCode);
        Type type = new TypeToken<List<Category>>() {}.getType();
        List<Category> list = gson.fromJson(response.reader, type);
        Log.d("Connector", "Result: " + list);
        Log.d("Connector", path);
        return null;
    }

    /**
     * Construct the connector.
     */
    public Connector() {
        Log.d("Connector", "constructor");
        Connector.getChallenges(new Search("just"), new Pagination(0, 1));
    }

    /**
     * Just for testing.
     *
     * @param args
     */
    public static void main(String[] args) {
        new Connector();
    }

}
