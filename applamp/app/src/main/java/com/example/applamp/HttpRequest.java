package com.example.applamp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpRequest {

    private String requestUrl;
    private String charset;


    HashMap<String, String> params = new HashMap<>();
    HashMap<String, File> files = new HashMap<>();


    private String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private OutputStream outputStream;
    private OutputStreamWriter writer;


    // Construtor da classe HttpRequest
    // requestUrl -> o endereco a ser conectado
    // method -> metodo HTTP a ser usado. Pode ser GET ou POST
    // charset -> codificacao de caracteres usada na troca de dados. Geralmente UTF-8
    public HttpRequest( String requestUrl, String charset) {
        this.requestUrl = requestUrl;
        this.charset = charset;
    }

    public InputStream makePostRequest(String payload, String token) throws IOException {
        URL url = new URL(this.requestUrl);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        String line;
        StringBuffer jsonString = new StringBuffer();

        if(token != "") { uc.setRequestProperty("Authorization", "Bearer" + token); }

        uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        uc.setRequestMethod("POST");
        uc.setDoInput(true);
        uc.setInstanceFollowRedirects(false);
        uc.connect();
        OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), this.charset);
        writer.write(payload);
        writer.close();

        return uc.getInputStream();
    }
}