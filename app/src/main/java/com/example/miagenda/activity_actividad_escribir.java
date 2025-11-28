package com.example.miagenda;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.miagenda.AgendaContract.ActividadEntry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class activity_actividad_escribir extends AppCompatActivity {

    private EditText etTitulo, etDescripcion;
    private Switch switchNotificacion;
    private TextView tvFecha;
    private AutoCompleteTextView autoCompleteCategoria;
    private AgendaManager manager;
    private long actividadId = -1;
    private String fechaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_escribir);

        manager = new AgendaManager(this);

        // Inicializar vistas
        initViews();
        setupCategorySpinner();

        // Obtener datos del intent
        if (getIntent().hasExtra(activity_calendario.EXTRA_ACTIVIDAD_ID)) {
            actividadId = getIntent().getLongExtra(activity_calendario.EXTRA_ACTIVIDAD_ID, -1);
        }

        if (getIntent().hasExtra(activity_calendario.EXTRA_FECHA)) {
            fechaSeleccionada = getIntent().getStringExtra(activity_calendario.EXTRA_FECHA);
        }

        // Si no hay fecha, usar la actual
        if (fechaSeleccionada == null) {
            fechaSeleccionada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }

        tvFecha.setText("Fecha: " + fechaSeleccionada);

        Button btnGuardar = findViewById(R.id.btn_actividad_guardar);

        if (actividadId != -1) {
            btnGuardar.setText("ACTUALIZAR");
            cargarDatosActividad(actividadId);
        } else {
            btnGuardar.setText("GUARDAR");
            // Por defecto, notificación activada para nuevas actividades
            switchNotificacion.setChecked(true);
        }

        btnGuardar.setOnClickListener(v -> guardarOActualizarActividad());
    }

    private void initViews() {
        etTitulo = findViewById(R.id.et_actividad_titulo);
        etDescripcion = findViewById(R.id.et_actividad_descripcion);
        switchNotificacion = findViewById(R.id.switchNotificacion);
        tvFecha = findViewById(R.id.tv_actividad_fecha);
        autoCompleteCategoria = findViewById(R.id.actividad_categoria);
    }

    // Este es el método correcto para cargar las categorías
    private void setupCategorySpinner() {
        String[] categories;
        try {
            categories = ColorApiHelper.getAvailableCategories();
        } catch (Exception e) {
            // Si falla o no existe la clase, usamos una lista básica por seguridad
            categories = new String[]{"General", "Trabajo", "Personal", "Urgente", "Salud"};
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories
        );
        autoCompleteCategoria.setAdapter(adapter);

        // Valor por defecto si el campo está vacío
        if (autoCompleteCategoria.getText().toString().isEmpty()) {
            autoCompleteCategoria.setText("General", false);
        }
    }

    private void cargarDatosActividad(long id) {
        Cursor cursor = manager.getActividadPorId(id);

        if (cursor != null && cursor.moveToFirst()) {
            etTitulo.setText(cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_TITULO)));
            etDescripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_DESCRIPCION)));

            // Recuperamos la fecha guardada
            fechaSeleccionada = cursor.getString(cursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_FECHA));
            tvFecha.setText("Fecha: " + fechaSeleccionada);

            // Cargar estado de notificación si existe en la base de datos
            int notificacionIndex = cursor.getColumnIndex(ActividadEntry.COLUMN_NOTIFICACION);
            if (notificacionIndex != -1) {
                int notificacion = cursor.getInt(notificacionIndex);
                switchNotificacion.setChecked(notificacion == 1);
            }

            // Cargar categoría
            int categoriaIndex = cursor.getColumnIndex(ActividadEntry.COLUMN_CATEGORIA);
            if (categoriaIndex != -1) {
                String categoria = cursor.getString(categoriaIndex);
                autoCompleteCategoria.setText(categoria, false);
            }
        }
        if (cursor != null) cursor.close();
    }

    private void guardarOActualizarActividad() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String categoria = autoCompleteCategoria.getText().toString().trim();
        boolean notificacionActivada = switchNotificacion.isChecked();

        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título es obligatorio.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (categoria.isEmpty()) {
            categoria = "General"; // Valor por defecto
        }

        if (actividadId == -1) {
            // CREAR NUEVA ACTIVIDAD
            long newRowId = manager.agregarActividadCompleta(
                    titulo,
                    descripcion,
                    fechaSeleccionada,
                    "", // hora
                    0,  // completada = false
                    notificacionActivada ? 1 : 0, // notificacion
                    categoria // NUEVO PARÁMETRO
            );

            if (newRowId != -1) {
                Toast.makeText(this, "Actividad guardada.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al guardar.", Toast.LENGTH_LONG).show();
            }
        } else {
            // ACTUALIZAR ACTIVIDAD EXISTENTE
            int resultado = manager.actualizarActividadCompleta(
                    actividadId,
                    titulo,
                    descripcion,
                    fechaSeleccionada,
                    "", // hora
                    0,  // completada
                    notificacionActivada ? 1 : 0, // notificacion
                    categoria // NUEVO PARÁMETRO
            );

            if (resultado > 0) {
                Toast.makeText(this, "Actividad actualizada.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar.", Toast.LENGTH_LONG).show();
            }
        }
    }
}