package com.example.asistech;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EmployerDashboardFragment extends Fragment {

    private TextView txtSaludo;
    private TextView txtFecha;
    private TextView txtHora;
    private TextView txtEmpleadosActivos;
    private TextView txtEmpleadosAusentes;
    private RecyclerView recyclerEmpleados;

    private final OkHttpClient client = new OkHttpClient();

    private final List<Empleado> empleados = new ArrayList<>();

    private EmpleadoAdapter adapter;

    // Reloj
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable actualizarReloj = new Runnable() {
        @Override
        public void run() {

            if (getActivity() == null) {
                return;
            }

            Date ahora = new Date();

            SimpleDateFormat formatoFecha =
                    new SimpleDateFormat(
                            "EEEE, d 'de' MMMM 'de' yyyy",
                            new Locale("es", "CO")
                    );

            SimpleDateFormat formatoHora =
                    new SimpleDateFormat(
                            "hh:mm:ss a",
                            new Locale("es", "CO")
                    );

            String fecha = formatoFecha.format(ahora);
            String hora = formatoHora.format(ahora);

            // Primera letra en mayúscula
            fecha = fecha.substring(0, 1).toUpperCase()
                    + fecha.substring(1);

            txtFecha.setText(fecha);
            txtHora.setText(hora);

            // Actualizar nuevamente en 1 segundo
            handler.postDelayed(this, 1000);
        }
    };


    public EmployerDashboardFragment() {
        // Constructor vacío
    }


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_employer_dashboard,
                container,
                false
        );


        // =========================
        // SALUDO
        // =========================

        txtSaludo = view.findViewById(
                R.id.txtSaludo
        );

        String nombre = "";

        if (getArguments() != null) {

            nombre = getArguments().getString(
                    "nombre",
                    ""
            );
        }

        txtSaludo.setText(
                "Hola, " + nombre
        );


        // =========================
        // FECHA Y HORA
        // =========================

        txtFecha = view.findViewById(
                R.id.txtFecha
        );

        txtHora = view.findViewById(
                R.id.txtHora
        );

        // Iniciar reloj
        handler.post(actualizarReloj);


        // =========================
        // TARJETAS
        // =========================

        txtEmpleadosActivos = view.findViewById(
                R.id.txtEmpleadosActivos
        );

        txtEmpleadosAusentes = view.findViewById(
                R.id.txtEmpleadosAusentes
        );


        // =========================
        // LISTA DE EMPLEADOS
        // =========================

        recyclerEmpleados = view.findViewById(
                R.id.recyclerEmpleados
        );

        recyclerEmpleados.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        adapter = new EmpleadoAdapter(
                empleados
        );

        recyclerEmpleados.setAdapter(
                adapter
        );


        // =========================
        // API
        // =========================

        cargarDashboard();

        cargarEmpleados();


        return view;
    }


    // =========================
    // DASHBOARD
    // =========================

    private void cargarDashboard() {

        Request request = new Request.Builder()
                .url(
                        "http://2.25.188.174:3000/dashboard/empleador"
                )
                .get()
                .build();

        client.newCall(request).enqueue(
                new Callback() {

                    @Override
                    public void onFailure(
                            Call call,
                            IOException e) {

                        if (getActivity() != null) {

                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(
                                            getActivity(),
                                            "Error al cargar dashboard",
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
                                response.body().string();

                        try {

                            JSONObject datos =
                                    new JSONObject(respuesta);

                            int activos =
                                    datos.getInt(
                                            "empleados_activos"
                                    );

                            int ausentes =
                                    datos.getInt(
                                            "empleados_ausentes"
                                    );

                            if (getActivity() != null) {

                                getActivity().runOnUiThread(() -> {

                                    txtEmpleadosActivos.setText(
                                            String.valueOf(activos)
                                    );

                                    txtEmpleadosAusentes.setText(
                                            String.valueOf(ausentes)
                                    );

                                });
                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                        }
                    }
                }
        );
    }


    // =========================
    // EMPLEADOS
    // =========================

    private void cargarEmpleados() {

        Request request = new Request.Builder()
                .url(
                        "http://2.25.188.174:3000/empleados"
                )
                .get()
                .build();

        client.newCall(request).enqueue(
                new Callback() {

                    @Override
                    public void onFailure(
                            Call call,
                            IOException e) {

                        if (getActivity() != null) {

                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(
                                            getActivity(),
                                            "Error al cargar empleados",
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
                                response.body().string();

                        try {

                            JSONArray lista =
                                    new JSONArray(respuesta);

                            empleados.clear();

                            for (
                                    int i = 0;
                                    i < lista.length();
                                    i++
                            ) {

                                JSONObject item =
                                        lista.getJSONObject(i);

                                Empleado empleado =
                                        new Empleado(

                                                item.getInt(
                                                        "id"
                                                ),

                                                item.getString(
                                                        "nombre"
                                                ),

                                                item.getString(
                                                        "correo"
                                                ),

                                                item.getString(
                                                        "area"
                                                ),

                                                item.getString(
                                                        "cargo"
                                                ),

                                                item.getString(
                                                        "fecha_ingreso"
                                                ),

                                                item.getString(
                                                        "estado"
                                                ),

                                                item.optString(
                                                        "hora_entrada",
                                                        ""
                                                ),

                                                item.optString(
                                                        "hora_salida",
                                                        ""
                                                ),

                                                item.optString(
                                                        "asistencia",
                                                        "ausente"
                                                )
                                        );

                                empleados.add(
                                        empleado
                                );
                            }


                            if (getActivity() != null) {

                                getActivity().runOnUiThread(() ->
                                        adapter.notifyDataSetChanged()
                                );
                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                        }
                    }
                }
        );
    }


    // =========================
    // DETENER RELOJ
    // =========================

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        handler.removeCallbacks(
                actualizarReloj
        );
    }
}