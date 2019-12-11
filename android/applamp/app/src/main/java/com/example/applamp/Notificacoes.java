package com.example.applamp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notificacoes extends AppCompatActivity {
    List<NotificacaoObject> notificacoes;
    MyAdapterNotificacoes meuAdapter;
    Context context;
    RecyclerView recyclerView;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);
        context = this;

        requestQueue = Volley.newRequestQueue(this);
        recyclerView = (RecyclerView) findViewById(R.id.notificacoes_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificacoes = new ArrayList<NotificacaoObject>();

        fetchNotifications();
    }

    public void fetchNotifications() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "http://" + R.string.server + "/notifications", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    notificacoes.clear();
                    for(int i = 0; i < response.length(); i++) {
                        JSONObject temp = response.getJSONObject(i);
                        NotificacaoObject n = new NotificacaoObject(temp.getString("text"), temp.getBoolean("to_accept"), temp.getBoolean("accepted"), temp.getInt("id"));
                        notificacoes.add(n);
                    }
                    meuAdapter = new MyAdapterNotificacoes(context, notificacoes);
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

    public void acceptRelation(final NotificacaoObject notificacao) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("notification", notificacao.id);
            payload.put("login", notificacao.text.split(" ")[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, "http://" + R.string.server + "/relations", payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                new AlertDialog.Builder(context)
                        .setTitle("Sucesso")
                        .setMessage("Agora voce esta pareado com " + notificacao.text.split(" ")[0] + ". Por favor, reinicie sua luminaria.")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                fetchNotifications();
                            }
                        }).show();
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
