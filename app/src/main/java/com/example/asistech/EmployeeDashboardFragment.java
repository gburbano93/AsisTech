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

        // Registrar Horas Extras
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

        // Mi Historial
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
}