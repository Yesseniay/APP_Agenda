package com.example.miagenda;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button; // Importante: Button para los botones con texto
import android.widget.EditText; // Importante: EditText porque tu XML usa EditText
import android.widget.ImageButton; // Para llamar y favorito
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog; // Asegúrate de tener este import
import androidx.appcompat.app.AppCompatActivity;

public class ItemContactoActivity extends AppCompatActivity {

    // 1. CAMBIO: Usamos EditText porque tu XML tiene EditText, no TextView
    private EditText etNombre, etNumero, etEmail, etNotas;

    // 2. CAMBIO: btnEditar, btnEliminar y btnGuardar son Button (con texto), NO ImageButton
    private Button btnEditar, btnEliminar, btnGuardar;
    private ImageButton btnFavorito, btnLlamar; // Estos sí son ImageButton en tu XML

    private AgendaManager manager;
    private long contactoId = -1;
    private boolean esFavorito = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_contacto); // Vinculamos con tu XML

        manager = new AgendaManager(this);


        contactoId = getIntent().getLongExtra("CONTACTO_ID", -1);


        // Campos de texto
        etNombre = findViewById(R.id.et_nombre);
        etNumero = findViewById(R.id.et_numero);
        etEmail = findViewById(R.id.et_email);
        etNotas = findViewById(R.id.et_notas);

        // Botones de imagen
        btnFavorito = findViewById(R.id.btn_favorito);
        btnLlamar = findViewById(R.id.btn_llamar);

        // Botones de texto
        btnEditar = findViewById(R.id.btn_editar);
        btnEliminar = findViewById(R.id.btn_eliminar);
        btnGuardar = findViewById(R.id.btn_guardar);

        // Validar si recibimos un ID válido
        if (contactoId != -1) {
            cargarDatosContacto(contactoId);

            deshabilitarEdicion();
        } else {
            Toast.makeText(this, "Error: No se recibió ID de contacto.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //  accion de llamar
        btnFavorito.setOnClickListener(v -> toggleFavorito());
        btnLlamar.setOnClickListener(v -> realizarLlamada());

        // El botón editar
        btnEditar.setOnClickListener(v -> abrirEdicion());

        btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());

        // Botón guardar
        btnGuardar.setOnClickListener(v -> guardarCambios());
    }

    private void deshabilitarEdicion() {
        // Hacemos que los campos sean solo lectura al principio
        etNombre.setEnabled(false);
        etNumero.setEnabled(false);
        etEmail.setEnabled(false);
        etNotas.setEnabled(false);
    }

    private void cargarDatosContacto(long id) {
        Cursor cursor = manager.obtenerContactoPorId(id);
        if (cursor != null && cursor.moveToFirst()) {
            // Asegúrate que las columnas "nombre", "numero", etc. existan en tu DB
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            String numero = cursor.getString(cursor.getColumnIndexOrThrow("numero"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            // Intenta obtener notas si existe la columna, si no, déjalo vacío
            String notas = "";
            try {
                notas = cursor.getString(cursor.getColumnIndexOrThrow("notas"));
            } catch (Exception e) { notas = ""; }

            // Asignamos a los EditText
            etNombre.setText(nombre);
            etNumero.setText(numero);
            etEmail.setText(email);
            etNotas.setText(notas);

            // Configurar favorito
            int favorito = cursor.getInt(cursor.getColumnIndexOrThrow("favorito"));
            esFavorito = (favorito == 1);
            actualizarIconoFavorito();

            cursor.close();
        }
    }

    private void toggleFavorito() {
        esFavorito = !esFavorito;
        actualizarIconoFavorito();
        if (manager.actualizarFavorito(contactoId, esFavorito ? 1 : 0)) {
            String mensaje = esFavorito ? "Agregado a favoritos" : "Removido de favoritos";
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarIconoFavorito() {
        if (esFavorito) {
            btnFavorito.setImageResource(R.drawable.ic_favorito_lleno);
        } else {
            btnFavorito.setImageResource(R.drawable.ic_favorito_outline);
        }
    }

    private void realizarLlamada() {
        String numero = etNumero.getText().toString();
        if (!numero.trim().isEmpty()) {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + numero));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No se puede realizar la llamada", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No hay número para llamar", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirEdicion() {

        Intent intent = new Intent(this, AgregarContactoActivity.class);
        intent.putExtra("MODO_EDICION", true);
        intent.putExtra("CONTACTO_ID", contactoId);
        startActivity(intent);
        finish(); // Cerramos esta para que al volver se recarguen los datos

       
    }

    private void guardarCambios() {
        // Lógica para guardar si decides editar en esta misma pantalla
        Toast.makeText(this, "Función guardar pendiente de implementar", Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Contacto")
                .setMessage("¿Estás seguro de que quieres eliminar este contacto?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarContacto())
                .setNegativeButton("Cancelar", null)
                .show();
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