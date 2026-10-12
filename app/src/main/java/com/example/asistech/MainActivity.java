
package com.example.asistech;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String nombre;
    private String rol;
    private int usuarioId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recibir los datos enviados desde LoginActivity
        nombre = getIntent().getStringExtra("nombre");
        rol = getIntent().getStringExtra("rol");


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        usuarioId = getIntent().getIntExtra("usuario_id", -1);

        LinearLayout bottomMenu = findViewById(R.id.bottom_menu);

        // Ocultar menú inferior para empleados
        if ("empleado".equalsIgnoreCase(rol)) {
            bottomMenu.setVisibility(View.GONE);
        }

        // Ajustar la pantalla a las barras del sistema
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

        // Mostrar dashboard inicial
        if (savedInstanceState == null) {

            Bundle datos = new Bundle();
            datos.putString("nombre", nombre);
            datos.putString("rol", rol);
            datos.putInt("usuario_id", usuarioId);

            if ("empleado".equalsIgnoreCase(rol)) {

                EmployeeDashboardFragment fragment =
                        new EmployeeDashboardFragment();

                fragment.setArguments(datos);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_container, fragment)
                        .commit();

            } else {

                EmployerDashboardFragment fragment =
                        new EmployerDashboardFragment();

                fragment.setArguments(datos);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_container, fragment)
                        .commit();

                actualizarMenuSeleccionado(R.id.menuInicio);
            }
        }

        // Botón Inicio
        LinearLayout menuInicio = findViewById(R.id.menuInicio);

        menuInicio.setOnClickListener(v -> {

            actualizarMenuSeleccionado(R.id.menuInicio);

            EmployerDashboardFragment fragment =
                    new EmployerDashboardFragment();

            Bundle datos = new Bundle();
            datos.putString("nombre", nombre);
            datos.putString("rol", rol);
            datos.putInt("usuario_id", usuarioId);

            fragment.setArguments(datos);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, fragment)
                    .commit();
        });

        // Botón Empleados
        LinearLayout menuEmpleados = findViewById(R.id.menuEmpleados);

        menuEmpleados.setOnClickListener(v -> {

            actualizarMenuSeleccionado(R.id.menuEmpleados);

            EmpleadosFragment fragment = new EmpleadosFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, fragment)
                    .commit();
        });

        // Botón Reportes
        LinearLayout menuReportes = findViewById(R.id.menuReportes);

        menuReportes.setOnClickListener(v -> {

            actualizarMenuSeleccionado(R.id.menuReportes);

            ReportesFragment fragment = new ReportesFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, fragment)
                    .commit();
        });

        // Botón Horas extra
        LinearLayout menuHorasExtra = findViewById(R.id.menuHorasExtra);

        menuHorasExtra.setOnClickListener(v -> {

            actualizarMenuSeleccionado(R.id.menuHorasExtra);

            HorasExtraFragment fragment = new HorasExtraFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, fragment)
                    .commit();
        });
    }

    // Actualizar el elemento seleccionado del menú inferior
    private void actualizarMenuSeleccionado(int idSeleccionado) {

        int[] ids = {
                R.id.menuInicio,
                R.id.menuEmpleados,
                R.id.menuReportes,
                R.id.menuHorasExtra
        };

        for (int id : ids) {

            View elemento = findViewById(id);

            if (elemento != null) {
                elemento.setSelected(id == idSeleccionado);
            }
        }
    }
}
