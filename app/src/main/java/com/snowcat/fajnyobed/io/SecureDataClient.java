package com.snowcat.fajnyobed.io;


import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


/**
 * Created by AntikAdmin on 25. 9. 2014.
 */
public class SecureDataClient implements IDataClient {
    private static final String LOG_TAG = "com.antik.tv.base";
    //private final String username;
    private final String password;
    //private final String usernameHash;
    //private final String passwordHash;
    public String apiUrl;
    String apiLocation;

    public SecureDataClient(/*String username, String password, String apiLocation*/) {
        //this.username = username;
        //this.password = password;
        //this.usernameHash = MD5.getHashString(username);
        //this.password = MD5.getHashString(password);
        this.password = SHA_256.getHashString("VFZN!7y5yiu#2&c0WBgUFajnyObedofOqtA4W%HO1snf+TLtw");//password;
        //this.usernameHash = SHA_256.getHashString(username);
        //Log.v("LOG - PASS", this.password);
        //Log.v("LOG - USER", this.usernameHash);
        setApiLocation("http://api.fajnyobed.sk");
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = "";

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            is.close();

            return sb.toString();
        } catch (Exception en) {
            return "";
        }

    }

    public String getApiLocation() {
        return apiLocation;
    }

    public void setApiLocation(String apiLocation) {
        this.apiLocation = apiLocation;
        this.apiUrl = this.apiLocation;
    }

    @Override
    public IDataClient.Retriever createRetriever() {
        return new Retriever();
    }

    private class Retriever implements IDataClient.Retriever {

        private EncryptedClient.Request request;

        @Override
        public String execute(JSONObject request) throws Exception {
            String response = null;

            EncryptedClient client = new EncryptedClient(password);


            Log.e("Request", request.toString());
            this.request = new EncryptedClient.Request(apiUrl, request.toString(), false);
            response = convertStreamToString(client.execute(this.request));
            return response;
        }

        @Override
        public JSONObject executeJSON(JSONObject request) {
            JSONObject response = null;
            try {
                EncryptedClient client = new EncryptedClient(password);

                this.request = new EncryptedClient.Request(apiUrl, request.toString(), false);
                String tmp_json = convertStreamToString(client.execute(this.request));
                if (tmp_json != "") {
                    response = new JSONObject(tmp_json);
                    String error = response.optString("error");
                    if (error != null && !error.equals("")) {
                        Log.e(LOG_TAG, "ERROR: " + error + ", request: " + request + " response: " + response.toString(4));
                        return null;
                    }
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "request error", e);
            }
            return response;
        }

        @Override
        public void cancel() {

        }
    }
}
