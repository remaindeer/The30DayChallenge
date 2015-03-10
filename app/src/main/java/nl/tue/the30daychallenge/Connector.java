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
 * Created by kevin on 3/10/15.
 */
public class Connector {

    private static String endpoint = "http://challenge.ovoweb.net/";
    public static enum SortField {
        LIKES, COMPLETIONS, DOWNLOADS, CREATED_AT, UPDATED_AT, RANDOM, TITLE, DESCRIPTION
    }

    public static class Response {
        public int statusCode = -1;
        public BufferedReader reader = null;
    }

    public abstract static class Filter {
        public abstract String getQueryString();
    }
    public static class Sort extends Filter {
        public SortField sortBy = SortField.LIKES;
        public boolean reverse = false;

        public Sort(SortField sortBy, boolean reverse) {
            this.sortBy = sortBy;
            this.reverse = reverse;
        }

        public Sort(SortField sortBy) {
            this.sortBy = sortBy;
        }

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
    public static class Search extends Filter {
        public String query = "";

        public Search(String query) {
            this.query = query;
        }

        public String getQueryString() {
            return "query=" + URLEncoder.encode(query);
        }
    }
    public static class Pagination extends Filter {
        public int page = 0;
        public int itemsPerPage = 15;

        public Pagination(int page) {
            this.page = page;
        }

        public Pagination(int page, int itemsPerPage) {
            this.page = page;
            this.itemsPerPage = itemsPerPage;
        }

        public String getQueryString() {
            return "page=" + page + "&itemsPerPage=" + itemsPerPage;
        }
    }
    public static class EditorsPicks extends Filter {
        public boolean enabled = true;

        public String getQueryString() {
            if (enabled) {
                return "editorsPicks=true";
            }
            return "";
        }
    }

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

    public Connector() {
        Log.d("Connector", "constructor");
        Connector.getChallenges(new Search("just"));
    }

    public static void main(String[] args) {
        new Connector();
    }

}
