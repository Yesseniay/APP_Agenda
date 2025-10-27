package com.example.miagenda;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.miagenda.AgendaContract.ContactoEntry;

public class ContactoCursorAdapter extends RecyclerView.Adapter<ContactoCursorAdapter.ContactosViewHolder> {


    private Cursor mCursor;
    private final OnActionListener listener;

    public interface OnActionListener {
        void onEditClick(long id);
        void onDeleteClick(long id);
    }


    public ContactoCursorAdapter(Cursor cursor, OnActionListener listener) {
        this.mCursor = cursor;
        this.listener = listener;
    }

    public class ContactosViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNombre;
        public TextView tvDetalle;
        public Button btnEditar;
        public Button btnEliminar;

        public ContactosViewHolder(View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tv_nombre_contacto);
            tvDetalle = itemView.findViewById(R.id.tv_detalle_contacto);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);

            btnEliminar.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    mCursor.moveToPosition(getAdapterPosition());
                    long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(ContactoEntry.COLUMN_ID));
                    listener.onDeleteClick(id);
                }
            });

            View.OnClickListener editListener = v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    mCursor.moveToPosition(getAdapterPosition());
                    long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(ContactoEntry.COLUMN_ID));
                    listener.onEditClick(id);
                }
            };
            btnEditar.setOnClickListener(editListener);
            itemView.setOnClickListener(editListener);
        }
    }

    @NonNull
    @Override
    public ContactosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacto, parent, false);
        return new ContactosViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ContactosViewHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) return;

        String nombre = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactoEntry.COLUMN_NAME));
        String numero = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactoEntry.COLUMN_NUMERO));
        String email = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactoEntry.COLUMN_EMAIL));

        holder.tvNombre.setText(nombre);

        String detalle = "";
        if (numero != null && !numero.isEmpty()) detalle = "Tel: " + numero;
        if (email != null && !email.isEmpty()) detalle += (detalle.isEmpty() ? "Email: " : " | Email: ") + email;

        holder.tvDetalle.setText(detalle.isEmpty() ? "Sin datos de contacto" : detalle);
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) mCursor.close(); // Cierra el cursor viejo
        mCursor = newCursor; // Asigna el cursor nuevo
        if (newCursor != null) this.notifyDataSetChanged();
    }
}