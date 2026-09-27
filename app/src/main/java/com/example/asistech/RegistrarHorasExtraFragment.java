package com.example.asistech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RegistrarHorasExtraFragment extends Fragment {

    public RegistrarHorasExtraFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_registrar_horas_extra,
                container,
                false
        );

        // Registrar Horas extras
        View btnEnviarSolicitud =
                view.findViewById(R.id.btnEnviarSolicitudHoraExtra);

        btnEnviarSolicitud.setOnClickListener(v ->
                enviarSolicitudHoraExtra()
        );

        // Botón regresar
        View btnRegresar =
                view.findViewById(R.id.btnRegresar);

        btnRegresar.setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );

        return view;
    }

    private void enviarSolicitudHoraExtra() {
        android.widget.Toast.makeText(
                requireContext(),
                "Solicitud de horas extras enviada",
                android.widget.Toast.LENGTH_SHORT
        ).show();

        getParentFragmentManager().popBackStack();
    }
}