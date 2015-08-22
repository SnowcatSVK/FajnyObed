package com.snowcat.fajnyobed.Logic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.snowcat.fajnyobed.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Snowcat on 26.7.2015.
 */
public class Restaurant {

    public int id;
    public String name;
    public String description;
    public String street;
    public int cityId;
    public String city;
    public int rating;
    public ArrayList<String> openHours;
    public ArrayList<String> promoPhotos;

}
