package com.example.asistech;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmpleadoAdapter
        extends RecyclerView.Adapter<EmpleadoAdapter.EmpleadoViewHolder> {

    private final List<Empleado> empleados;

    public EmpleadoAdapter(List<Empleado> empleados) {
        this.empleados = empleados;
    }

    @NonNull
    @Override
    public EmpleadoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_empleado, parent, false);

        return new EmpleadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull EmpleadoViewHolder holder,
            int position) {

        Empleado empleado = empleados.get(position);

        // Nombre
        holder.txtNombre.setText(
                empleado.getNombre()
        );

        // Área
        holder.txtArea.setText(
                empleado.getArea()
        );

        // Cargo
        holder.txtCargo.setText(
                empleado.getCargo()
        );

        // Estado de asistencia
        String asistencia = empleado.getAsistencia();

        if (asistencia == null || asistencia.isEmpty()) {
            asistencia = "ausente";
        }

        if (asistencia.equalsIgnoreCase("presente")) {

            holder.txtEstado.setText("Activo");

            holder.txtEstado.setBackgroundResource(
                    R.drawable.bg_estado_activo
            );

            holder.txtEstado.setTextColor(
                    Color.parseColor("#047857")
            );

        } else {

            holder.txtEstado.setText("Ausente");

            holder.txtEstado.setBackgroundResource(
                    R.drawable.bg_estado_ausente
            );

            holder.txtEstado.setTextColor(
                    Color.parseColor("#DC2626")
            );
        }
    }

    @Override
    public int getItemCount() {
        return empleados.size();
    }

    static class EmpleadoViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtNombre;
        TextView txtArea;
        TextView txtCargo;
        TextView txtEstado;

        public EmpleadoViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtNombre = itemView.findViewById(
                    R.id.txtNombreEmpleado
            );

            txtArea = itemView.findViewById(
                    R.id.txtAreaCargo
            );

            txtCargo = itemView.findViewById(
                    R.id.txtCargoEmpleado
            );

            txtEstado = itemView.findViewById(
                    R.id.txtEstadoEmpleado
            );
        }
    }
}