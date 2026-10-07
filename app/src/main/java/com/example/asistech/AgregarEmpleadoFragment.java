package com.example.asistech;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AgregarEmpleadoFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();

    private static final MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");

    public AgregarEmpleadoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_agregar_empleado,
                container,
                false
        );

        // =========================
        // FECHA DE INGRESO
        // =========================

        EditText edtFechaIngreso =
                view.findViewById(
                        R.id.edtFechaIngresoNuevoEmpleado
                );

        edtFechaIngreso.setFocusable(false);

        edtFechaIngreso.setOnClickListener(v -> {

            Calendar calendario = Calendar.getInstance();

            DatePickerDialog calendarioDialog =
                    new DatePickerDialog(
                            requireContext(),
                            (datePicker, year, month, dayOfMonth) -> {

                                Calendar fechaSeleccionada =
                                        Calendar.getInstance();

                                fechaSeleccionada.set(
                                        year,
                                        month,
                                        dayOfMonth
                                );

                                SimpleDateFormat formato =
                                        new SimpleDateFormat(
                                                "yyyy-MM-dd",
                                                Locale.getDefault()
                                        );

                                edtFechaIngreso.setText(
                                        formato.format(
                                                fechaSeleccionada.getTime()
                                        )
                                );
                            },
                            calendario.get(Calendar.YEAR),
                            calendario.get(Calendar.MONTH),
                            calendario.get(Calendar.DAY_OF_MONTH)
                    );

            // No permite fechas futuras
            calendarioDialog.getDatePicker().setMaxDate(
                    System.currentTimeMillis()
            );

            calendarioDialog.show();
        });

        // =========================
        // VALIDACIÓN DEL CORREO
        // =========================

        EditText edtCorreo =
                view.findViewById(
                        R.id.edtCorreoNuevoEmpleado
                );

        TextView txtErrorCorreo =
                view.findViewById(
                        R.id.txtErrorCorreo
                );

        edtCorreo.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {

                String correo =
                        s.toString().trim();

                if (correo.isEmpty()) {
                    txtErrorCorreo.setVisibility(View.GONE);
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS
                        .matcher(correo)
                        .matches()) {

                    txtErrorCorreo.setVisibility(View.VISIBLE);

                } else {

                    txtErrorCorreo.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(
                    Editable s) {
            }
        });

        // =========================
        // BOTÓN REGRESAR
        // =========================

        View btnRegresar =
                view.findViewById(
                        R.id.btnRegresarAgregarEmpleado
                );

        btnRegresar.setOnClickListener(v ->
                getParentFragmentManager()
                        .popBackStack()
        );

        // =========================
        // BOTÓN GUARDAR
        // =========================

        View btnGuardar =
                view.findViewById(
                        R.id.btnGuardarNuevoEmpleado
                );

        btnGuardar.setOnClickListener(v ->
                guardarEmpleado()
        );

        return view;
    }

    private void guardarEmpleado() {

        EditText edtNombre =
                requireView().findViewById(
                        R.id.edtNombreNuevoEmpleado
                );

        EditText edtCorreo =
                requireView().findViewById(
                        R.id.edtCorreoNuevoEmpleado
                );

        EditText edtContrasena =
                requireView().findViewById(
                        R.id.edtContrasenaNuevoEmpleado
                );

        EditText edtArea =
                requireView().findViewById(
                        R.id.edtAreaNuevoEmpleado
                );

        EditText edtCargo =
                requireView().findViewById(
                        R.id.edtCargoNuevoEmpleado
                );

        EditText edtFechaIngreso =
                requireView().findViewById(
                        R.id.edtFechaIngresoNuevoEmpleado
                );

        String nombre =
                edtNombre.getText().toString().trim();

        String correo =
                edtCorreo.getText().toString().trim();

        String contrasena =
                edtContrasena.getText().toString().trim();

        String area =
                edtArea.getText().toString().trim();

        String cargo =
                edtCargo.getText().toString().trim();

        String fechaIngreso =
                edtFechaIngreso.getText().toString().trim();

        // =========================
        // VALIDAR CAMPOS
        // =========================

        if (nombre.isEmpty()
                || correo.isEmpty()
                || contrasena.isEmpty()
                || area.isEmpty()
                || cargo.isEmpty()
                || fechaIngreso.isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Validar correo
        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(correo)
                .matches()) {

            Toast.makeText(
                    requireContext(),
                    "Ingresa un correo válido",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =========================
        // CREAR JSON
        // =========================

        try {

            JSONObject datos =
                    new JSONObject();

            datos.put("nombre", nombre);
            datos.put("correo", correo);
            datos.put("contrasena", contrasena);
            datos.put("area", area);
            datos.put("cargo", cargo);
            datos.put("fecha_ingreso", fechaIngreso);

            // =========================
            // PREPARAR PETICIÓN
            // =========================

            RequestBody body =
                    RequestBody.create(
                            datos.toString(),
                            JSON
                    );

            Request request =
                    new Request.Builder()
                            .url(
                                    "http://2.25.188.174:3000/addEmpleados"
                            )
                            .post(body)
                            .build();

            // =========================
            // ENVIAR AL SERVIDOR
            // =========================

            client.newCall(request).enqueue(
                    new Callback() {

                        @Override
                        public void onFailure(
                                Call call,
                                IOException e) {

                            if (getActivity() != null) {

                                getActivity()
                                        .runOnUiThread(() ->
                                                Toast.makeText(
                                                        requireContext(),
                                                        "Error de conexión con el servidor",
                                                        Toast.LENGTH_SHORT
                                                ).show()
                                        );
                            }
                        }

                        @Override
                        public void onResponse(
                                Call call,
                                Response response)
                                throws IOException {

                            String respuesta =
                                    response.body() != null
                                            ? response.body().string()
                                            : "";

                            if (getActivity() != null) {

                                getActivity()
                                        .runOnUiThread(() -> {

                                            if (response.isSuccessful()) {

                                                Toast.makeText(
                                                        requireContext(),
                                                        "Empleado guardado correctamente",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                // Cerrar formulario
                                                getParentFragmentManager()
                                                        .popBackStack();

                                            } else {

                                                try {

                                                    JSONObject error =
                                                            new JSONObject(
                                                                    respuesta
                                                            );

                                                    String mensaje =
                                                            error.optString(
                                                                    "mensaje",
                                                                    "No se pudo guardar el empleado"
                                                            );

                                                    Toast.makeText(
                                                            requireContext(),
                                                            mensaje,
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                } catch (Exception e) {

                                                    Toast.makeText(
                                                            requireContext(),
                                                            "No se pudo guardar el empleado",
                                                            Toast.LENGTH_SHORT
                                                    ).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }
            );

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Error preparando los datos",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}