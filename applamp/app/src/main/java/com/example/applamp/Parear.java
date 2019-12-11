package com.example.applamp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Parear extends AppCompatActivity {
    ImageView parear;
    EditText login;
    Context context;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parear);
        context = this;

        parear = (ImageView) findViewById(R.id.parear_buscar);
        login = (EditText) findViewById(R.id.parear_login);

        requestQueue = Volley.newRequestQueue(this);

        parear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRelationStore();
            }
        });
    }

    private void handleRelationStore() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://" + R.string.server + "/relations/" + login.getText().toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                new AlertDialog.Builder(context)
                        .setTitle("Sucesso")
                        .setMessage("Pedido de pareamento enviado com sucesso.")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Parear.this, Casa.class);
                                startActivity(intent);
                            }
                        }).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject jsonError = new JSONObject(new String(error.networkResponse.data));
                    if(jsonError.getString("error").contains("relationship")) {
                        new AlertDialog.Builder(context)
                                .setTitle("Erro")
                                .setMessage("Voce ja tem uma relacao ativa, deseja apaga-la?")
                                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        handleRelationUpdate();
                                    }
                                }).setNegativeButton("NAO", null).show();
                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle("Erro")
                                .setMessage("Usuario solicitado nao existe.")
                                .setPositiveButton("OK", null).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


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

    private void handleRelationUpdate() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, "http://" + R.string.server + "/relations", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                new AlertDialog.Builder(context)
                        .setTitle("Sucesso")
                        .setMessage("Relaçao deletada, por favor faça o pedido novamente.")
                        .setPositiveButton(R.string.ok, null).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
