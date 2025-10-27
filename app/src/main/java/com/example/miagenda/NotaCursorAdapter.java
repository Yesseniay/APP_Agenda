package com.example.miagenda;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.miagenda.AgendaContract.NotaEntry;

public class NotaCursorAdapter extends RecyclerView.Adapter<NotaCursorAdapter.NotasViewHolder> {

    private Cursor mCursor;
    private final OnActionListener listener;


    public interface OnActionListener {
        void onEditClick(long id);
        void onDeleteClick(long id);
    }

    public NotaCursorAdapter(Cursor cursor, OnActionListener listener) {
        this.mCursor = cursor;
        this.listener = listener;
    }

    public class NotasViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitulo;
        public TextView tvContenido;
        public Button btnEditar;
        public Button btnEliminar;

        public NotasViewHolder(View itemView) {
            super(itemView);

            tvTitulo = itemView.findViewById(R.id.txtTituloNota);
            tvContenido = itemView.findViewById(R.id.txtTextoNota);
            btnEditar = itemView.findViewById(R.id.btn_editar_nota);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_nota);


            btnEliminar.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    mCursor.moveToPosition(getAdapterPosition());
                    long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(NotaEntry.COLUMN_ID));
                    listener.onDeleteClick(id);
                }
            });


            btnEditar.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    mCursor.moveToPosition(getAdapterPosition());
                    long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(NotaEntry.COLUMN_ID));
                    listener.onEditClick(id);
                }
            });


            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    mCursor.moveToPosition(getAdapterPosition());
                    long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(NotaEntry.COLUMN_ID));
                    listener.onEditClick(id);
                }
            });
        }
    }

    @NonNull
    @Override
    public NotasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nota, parent, false);
        return new NotasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotasViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) return;

        String titulo = mCursor.getString(mCursor.getColumnIndexOrThrow(NotaEntry.COLUMN_TITULO));
        String contenido = mCursor.getString(mCursor.getColumnIndexOrThrow(NotaEntry.COLUMN_CONTENIDO));

        holder.tvTitulo.setText(titulo);
        holder.tvContenido.setText(contenido.length() > 100 ? contenido.substring(0, 100) + "..." : contenido);
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) this.notifyDataSetChanged();
    }
}
