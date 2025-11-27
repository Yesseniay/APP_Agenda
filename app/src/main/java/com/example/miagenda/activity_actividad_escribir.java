package com.example.miagenda;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.miagenda.AgendaContract.ActividadEntry;

public class activity_actividad_escribir extends AppCompatActivity {

    private EditText etTitulo, etDescripcion;
    private TextView tvFecha;
    private AgendaManager manager;
    private long actividadId = -1;
    private String fechaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_escribir);

        manager = new AgendaManager(this);

        etTitulo = findViewById(R.id.et_actividad_titulo);
        etDescripcion = findViewById(R.id.et_actividad_descripcion);
        tvFecha = findViewById(R.id.tv_actividad_fecha);
        Button btnGuardar = findViewById(R.id.btn_actividad_guardar);

        actividadId = getIntent().getLongExtra(activity_calendario.EXTRA_ACTIVIDAD_ID, -1);
        fechaSeleccionada = getIntent().getStringExtra(activity_calendario.EXTRA_FECHA);

        if (fechaSeleccionada == null) {
            // Esto no debería suceder si vienes del calendario, pero es un caso seguro.
            // Aquí podrías obtener la fecha actual del sistema si fuera necesario.
            fechaSeleccionada = "Fecha no disponible";
        }

        tvFecha.setText("Fecha: " + fechaSeleccionada);

        if (actividadId != -1) {
            btnGuardar.setText("ACTUALIZAR");
            cargarDatosActividad(actividadId);
        } else {
            btnGuardar.setText("GUARDAR");
        }

        btnGuardar.setOnClickListener(v -> guardarOActualizarActividad());
    }

    private void cargarDatosActividad(long id) {
        Cursor cursor = manager.getActividadPorId(id);

        if (cursor != null && cursor.moveToFirst()) {
            etTitulo.setText(cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_TITULO)));
            etDescripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_DESCRIPCION)));
            fechaSeleccionada = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_FECHA));
            tvFecha.setText("Fecha: " + fechaSeleccionada);
        }
        if (cursor != null) cursor.close();
    }

    private void guardarOActualizarActividad() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título es obligatorio.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (actividadId == -1) {
            // CREAR
            long newRowId = manager.agregarActividad(titulo, descripcion, fechaSeleccionada);
            if (newRowId != -1) {
                Toast.makeText(this, "Actividad guardada.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al guardar.", Toast.LENGTH_LONG).show();
            }
        } else {
            // ACTUALIZAR
            if (manager.actualizarActividad(actividadId, titulo, descripcion, fechaSeleccionada) > 0) {
                Toast.makeText(this, "Actividad actualizada.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar.", Toast.LENGTH_LONG).show();
            }
        }
    }
}