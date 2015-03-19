package nl.tue.the30daychallenge.data;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import nl.tue.the30daychallenge.exception.NoServerConnectionException;
import nl.tue.the30daychallenge.exception.RemoteChallengeNotFoundException;

/**
 * An object which is a gateway to the back-end.
 *
 * Created by Daan and Kevin on 3/10/15.
 */
public class RemoteConnector {

    // the endpoint of the back-end
    private static String endpoint = "https://challenge.ovoweb.net/";

    // the device identifier
    private static String deviceID = null;

    private static InputStream certificate = null;

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
    public static class SortFilter extends Filter {

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
        public SortFilter(SortField sortBy, boolean reverse) {
            this.sortBy = sortBy;
            this.reverse = reverse;
        }

        /**
         * Sort back-end results.
         *
         * @param sortBy the field to sort by
         */
        public SortFilter(SortField sortBy) {
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
     * A search filter for the back-end.
     */
    public static class SearchFilter extends Filter {

        // the query to search for
        public String query = "";

        /**
         * Filter results given a query to search on.
         *
         * @param query query to search on
         */
        public SearchFilter(String query) {
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
    public static class PaginationFilter extends Filter {

        // the page to start from
        public int page = 0;
        // items per page
        public int itemsPerPage = 15;

        /**
         * Paginate back-end results.
         *
         * @param page the page to start from
         */
        public PaginationFilter(int page) {
            this.page = page;
        }

        /**
         * Paginate back-end results.
         *
         * @param page the page to start from
         * @param itemsPerPage the number of items per page
         */
        public PaginationFilter(int page, int itemsPerPage) {
            this.page = page;
            this.itemsPerPage = itemsPerPage;
        }

        // retrieve the query string
        public String getQueryString() {
            return "page=" + page + "&itemsPerPage=" + itemsPerPage;
        }

    }

    /**
     * Retrieve challenges from a particular category.
     */
    public static class CategoryFilter extends Filter {

        // category identifier
        private int categoryID = -1;

        /**
         * Filter challenges for a certain category identifier.
         *
         * @param categoryID category identifier
         */
        public CategoryFilter(int categoryID) {
            this.categoryID = categoryID;
        }

        // retrieve the query string
        public String getQueryString() {
            return "categoryID=" + categoryID;
        }

    }

    /**
     * Retrieve editors picks from the back-end.
     */
    public static class EditorsPicksFilter extends Filter {

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

    public static void setCertificate(InputStream cert) {
        RemoteConnector.certificate = cert;
    }

    /**
     * Send a back-end request.
     *
     * @param path path to go for
     * @param method the method to use (POST, GET, ...)
     * @return a response
     */
    public static Response sendRequest(String path, String method) throws NoServerConnectionException {
        Response result = new Response();
        URL url = null;
        try {
            url = new URL(endpoint + path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("Connector", "New request [url=" + (endpoint + path) + "]");
        try {
// Load CAs from an InputStream
// (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
// From https://www.washington.edu/itconnect/security/ca/load-der.crt

            InputStream caInput = new BufferedInputStream(RemoteConnector.certificate);
            Certificate ca = null;
            try {
                ca = cf.generateCertificate(caInput);
                Log.d("Connector", "ca=" + ((X509Certificate) ca).getSubjectDN());
            } catch (CertificateException e) {
                Log.d("Connector", e.toString());
            } finally {
                caInput.close();
            }

// Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

// Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

// Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, tmf.getTrustManagers(), null);

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    HostnameVerifier hv =
                            HttpsURLConnection.getDefaultHostnameVerifier();
                    return hv.verify("www.ovoweb.net", session);
                }
            };

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(context.getSocketFactory());
            connection.setRequestMethod(method);
            connection.setHostnameVerifier(hostnameVerifier);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.connect();
            result.statusCode = connection.getResponseCode();
            result.reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Log.d("Connector", "status code: " + connection.getResponseCode());
        } catch (IOException exception) {
            Log.d("Connector", exception.toString());
            throw new NoServerConnectionException();
        } catch (NoSuchAlgorithmException e) {
            Log.d("Connector", e.toString());
        } catch (CertificateException e) {
            Log.d("Connector", e.toString());
        } catch (KeyStoreException e) {
            Log.d("Connector", e.toString());
        } catch (KeyManagementException e) {
            Log.d("Connector", e.toString());
        }
        return result;
    }

    /**
     * Retrieve a list of challenges.
     *
     * @param filters filters to use
     * @return a list of challenges
     */
    public static List<RemoteChallenge> getChallenges(Filter... filters) throws NoServerConnectionException {
        Gson gson = new Gson();
        Log.d("Connector", "getChallenges");
        String path = "challenge?";
        for (Filter filter: filters) {
            path += filter.getQueryString() + "&";
        }
        Response response = RemoteConnector.sendRequest(path, "GET");
        Log.d("Connector", "Response code: " + response.statusCode);
        Type type = new TypeToken<List<RemoteChallenge>>() {}.getType();
        List<RemoteChallenge> list = gson.fromJson(response.reader, type);
        Log.d("Connector", "Result: " + list);
        return list;
    }

    /**
     * Add a challenge.
     *
     * @param categoryID the ID of the category for the challenge
     * @param title the title of the challenge
     * @param description the description of the challenge
     */
    public static RemoteChallenge addChallenge(int categoryID, String title, String description) throws NoServerConnectionException {
        Gson gson = new Gson();
        Log.d("Connector", "addChallenge");
        Response response = sendRequest("challenge/create?categoryID=" + categoryID + "&title=" + URLEncoder.encode(title) + "&description=" + URLEncoder.encode(description), "GET");
        Type type = new TypeToken<RemoteChallenge>() {}.getType();
        RemoteChallenge remoteChallenge = gson.fromJson(response.reader, type);
        Log.d("Connector", "Result: " + remoteChallenge);
        return remoteChallenge;
    }

    /**
     * Download a challenge (aka create an attempt).
     *
     * @param challengeID the ID of the challenge to download
     */
    public static boolean downloadChallenge(int challengeID) throws NoServerConnectionException {
        Log.d("Connector", "downloadChallenge");
        Response response = sendRequest("download?deviceID=" + getDeviceID() + "&challengeID=" + challengeID, "GET");
        if (response.statusCode == 200) return true;
        return false;
    }

    /**
     * (Un)like a challenge.
     *
     * @param challengeID the ID of the challenge to (un)like
     * @param hasLiked whether or not the challenge was liked (true for like, false for unlike)
     */
    public static boolean likeChallenge(int challengeID, boolean hasLiked) throws NoServerConnectionException {
        Log.d("Connector", "downloadChallenge");
        String hasLikedString = "0";
        if (hasLiked) hasLikedString = "1";
        Response response = sendRequest("like?deviceID=" + getDeviceID() + "&challengeID=" + challengeID + "&hasLiked=" + hasLikedString, "GET");
        if (response.statusCode == 200) return true;
        return false;
    }

    /**
     * Complete a challenge.
     *
     * @param challengeID the ID of the challenge that was completed
     */
    public static boolean completeChallenge(int challengeID) throws NoServerConnectionException {
        Log.d("Connector", "completeChallenge");
        Response response = sendRequest("complete?deviceID=" + getDeviceID() + "&challengeID=" + challengeID, "GET");
        if (response.statusCode == 200) return true;
        return false;
    }

    public static String getDeviceID() {
        return deviceID;
    }

    public static void setDeviceID(String deviceID) {
        RemoteConnector.deviceID = deviceID;
        Log.d("Connector", "setDeviceID [deviceID=" + deviceID + "]");
    }

    public static RemoteChallenge getChallenge(int challengeID) throws RemoteChallengeNotFoundException, NoServerConnectionException {
        Gson gson = new Gson();
        Response response = sendRequest("get-challenge?challengeID=" + challengeID, "GET");
        if (response.statusCode != 200) throw new RemoteChallengeNotFoundException();
        Type type = new TypeToken<RemoteChallenge>() {}.getType();
        return gson.fromJson(response.reader, type);
    }

    /**
     * Retrieve a list of categories.
     *
     * @return a list of categories
     */
    public static List<Category> getCategories() throws NoServerConnectionException {
        Gson gson = new Gson();
        Log.d("Connector", "getChallenges");
        String path = "category?";
        Response response = RemoteConnector.sendRequest(path, "GET");
        Log.d("Connector", "Response code: " + response.statusCode);
        Type type = new TypeToken<List<Category>>() {}.getType();
        List<Category> list = gson.fromJson(response.reader, type);
        Log.d("Connector", "Result: " + list);
        return list;
    }

    /**
     * Construct the connector.
     */
    public RemoteConnector(String deviceID) {
        //Log.d("Connector", "constructor");
        //Connector.getChallenges(new SearchFilter("just"));
        //Connector.setDeviceID(deviceID);
        //Connector.downloadChallenge(14);
        //Connector.likeChallenge(14, true);
        //Connector.completeChallenge(14);*/
    }

}
