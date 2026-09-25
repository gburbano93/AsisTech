package com.example.asistech;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtCorreo;
    EditText edtContrasena;
    Button btnIniciarSesion;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtCorreo = findViewById(R.id.edtCorreo);
        edtContrasena = findViewById(R.id.edtContrasena);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {

        String correo = edtCorreo.getText().toString().trim();
        String contrasena = edtContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this,
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            JSONObject datos = new JSONObject();
            datos.put("correo", correo);
            datos.put("contrasena", contrasena);

            RequestBody body = RequestBody.create(
                    datos.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url("http://2.25.188.174:3000/login")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this,
                                    "Error de conexión",
                                    Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response)
                        throws IOException {

                    String respuesta = response.body().string();

                    runOnUiThread(() -> {

                        if (response.isSuccessful()) {

                            Toast.makeText(LoginActivity.this,
                                    "Inicio de sesión correcto",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(
                                    LoginActivity.this,
                                    MainActivity.class
                            );

                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(LoginActivity.this,
                                    "Correo o contraseña incorrectos",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}