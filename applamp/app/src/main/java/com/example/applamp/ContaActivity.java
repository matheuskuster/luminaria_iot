package com.example.applamp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContaActivity extends AppCompatActivity {
    String avatarUrl;

    RequestQueue requestQueue;
    ImageLoader mImageLoader;
    Context context;

    NetworkImageView avatarImage;

    EditText login;
    EditText email;
    EditText telefone;
    Button sair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);
        context = this;
        requestQueue = Volley.newRequestQueue(context);

        avatarImage = (NetworkImageView) findViewById(R.id.avatar);

        mImageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

        login = (EditText) findViewById(R.id.conta_login);
        email = (EditText) findViewById(R.id.conta_email);
        telefone = (EditText) findViewById(R.id.conta_telefone);
        sair = (Button) findViewById(R.id.sair);



        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.setLogin(context, false, "");
                Intent intent = new Intent(ContaActivity.this, Login.class);
                startActivity(intent);
            }
        });


        fetchProfile();
    }

    private void fetchProfile() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://" + R.string.server + "/users", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    login.setText(response.getString("login"));
                    email.setText(response.getString("email"));
                    telefone.setText(response.getString("cellphone"));
                    JSONObject avatar = response.getJSONObject("avatar");
                    avatarUrl = avatar.getString("url");
                    avatarImage.setImageUrl(avatarUrl, mImageLoader);
                } catch (JSONException e) {
                    avatarImage.setImageUrl("https://cdn2.iconfinder.com/data/icons/facebook-51/32/FACEBOOK_LINE-01-512.png", mImageLoader);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", "Bearer " + Util.getToken(context));
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}
