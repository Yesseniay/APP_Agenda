package com.example.miagenda;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AgregarContactoActivity extends AppCompatActivity {

    private EditText etNombre, etNumero, etEmail, etNotas;
    private ImageButton btnFavorito, btnLlamar;
    private ImageView ivFotoContacto;
    private AgendaManager manager;
    private Bitmap imagenSeleccionada;
    private boolean esFavorito = false;

    // Códigos de solicitud
    private static final int CODIGO_GALERIA = 1;
    private static final int CODIGO_CAMARA = 2;
    private static final int PERMISO_GALERIA = 100;
    private static final int PERMISO_CAMARA = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_contacto);
        manager = new AgendaManager(this);

        // Inicializar Vistas del item_contacto.xml
        etNombre = findViewById(R.id.et_nombre);
        etNumero = findViewById(R.id.et_numero);
        etEmail = findViewById(R.id.et_email);
        etNotas = findViewById(R.id.et_notas);
        btnFavorito = findViewById(R.id.btn_favorito);
        btnLlamar = findViewById(R.id.btn_llamar);
        ivFotoContacto = findViewById(R.id.iv_foto_contacto);

        Button btnGuardar = findViewById(R.id.btn_guardar);
        Button btnEditar = findViewById(R.id.btn_editar);
        Button btnEliminar = findViewById(R.id.btn_eliminar);

        // Ocultar botones que no necesitamos en agregar contacto
        btnEditar.setVisibility(android.view.View.GONE);
        btnEliminar.setVisibility(android.view.View.GONE);

        // Configurar Listeners
        ivFotoContacto.setOnClickListener(v -> mostrarOpcionesFoto());
        btnFavorito.setOnClickListener(v -> toggleFavorito());
        btnLlamar.setOnClickListener(v -> realizarLlamada());
        btnGuardar.setOnClickListener(v -> guardarContacto());
    }

    // Método para alternar el estado de favorito
    private void toggleFavorito() {
        esFavorito = !esFavorito;

        if (esFavorito) {
            btnFavorito.setImageResource(R.drawable.ic_favorito_lleno);
            Toast.makeText(this, "Contacto marcado como favorito", Toast.LENGTH_SHORT).show();
        } else {
            btnFavorito.setImageResource(R.drawable.ic_favorito_outline);
            Toast.makeText(this, "Contacto removido de favoritos", Toast.LENGTH_SHORT).show();
        }
    }

    private void realizarLlamada() {
        String numero = etNumero.getText().toString().trim();
        if (!numero.isEmpty()) {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + numero));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No se puede realizar la llamada", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ingresa un número primero", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarOpcionesFoto() {
        String[] opciones = {"Tomar Foto", "Elegir de Galería", "Cancelar"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Foto");
        builder.setItems(opciones, (dialog, cual) -> {
            if (cual == 0) {
                verificarPermisoCamara();
            } else if (cual == 1) {
                verificarPermisoGaleria();
            }
        });
        builder.show();
    }

    private void verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISO_CAMARA);
        } else {
            abrirCamara();
        }
    }

    private void verificarPermisoGaleria() {
        String permiso = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permiso}, PERMISO_GALERIA);
        } else {
            abrirGaleria();
        }
    }

    private void abrirCamara() {
        Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentCamara.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentCamara, CODIGO_CAMARA);
        } else {
            Toast.makeText(this, "No se encontró app de cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirGaleria() {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intentGaleria, "Elige una foto"), CODIGO_GALERIA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_GALERIA || requestCode == PERMISO_CAMARA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == PERMISO_GALERIA) abrirGaleria();
                else abrirCamara();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == CODIGO_GALERIA) {
                Uri uriImagen = data.getData();
                try {
                    InputStream imageStream = getContentResolver().openInputStream(uriImagen);
                    imagenSeleccionada = BitmapFactory.decodeStream(imageStream);
                    ivFotoContacto.setImageBitmap(imagenSeleccionada);
                } catch (Exception e) {
                    Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CODIGO_CAMARA) {
                imagenSeleccionada = (Bitmap) data.getExtras().get("data");
                ivFotoContacto.setImageBitmap(imagenSeleccionada);
            }
        }
    }

    private byte[] convertirImagenABytes(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void guardarContacto() {
        String nombre = etNombre.getText().toString().trim();
        String numero = etNumero.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String notas = etNotas.getText().toString().trim();
        int favorito = esFavorito ? 1 : 0;
        byte fotoByte = 0;

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir imagen si existe
        if (imagenSeleccionada != null) {
            byte[] fotoBytes = convertirImagenABytes(imagenSeleccionada);
            if (fotoBytes != null && fotoBytes.length > 0) {
                fotoByte = fotoBytes[0];
            }
        }

        // Llamar al método del manager
        long id = manager.agregarContacto(nombre, numero, email, notas, fotoByte, favorito);

        if (id != -1) {
            Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }
}