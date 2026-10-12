
package com.example.asistech;

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

public class HorasExtraFragment extends Fragment {

    private EditText edtBuscar;
    private TextView txtTotal;
    private TextView txtSinSolicitudes;
    private RecyclerView recycler;
    private Button btnFiltrar;

    private HoraExtraAdapter adapter;
    private final List<HoraExtra> solicitudes = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();

    private static final String URL_API =
            "http://2.25.188.174:3000/horas-extra";

    public HorasExtraFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_horas_extra,
                container,
                false
        );

        edtBuscar = view.findViewById(R.id.edtBuscarHorasExtra);
        txtTotal = view.findViewById(R.id.txtTotalHorasExtra);
        txtSinSolicitudes = view.findViewById(R.id.txtSinHorasExtra);
        recycler = view.findViewById(R.id.recyclerHorasExtra);
        btnFiltrar = view.findViewById(R.id.btnFiltrarHorasExtra);

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HoraExtraAdapter(new ArrayList<>());
        recycler.setAdapter(adapter);

        btnFiltrar.setOnClickListener(v ->
                                              filtrarSolicitudes(edtBuscar.getText().toString())
        );

        edtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                filtrarSolicitudes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cargarSolicitudes();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarSolicitudes();
    }

    private void cargarSolicitudes() {
        Request request = new Request.Builder()
                .url(URL_API)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call,
                                  @NonNull IOException e) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() ->
                                                        mostrarMensaje("No se pudieron cargar las solicitudes")
                );
            }

            @Override
            public void onResponse(@NonNull Call call,
                                   @NonNull Response response) throws IOException {

                try (Response respuesta = response) {
                    if (!respuesta.isSuccessful() || respuesta.body() == null) {
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() ->
                                                                    mostrarMensaje("Error al consultar las horas extras")
                            );
                        }
                        return;
                    }

                    String contenido = respuesta.body().string();
                    JSONArray json = new JSONArray(contenido);
                    List<HoraExtra> lista = new ArrayList<>();

                    for (int i = 0; i < json.length(); i++) {
                        JSONObject item = json.getJSONObject(i);

                        String fecha = item.optString("fecha", "");
                        if (fecha.length() >= 10) {
                            fecha = fecha.substring(0, 10);
                        }

                        lista.add(new HoraExtra(
                                item.optInt("id"),
                                item.optInt("usuario_id"),
                                item.optString("nombre", ""),
                                item.optString("area", ""),
                                item.optString("cargo", ""),
                                fecha,
                                item.optDouble("horas", 0),
                                item.optString("motivo", ""),
                                item.optString("estado", "pendiente")
                        ));
                    }

                    if (!isAdded()) return;

                    requireActivity().runOnUiThread(() -> {
                        solicitudes.clear();
                        solicitudes.addAll(lista);
                        filtrarSolicitudes(
                                edtBuscar.getText().toString()
                        );
                    });

                } catch (Exception e) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                                                mostrarMensaje("Error al procesar las solicitudes")
                        );
                    }
                }
            }
        });
    }

    private void filtrarSolicitudes(String texto) {
        String consulta = texto.toLowerCase(Locale.ROOT).trim();
        List<HoraExtra> filtradas = new ArrayList<>();

        for (HoraExtra solicitud : solicitudes) {
            String nombre = solicitud.getNombre() == null
                    ? "" : solicitud.getNombre().toLowerCase(Locale.ROOT);
            String cargo = solicitud.getCargo() == null
                    ? "" : solicitud.getCargo().toLowerCase(Locale.ROOT);
            String fecha = solicitud.getFecha() == null
                    ? "" : solicitud.getFecha().toLowerCase(Locale.ROOT);

            if (nombre.contains(consulta)
                    || cargo.contains(consulta)
                    || fecha.contains(consulta)) {
                filtradas.add(solicitud);
            }
        }

        adapter.actualizarLista(filtradas);
        txtTotal.setText("Total de solicitudes: " + filtradas.size());

        txtSinSolicitudes.setVisibility(
                filtradas.isEmpty() ? View.VISIBLE : View.GONE
        );
        recycler.setVisibility(
                filtradas.isEmpty() ? View.GONE : View.VISIBLE
        );
    }

    private void mostrarMensaje(String mensaje) {
        if (isAdded()) {
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
        }
    }
}
