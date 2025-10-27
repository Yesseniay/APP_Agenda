package com.example.miagenda;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;

public class ContactoActivity extends AppCompatActivity
        implements ContactoCursorAdapter.OnActionListener {

    public static final String EXTRA_CONTACTO_ID = "contacto_id";

    private TextInputEditText etBuscador;
    private AgendaManager manager;
    private ContactoCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);


        manager = new AgendaManager(this);

        etBuscador = findViewById(R.id.buscador);
        RecyclerView recyclerView = findViewById(R.id.contenedor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor initialCursor = manager.buscarContactos("");
        adapter = new ContactoCursorAdapter(initialCursor, this);
        recyclerView.setAdapter(adapter);

        // Lógica para busqueda
        etBuscador.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                realizarBusqueda(s.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        realizarBusqueda(etBuscador.getText().toString()); // Refresca la lista después de cualquier cambio
    }

    private void realizarBusqueda(String texto) {
        Cursor nuevoCursor = manager.buscarContactos(texto);
        adapter.swapCursor(nuevoCursor);
    }


    @Override
    public void onEditClick(long id) {
        Intent intent = new Intent(ContactoActivity.this, ItemContactoActivity.class);
        intent.putExtra(EXTRA_CONTACTO_ID, id);
        startActivity(intent);
    }


    @Override
    public void onDeleteClick(long id) {
        // Llama al metodo del manager para eliminar
        if (manager.eliminarContacto(id) > 0) {
            Toast.makeText(this, "Contacto eliminado.", Toast.LENGTH_SHORT).show();
            realizarBusqueda(etBuscador.getText().toString()); // Recargar lista
        } else {
            Toast.makeText(this, "Error al eliminar.", Toast.LENGTH_SHORT).show();
        }
    }
}