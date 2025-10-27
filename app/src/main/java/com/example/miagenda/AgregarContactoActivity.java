package com.example.miagenda;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AgregarContactoActivity extends AppCompatActivity {


    private EditText etNombre, etNumero, etEmail, etNotas;
    private AgendaManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contacto);

        manager = new AgendaManager(this);

        // Referencias a los campos
        etNombre = findViewById(R.id.et_nombre);
        etNumero = findViewById(R.id.et_numero);
        etEmail = findViewById(R.id.et_email);
        etNotas = findViewById(R.id.et_notas);
        Button btnGuardar = findViewById(R.id.btn_guardar);



        btnGuardar.setOnClickListener(v -> guardarContacto());

    }

    private void guardarContacto() {
        String nombre = etNombre.getText().toString().trim();
        String numero = etNumero.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String notas = etNotas.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio.", Toast.LENGTH_SHORT).show();
            return;
        }

        long newRowId = manager.agregarContacto(nombre, numero, email, notas);

        if (newRowId != -1) {
            Toast.makeText(this, "Contacto guardado.", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la Activity y regresa a la lista
        } else {
            Toast.makeText(this, "Error al guardar.", Toast.LENGTH_LONG).show();
        }
    }
}