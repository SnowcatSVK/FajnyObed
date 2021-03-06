package com.snowcat.fajnyobed.io;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Snowcat on 23-Aug-15.
 */
public class RequestHandler {

    private URL apiUrl;
    private HttpURLConnection urlConnection;
    private SecretKeySpec key;


    public RequestHandler(String url, String password) {
        try {
            apiUrl = new URL(url);
            Log.e("SHA256_Handler", password);
            MessageDigest digest = MessageDigest.getInstance("SHA256");
            byte[] key = digest.digest(password.getBytes());
            this.key = new SecretKeySpec(key, "AES");
            Log.e("SHA256_Handler", getKey().toString());
        } catch (MalformedURLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public SecretKeySpec getKey() {
        return key;
    }

    public JSONObject handleRequest(String function, String param, String param2) throws IOException {
        JSONObject returnJSON;
        try {
            urlConnection = (HttpURLConnection) apiUrl.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoInput(true);
            byte[] iv = new byte[16];
            new Random().nextBytes(iv);
            BufferedOutputStream byteArrayOutputStream = new BufferedOutputStream(urlConnection.getOutputStream());
            byteArrayOutputStream.write(iv);
            Log.e("SHA256_Handler", getKey().toString());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), new IvParameterSpec(iv));
            OutputStream outputStream = new CipherOutputStream(byteArrayOutputStream, cipher);
            DeflaterOutputStream compressedStream = new DeflaterOutputStream(outputStream);
            String requestJSON = parseJSON(function, param, param2);
            Log.e("Request", requestJSON);
            compressedStream.write(requestJSON.getBytes());
            compressedStream.flush();
            compressedStream.close();
            JSONObject jsonObject = new JSONObject(convertStreamToString(getDataInputStream(urlConnection.getInputStream())));
            Log.e("response_handler", jsonObject.toString());
            return jsonObject;

        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String parseJSON(String function, String param, String param2) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("function", function);
            if (param != null) {
                switch (function) {
                    case "GetRestaurantDetail":
                        jsonObject.put("restaurant_id", param);
                        break;
                    case "GetRestaurantListByCity":
                        jsonObject.put("city_id", param);
                        break;
                    case "GetMenu":
                        jsonObject.put("restaurant_id", param);
                        break;
                    case "GetFavoriteRestaurant":
                        jsonObject.put("device_id", param);
                        break;
                    case "AddFavoriteRestaurant":
                        jsonObject.put("restaurant_id", param);
                        jsonObject.put("device_id", param2);
                        break;
                    case "DeleteFavoriteRestaurant":
                        jsonObject.put("restaurant_id", param);
                        jsonObject.put("device_id", param2);
                        break;
                }
                //{"function":"AddFavoriteRestaurant","restaurant_id":"2853","device_id":"AF8D65A89FSD59A85F689FAS"}
                //{"function":"GetFavoriteRestaurant","device_id":"AF8D65A89FSD59A85F689FAS"}

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Request",jsonObject.toString());
        return jsonObject.toString();
    }

    public String convertStreamToString(InputStream is) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = "";

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            is.close();
            urlConnection.disconnect();

            return sb.toString();
        } catch (Exception en) {
            return "";
        }

    }

    private InputStream getDataInputStream(InputStream stream) throws IOException {
        byte[] iv = new byte[16];
        try {
            stream.read(iv, 0, 16);
            Log.e("IV", HexHelper.toString(iv));


            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, getKey(), new IvParameterSpec(iv));

            CipherInputStream gis = new CipherInputStream(stream, cipher);
            return new InflaterInputStream(gis);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
