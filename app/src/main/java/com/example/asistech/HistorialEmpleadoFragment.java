package com.example.asistech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HistorialEmpleadoFragment extends Fragment {

    public HistorialEmpleadoFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_historial_empleado,
                container,
                false
        );

        View btnRegresar =
                view.findViewById(R.id.btnRegresar);

        btnRegresar.setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );

        return view;
    }
}