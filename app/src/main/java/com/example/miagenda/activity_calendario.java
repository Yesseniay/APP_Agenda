package com.example.miagenda;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class activity_calendario extends AppCompatActivity
        implements ActividadCursorAdapter.OnActionListener {

    public static final String EXTRA_ACTIVIDAD_ID = "actividad_id";
    public static final String EXTRA_FECHA = "fecha_seleccionada";

    private AgendaManager manager;
    private ActividadCursorAdapter adapter;
    private CalendarView calendarView;
    private String fechaActualSeleccionada;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        try {
            manager = new AgendaManager(this);
            dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            calendarView = findViewById(R.id.calendar_view);
            RecyclerView recyclerView = findViewById(R.id.rv_actividades);
            FloatingActionButton fabAddActividad = findViewById(R.id.fab_add_actividad);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Establecer la fecha actual como inicial
            Calendar cal = Calendar.getInstance();
            fechaActualSeleccionada = dateFormat.format(cal.getTime());

            // Cargar datos (si la DB está vacía o nueva, esto funcionará)
            Cursor initialCursor = manager.buscarActividadesPorFecha(fechaActualSeleccionada);
            adapter = new ActividadCursorAdapter(initialCursor, this);
            recyclerView.setAdapter(adapter);

            // Listener para el cambio de fecha
            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                fechaActualSeleccionada = dateFormat.format(c.getTime());
                cargarActividades(fechaActualSeleccionada);
            });

            // --- BOTÓN CON PROTECCIÓN CONTRA ERRORES ---
            fabAddActividad.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(activity_calendario.this, activity_actividad_escribir.class);
                    // Aseguramos que la fecha no vaya nula
                    if (fechaActualSeleccionada == null) {
                        fechaActualSeleccionada = dateFormat.format(Calendar.getInstance().getTime());
                    }
                    intent.putExtra(EXTRA_FECHA, fechaActualSeleccionada);
                    startActivity(intent);
                } catch (Exception e) {
                    // Si falla, te dirá el error en pantalla
                    Toast.makeText(activity_calendario.this, "Error al abrir: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error de inicio: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (manager != null && fechaActualSeleccionada != null) {
            cargarActividades(fechaActualSeleccionada);
        }
    }

    private void cargarActividades(String fecha) {
        if (manager == null || adapter == null) return;
        try {
            Cursor nuevoCursor = manager.buscarActividadesPorFecha(fecha);
            adapter.swapCursor(nuevoCursor);
        } catch (Exception e) {
            Log.e("Calendario", "Error cargando actividades", e);
        }
    }

    @Override
    public void onEditClick(long id) {
        try {
            Intent intent = new Intent(activity_calendario.this, activity_actividad_escribir.class);
            intent.putExtra(EXTRA_ACTIVIDAD_ID, id);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error al editar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(long id) {
        try {
            if (manager.eliminarActividad(id) > 0) {
                Toast.makeText(this, "Actividad eliminada.", Toast.LENGTH_SHORT).show();
                cargarActividades(fechaActualSeleccionada);
            } else {
                Toast.makeText(this, "Error al eliminar.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error de base de datos", Toast.LENGTH_SHORT).show();
        }
    }
}