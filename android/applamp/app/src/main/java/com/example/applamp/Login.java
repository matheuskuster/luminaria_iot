package com.example.applamp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    ImageView icon;
    Button logar;
    EditText email;
    EditText senha;
    RequestQueue requestQueue;
    Context context;
    TextView cadastre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;

        if(Util.isLogged(context)) {
            Intent intent = new Intent(Login.this, Casa.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_login2);

        this.icon = (ImageView) findViewById(R.id.icon);
        this.icon.setImageResource(R.mipmap.coracao);

        logar = (Button) findViewById(R.id.button_logar);
        this.email = (EditText) findViewById(R.id.login_email);
        this.senha = (EditText) findViewById(R.id.login_senha);
        this.cadastre = (TextView) findViewById(R.id.cadastre);

        requestQueue = Volley.newRequestQueue(this);

        logar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        this.cadastre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Cadastro.class);
                startActivity(intent);
            }
        });
    }

    public void handleLogin() {
        JSONObject payload = new JSONObject();
        try {
            payload.put("email", email.getText().toString());
            payload.put("password", senha.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://" + R.string.server + "/sessions", payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Util.setLogin(context, true, response.getString("token"));
                    Intent intent = new Intent(Login.this, Casa.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new AlertDialog.Builder(context)
                        .setTitle("Erro")
                        .setMessage("Usuario e/ou senha incorretos. Tente novamente!")
                        .setPositiveButton(R.string.ok, null).show();

            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}
