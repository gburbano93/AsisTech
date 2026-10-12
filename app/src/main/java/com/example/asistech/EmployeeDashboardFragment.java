package com.example.asistech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.os.Bundle;
import org.json.JSONObject;

import java.io.IOException;

public class EmployeeDashboardFragment extends Fragment {

    public EmployeeDashboardFragment() {
        // Constructor vacío requerido
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
                                                 ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_employee_dashboard, container, false);
        consultarEstadoLaboral();
        // Mostrar el nombre del empleado que inició sesión
        TextView txtNombreEmpleado = view.findViewById(R.id.txtNombreEmpleado);

        Bundle datos = getArguments();

        if (datos != null) {
            String nombre = datos.getString("nombre", "");

            if (nombre != null && !nombre.trim().isEmpty()) {
                txtNombreEmpleado.setText(nombre);
            } else {
                txtNombreEmpleado.setText("Empleado");
            }
        } else {
            txtNombreEmpleado.setText("Empleado");
        }

        // Mostrar la fecha actual en español
        TextView txtFechaEmpleado = view.findViewById(R.id.txtFechaEmpleado);

        SimpleDateFormat formatoFecha = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "CO"));

        String fechaActual = formatoFecha.format(new Date());

        TextView txtHoraEmpleado = view.findViewById(R.id.txtHoraEmpleado);

        Handler handler = new Handler();
        Runnable actualizarHora = new Runnable() {
            @Override public void run() {
                SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                String horaActual = formatoHora.format(new Date());
                txtHoraEmpleado.setText(horaActual);

                handler.postDelayed(this, 1000);
            }
        };

        handler.post(actualizarHora);

        // Primera letra en mayúscula
        fechaActual = fechaActual.substring(0, 1)
                                 .toUpperCase(new Locale("es", "CO")) + fechaActual.substring(1);

        txtFechaEmpleado.setText(fechaActual);

        // Marcar Entrada
        View btnMarcarEntrada = view.findViewById(R.id.btnMarcarEntrada);

        btnMarcarEntrada.setOnClickListener(v -> marcarEntrada());

        // Marcar Salida
        View btnMarcarSalida = view.findViewById(R.id.btnMarcarSalida);

        btnMarcarSalida.setOnClickListener(v -> marcarSalida());

        // Perfil
        View btnPerfilEmpleado = view.findViewById(R.id.btnPerfilEmpleado);

        btnPerfilEmpleado.setOnClickListener(v -> {

            PerfilFragment perfilFragment = new PerfilFragment();

            requireActivity().getSupportFragmentManager().beginTransaction()
                             .replace(R.id.perfil_container, perfilFragment).commit();

            requireActivity().findViewById(R.id.perfil_container).setVisibility(View.VISIBLE);
        });

        // Registrar Horas Extras
        View btnHorasExtraEmpleado = view.findViewById(R.id.btnHorasExtraEmpleado);

        btnHorasExtraEmpleado.setOnClickListener(v -> {
            RegistrarHorasExtraFragment fragment = new RegistrarHorasExtraFragment();

            Bundle argumentosUsuario = getArguments();

            if (argumentosUsuario != null) {
                fragment.setArguments(new Bundle(argumentosUsuario));
            }

            getParentFragmentManager().beginTransaction()
                                      .replace(R.id.content_container, fragment)
                                      .addToBackStack(null)
                                      .commit();
        });

        // Mi Historial
        View btnHistorialEmpleado = view.findViewById(R.id.btnHistorialEmpleado);

        btnHistorialEmpleado.setOnClickListener(v -> {
            HistorialEmpleadoFragment fragment = new HistorialEmpleadoFragment();

            Bundle argumentosUsuario = getArguments();

            if (argumentosUsuario != null) {
                fragment.setArguments(new Bundle(argumentosUsuario));
            }

            getParentFragmentManager().beginTransaction()
                                      .replace(R.id.content_container, fragment)
                                      .addToBackStack(null)
                                      .commit();
        });

        return view;
    }


    private void marcarEntrada() {
        Bundle datos = getArguments();

        int usuarioId = datos != null
                ? datos.getInt("usuario_id", -1)
                : -1;

        if (usuarioId == -1) {
            Toast.makeText(requireContext(),
                           "No se encontró el ID del usuario",
                           Toast.LENGTH_LONG).show();
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("usuario_id", usuarioId);

            MediaType mediaType =
                    MediaType.parse("application/json; charset=utf-8");

            RequestBody body =
                    RequestBody.create(json.toString(), mediaType);

            Request request = new Request.Builder()
                    .url("http://2.25.188.174:3000/asistencias/entrada")
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    if (!isAdded()) return;

                    requireActivity().runOnUiThread(() ->
                                                            Toast.makeText(requireContext(),
                                                                           "Error de conexión al registrar entrada",
                                                                           Toast.LENGTH_LONG).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response)
                        throws IOException {

                    String respuesta = response.body() != null
                            ? response.body().string()
                            : "";

                    boolean correcto = response.isSuccessful();
                    int codigoHttp = response.code();
                    response.close();

                    if (!isAdded()) return;

                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded()) return;

                        try {
                            JSONObject resultado =
                                    new JSONObject(respuesta);

                            String mensaje = resultado.optString(
                                    "mensaje",
                                    correcto
                                            ? "Entrada registrada correctamente"
                                            : "No se pudo registrar la entrada"
                            );

                            android.util.Log.d(
                                    "ASISTENCIA_ENTRADA",
                                    "Código HTTP: " + codigoHttp
                                            + " | Respuesta: " + respuesta
                            );

                            Toast.makeText(requireContext(),
                                           mensaje,
                                           Toast.LENGTH_LONG).show();

                            // Actualizar el indicador si la entrada fue exitosa
                            if (correcto) {
                                consultarEstadoLaboral();
                            }

                        } catch (Exception e) {
                            android.util.Log.e(
                                    "ASISTENCIA_ENTRADA",
                                    "Error procesando respuesta", e
                            );

                            Toast.makeText(requireContext(),
                                           correcto
                                                   ? "Entrada registrada correctamente"
                                                   : "Error al registrar la entrada",
                                           Toast.LENGTH_LONG).show();

                            if (correcto) {
                                consultarEstadoLaboral();
                            }
                        }
                    });
                }
            });

        } catch (Exception e) {
            android.util.Log.e(
                    "ASISTENCIA_ENTRADA",
                    "Error al preparar el registro", e
            );

            Toast.makeText(requireContext(),
                           "Error al preparar el registro",
                           Toast.LENGTH_LONG).show();
        }
    }



    private void marcarSalida() {
        Bundle datos = getArguments();

        int usuarioId = datos != null
                ? datos.getInt("usuario_id", -1)
                : -1;

        if (usuarioId == -1) {
            Toast.makeText(requireContext(),
                           "No se encontró el ID del usuario",
                           Toast.LENGTH_LONG).show();
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("usuario_id", usuarioId);

            MediaType mediaType =
                    MediaType.parse("application/json; charset=utf-8");

            RequestBody body =
                    RequestBody.create(json.toString(), mediaType);

            Request request = new Request.Builder()
                    .url("http://2.25.188.174:3000/asistencias/salida")
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    if (!isAdded()) return;

                    requireActivity().runOnUiThread(() ->
                                                            Toast.makeText(requireContext(),
                                                                           "Error de conexión al registrar salida",
                                                                           Toast.LENGTH_LONG).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response)
                        throws IOException {

                    String respuesta = response.body() != null
                            ? response.body().string()
                            : "";

                    boolean correcto = response.isSuccessful();
                    int codigoHttp = response.code();
                    response.close();

                    if (!isAdded()) return;

                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded()) return;

                        try {
                            JSONObject resultado =
                                    new JSONObject(respuesta);

                            String mensaje = resultado.optString(
                                    "mensaje",
                                    correcto
                                            ? "Salida registrada correctamente"
                                            : "No se pudo registrar la salida"
                            );

                            android.util.Log.d(
                                    "ASISTENCIA_SALIDA",
                                    "Código HTTP: " + codigoHttp
                                            + " | Respuesta: " + respuesta
                            );

                            Toast.makeText(requireContext(),
                                           mensaje,
                                           Toast.LENGTH_LONG).show();

                            // Consultar el estado actualizado en el servidor
                            if (correcto) {
                                consultarEstadoLaboral();
                            }

                        } catch (Exception e) {
                            android.util.Log.e(
                                    "ASISTENCIA_SALIDA",
                                    "Error procesando respuesta", e
                            );

                            Toast.makeText(requireContext(),
                                           correcto
                                                   ? "Salida registrada correctamente"
                                                   : "Error al registrar la salida",
                                           Toast.LENGTH_LONG).show();

                            if (correcto) {
                                consultarEstadoLaboral();
                            }
                        }
                    });
                }
            });

        } catch (Exception e) {
            android.util.Log.e(
                    "ASISTENCIA_SALIDA",
                    "Error al preparar el registro", e
            );

            Toast.makeText(requireContext(),
                           "Error al preparar el registro de salida",
                           Toast.LENGTH_LONG).show();
        }
    }


    private void consultarEstadoLaboral() {
        Bundle datos = getArguments();
        int usuarioId = datos != null ? datos.getInt("usuario_id", -1) : -1;

        if (usuarioId == -1 || !isAdded()) {
            return;
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("http://2.25.188.174:3000/asistencias/estado/" + usuarioId)
                                               .get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                android.util.Log.e("ESTADO_LABORAL", "Error al consultar estado", e);
            }

            @Override public void onResponse(Call call, Response response) throws IOException {

                String respuesta = response.body() != null ? response.body().string() : "";

                boolean correcto = response.isSuccessful();
                response.close();

                if (!correcto || !isAdded()) {
                    return;
                }

                try {
                    JSONObject json = new JSONObject(respuesta);
                    boolean laborando = json.optBoolean("laborando", false);

                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded()) {
                            return;
                        }

                        TextView txtEstado = requireView().findViewById(R.id.txtEstadoLaboral);

                        TextView txtDescripcion = requireView().findViewById(R.id.txtDescripcionEstadoLaboral);

                        View indicador = requireView().findViewById(R.id.indicadorEstadoLaboral);

                        txtEstado.setText(laborando ? "Laborando" : "No Laborando");

                        txtDescripcion.setText(laborando ? "Tu jornada está en curso" : "No tienes una jornada activa");

                        int color = android.graphics.Color.parseColor(laborando ? "#24B47E" : "#94A3B8");

                        indicador.setBackgroundColor(color);
                    });

                } catch (Exception e) {
                    android.util.Log.e("ESTADO_LABORAL", "Error procesando respuesta", e);
                }
            }
        });
    }
}
