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
    private ImageButton btnCambiarFoto;
    private ImageView ivFotoPerfil;
    private AgendaManager manager;
    private Bitmap imagenSeleccionada;

    // Códigos de solicitud
    private static final int CODIGO_GALERIA = 1;
    private static final int CODIGO_CAMARA = 2;
    private static final int PERMISO_GALERIA = 100;
    private static final int PERMISO_CAMARA = 101;

    // Para Android 13+ y versiones anteriores
    private static final String PERMISO_GALERIA_ACTUAL;

    static {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            PERMISO_GALERIA_ACTUAL = Manifest.permission.READ_MEDIA_IMAGES;
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            PERMISO_GALERIA_ACTUAL = Manifest.permission.READ_EXTERNAL_STORAGE;
        } else {
            PERMISO_GALERIA_ACTUAL = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contacto);

        // Inicializar el manager de agenda
        try {
            manager = new AgendaManager(this);
        } catch (Exception e) {
            Toast.makeText(this, "Error al inicializar base de datos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar Vistas
        etNombre = findViewById(R.id.et_nombre);
        etNumero = findViewById(R.id.et_numero);
        etEmail = findViewById(R.id.et_email);
        etNotas = findViewById(R.id.et_notas);
        ivFotoPerfil = findViewById(R.id.iv_profile_picture);
        btnCambiarFoto = findViewById(R.id.btn_change_photo);
        Button btnGuardar = findViewById(R.id.btn_guardar);

        // Configurar listeners
        btnCambiarFoto.setOnClickListener(v -> mostrarOpcionesFoto());
        btnGuardar.setOnClickListener(v -> guardarContacto());

        // Verificar permisos al inicio
        verificarPermisosIniciales();
    }

    private void verificarPermisosIniciales() {
        // Solo verificamos, no solicitamos inmediatamente
        boolean tienePermisoCamara = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        boolean tienePermisoGaleria = ContextCompat.checkSelfPermission(this,
                PERMISO_GALERIA_ACTUAL) == PackageManager.PERMISSION_GRANTED;

        if (!tienePermisoCamara || !tienePermisoGaleria) {
            Toast.makeText(this,
                    "Se necesitan permisos para acceder a la cámara y galería",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarOpcionesFoto() {
        String[] opciones = {"Tomar Foto", "Elegir de Galería", "Cancelar"};
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Mostrar explicación si es necesario
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Permiso de Cámara Necesario")
                        .setMessage("Esta aplicación necesita acceso a la cámara para tomar fotos de contactos")
                        .setPositiveButton("Aceptar", (dialog, which) ->
                                ActivityCompat.requestPermissions(
                                        AgregarContactoActivity.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        PERMISO_CAMARA
                                ))
                        .setNegativeButton("Cancelar", null)
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISO_CAMARA);
            }
        } else {
            abrirCamara();
        }
    }

    private void verificarPermisoGaleria() {
        if (ContextCompat.checkSelfPermission(this, PERMISO_GALERIA_ACTUAL)
                != PackageManager.PERMISSION_GRANTED) {

            // Mostrar explicación si es necesario
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    PERMISO_GALERIA_ACTUAL)) {

                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Permiso de Galería Necesario")
                        .setMessage("Esta aplicación necesita acceso a la galería para seleccionar fotos de contactos")
                        .setPositiveButton("Aceptar", (dialog, which) ->
                                ActivityCompat.requestPermissions(
                                        AgregarContactoActivity.this,
                                        new String[]{PERMISO_GALERIA_ACTUAL},
                                        PERMISO_GALERIA
                                ))
                        .setNegativeButton("Cancelar", null)
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{PERMISO_GALERIA_ACTUAL},
                        PERMISO_GALERIA);
            }
        } else {
            abrirGaleria();
        }
    }

    private void abrirCamara() {
        try {
            Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intentCamara.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intentCamara, CODIGO_CAMARA);
            } else {
                Toast.makeText(this, "No se encontró app de cámara", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirGaleria() {
        try {
            Intent intentGaleria = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intentGaleria.setType("image/*");

            Intent chooser = Intent.createChooser(intentGaleria, "Elige una foto");
            if (intentGaleria.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(chooser, CODIGO_GALERIA);
            } else {
                Toast.makeText(this, "No se encontró app de galería", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir galería: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            if (requestCode == PERMISO_GALERIA) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    abrirGaleria();
                } else {
                    Toast.makeText(this,
                            "Permiso de galería denegado. No podrás seleccionar fotos.",
                            Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == PERMISO_CAMARA) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    abrirCamara();
                } else {
                    Toast.makeText(this,
                            "Permiso de cámara denegado. No podrás tomar fotos.",
                            Toast.LENGTH_LONG).show();
                }
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
                    // Reducir el tamaño de la imagen para evitar problemas de memoria
                    InputStream imageStream = getContentResolver().openInputStream(uriImagen);

                    // Decodificar con opciones para reducir tamaño
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4; // Reducir a 1/4 del tamaño original
                    options.inJustDecodeBounds = false;

                    imagenSeleccionada = BitmapFactory.decodeStream(imageStream, null, options);

                    if (imagenSeleccionada != null) {
                        ivFotoPerfil.setImageBitmap(imagenSeleccionada);
                    } else {
                        Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error al cargar imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CODIGO_CAMARA && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    imagenSeleccionada = (Bitmap) extras.get("data");
                    if (imagenSeleccionada != null) {
                        ivFotoPerfil.setImageBitmap(imagenSeleccionada);
                    }
                }
            }
        }
    }

    private byte[] convertirImagenABytes(Bitmap bitmap) {
        if (bitmap == null) return null;

        try {
            // Reducir tamaño antes de convertir a bytes
            Bitmap reducida = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth()/2, bitmap.getHeight()/2, true);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            reducida.compress(Bitmap.CompressFormat.JPEG, 70, stream); // JPEG con 70% calidad
            return stream.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    private void guardarContacto() {
        String nombre = etNombre.getText().toString().trim();
        String numero = etNumero.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String notas = etNotas.getText().toString().trim();

        // Validaciones básicas
        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es obligatorio");
            etNombre.requestFocus();
            return;
        }

        if (numero.isEmpty()) {
            etNumero.setError("El número es obligatorio");
            etNumero.requestFocus();
            return;
        }

        // Validar formato de email si se proporciona
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email no válido");
            etEmail.requestFocus();
            return;
        }

        // El XML no tiene botón de favorito, así que lo establecemos en 0
        int favorito = 0;
        byte[] fotoBytes = null;

        // Convertir imagen si existe
        if (imagenSeleccionada != null) {
            fotoBytes = convertirImagenABytes(imagenSeleccionada);
        }

        try {
            // Agregar contacto a la base de datos
            long id = manager.agregarContacto(nombre, numero, email, notas, favorito, fotoBytes);

            if (id != -1) {
                Toast.makeText(this, "Contacto guardado exitosamente", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}