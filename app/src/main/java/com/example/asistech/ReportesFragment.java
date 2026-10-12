package com.example.asistech;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReportesFragment extends Fragment {

    private TextView txtEmpleadosActivos;
    private TextView txtEmpleadosAusentes;
    private EditText edtBuscarReporte;
    private RecyclerView recyclerReportes;

    private final List<Empleado> empleados = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();

    private ReporteAdapter adapter;

    public ReportesFragment() {
        // Constructor vacío
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_reportes,
                container,
                false
        );

        txtEmpleadosActivos =
                view.findViewById(R.id.txtEmpleadosActivos);

        txtEmpleadosAusentes =
                view.findViewById(R.id.txtEmpleadosAusentes);

        edtBuscarReporte =
                view.findViewById(R.id.edtBuscarReporte);

        recyclerReportes =
                view.findViewById(R.id.recyclerReportes);

        recyclerReportes.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        adapter = new ReporteAdapter(new ArrayList<>());
        recyclerReportes.setAdapter(adapter);

        edtBuscarReporte.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                filtrarEmpleados(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cargarIndicadores();
        cargarEmpleados();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarIndicadores();
        cargarEmpleados();
    }

    private void cargarIndicadores() {

        Request request = new Request.Builder()
                .url("http://2.25.188.174:3000/dashboard/empleador")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(
                    @NonNull Call call,
                    @NonNull IOException e) {
                mostrarError("Error al cargar los indicadores");
            }

            @Override
            public void onResponse(
                    @NonNull Call call,
                    @NonNull Response response) throws IOException {

                try (Response respuestaApi = response) {
                    if (!respuestaApi.isSuccessful()
                            || respuestaApi.body() == null) {
                        mostrarError("No se pudieron cargar los indicadores");
                        return;
                    }

                    JSONObject datos = new JSONObject(
                            respuestaApi.body().string()
                    );

                    int activos = datos.getInt("empleados_activos");
                    int ausentes = datos.getInt("empleados_ausentes");

                    if (!isAdded()) return;

                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded() || getView() == null) return;

                        txtEmpleadosActivos.setText(
                                String.valueOf(activos)
                        );

                        txtEmpleadosAusentes.setText(
                                String.valueOf(ausentes)
                        );
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarError("Error al procesar los indicadores");
                }
            }
        });
    }

    private void cargarEmpleados() {

        Request request = new Request.Builder()
                .url("http://2.25.188.174:3000/empleados")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(
                    @NonNull Call call,
                    @NonNull IOException e) {
                mostrarError("Error al cargar los empleados");
            }

            @Override
            public void onResponse(
                    @NonNull Call call,
                    @NonNull Response response) throws IOException {

                try (Response respuestaApi = response) {
                    if (!respuestaApi.isSuccessful()
                            || respuestaApi.body() == null) {
                        mostrarError("No se pudo obtener la lista");
                        return;
                    }

                    JSONArray lista = new JSONArray(
                            respuestaApi.body().string()
                    );

                    List<Empleado> empleadosNuevos = new ArrayList<>();

                    for (int i = 0; i < lista.length(); i++) {
                        JSONObject item = lista.getJSONObject(i);

                        Empleado empleado = new Empleado(
                                item.getInt("id"),
                                item.getString("nombre"),
                                item.getString("correo"),
                                item.getString("area"),
                                item.getString("cargo"),
                                item.optString("fecha_ingreso", ""),
                                item.optString("estado", "activo"),
                                item.optString("hora_entrada", ""),
                                item.optString("hora_salida", ""),
                                item.optString("asistencia", "ausente")
                        );

                        empleadosNuevos.add(empleado);
                    }

                    if (!isAdded()) return;

                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded() || getView() == null) return;

                        empleados.clear();
                        empleados.addAll(empleadosNuevos);

                        filtrarEmpleados(
                                edtBuscarReporte.getText().toString()
                        );

                        cargarHorasReporte();

                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarError("Error al procesar los empleados");
                }
            }
        });
    }

    private void cargarHorasReporte() {

        Request request = new Request.Builder()
                .url("http://2.25.188.174:3000/reportes/horas")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(
                    @NonNull Call call,
                    @NonNull IOException e) {
                mostrarError("Error al cargar las horas trabajadas");
            }

            @Override
            public void onResponse(
                    @NonNull Call call,
                    @NonNull Response response) throws IOException {

                try (Response respuestaApi = response) {

                    if (!respuestaApi.isSuccessful()
                            || respuestaApi.body() == null) {
                        mostrarError("No se pudieron consultar las horas");
                        return;
                    }

                    JSONArray lista = new JSONArray(
                            respuestaApi.body().string()
                    );

                    java.util.Map<Integer, int[]> horasPorEmpleado =
                            new java.util.HashMap<>();

                    for (int i = 0; i < lista.length(); i++) {
                        JSONObject item = lista.getJSONObject(i);

                        int id = item.getInt("empleado_id");
                        int horas = item.optInt("total_horas", 0);
                        int minutos = item.optInt("minutos", 0);

                        horasPorEmpleado.put(
                                id,
                                new int[]{horas, minutos}
                        );
                    }

                    if (!isAdded()) return;

                    requireActivity().runOnUiThread(() -> {

                        if (!isAdded() || getView() == null) return;

                        for (Empleado empleado : empleados) {
                            int[] tiempo = horasPorEmpleado.get(
                                    empleado.getId()
                            );

                            if (tiempo != null) {
                                empleado.setTotalHoras(tiempo[0]);
                                empleado.setMinutos(tiempo[1]);
                            } else {
                                empleado.setTotalHoras(0);
                                empleado.setMinutos(0);
                            }
                        }

                        filtrarEmpleados(
                                edtBuscarReporte.getText().toString()
                        );
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarError("Error al procesar las horas");
                }
            }
        });
    }

    private void filtrarEmpleados(String texto) {

        if (adapter == null) return;

        String busqueda = texto.toLowerCase(Locale.ROOT).trim();

        List<Empleado> filtrados = new ArrayList<>();

        for (Empleado empleado : empleados) {
            if (empleado.getNombre().toLowerCase(Locale.ROOT)
                        .contains(busqueda)
                    || empleado.getArea().toLowerCase(Locale.ROOT)
                               .contains(busqueda)
                    || empleado.getCargo().toLowerCase(Locale.ROOT)
                               .contains(busqueda)) {

                filtrados.add(empleado);
            }
        }

        adapter.actualizarLista(filtrados);
    }

    private void mostrarError(String mensaje) {
        if (!isAdded()) return;

        requireActivity().runOnUiThread(() -> {
            if (isAdded()) {
                Toast.makeText(
                        requireContext(),
                        mensaje,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}