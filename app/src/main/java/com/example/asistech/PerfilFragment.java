package com.example.asistech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PerfilFragment extends Fragment {

    public PerfilFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_perfil,
                container,
                false
        );

        // Botón regresar
        View btnRegresar =
                view.findViewById(R.id.btnRegresarPerfil);

        btnRegresar.setOnClickListener(v -> {

            requireActivity()
                    .findViewById(R.id.perfil_container)
                    .setVisibility(View.GONE);
        });

        return view;
    }
}