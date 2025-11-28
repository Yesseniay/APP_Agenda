package com.example.miagenda;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AgregarContactoActivity extends AppCompatActivity {

    private EditText etNombre, etNumero, etEmail, etNotas;
    private AgendaManager manager;
    private ImageView ivProfilePicture;
    private Bitmap imagenSeleccionada;

    private int CODIGO_GALERIA = 1;
    private int CODIGO_CAMARA = 2;
    private int PERMISO_GALERIA = 100;
    private int PERMISO_CAMARA = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contacto);

        manager = new AgendaManager(this);

        etNombre = findViewById(R.id.et_nombre);
        etNumero = findViewById(R.id.et_numero);
        etEmail = findViewById(R.id.et_email);
        etNotas = findViewById(R.id.et_notas);
        Button btnGuardar = findViewById(R.id.btn_guardar);

        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        ImageView btnChangePhoto = findViewById(R.id.btn_change_photo);

        ivProfilePicture.setOnClickListener(v -> mostrarOpcionesFoto());
        btnChangePhoto.setOnClickListener(v -> mostrarOpcionesFoto());

        btnGuardar.setOnClickListener(v -> guardarContacto());
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISO_GALERIA);
        } else {
            abrirGaleria();
        }
    }

    private void abrirCamara() {
        Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentCamara.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentCamara, CODIGO_CAMARA);
        }
    }

    private void abrirGaleria() {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intentGaleria, "Elige una foto"), CODIGO_GALERIA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_GALERIA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                Toast.makeText(this, "Permiso denegado para galería", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISO_CAMARA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso denegado para cámara", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CODIGO_GALERIA && data != null) {
                Uri uriImagen = data.getData();
                try {
                    imagenSeleccionada = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImagen);
                    ivProfilePicture.setImageBitmap(imagenSeleccionada);
                } catch (IOException e) {
                    Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CODIGO_CAMARA && data != null) {
                imagenSeleccionada = (Bitmap) data.getExtras().get("data");
                ivProfilePicture.setImageBitmap(imagenSeleccionada);
            }
        }
    }

    private byte[] convertirImagenABytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
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

        byte[] fotoBytes = null;
        if (imagenSeleccionada != null) {
            fotoBytes = convertirImagenABytes(imagenSeleccionada);
        }

        long newRowId = manager.agregarContacto(nombre, numero, email, notas);

        if (newRowId != -1) {
            Toast.makeText(this, "Contacto guardado.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar.", Toast.LENGTH_LONG).show();
        }
    }
}