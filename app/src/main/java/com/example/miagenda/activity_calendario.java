package com.example.miagenda;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

        manager = new AgendaManager(this);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        calendarView = findViewById(R.id.calendar_view);
        RecyclerView recyclerView = findViewById(R.id.rv_actividades);
        FloatingActionButton fabAddActividad = findViewById(R.id.fab_add_actividad);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Establecer la fecha actual como inicial
        Calendar cal = Calendar.getInstance();
        fechaActualSeleccionada = dateFormat.format(cal.getTime());

        Cursor initialCursor = manager.buscarActividadesPorFecha(fechaActualSeleccionada);
        adapter = new ActividadCursorAdapter(initialCursor, this);
        recyclerView.setAdapter(adapter);

        // Listener para el cambio de fecha en el calendario
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            cal.set(year, month, dayOfMonth);
            fechaActualSeleccionada = dateFormat.format(cal.getTime());
            cargarActividades(fechaActualSeleccionada);
        });

        // Botón FAB para agregar una nueva actividad en la fecha seleccionada
        fabAddActividad.setOnClickListener(v -> {
            Intent intent = new Intent(activity_calendario.this, activity_actividad_escribir.class);
            intent.putExtra(EXTRA_FECHA, fechaActualSeleccionada);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar la lista al volver de crear o editar
        cargarActividades(fechaActualSeleccionada);
    }

    private void cargarActividades(String fecha) {
        Cursor nuevoCursor = manager.buscarActividadesPorFecha(fecha);
        adapter.swapCursor(nuevoCursor);
    }

    // Implementación del Adaptador: Navega a la Activity de edición
    @Override
    public void onEditClick(long id) {
        Intent intent = new Intent(activity_calendario.this, activity_actividad_escribir.class);
        intent.putExtra(EXTRA_ACTIVIDAD_ID, id);
        startActivity(intent);
    }

    // Implementación del Adaptador: Elimina directamente
    @Override
    public void onDeleteClick(long id) {
        if (manager.eliminarActividad(id) > 0) {
            Toast.makeText(this, "Actividad eliminada.", Toast.LENGTH_SHORT).show();
            cargarActividades(fechaActualSeleccionada); // Actualiza la lista
        } else {
            Toast.makeText(this, "Error al eliminar la actividad.", Toast.LENGTH_SHORT).show();
        }
    }
}