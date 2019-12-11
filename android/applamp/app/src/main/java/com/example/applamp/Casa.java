package com.example.applamp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Casa extends AppCompatActivity {
    RecyclerView recyclerView;
    MyAdapter meuAdapter;
    RequestQueue requestQueue;
    Context context;
    List<Luminaria> luminariaList;
    FloatingActionButton adicionar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casa);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        recyclerView = findViewById(R.id.lista);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adicionar = (FloatingActionButton) findViewById(R.id.adicionar);

        adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Casa.this, Wifi.class);
                startActivity(intent);
            }
        });

        luminariaList = new ArrayList<Luminaria>();

        requestQueue = Volley.newRequestQueue(this);
        context = this;
        fetchLamps();
    }


    private void fetchLamps() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "http://" + R.string.server + "/lamps", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    luminariaList.clear();
                    for(int i = 0; i < response.length(); i++) {
                        JSONObject temp = response.getJSONObject(i);
                        Luminaria l = new Luminaria(temp.getString("code"), temp.getString("nickname"));
                        luminariaList.add(l);
                    }
                    meuAdapter = new MyAdapter(context, luminariaList);
                    recyclerView.setAdapter(meuAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", "Bearer " + Util.getToken(context));
                return headers;
            }
        };

        requestQueue.add(jsonArrayRequest);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    fetchLamps();
                    return true;
                case R.id.notificacao:
                    Intent intent = new Intent(Casa.this, Notificacoes.class);
                    startActivity(intent);
                    return true;
                case R.id.conta:
                    Intent intent2 = new Intent(Casa.this, ContaActivity.class);
                    startActivity(intent2);
                    return true;
            }

            return false;
        }
    };
}
