package com.snowcat.fajnyobed.io;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
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
 * Created by AntikAdmin on 25. 9. 2014.
 */
public class EncryptedClient {
    private static final String LOG_TAG = "com.antik.tv.base";

    //DefaultHttpClient httpClient;

    String password;
    MessageDigest digest;
    SecretKeySpec key;

    public EncryptedClient(String password) {
        this.password = password;
        digest = null;
        try {
            digest = MessageDigest.getInstance("SHA256");
        } catch (Exception e) {
            Log.e(LOG_TAG, "algorithm missing", e);
        }
        byte[] key = digest.digest(password.getBytes());
        /*try {
            key = password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        //Log.e("Kluc", password);
        this.key = new SecretKeySpec(key, "AES");
        //Log.e("kluc",HexHelper.toString(key));

        //HttpParams httpParameters = new BasicHttpParams();
// Set the timeout in milliseconds until a connection is established.
// The default value is zero, that means the timeout is not used.
        //HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        //HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        //httpClient = new DefaultHttpClient(httpParameters);
    }


    private SecretKeySpec getKey() {
        return key;
    }

    private InputStream getDataInputStream(InputStream stream) {
        byte[] iv = new byte[16];
        try {
            stream.read(iv, 0, 16);
            Log.e("IV", HexHelper.toString(iv));


            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, getKey(), new IvParameterSpec(iv));

            CipherInputStream gis = new CipherInputStream(stream, cipher);
            return new InflaterInputStream(gis);
        } catch (Exception e) {
            Log.e(LOG_TAG, "request error", e);
        }
        return null;
    }

    public InputStream execute(Request r) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {


        //r.httppost = new HttpPost(r.uri);

        byte[] iv = new byte[16];
                /*String string = "4297bfa44cc40ef1a6988a8115f7d82f";
                iv = HexHelper.toByteArray(string);*/
        //Log.e("IV dlzka", "" + iv.length);
        //iv = string.getBytes("UTF-8");

        URL url = new URL(r.uri);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        BufferedInputStream returnInputStream;
        urlConnection.setDoOutput(true);
        new Random().nextBytes(iv);
        BufferedOutputStream byteArrayOutputStream = new BufferedOutputStream(urlConnection.getOutputStream());
        byteArrayOutputStream.write(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Log.e("SHA256_Encrypted", getKey().toString());
        cipher.init(Cipher.ENCRYPT_MODE, getKey(), new IvParameterSpec(iv));
        OutputStream outputStream = new CipherOutputStream(byteArrayOutputStream, cipher);
        DeflaterOutputStream compressedStream = new DeflaterOutputStream(outputStream);
        compressedStream.write(r.request.getBytes());
        compressedStream.close();
        returnInputStream = new BufferedInputStream(getDataInputStream(urlConnection.getInputStream()));
        //urlConnection.disconnect();

        //byte[] data = byteArrayOutputStream.toByteArray();

        //r.httppost.setEntity(new ByteArrayEntity(data));

                /*if (r.gzipData) {
                    ByteArrayInputStream test = new ByteArrayInputStream(data);
                    InputStream s = getDataInputStream(test);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(s));
                    Log.v("DATA: ", reader.readLine() + String.valueOf(data[16]));
                }*/

        //HttpResponse httpResponse = httpClient.execute(r.httppost);
        //Log.e("httpResponse",httpResponse.getEntity().getContent().toString());
        return returnInputStream;
    }

    private int writeShort(OutputStream out, int i) throws IOException {
        out.write(i & 0xFF);
        out.write((i >> 8) & 0xFF);
        return i;
    }

    private long writeLong(OutputStream out, long i) throws IOException {
        // Write out the long value as an unsigned int
        int unsigned = (int) i;
        out.write(unsigned & 0xFF);
        out.write((unsigned >> 8) & 0xFF);
        out.write((unsigned >> 16) & 0xFF);
        out.write((unsigned >> 24) & 0xFF);
        return i;
    }

    public static class Request {
        String uri;
        String request;
        boolean gzipData;
        //HttpPost httppost;

        public Request(String uri, String request, boolean gzipData) {
            this.uri = uri;
            this.request = request;
            this.gzipData = gzipData;
        }


    }
}
