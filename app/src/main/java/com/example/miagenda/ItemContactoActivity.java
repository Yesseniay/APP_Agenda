package com.example.miagenda;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View; // Se usará si agregas un botón de eliminar
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.miagenda.AgendaContract.ContactoEntry;

public class ItemContactoActivity extends AppCompatActivity {

    private EditText etNombre, etNumero, etEmail, etNotas;
    private AgendaManager manager;
    private long contactoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Carga el layout del formulario de adición/edición
        setContentView(R.layout.activity_agregar_contacto);

        manager = new AgendaManager(this);


        etNombre = findViewById(R.id.et_nombre);
        etNumero = findViewById(R.id.et_numero);
        etEmail = findViewById(R.id.et_email);
        etNotas = findViewById(R.id.et_notas);
        Button btnGuardar = findViewById(R.id.btn_guardar);


        contactoId = getIntent().getLongExtra(ContactoActivity.EXTRA_CONTACTO_ID, -1);

        if (contactoId != -1) {
            btnGuardar.setText("ACTUALIZAR");
            cargarDatosContacto(contactoId);
        } else {
            Toast.makeText(this, "Error: No se recibió ID de contacto.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnGuardar.setOnClickListener(v -> actualizarContacto());
    }

    private void cargarDatosContacto(long id) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = manager.getDatabase();
            String selection = ContactoEntry.COLUMN_ID + " = ?";
            String[] selectionArgs = { String.valueOf(id) };

            cursor = db.query(
                    ContactoEntry.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                etNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactoEntry.COLUMN_NAME)));
                etNumero.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactoEntry.COLUMN_NUMERO)));
                etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactoEntry.COLUMN_EMAIL)));
                etNotas.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactoEntry.COLUMN_NOTAS)));
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void actualizarContacto() {
        String nombre = etNombre.getText().toString().trim();
        String numero = etNumero.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String notas = etNotas.getText().toString().trim();


        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (manager.actualizarContacto(contactoId, nombre, numero, email, notas) > 0) {
            Toast.makeText(this, "Contacto actualizado.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar.", Toast.LENGTH_LONG).show();
        }
    }


    private void eliminarContacto() {
        if (manager.eliminarContacto(contactoId) > 0) {
            Toast.makeText(this, "Contacto eliminado.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al eliminar.", Toast.LENGTH_SHORT).show();
        }
    }
}