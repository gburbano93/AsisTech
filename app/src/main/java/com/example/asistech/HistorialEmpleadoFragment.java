
package com.example.asistech;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.AlertDialog;

public class HistorialEmpleadoFragment extends Fragment {

    private static final String BASE_URL = "http://2.25.188.174:3000";

    private int usuarioId = -1;
    private OkHttpClient client;

    private TextView txtDiasTrabajados;
    private TextView txtHorasTrabajadas;
    private TextView txtSinRegistros;
    private LinearLayout contenedorRegistros;
    private Button btnFiltroMes;
    private Button btnFiltroTodos;

    private final ArrayList<Asistencia> asistencias = new ArrayList<>();

    private Integer mesSeleccionado = null;
    private Integer anioSeleccionado = null;

    public HistorialEmpleadoFragment() {
        // Constructor requerido
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_historial_empleado,
                container,
                false
        );

        client = new OkHttpClient();

        txtDiasTrabajados = view.findViewById(R.id.txtDiasTrabajados);
        txtHorasTrabajadas = view.findViewById(R.id.txtHorasTrabajadas);
        txtSinRegistros = view.findViewById(R.id.txtSinRegistros);
        contenedorRegistros = view.findViewById(R.id.contenedorRegistros);
        btnFiltroMes = view.findViewById(R.id.btnFiltroMes);
        btnFiltroTodos = view.findViewById(R.id.btnFiltroTodos);

        View btnRegresar = view.findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v ->
                                               getParentFragmentManager().popBackStack()
        );

        btnFiltroMes.setOnClickListener(v -> seleccionarMes());
        btnFiltroTodos.setOnClickListener(v -> {
            mesSeleccionado = null;
            anioSeleccionado = null;
            btnFiltroMes.setText("Elegir mes");
            actualizarPantalla();
        });

        Bundle datos = getArguments();
        if (datos != null) {
            usuarioId = datos.getInt("usuario_id", -1);
        }

        if (usuarioId == -1) {
            Toast.makeText(
                    requireContext(),
                    "No se encontró el ID del usuario",
                    Toast.LENGTH_LONG
            ).show();
        } else {
            cargarHistorial();
        }

        return view;
    }

    private void cargarHistorial() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/asistencias/usuario/" + usuarioId)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call,
                                  @NonNull IOException e) {
                mostrarMensaje("Error de conexión al cargar el historial");
            }

            @Override
            public void onResponse(@NonNull Call call,
                                   @NonNull Response response)
                    throws IOException {

                String respuesta = response.body() != null
                        ? response.body().string() : "";

                boolean correcto = response.isSuccessful();
                response.close();

                if (!correcto) {
                    mostrarMensaje("No se pudo cargar el historial");
                    return;
                }

                try {
                    JSONArray arreglo = new JSONArray(respuesta);
                    ArrayList<Asistencia> nuevas = new ArrayList<>();

                    for (int i = 0; i < arreglo.length(); i++) {
                        JSONObject item = arreglo.getJSONObject(i);

                        Asistencia asistencia = new Asistencia();
                        asistencia.fecha = item.optString("fecha", "");
                        asistencia.entrada = item.optString("hora_entrada", "");
                        asistencia.salida = item.optString("hora_salida", "");
                        asistencia.estado = item.optString("estado", "");

                        asistencia.horas = calcularHoras(
                                asistencia.entrada,
                                asistencia.salida
                        );

                        nuevas.add(asistencia);
                    }

                    if (!isAdded()) return;

                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded()) return;

                        asistencias.clear();
                        asistencias.addAll(nuevas);

                        Collections.sort(asistencias,
                                         (a, b) -> b.fecha.compareTo(a.fecha));

                        actualizarPantalla();
                    });

                } catch (Exception e) {
                    android.util.Log.e(
                            "HISTORIAL",
                            "Error procesando asistencias",
                            e
                    );
                    mostrarMensaje("La respuesta del historial no es válida");
                }
            }
        });
    }

    private void mostrarMensaje(String mensaje) {
        if (!isAdded()) return;

        requireActivity().runOnUiThread(() -> {
            if (isAdded()) {
                Toast.makeText(
                        requireContext(),
                        mensaje,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }


    private void seleccionarMes() {
        final String[] meses = {
                "Enero", "Febrero", "Marzo", "Abril",
                "Mayo", "Junio", "Julio", "Agosto",
                "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        Calendar calendario = Calendar.getInstance();

        int mesInicial = mesSeleccionado != null
                ? mesSeleccionado
                : calendario.get(Calendar.MONTH);

        int anioInicial = anioSeleccionado != null
                ? anioSeleccionado
                : calendario.get(Calendar.YEAR);

        final int[] mesTemporal = {mesInicial};
        final int[] anioTemporal = {anioInicial};

        AlertDialog dialogo = new AlertDialog.Builder(requireContext())
                .setTitle("Seleccionar mes y año")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Aplicar", (dialog, which) -> {
                    mesSeleccionado = mesTemporal[0];
                    anioSeleccionado = anioTemporal[0];

                    btnFiltroMes.setText(
                            meses[mesSeleccionado] + " " + anioSeleccionado
                    );

                    actualizarPantalla();
                })
                .create();

        LinearLayout contenido = new LinearLayout(requireContext());
        contenido.setOrientation(LinearLayout.VERTICAL);
        contenido.setPadding(dp(24), dp(12), dp(24), dp(12));

        android.widget.NumberPicker selectorMes =
                new android.widget.NumberPicker(requireContext());

        selectorMes.setMinValue(0);
        selectorMes.setMaxValue(11);
        selectorMes.setDisplayedValues(meses);
        selectorMes.setValue(mesInicial);

        android.widget.NumberPicker selectorAnio =
                new android.widget.NumberPicker(requireContext());

        selectorAnio.setMinValue(2000);
        selectorAnio.setMaxValue(2100);
        selectorAnio.setValue(anioInicial);

        contenido.addView(selectorMes);
        contenido.addView(selectorAnio);

        dialogo.setView(contenido);

        selectorMes.setOnValueChangedListener(
                (picker, oldValue, newValue) -> mesTemporal[0] = newValue
        );

        selectorAnio.setOnValueChangedListener(
                (picker, oldValue, newValue) -> anioTemporal[0] = newValue
        );

        dialogo.show();
    }


    private void actualizarPantalla() {
        if (!isAdded()) return;

        ArrayList<Asistencia> filtradas = new ArrayList<>();
        int diasTrabajados = 0;
        double totalHoras = 0;

        for (Asistencia asistencia : asistencias) {
            if (!coincideConFiltro(asistencia.fecha)) continue;

            filtradas.add(asistencia);

            if (asistencia.horas >= 0) {
                totalHoras += asistencia.horas;
                diasTrabajados++;
            }
        }

        txtDiasTrabajados.setText(String.valueOf(diasTrabajados));
        txtHorasTrabajadas.setText(
                String.format(Locale.getDefault(), "%.2f h", totalHoras)
        );

        contenedorRegistros.removeAllViews();

        if (filtradas.isEmpty()) {
            txtSinRegistros.setVisibility(View.VISIBLE);
        } else {
            txtSinRegistros.setVisibility(View.GONE);

            for (int i = 0; i < filtradas.size(); i++) {
                contenedorRegistros.addView(
                        crearFila(filtradas.get(i), i)
                );
            }
        }
    }

    private boolean coincideConFiltro(String fecha) {
        if (mesSeleccionado == null || anioSeleccionado == null) {
            return true;
        }

        try {
            Calendar calendario = Calendar.getInstance();

            if (fecha.length() >= 10 && fecha.charAt(4) == '-') {
                int anio = Integer.parseInt(fecha.substring(0, 4));
                int mes = Integer.parseInt(fecha.substring(5, 7)) - 1;

                return anio == anioSeleccionado
                        && mes == mesSeleccionado;
            }

            return false;

        } catch (Exception e) {
            return false;
        }
    }

    private View crearFila(Asistencia asistencia, int posicion) {
        LinearLayout fila = new LinearLayout(requireContext());
        fila.setLayoutParams(new LinearLayout.LayoutParams(
                dp(500),
                dp(60)
        ));
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setPadding(dp(8), 0, dp(8), 0);
        fila.setBackgroundColor(posicion % 2 == 0
                                        ? Color.WHITE : Color.rgb(248, 252, 252));

        agregarTexto(fila, formatearFecha(asistencia.fecha),
                     115, "#172554", false);

        agregarTexto(fila, formatearHora(asistencia.entrada),
                     85, "#64748B", false);

        agregarTexto(fila, formatearHora(asistencia.salida),
                     85, "#64748B", false);

        String horas = asistencia.horas >= 0
                ? String.format(Locale.getDefault(), "%.2f h", asistencia.horas)
                : "--";

        agregarTexto(fila, horas, 75, "#172554", true);

        TextView estado = new TextView(requireContext());
        estado.setLayoutParams(new LinearLayout.LayoutParams(dp(90), dp(30)));
        estado.setGravity(Gravity.CENTER);
        estado.setText(asistencia.estado.isEmpty()
                               ? "Sin estado" : asistencia.estado);
        estado.setTextSize(9);
        estado.setTypeface(null, Typeface.BOLD);

        boolean completa = asistencia.horas >= 0;

        GradientDrawable fondo = new GradientDrawable();
        fondo.setCornerRadius(dp(6));
        fondo.setColor(completa
                               ? Color.rgb(220, 252, 231)
                               : Color.rgb(255, 244, 214));

        estado.setBackground(fondo);
        estado.setTextColor(completa
                                    ? Color.rgb(4, 120, 87)
                                    : Color.rgb(180, 83, 9));

        fila.addView(estado);

        return fila;
    }

    private void agregarTexto(LinearLayout fila, String texto,
                              int ancho, String color, boolean negrita) {
        TextView campo = new TextView(requireContext());

        campo.setLayoutParams(new LinearLayout.LayoutParams(
                dp(ancho), ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        campo.setText(texto);
        campo.setTextColor(Color.parseColor(color));
        campo.setTextSize(11);

        if (negrita) {
            campo.setTypeface(null, Typeface.BOLD);
        }

        fila.addView(campo);
    }

    private double calcularHoras(String entrada, String salida) {
        if (entrada == null || salida == null
                || entrada.isEmpty() || salida.isEmpty()
                || entrada.equals("null") || salida.equals("null")) {
            return -1;
        }

        try {
            int segundosEntrada = segundosDesdeMedianoche(entrada);
            int segundosSalida = segundosDesdeMedianoche(salida);

            if (segundosEntrada < 0 || segundosSalida < 0) {
                return -1;
            }

            int diferencia = segundosSalida - segundosEntrada;

            // No sumar automáticamente jornadas que crucen medianoche.
            if (diferencia < 0) return -1;

            return diferencia / 3600.0;

        } catch (Exception e) {
            return -1;
        }
    }

    private int segundosDesdeMedianoche(String hora) {
        String[] partes = hora.split(":");

        if (partes.length < 2) return -1;

        int horas = Integer.parseInt(partes[0]);
        int minutos = Integer.parseInt(partes[1]);
        int segundos = partes.length >= 3
                ? Integer.parseInt(partes[2]) : 0;

        if (horas < 0 || horas > 23
                || minutos < 0 || minutos > 59
                || segundos < 0 || segundos > 59) {
            return -1;
        }

        return horas * 3600 + minutos * 60 + segundos;
    }

    private String formatearHora(String hora) {
        if (hora == null || hora.isEmpty() || hora.equals("null")) {
            return "--";
        }

        return hora.length() >= 5 ? hora.substring(0, 5) : hora;
    }

    private String formatearFecha(String fecha) {
        try {
            if (fecha.length() >= 10 && fecha.charAt(4) == '-') {
                SimpleDateFormat entrada =
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                Date date = entrada.parse(fecha.substring(0, 10));

                if (date != null) {
                    SimpleDateFormat salida =
                            new SimpleDateFormat("dd MMM yyyy", new Locale("es", "CO"));

                    return salida.format(date);
                }
            }
        } catch (Exception ignored) {
        }

        return fecha;
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static class Asistencia {
        String fecha = "";
        String entrada = "";
        String salida = "";
        String estado = "";
        double horas = -1;
    }
}
