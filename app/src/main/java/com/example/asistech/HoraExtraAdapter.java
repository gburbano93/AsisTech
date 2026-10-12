package com.example.asistech;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.MediaType;

public class HoraExtraAdapter extends RecyclerView.Adapter<HoraExtraAdapter.HoraExtraViewHolder> {

    private final List<HoraExtra> solicitudes = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();

    public HoraExtraAdapter(List<HoraExtra> lista) {
        if (lista != null) {
            solicitudes.addAll(lista);
        }
    }

    @NonNull @Override public HoraExtraViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                     int viewType) {

        View vista = LayoutInflater.from(parent.getContext())
                                   .inflate(R.layout.item_hora_extra, parent, false);

        return new HoraExtraViewHolder(vista);
    }

    @Override public void onBindViewHolder(@NonNull HoraExtraViewHolder holder, int position) {

        HoraExtra solicitud = solicitudes.get(position);

        holder.txtNombre.setText(solicitud.getNombre());
        holder.txtArea.setText("Área: " + solicitud.getArea());
        holder.txtCargo.setText(solicitud.getCargo());
        holder.txtFecha.setText(solicitud.getFecha());
        holder.txtHoras.setText(solicitud.getHoras() + " horas");
        holder.txtMotivo.setText(solicitud.getMotivo());
        holder.txtEstado.setText(solicitud.getEstado());

        holder.btnAprobar.setEnabled("pendiente".equalsIgnoreCase(solicitud.getEstado()));

        holder.btnAprobar.setOnClickListener(v -> {
            int idSolicitud = solicitud.getId();

            RequestBody cuerpo = RequestBody.create(new byte[0], MediaType.parse("application/json"));

            Request request = new Request.Builder().url("http://2.25.188.174:3000/horas-extra/" + idSolicitud + "/aprobar")
                                                   .patch(cuerpo).build();

            holder.btnAprobar.setEnabled(false);

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    mostrarResultado(holder, false, "Error de conexión al aprobar");
                }

                @Override public void onResponse(@NonNull Call call, @NonNull Response response) {

                    boolean aprobado = response.isSuccessful();
                    response.close();

                    mostrarResultado(holder, aprobado, aprobado ? "Solicitud aprobada" : "No se pudo aprobar la solicitud");

                    if (aprobado) {
                        holder.itemView.post(() -> {
                            int posicion = holder.getAdapterPosition();

                            if (posicion != RecyclerView.NO_POSITION) {
                                solicitudes.remove(posicion);
                                notifyItemRemoved(posicion);
                            }
                        });
                    }
                }
            });
        });
    }

    private void mostrarResultado(HoraExtraViewHolder holder, boolean aprobado, String mensaje) {

        if (!(holder.itemView.getContext() instanceof FragmentActivity)) {
            return;
        }

        FragmentActivity activity = (FragmentActivity) holder.itemView.getContext();

        activity.runOnUiThread(() -> {
            if (activity.isFinishing() || activity.isDestroyed()) {
                return;
            }

            Toast.makeText(activity, mensaje, Toast.LENGTH_SHORT).show();

            if (!aprobado) {
                holder.btnAprobar.setEnabled(true);
            }
        });
    }

    @Override public int getItemCount() {
        return solicitudes.size();
    }

    public void actualizarLista(List<HoraExtra> nuevaLista) {
        solicitudes.clear();
        solicitudes.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    static class HoraExtraViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtArea, txtCargo;
        TextView txtFecha, txtHoras, txtMotivo, txtEstado;
        Button btnAprobar;

        public HoraExtraViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombreHoraExtra);
            txtArea = itemView.findViewById(R.id.txtAreaHoraExtra);
            txtCargo = itemView.findViewById(R.id.txtCargoHoraExtra);
            txtFecha = itemView.findViewById(R.id.txtFechaHoraExtra);
            txtHoras = itemView.findViewById(R.id.txtHorasHoraExtra);
            txtMotivo = itemView.findViewById(R.id.txtMotivoHoraExtra);
            txtEstado = itemView.findViewById(R.id.txtEstadoHoraExtra);
            btnAprobar = itemView.findViewById(R.id.btnAprobarHoraExtra);
        }
    }
}
