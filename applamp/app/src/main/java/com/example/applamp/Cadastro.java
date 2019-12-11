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
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Cadastro extends AppCompatActivity {
    EditText login;
    EditText senha;
    EditText email;
    EditText telefone;
    RequestQueue requestQueue;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;

        setContentView(R.layout.activity_login);

        final ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.mipmap.coracao);

        login = (EditText) findViewById(R.id.Login);
        senha = (EditText) findViewById(R.id.Senha);
        email = (EditText) findViewById(R.id.Email);
        telefone = (EditText) findViewById(R.id.Telefone);

        requestQueue = Volley.newRequestQueue(this);

        Button cadastrar = (Button) findViewById(R.id.Cadastrar);
        cadastrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              handleSubscribe();
            }
        });

    }

    public void handleSubscribe() {
        JSONObject payload = new JSONObject();
        try {
            payload.put("email", email.getText().toString());
            payload.put("password", senha.getText().toString());
            payload.put("cellphone", telefone.getText().toString());
            payload.put("login", login.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://" + R.string.server + "/users", payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                new AlertDialog.Builder(context)
                        .setTitle("Sucesso")
                        .setMessage("Usuario criado com sucesso, logue na proxima pagina.")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Cadastro.this, Login.class);
                                    startActivity(intent);
                                }
                            }).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);
    };

}
