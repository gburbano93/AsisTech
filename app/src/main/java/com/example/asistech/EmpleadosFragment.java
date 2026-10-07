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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class EmpleadosFragment extends Fragment {

    private RecyclerView recyclerListaEmpleados;

    private TextView txtTotalEmpleados;

    private EditText edtBuscarEmpleado;

    private final List<Empleado> empleados = new ArrayList<>();

    private EmpleadosGestionAdapter adapter;

    private final OkHttpClient client = new OkHttpClient();

    public EmpleadosFragment() {
        // Constructor vacío
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_empleados,
                container,
                false
        );

        //Agregar Empleado
        View btnAgregarEmpleado =
                view.findViewById(R.id.btnAgregarEmpleado);

        btnAgregarEmpleado.setOnClickListener(v ->
                agregarEmpleado()
        );

        recyclerListaEmpleados =
                view.findViewById(
                        R.id.recyclerListaEmpleados
                );

        txtTotalEmpleados =
                view.findViewById(
                        R.id.txtTotalEmpleados
                );

        edtBuscarEmpleado = view.findViewById(
                R.id.edtBuscarEmpleado
        );

        edtBuscarEmpleado.addTextChangedListener(new TextWatcher() {

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

                filtrarEmpleados(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        recyclerListaEmpleados.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        adapter =
                new EmpleadosGestionAdapter(empleados);

        recyclerListaEmpleados.setAdapter(adapter);

        cargarEmpleados();

        return view;
    }

    private void agregarEmpleado() {
        AgregarEmpleadoFragment fragment =
                new AgregarEmpleadoFragment();

        getParentFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.content_container,
                        fragment
                )
                .addToBackStack(null)
                .commit();
    }

    private void cargarEmpleados() {

        Request request = new Request.Builder()
                .url("http://2.25.188.174:3000/empleados")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {

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

                    // Crear la lista de empleados
                    for (int i = 0;
                         i < lista.length();
                         i++) {

                        JSONObject item =
                                lista.getJSONObject(i);

                        Empleado empleado =
                                new Empleado(
                                        item.getInt("id"),
                                        item.getString("nombre"),
                                        item.getString("correo"),
                                        item.getString("area"),
                                        item.getString("cargo"),
                                        item.getString("fecha_ingreso"),
                                        item.getString("estado"),
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

                        empleados.add(empleado);
                    }

                    // Actualizar la interfaz
                    if (getActivity() != null) {

                        getActivity().runOnUiThread(() -> {

                            txtTotalEmpleados.setText(
                                    "Total de empleados: "
                                            + empleados.size()
                            );

                            adapter.notifyDataSetChanged();

                        });
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                    if (getActivity() != null) {

                        getActivity().runOnUiThread(() ->
                                Toast.makeText(
                                        getActivity(),
                                        "Error procesando empleados",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
                    }
                }
            }
        });
    }

    private void filtrarEmpleados(String texto) {

        String busqueda = texto.toLowerCase().trim();

        List<Empleado> empleadosFiltrados = new ArrayList<>();

        for (Empleado empleado : empleados) {

            if (empleado.getNombre().toLowerCase().contains(busqueda)
                    || empleado.getArea().toLowerCase().contains(busqueda)
                    || empleado.getCargo().toLowerCase().contains(busqueda)) {

                empleadosFiltrados.add(empleado);
            }
        }

        adapter.actualizarLista(empleadosFiltrados);

        txtTotalEmpleados.setText(
                "Total de empleados: " + empleadosFiltrados.size()
        );
    }
}