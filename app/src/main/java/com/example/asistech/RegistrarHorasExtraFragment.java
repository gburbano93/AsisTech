package com.example.asistech;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Locale;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegistrarHorasExtraFragment extends Fragment {

    public RegistrarHorasExtraFragment() {
        // Constructor vacío requerido
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
                                                 ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registrar_horas_extra, container, false);

        // Campo de fecha
        EditText edtFechaHoraExtra = view.findViewById(R.id.edtFechaHoraExtra);

        edtFechaHoraExtra.setFocusable(false);
        edtFechaHoraExtra.setClickable(true);

        edtFechaHoraExtra.setOnClickListener(v -> {

            Calendar calendario = Calendar.getInstance();

            int anio = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog selectorFecha = new DatePickerDialog(requireContext(), (datePicker, anioSeleccionado, mesSeleccionado, diaSeleccionado) -> {

                String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", anioSeleccionado, mesSeleccionado + 1, diaSeleccionado);

                edtFechaHoraExtra.setText(fecha);
            }, anio, mes, dia);

            // Bloquear hoy y las fechas futuras
            Calendar limite = Calendar.getInstance();

            limite.set(Calendar.HOUR_OF_DAY, 0);
            limite.set(Calendar.MINUTE, 0);
            limite.set(Calendar.SECOND, 0);
            limite.set(Calendar.MILLISECOND, 0);

            selectorFecha.getDatePicker().setMaxDate(limite.getTimeInMillis() - 1);

            selectorFecha.show();
        });

        // Botón enviar solicitud
        View btnEnviarSolicitud = view.findViewById(R.id.btnEnviarSolicitudHoraExtra);

        btnEnviarSolicitud.setOnClickListener(v -> enviarSolicitudHoraExtra());

        // Botón regresar
        View btnRegresar = view.findViewById(R.id.btnRegresar);

        btnRegresar.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void enviarSolicitudHoraExtra() {

        Bundle datos = getArguments();

        int usuarioId = datos != null ? datos.getInt("usuario_id", -1) : -1;

        if (usuarioId == -1) {
            Toast.makeText(requireContext(), "No se encontró el ID del usuario", Toast.LENGTH_LONG)
                 .show();
            return;
        }

        EditText edtFecha = requireView().findViewById(R.id.edtFechaHoraExtra);

        EditText edtHoras = requireView().findViewById(R.id.edtHorasExtra);

        EditText edtMotivo = requireView().findViewById(R.id.edtMotivoHoraExtra);

        String fecha = edtFecha.getText().toString().trim();
        String horasTexto = edtHoras.getText().toString().trim();
        String motivo = edtMotivo.getText().toString().trim();

        if (fecha.isEmpty() || horasTexto.isEmpty() || motivo.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_LONG).show();
            return;
        }

        double horas;

        try {
            horas = Double.parseDouble(horasTexto.replace(",", "."));

            if (!Double.isFinite(horas) || horas <= 0) {
                Toast.makeText(requireContext(), "Ingresa una cantidad de horas válida", Toast.LENGTH_LONG)
                     .show();
                return;
            }

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "La cantidad de horas no es válida", Toast.LENGTH_LONG)
                 .show();
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("usuario_id", usuarioId);
            json.put("fecha", fecha);
            json.put("horas", horas);
            json.put("motivo", motivo);

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

            RequestBody body = RequestBody.create(json.toString(), mediaType);

            Request request = new Request.Builder().url("http://2.25.188.174:3000/horas-extra")
                                                   .post(body).build();

            OkHttpClient client = new OkHttpClient();

            client.newCall(request).enqueue(new Callback() {

                @Override public void onFailure(Call call, IOException e) {
                    if (!isAdded()) {
                        return;
                    }

                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Error de conexión al enviar la solicitud", Toast.LENGTH_LONG)
                                                               .show());
                }

                @Override public void onResponse(Call call, Response response) throws IOException {

                    String respuesta = response.body() != null ? response.body().string() : "";

                    boolean correcto = response.isSuccessful();
                    response.close();

                    if (!isAdded()) {
                        return;
                    }

                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded()) {
                            return;
                        }

                        String mensaje;

                        try {
                            JSONObject resultado = new JSONObject(respuesta);
                            mensaje = resultado.optString("mensaje", correcto ? "Solicitud registrada correctamente" : "No se pudo registrar la solicitud");
                        } catch (Exception e) {
                            mensaje = correcto ? "Solicitud registrada correctamente" : "Error al registrar la solicitud";
                        }

                        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();

                        if (correcto) {
                            getParentFragmentManager().popBackStack();
                        }
                    });
                }
            });

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al preparar la solicitud", Toast.LENGTH_LONG)
                 .show();
        }
    }

}