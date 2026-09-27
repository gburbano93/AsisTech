package com.example.asistech;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recibir los datos enviados desde LoginActivity
        String nombre = getIntent().getStringExtra("nombre");
        String rol = getIntent().getStringExtra("rol");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        LinearLayout bottomMenu = findViewById(R.id.bottom_menu);

        if ("empleado".equalsIgnoreCase(rol)) {
            bottomMenu.setVisibility(View.GONE);
        }

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // Mostrar el dashboard correspondiente al usuario
        if (savedInstanceState == null) {

            Bundle datos = new Bundle();
            datos.putString("nombre", nombre);
            datos.putString("rol", rol);

            if ("empleado".equalsIgnoreCase(rol)) {

                EmployeeDashboardFragment fragment =
                        new EmployeeDashboardFragment();

                fragment.setArguments(datos);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.content_container,
                                fragment
                        )
                        .commit();

            } else {

                EmployerDashboardFragment fragment =
                        new EmployerDashboardFragment();

                fragment.setArguments(datos);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.content_container,
                                fragment
                        )
                        .commit();
            }
        }

        // Botón Inicio
        LinearLayout menuInicio =
                findViewById(R.id.menuInicio);

        menuInicio.setOnClickListener(v -> {

            EmployerDashboardFragment fragment =
                    new EmployerDashboardFragment();

            Bundle datos = new Bundle();
            datos.putString("nombre", nombre);
            datos.putString("rol", rol);

            fragment.setArguments(datos);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.content_container,
                            fragment
                    )
                    .commit();
        });

        // Botón Empleados
        LinearLayout menuEmpleados =
                findViewById(R.id.menuEmpleados);

        menuEmpleados.setOnClickListener(v -> {

            EmpleadosFragment fragment =
                    new EmpleadosFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.content_container,
                            fragment
                    )
                    .commit();
        });

        // Botón Reportes
        LinearLayout menuReportes =
                findViewById(R.id.menuReportes);

        menuReportes.setOnClickListener(v -> {

            ReportesFragment fragment =
                    new ReportesFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.content_container,
                            fragment
                    )
                    .commit();
        });

        // Botón Horas extra
        LinearLayout menuHorasExtra =
                findViewById(R.id.menuHorasExtra);

        menuHorasExtra.setOnClickListener(v -> {

            HorasExtraFragment fragment =
                    new HorasExtraFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.content_container,
                            fragment
                    )
                    .commit();
        });

    }
}