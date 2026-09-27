package com.example.asistech;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmpleadosGestionAdapter
        extends RecyclerView.Adapter<EmpleadosGestionAdapter.EmpleadoViewHolder> {

    private final List<Empleado> empleados;

    public EmpleadosGestionAdapter(List<Empleado> empleados) {
        this.empleados = empleados;
    }

    @NonNull
    @Override
    public EmpleadoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_empleado_gestion,
                        parent,
                        false
                );

        return new EmpleadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull EmpleadoViewHolder holder,
            int position) {

        Empleado empleado = empleados.get(position);

        // Datos del empleado
        holder.txtNombre.setText(empleado.getNombre());
        holder.txtCargo.setText(empleado.getCargo());
        holder.txtArea.setText(
                "Área: " + empleado.getArea()
        );
        holder.txtCorreo.setText(
                empleado.getCorreo()
        );

        // Estado
        String estado = empleado.getEstado();

        if (estado != null &&
                estado.equalsIgnoreCase("activo")) {

            holder.txtEstado.setText("Activo");

            holder.txtEstado.setBackgroundResource(
                    R.drawable.bg_estado_activo
            );

            holder.txtEstado.setTextColor(
                    Color.parseColor("#047857")
            );

        } else {

            holder.txtEstado.setText("Inactivo");

            holder.txtEstado.setBackgroundResource(
                    R.drawable.bg_estado_ausente
            );

            holder.txtEstado.setTextColor(
                    Color.parseColor("#DC2626")
            );
        }

        // Botón editar
        holder.btnEditar.setOnClickListener(v -> {

            // Más adelante abriremos la edición del empleado

        });

        // Botón desactivar
        holder.btnDesactivar.setOnClickListener(v -> {

            // Más adelante programaremos la desactivación

        });
    }

    @Override
    public int getItemCount() {
        return empleados.size();
    }

    static class EmpleadoViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtNombre;
        TextView txtCargo;
        TextView txtArea;
        TextView txtCorreo;
        TextView txtEstado;

        Button btnEditar;
        Button btnDesactivar;

        public EmpleadoViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtNombre = itemView.findViewById(
                    R.id.txtNombreGestion
            );

            txtCargo = itemView.findViewById(
                    R.id.txtCargoGestion
            );

            txtArea = itemView.findViewById(
                    R.id.txtAreaGestion
            );

            txtCorreo = itemView.findViewById(
                    R.id.txtCorreoGestion
            );

            txtEstado = itemView.findViewById(
                    R.id.txtEstadoGestion
            );

            btnEditar = itemView.findViewById(
                    R.id.btnEditarEmpleado
            );

            btnDesactivar = itemView.findViewById(
                    R.id.btnDesactivarEmpleado
            );
        }
    }
}