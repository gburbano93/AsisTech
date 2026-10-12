
package com.example.asistech;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private boolean modoEdicion = false;
    private int empleadoId = -1;

    private EditText edtNombre;
    private EditText edtCorreo;
    private EditText edtContrasena;
    private EditText edtArea;
    private EditText edtCargo;
    private EditText edtFechaIngreso;
    private TextView txtErrorCorreo;
    private Button btnGuardar;

    private final OkHttpClient client = new OkHttpClient();

    private static final MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");

    public AgregarEmpleadoFragment() {
        // Constructor vacío
    }

    public void prepararEdicion(Empleado empleado) {
        Bundle datos = new Bundle();

        datos.putBoolean("modoEdicion", true);
        datos.putInt("empleadoId", empleado.getId());
        datos.putString("nombre", empleado.getNombre());
        datos.putString("correo", empleado.getCorreo());
        datos.putString("area", empleado.getArea());
        datos.putString("cargo", empleado.getCargo());
        datos.putString("fechaIngreso", empleado.getFechaIngreso());

        setArguments(datos);
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

        Bundle datos = getArguments();

        modoEdicion = datos != null
                && datos.getBoolean("modoEdicion", false);

        if (modoEdicion) {
            empleadoId = datos.getInt("empleadoId", -1);
        }

        // Referencias de los campos
        edtNombre = view.findViewById(
                R.id.edtNombreNuevoEmpleado
        );

        edtCorreo = view.findViewById(
                R.id.edtCorreoNuevoEmpleado
        );

        edtContrasena = view.findViewById(
                R.id.edtContrasenaNuevoEmpleado
        );

        edtArea = view.findViewById(
                R.id.edtAreaNuevoEmpleado
        );

        edtCargo = view.findViewById(
                R.id.edtCargoNuevoEmpleado
        );

        edtFechaIngreso = view.findViewById(
                R.id.edtFechaIngresoNuevoEmpleado
        );

        txtErrorCorreo = view.findViewById(
                R.id.txtErrorCorreo
        );

        btnGuardar = view.findViewById(
                R.id.btnGuardarNuevoEmpleado
        );

        // Configurar el formulario según el modo
        if (modoEdicion && datos != null) {

            edtNombre.setText(datos.getString("nombre", ""));
            edtCorreo.setText(datos.getString("correo", ""));
            edtArea.setText(datos.getString("area", ""));
            edtCargo.setText(datos.getString("cargo", ""));
            edtFechaIngreso.setText(
                    datos.getString("fechaIngreso", "")
            );

            // La contraseña es opcional al editar
            edtContrasena.setHint(
                    "Nueva contraseña (opcional)"
            );

            btnGuardar.setText("Guardar cambios");

        } else {
            btnGuardar.setText("Guardar");
        }

        // Selector de fecha
        edtFechaIngreso.setFocusable(false);
        edtFechaIngreso.setClickable(true);

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

            calendarioDialog.getDatePicker().setMaxDate(
                    System.currentTimeMillis()
            );

            calendarioDialog.show();
        });

        // Validación del correo
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

                String correo = s.toString().trim();

                if (correo.isEmpty()
                        || !android.util.Patterns.EMAIL_ADDRESS
                        .matcher(correo).matches()) {

                    txtErrorCorreo.setVisibility(
                            correo.isEmpty() ? View.GONE : View.VISIBLE
                    );

                } else {
                    txtErrorCorreo.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Botón regresar
        View btnRegresar = view.findViewById(
                R.id.btnRegresarAgregarEmpleado
        );

        btnRegresar.setOnClickListener(v ->
                                               getParentFragmentManager().popBackStack()
        );

        // Botón guardar
        btnGuardar.setOnClickListener(v -> guardarEmpleado());

        return view;
    }

    private void guardarEmpleado() {

        String nombre = edtNombre.getText().toString().trim();
        String correo = edtCorreo.getText().toString().trim();
        String contrasena = edtContrasena.getText().toString().trim();
        String area = edtArea.getText().toString().trim();
        String cargo = edtCargo.getText().toString().trim();
        String fechaIngreso =
                edtFechaIngreso.getText().toString().trim();

        // Validar campos obligatorios
        if (nombre.isEmpty()
                || correo.isEmpty()
                || area.isEmpty()
                || cargo.isEmpty()
                || fechaIngreso.isEmpty()
                || (!modoEdicion && contrasena.isEmpty())) {

            Toast.makeText(
                    requireContext(),
                    "Completa todos los campos obligatorios",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (modoEdicion && empleadoId <= 0) {
            Toast.makeText(
                    requireContext(),
                    "No se identificó el empleado que deseas editar",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Validar correo
        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(correo).matches()) {

            Toast.makeText(
                    requireContext(),
                    "Ingresa un correo válido",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {
            JSONObject datos = new JSONObject();

            datos.put("nombre", nombre);
            datos.put("correo", correo);
            datos.put("area", area);
            datos.put("cargo", cargo);
            datos.put("fecha_ingreso", fechaIngreso);

            // Enviar contraseña solamente si se escribió una
            if (!contrasena.isEmpty()) {
                datos.put("contrasena", contrasena);
            }

            RequestBody body = RequestBody.create(
                    datos.toString(),
                    JSON
            );

            String url;
            Request.Builder constructor = new Request.Builder();

            if (modoEdicion) {
                url = "http://2.25.188.174:3000/empleados/"
                        + empleadoId;

                constructor.url(url).put(body);

            } else {
                url = "http://2.25.188.174:3000/addEmpleados";

                constructor.url(url).post(body);
            }

            Request request = constructor.build();

            btnGuardar.setEnabled(false);

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(
                        Call call,
                        IOException e) {

                    mostrarResultado(
                            "Error de conexión con el servidor",
                            false
                    );
                }

                @Override
                public void onResponse(
                        Call call,
                        Response response) throws IOException {

                    boolean exitoso = response.isSuccessful();
                    String respuesta;

                    try (Response resultado = response) {
                        respuesta = resultado.body() != null
                                ? resultado.body().string()
                                : "";
                    }

                    String mensaje;

                    try {
                        JSONObject resultado =
                                new JSONObject(respuesta);

                        mensaje = resultado.optString(
                                "mensaje",
                                exitoso
                                        ? "Operación realizada correctamente"
                                        : "No se pudo guardar el empleado"
                        );

                    } catch (Exception e) {
                        mensaje = exitoso
                                ? "Operación realizada correctamente"
                                : "No se pudo guardar el empleado";
                    }

                    mostrarResultado(mensaje, exitoso);
                }
            });

        } catch (Exception e) {
            btnGuardar.setEnabled(true);

            Toast.makeText(
                    requireContext(),
                    "Error preparando los datos",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void mostrarResultado(
            String mensaje,
            boolean exitoso) {

        if (!isAdded()) {
            return;
        }

        requireActivity().runOnUiThread(() -> {

            if (!isAdded()) {
                return;
            }

            btnGuardar.setEnabled(true);

            Toast.makeText(
                    requireContext(),
                    mensaje,
                    Toast.LENGTH_SHORT
            ).show();

            if (exitoso) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
}
