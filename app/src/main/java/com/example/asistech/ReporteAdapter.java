package com.example.asistech;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReporteAdapter
        extends RecyclerView.Adapter<ReporteAdapter.ReporteViewHolder> {

    private final List<Empleado> empleados = new ArrayList<>();

    public ReporteAdapter(List<Empleado> listaEmpleados) {
        if (listaEmpleados != null) {
            empleados.addAll(listaEmpleados);
        }
    }

    @NonNull
    @Override
    public ReporteViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View vista = LayoutInflater.from(parent.getContext())
                                   .inflate(R.layout.item_reporte, parent, false);

        return new ReporteViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ReporteViewHolder holder,
            int position) {

        Empleado empleado = empleados.get(position);

        holder.txtNombre.setText(empleado.getNombre());
        holder.txtArea.setText(empleado.getArea());
        holder.txtCargo.setText(empleado.getCargo());

        // Horas registradas.
        holder.txtHoras.setText(
                empleado.getTotalHoras() + " h "
                        + empleado.getMinutos() + " min"
        );
    }

    @Override
    public int getItemCount() {
        return empleados.size();
    }

    public void actualizarLista(List<Empleado> nuevaLista) {
        empleados.clear();

        if (nuevaLista != null) {
            empleados.addAll(nuevaLista);
        }

        notifyDataSetChanged();
    }

    static class ReporteViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre;
        TextView txtArea;
        TextView txtCargo;
        TextView txtHoras;

        ReporteViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombreReporte);
            txtArea = itemView.findViewById(R.id.txtAreaReporte);
            txtCargo = itemView.findViewById(R.id.txtCargoReporte);
            txtHoras = itemView.findViewById(R.id.txtHorasReporte);
        }
    }
}