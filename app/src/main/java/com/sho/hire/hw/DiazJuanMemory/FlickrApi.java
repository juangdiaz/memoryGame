package com.sho.hire.hw.DiazJuanMemory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author juandiaz <juandiaz@us.univision.com> Android Developer
 *         Copyright (C) 2016, Univision Communications Inc.
 *
 *         takes care of retriving the data from the APIs and storing them into the model container,
 *         Image download via Bitmap
 */
class FlickrApi {

    // String to create Flickr API urls
    private static final String FLICKR_BASE_URL = "https://api.flickr.com/services/rest/?method=";
    private static final String FLICKR_PHOTOS_SEARCH_STRING = "flickr.photos.search";
    private static final int NUMBER_OF_PHOTOS = 8;

    //You can set here your API_KEY
    private static final String APIKEY_SEARCH_STRING = "5423dbab63f23a62ca4a986e7cbb35e2";

    private static final String TAGS_STRING = "&tags=";
    private static final String FORMAT_STRING = "&format=json";

    public static MainActivity.RequestHandler uihandler;


    static String createURL(String parameter) {

        return FLICKR_BASE_URL +
                FLICKR_PHOTOS_SEARCH_STRING +
                "&api_key=" + APIKEY_SEARCH_STRING +
                TAGS_STRING +
                parameter +
                FORMAT_STRING +
                "&per_page=" + NUMBER_OF_PHOTOS +
                "&media=photos";
    }


    public static ArrayList<Image> imageSearchByTag(MainActivity.RequestHandler uih, Context ctx, String tag) {
        uihandler = uih;
        String url = FlickrApi.createURL(tag);
        ArrayList<Image> tmp = new ArrayList<>();
        String jsonString = null;
        try {
            if (URLConnector.isOnline(ctx)) {
                ByteArrayOutputStream baos = URLConnector.readBytes(url);
                jsonString = baos.toString();
            }
            try {
                JSONObject root = new JSONObject(jsonString.replace("jsonFlickrApi(", "").replace(")", ""));
                JSONObject photos = root.getJSONObject("photos");
                JSONArray imageJSONArray = photos.getJSONArray("photo");
                for (int i = 0; i < imageJSONArray.length(); i++) {
                    JSONObject item = imageJSONArray.getJSONObject(i);
                    Image imgCon = new Image(Long.parseLong(item.getString("id")), item.getString("secret"), item.getString("server"),
                            item.getString("farm"));
                    imgCon.position = i;
                    tmp.add(imgCon);
                }
                Message msg = Message.obtain(uih, MainActivity.RequestHandler.ID_METADATA_DOWNLOADED);
                msg.obj = tmp;
                uih.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException nue) {
            nue.printStackTrace();
        }

        return tmp;
    }


    static Bitmap getImageBitmap(Image imgCon) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bitmap bm = null;
        try {
            URL url = new URL(imgCon.getImgUrl());
            bm = BitmapFactory.decodeStream((InputStream)url.getContent());
        } catch (IOException e) {
            Log.e("FlickrAPI", e.getMessage());
        }
        return bm;
    }

}
