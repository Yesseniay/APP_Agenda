package com.example.miagenda;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // IMPORTANTE: Para los botones con texto
import android.widget.EditText; // IMPORTANTE: Porque tu XML usa EditText
import android.widget.ImageButton; // Para llamar y favorito
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactoCursorAdapter extends RecyclerView.Adapter<ContactoCursorAdapter.ContactoViewHolder> {

    private Cursor cursor;
    private final OnActionListener listener;
    private final Context context;

    public interface OnActionListener {
        void onEditClick(long id);
        void onDeleteClick(long id);
    }

    public ContactoCursorAdapter(Cursor cursor, OnActionListener listener) {
        this.cursor = cursor;
        this.listener = listener;
        this.context = (Context) listener;
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        if (cursor == null || !cursor.moveToPosition(position)) {
            return;
        }

        // Obtener datos del cursor de forma segura
        // Usamos try-catch por si alguna columna no existe en tu base de datos
        try {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            String numero = cursor.getString(cursor.getColumnIndexOrThrow("numero"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            // Notas es opcional, verificamos si existe
            String notas = "";
            int indexNotas = cursor.getColumnIndex("notas");
            if(indexNotas != -1) notas = cursor.getString(indexNotas);

            int favorito = cursor.getInt(cursor.getColumnIndexOrThrow("favorito"));

            byte[] fotoBytes = null;
            int indexFoto = cursor.getColumnIndex("foto");
            if(indexFoto != -1) fotoBytes = cursor.getBlob(indexFoto);

            holder.bind(id, nombre, numero, email, notas, favorito, fotoBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public class ContactoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivFotoContacto;

        // CAMBIO: Ahora son EditText porque así está en tu XML
        private EditText etNombre, etNumero, etEmail, etNotas;

        private ImageButton btnFavorito, btnLlamar;

        // CAMBIO: Ahora son Button (con texto)
        private Button btnEditar, btnEliminar, btnGuardar;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);

            // 1. Vincular con IDs nuevos de tu XML
            ivFotoContacto = itemView.findViewById(R.id.iv_foto_contacto);

            etNombre = itemView.findViewById(R.id.et_nombre);
            etNumero = itemView.findViewById(R.id.et_numero);
            etEmail = itemView.findViewById(R.id.et_email);
            etNotas = itemView.findViewById(R.id.et_notas);

            btnFavorito = itemView.findViewById(R.id.btn_favorito);
            btnLlamar = itemView.findViewById(R.id.btn_llamar);

            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
            btnGuardar = itemView.findViewById(R.id.btn_guardar);

            // Ocultamos el botón "Guardar" en la lista, porque solo es para ver
            if(btnGuardar != null) btnGuardar.setVisibility(View.GONE);

            // Hacemos que los campos no se puedan editar en la lista
            etNombre.setFocusable(false); etNombre.setClickable(false);
            etNumero.setFocusable(false); etNumero.setClickable(false);
            etEmail.setFocusable(false); etEmail.setClickable(false);
            etNotas.setFocusable(false); etNotas.setClickable(false);

            setupClickListeners();
        }

        private void setupClickListeners() {
            btnEditar.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && cursor != null && cursor.moveToPosition(position)) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                    listener.onEditClick(id);
                }
            });

            btnEliminar.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && cursor != null && cursor.moveToPosition(position)) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                    listener.onDeleteClick(id);
                }
            });

            btnLlamar.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && cursor != null && cursor.moveToPosition(position)) {
                    String numero = cursor.getString(cursor.getColumnIndexOrThrow("numero"));
                    if (numero != null && !numero.trim().isEmpty()) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + numero));
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(context, "No se puede llamar", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Sin número", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnFavorito.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && cursor != null && cursor.moveToPosition(position)) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                    int favoritoActual = cursor.getInt(cursor.getColumnIndexOrThrow("favorito"));
                    int nuevoFavorito = favoritoActual == 1 ? 0 : 1;

                    AgendaManager manager = new AgendaManager(context);
                    if (manager.actualizarFavorito(id, nuevoFavorito)) {
                        actualizarIcono(nuevoFavorito);
                        // IMPORTANTE: Refrescar el cursor para que la lista se actualice
                        Cursor newCursor = manager.obtenerContactos();
                        swapCursor(newCursor);
                    }
                }
            });
        }

        private void actualizarIcono(int favorito) {
            if (favorito == 1) {
                btnFavorito.setImageResource(R.drawable.ic_favorito_lleno);
            } else {
                btnFavorito.setImageResource(R.drawable.ic_favorito_outline);
            }
        }

        public void bind(long id, String nombre, String numero, String email, String notas, int favorito, byte[] fotoBytes) {
            etNombre.setText(nombre != null ? nombre : "Sin nombre");
            etNumero.setText(numero != null ? numero : "");
            etEmail.setText(email != null ? email : "");

            // Si notas es null, ponemos vacío
            if(etNotas != null) etNotas.setText(notas != null ? notas : "");

            actualizarIcono(favorito);

            if (fotoBytes != null && fotoBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                ivFotoContacto.setImageBitmap(bitmap);
            } else {
                ivFotoContacto.setImageResource(R.drawable.ic_default_avatar);
            }
        }
    }
}