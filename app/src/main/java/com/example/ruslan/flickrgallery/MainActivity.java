package com.example.ruslan.flickrgallery;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends Activity {

    private final static String TAG = "MainActivity";
    private ListView listView = null;
    private RequestQueue requestQueue = null;
    private ImageLoader imageLoader = null;
    private LruCache<String, Bitmap> lruCache = null;
    private ImageAdapter adapter = null;





    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(TAG, "onCreate");
        verifyKey();
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.main_listview);
        initVolley();
    }




    private void verifyKey() {
        if (TextUtils.isEmpty(Config.FLICKR_KEY))
            throw new IllegalArgumentException("Please set flickr key");
    }

    private void initVolley() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        lruCache = new LruCache<String, Bitmap>(Config.MEMORY_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        imageLoader = new ImageLoader(requestQueue, new ImageCache() {
            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                lruCache.put(url, bitmap);
            }

            @Override
            public Bitmap getBitmap(String url) {
                return lruCache.get(url);
            }
        });
        String api = String.format(Config.API_URL, Config.FETCH_IMAGE, Config.FLICKR_KEY);
        requestQueue.add(new StringRequest(api, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Logger.d(TAG, response);
                    JSONObject json = new JSONObject(response);
                    JSONObject photosJson = json.getJSONObject("photos");
                    adapter = new ImageAdapter(MainActivity.this, imageLoader, photosJson.getJSONArray("photo"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listView.setAdapter(adapter);
            }
        }, null));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.i(TAG, "onStop");
        requestQueue.cancelAll(this);
    }

    @Override
    protected void onResume() {
        super.onResume();


        Logger.i(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.i(TAG, "onDestory");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.i(TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.i(TAG, "onRestart");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Logger.i(TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Logger.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.i(TAG, "onStart");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
}
