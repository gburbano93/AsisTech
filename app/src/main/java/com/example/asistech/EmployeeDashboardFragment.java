package com.example.asistech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EmployeeDashboardFragment extends Fragment {

    public EmployeeDashboardFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_employee_dashboard,
                container,
                false
        );

        // Marcar Entrada
        View btnMarcarEntrada =
                view.findViewById(R.id.btnMarcarEntrada);

        btnMarcarEntrada.setOnClickListener(v ->
                marcarEntrada()
        );

        // Marcar Salida
        View btnMarcarSalida =
                view.findViewById(R.id.btnMarcarSalida);

        btnMarcarSalida.setOnClickListener(v ->
                marcarSalida()
        );

        // --------------------------------
        // Perfil
        // --------------------------------

        View btnPerfilEmpleado =
                view.findViewById(R.id.btnPerfilEmpleado);

        btnPerfilEmpleado.setOnClickListener(v -> {

            PerfilFragment perfilFragment =
                    new PerfilFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.perfil_container,
                            perfilFragment
                    )
                    .commit();

            requireActivity()
                    .findViewById(R.id.perfil_container)
                    .setVisibility(View.VISIBLE);
        });

        // --------------------------------
        // Registrar Horas Extras
        // --------------------------------

        View btnHorasExtraEmpleado =
                view.findViewById(R.id.btnHorasExtraEmpleado);

        btnHorasExtraEmpleado.setOnClickListener(v -> {

            RegistrarHorasExtraFragment fragment =
                    new RegistrarHorasExtraFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.content_container,
                            fragment
                    )
                    .addToBackStack(null)
                    .commit();
        });

        // --------------------------------
        // Mi Historial
        // --------------------------------

        View btnHistorialEmpleado =
                view.findViewById(R.id.btnHistorialEmpleado);

        btnHistorialEmpleado.setOnClickListener(v -> {

            HistorialEmpleadoFragment fragment =
                    new HistorialEmpleadoFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.content_container,
                            fragment
                    )
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void marcarEntrada() {

        android.widget.Toast.makeText(
                requireContext(),
                "Entrada registrada correctamente",
                android.widget.Toast.LENGTH_SHORT
        ).show();
    }

    private void marcarSalida() {
        android.widget.Toast.makeText(
                requireContext(),
                "Salida registrada correctamente",
                android.widget.Toast.LENGTH_SHORT
        ).show();
    }
}