package com.example.applamp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class Apelido extends AppCompatActivity {
    EditText apelido;
    Button ok;
    RequestQueue requestQueue;
    Context context;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apelido);
        Intent intent = getIntent();
        context = this;

        apelido = (EditText) findViewById(R.id.apelido);
        ok = (Button) findViewById(R.id.apelido_ok);

        requestQueue = Volley.newRequestQueue(this);

        code = intent.getStringExtra("code");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLampUpdate();
            }
        });
    }

    private void handleLampUpdate() {
        JSONObject payload = new JSONObject();
        try {
            payload.put("nickname", apelido.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, "http:// "+ R.string.server +"/lamps/" + code, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                new AlertDialog.Builder(context)
                        .setTitle("Sucesso")
                        .setMessage("Luminaria adicionada com sucesso")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Apelido.this, Casa.class);
                                startActivity(intent);
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
