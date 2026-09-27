package com.example.asistech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HorasExtraFragment extends Fragment {

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

        // Botón Aprobar horas extra
        View btnAprobarHoraExtra =
                view.findViewById(R.id.btnAprobarHoraExtra);

        btnAprobarHoraExtra.setOnClickListener(v ->
                aprobarHoraExtra()
        );

        return view;
    }

    private void aprobarHoraExtra() {
        android.widget.Toast.makeText(
                requireContext(),
                "Hora extra aprobada",
                android.widget.Toast.LENGTH_SHORT
        ).show();
    }
}