package com.example.miagenda;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.miagenda.AgendaContract.ActividadEntry;

public class ActividadCursorAdapter extends RecyclerView.Adapter<ActividadCursorAdapter.ActividadViewHolder> {

    private Cursor mCursor;
    private final OnActionListener listener;

    public interface OnActionListener {
        void onEditClick(long id);
        void onDeleteClick(long id);
    }

    public ActividadCursorAdapter(Cursor cursor, OnActionListener listener) {
        this.mCursor = cursor;
        this.listener = listener;
    }

    public class ActividadViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitulo;
        public TextView tvDescripcion;
        public Button btnEditar;
        public Button btnEliminar;
        public View colorIndicator; // Añade esta línea

        public ActividadViewHolder(View itemView) {
            super(itemView);

            tvTitulo = itemView.findViewById(R.id.tv_titulo_actividad);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion_actividad);
            btnEditar = itemView.findViewById(R.id.btn_editar_actividad);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_actividad);
            colorIndicator = itemView.findViewById(R.id.color_indicator); // Añade esta línea

            // Clics en botones
            btnEliminar.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    mCursor.moveToPosition(getAdapterPosition());
                    long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(ActividadEntry._ID));
                    listener.onDeleteClick(id);
                }
            });

            View.OnClickListener editListener = v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    mCursor.moveToPosition(getAdapterPosition());
                    long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(ActividadEntry._ID));
                    listener.onEditClick(id);
                }
            };
            btnEditar.setOnClickListener(editListener);
            itemView.setOnClickListener(editListener);
        }
    }

    @NonNull
    @Override
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_actividad, parent, false); // Asegúrate que sea el layout correcto
        return new ActividadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) return;

        String titulo = mCursor.getString(mCursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_TITULO));
        String descripcion = mCursor.getString(mCursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_DESCRIPCION));

        // OBTENER CATEGORÍA Y APLICAR COLOR
        String categoria = mCursor.getString(mCursor.getColumnIndexOrThrow(ActividadEntry.COLUMN_CATEGORIA));
        String colorHex = ColorApiHelper.getColorForCategory(categoria);
        int color = ColorApiHelper.getColorInt(colorHex);

        holder.tvTitulo.setText(titulo);
        holder.tvDescripcion.setText(descripcion);

        // APLICAR COLOR AL INDICADOR
        if (holder.colorIndicator != null) {
            holder.colorIndicator.setBackgroundColor(color);
        }
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