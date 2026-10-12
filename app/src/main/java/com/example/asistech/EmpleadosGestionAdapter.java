package com.example.asistech;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Context;
import android.content.ContextWrapper;


public class EmpleadosGestionAdapter extends RecyclerView.Adapter<EmpleadosGestionAdapter.EmpleadoViewHolder> {

    private List<Empleado> empleados;

    private final OkHttpClient client = new OkHttpClient();

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public EmpleadosGestionAdapter(List<Empleado> empleados) {
        this.empleados = empleados;
    }

    @NonNull @Override public EmpleadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                    int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.item_empleado_gestion, parent, false);

        return new EmpleadoViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull EmpleadoViewHolder holder, int position) {

        Empleado empleado = empleados.get(position);

        holder.txtNombre.setText(empleado.getNombre());
        holder.txtCargo.setText(empleado.getCargo());
        holder.txtArea.setText("Área: " + empleado.getArea());
        holder.txtCorreo.setText(empleado.getCorreo());

        // Botón editar


        holder.btnEditar.setOnClickListener(v -> {

            androidx.fragment.app.FragmentActivity activity = obtenerActividad(v.getContext());

            if (activity == null) {
                Toast.makeText(v.getContext(), "No se pudo abrir el formulario", Toast.LENGTH_SHORT)
                     .show();
                return;
            }

            AgregarEmpleadoFragment fragment = new AgregarEmpleadoFragment();

            fragment.prepararEdicion(empleado);

            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_container, fragment).addToBackStack(null).commit();
        });


        // Configurar texto y colores según el estado
        boolean estaActivo = empleado.getEstado() != null && empleado.getEstado()
                                                                     .equalsIgnoreCase("activo");

        holder.btnDesactivar.setText(estaActivo ? "Desactivar" : "Activar");

        if (estaActivo) {
            holder.btnDesactivar.setTextColor(Color.parseColor("#DC2626"));

            holder.btnDesactivar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFE4E8")));
        } else {
            holder.btnDesactivar.setTextColor(Color.parseColor("#047857"));

            holder.btnDesactivar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1FAE5")));
        }

        // Popup de confirmación
        holder.btnDesactivar.setOnClickListener(v -> {

            boolean activoActual = empleado.getEstado() != null && empleado.getEstado()
                                                                           .equalsIgnoreCase("activo");

            String titulo = activoActual ? "Confirmar desactivación" : "Confirmar activación";

            String mensaje = activoActual ? "¿Estás seguro de que deseas desactivar a " + empleado.getNombre() + "?" : "¿Estás de acuerdo con activar a " + empleado.getNombre() + "?";

            new AlertDialog.Builder(v.getContext()).setTitle(titulo).setMessage(mensaje)
                                                   .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())


                                                   .setPositiveButton("Confirmar", (dialog, which) -> {

                                                       String nuevoEstado = activoActual ? "inactivo" : "activo";

                                                       try {
                                                           JSONObject json = new JSONObject();
                                                           json.put("estado", nuevoEstado);

                                                           RequestBody body = RequestBody.create(json.toString(), JSON);

                                                           Request request = new Request.Builder().url("http://2.25.188.174:3000/empleados/" + empleado.getId() + "/estado")
                                                                                                  .patch(body)
                                                                                                  .build();

                                                           client.newCall(request)
                                                                 .enqueue(new Callback() {

                                                                     @Override public void onFailure(
                                                                             Call call,
                                                                             IOException e) {
                                                                         mostrarMensaje(v, "Error de conexión al cambiar el estado");
                                                                     }

                                                                     @Override public void onResponse(
                                                                             Call call,
                                                                             Response response) throws IOException {

                                                                         try (Response resultado = response) {
                                                                             if (resultado.isSuccessful()) {

                                                                                 empleado.setEstado(nuevoEstado);

                                                                                 android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());

                                                                                 handler.post(() -> {
                                                                                     int pos = holder.getAdapterPosition();

                                                                                     if (pos != RecyclerView.NO_POSITION) {
                                                                                         notifyItemChanged(pos);
                                                                                     }
                                                                                     mostrarMensaje(v, "Empleado actualizado correctamente");
                                                                                 });

                                                                             } else {
                                                                                 mostrarMensaje(v, "No se pudo cambiar el estado del empleado");
                                                                             }
                                                                         }
                                                                     }
                                                                 });

                                                       } catch (Exception e) {
                                                           mostrarMensaje(v, "Error al procesar la solicitud");
                                                       }
                                                   })


                                                   .show();
        });
    }

    public void actualizarLista(List<Empleado> nuevaLista) {
        empleados = new ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

    @Override public int getItemCount() {
        return empleados.size();
    }

    static class EmpleadoViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre;
        TextView txtCargo;
        TextView txtArea;
        TextView txtCorreo;

        Button btnEditar;
        Button btnDesactivar;

        public EmpleadoViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombreGestion);
            txtCargo = itemView.findViewById(R.id.txtCargoGestion);
            txtArea = itemView.findViewById(R.id.txtAreaGestion);
            txtCorreo = itemView.findViewById(R.id.txtCorreoGestion);

            btnEditar = itemView.findViewById(R.id.btnEditarEmpleado);
            btnDesactivar = itemView.findViewById(R.id.btnDesactivarEmpleado);
        }


    }


    private void mostrarMensaje(View vista, String mensaje) {
        android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());

        handler.post(() -> Toast.makeText(vista.getContext(), mensaje, Toast.LENGTH_SHORT).show());
    }

    private androidx.fragment.app.FragmentActivity obtenerActividad(
            android.content.Context context) {

        while (context instanceof android.content.ContextWrapper) {
            if (context instanceof androidx.fragment.app.FragmentActivity) {
                return (androidx.fragment.app.FragmentActivity) context;
            }

            context = ((android.content.ContextWrapper) context).getBaseContext();
        }

        return null;
    }

}
