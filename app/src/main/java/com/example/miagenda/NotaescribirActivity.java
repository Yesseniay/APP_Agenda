package com.example.miagenda;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.miagenda.AgendaContract.NotaEntry;

public class NotaescribirActivity extends AppCompatActivity {

    private EditText etTitulo, etContenido;
    private TextView tvTituloPantalla;
    private AgendaManager manager;
    private long notaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notaescribir);

        manager = new AgendaManager(this);

        tvTituloPantalla = findViewById(R.id.txtTituloPantalla);
        etTitulo = findViewById(R.id.Titulo);
        etContenido = findViewById(R.id.Contenido);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        notaId = getIntent().getLongExtra(NotaActivity.EXTRA_NOTA_ID, -1);

        if (notaId != -1) {
            tvTituloPantalla.setText("Editar Nota");
            cargarDatosNota(notaId);
        } else {
            tvTituloPantalla.setText("Nueva Nota");
        }

        btnGuardar.setOnClickListener(v -> guardarOActualizarNota());
    }

    private void cargarDatosNota(long id) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = manager.getDatabase();
            String selection = NotaEntry.COLUMN_ID + " = ?";
            String[] selectionArgs = { String.valueOf(id) };

            cursor = db.query(
                    NotaEntry.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                etTitulo.setText(cursor.getString(cursor.getColumnIndexOrThrow(NotaEntry.COLUMN_TITULO)));
                etContenido.setText(cursor.getString(cursor.getColumnIndexOrThrow(NotaEntry.COLUMN_CONTENIDO)));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar datos.", Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void guardarOActualizarNota() {
        String titulo = etTitulo.getText().toString().trim();
        String contenido = etContenido.getText().toString().trim();

        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título no puede estar vacío.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (notaId == -1) {

            long newRowId = manager.agregarNota(titulo, contenido);
            if (newRowId != -1) {
                Toast.makeText(this, "Nota guardada.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al guardar la nota.", Toast.LENGTH_LONG).show();
            }
        } else {

            if (manager.actualizarNota(notaId, titulo, contenido) > 0) {
                Toast.makeText(this, "Nota actualizada.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar la nota.", Toast.LENGTH_LONG).show();
            }
        }
    }
}