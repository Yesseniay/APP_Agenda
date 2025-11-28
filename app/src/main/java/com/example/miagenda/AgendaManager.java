package com.example.miagenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.miagenda.AgendaContract.ContactoEntry;
import com.example.miagenda.AgendaContract.NotaEntry;

public class AgendaManager {

    private DBHelper dbHelper;

    public AgendaManager(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    private SQLiteDatabase getReadableDB() {
        return dbHelper.getReadableDatabase();
    }

    private SQLiteDatabase getWritableDB() {
        return dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDatabase() {
        return getWritableDB();
    }

    // CRUD CONTACTOS

    // Metodo para agregar contactos
    public long agregarContacto(String nombre, String numero, String email, String notas,byte foto, int favorito) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(ContactoEntry.COLUMN_NAME, nombre);
        values.put(ContactoEntry.COLUMN_NUMERO, numero);
        values.put(ContactoEntry.COLUMN_EMAIL, email);
        values.put(ContactoEntry.COLUMN_NOTAS, notas);
        values.put(ContactoEntry.COLUMN_FOTO,foto);
        values.put(ContactoEntry.COLUMN_FAVORITO,favorito);

        long newRowId = db.insert(ContactoEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    //Metodo para buscar contactos
    public Cursor buscarContactos(String query) {
        SQLiteDatabase db = getReadableDB();
        String selection = null;
        String[] selectionArgs = null;
        if (query != null && !query.isEmpty()) {
            String likeQuery = "%" + query + "%";
            selection = ContactoEntry.COLUMN_NAME + " LIKE ? OR " +
                    ContactoEntry.COLUMN_NUMERO + " LIKE ? OR " +
                    ContactoEntry.COLUMN_EMAIL + " LIKE ?";
            selectionArgs = new String[]{likeQuery, likeQuery, likeQuery};
        }
        String sortOrder = ContactoEntry.COLUMN_NAME + " ASC";

        return db.query(ContactoEntry.TABLE_NAME, null, selection, selectionArgs, null, null, sortOrder);
    }

    //Metodo para actualizar los contactos
    public int actualizarContacto(long id, String nombre, String numero, String email, String notas) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(ContactoEntry.COLUMN_NAME, nombre);
        values.put(ContactoEntry.COLUMN_NUMERO, numero);
        values.put(ContactoEntry.COLUMN_EMAIL, email);
        values.put(ContactoEntry.COLUMN_NOTAS, notas);

        String selection = ContactoEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(ContactoEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return count;
    }

    //Metodo para eliminar los contactos
    public int eliminarContacto(long id) {
        SQLiteDatabase db = getWritableDB();
        String selection = ContactoEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        int deletedRows = db.delete(ContactoEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows;
    }

    // CRUD NOTAS

    // Metodo de Agregar Nota
    public long agregarNota(String titulo, String contenido) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(NotaEntry.COLUMN_TITULO, titulo);
        values.put(NotaEntry.COLUMN_CONTENIDO, contenido);
        long newRowId = db.insert(NotaEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    // Metodo para buscar
    public Cursor buscarNotas(String query) {
        SQLiteDatabase db = getReadableDB();
        String selection = null;
        String[] selectionArgs = null;
        if (query != null && !query.isEmpty()) {
            String likeQuery = "%" + query + "%";
            selection = NotaEntry.COLUMN_TITULO + " LIKE ? OR " +
                    NotaEntry.COLUMN_CONTENIDO + " LIKE ?";
            selectionArgs = new String[]{likeQuery, likeQuery};
        }
        // Se ordena por ID de manera descendente"
        String sortOrder = NotaEntry.COLUMN_ID + " DESC";

        return db.query(
                NotaEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    // Metodo para actualizar la Nota
    public int actualizarNota(long id, String titulo, String contenido) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(NotaEntry.COLUMN_TITULO, titulo);
        values.put(NotaEntry.COLUMN_CONTENIDO, contenido);

        String selection = NotaEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(NotaEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return count;
    }

    // Metodo para eliminar una Nota
    public int eliminarNota(long id) {
        SQLiteDatabase db = getWritableDB();
        String selection = NotaEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        int deletedRows = db.delete(NotaEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows;
    }

    //CRUD actividades

    //Metodo para egreagr actividades
    public long agregarActividad(String titulo, String descripcion, String fecha) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(AgendaContract.ActividadEntry.COLUMN_TITULO, titulo);
        values.put(AgendaContract.ActividadEntry.COLUMN_DESCRIPCION, descripcion);
        values.put(AgendaContract.ActividadEntry.COLUMN_FECHA, fecha);
        long newRowId = db.insert(AgendaContract.ActividadEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    // Metodo para buscar actividades por fecha
    public Cursor buscarActividadesPorFecha(String fecha) {
        SQLiteDatabase db = getReadableDB();
        String selection = AgendaContract.ActividadEntry.COLUMN_FECHA + " = ?";
        String[] selectionArgs = { fecha };
        String sortOrder = AgendaContract.ActividadEntry.COLUMN_TITULO + " ASC";

        return db.query(AgendaContract.ActividadEntry.TABLE_NAME, null, selection, selectionArgs, null, null, sortOrder);
    }

    //Metodo para obtener una actividad por ID
    public Cursor getActividadPorId(long id) {
        SQLiteDatabase db = getReadableDB();
        String selection = AgendaContract.ActividadEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        return db.query(AgendaContract.ActividadEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
    }

    //Metodo para actualizar actividades
    public int actualizarActividad(long id, String titulo, String descripcion, String fecha) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(AgendaContract.ActividadEntry.COLUMN_TITULO, titulo);
        values.put(AgendaContract.ActividadEntry.COLUMN_DESCRIPCION, descripcion);
        values.put(AgendaContract.ActividadEntry.COLUMN_FECHA, fecha);

        String selection = AgendaContract.ActividadEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(AgendaContract.ActividadEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return count;
    }

    //Metodo para eliminar una actividad
    public int eliminarActividad(long id) {
        SQLiteDatabase db = getWritableDB();
        String selection = AgendaContract.ActividadEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        int deletedRows = db.delete(AgendaContract.ActividadEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows;
    }

    // MÉTODOS NUEVOS PARA NOTIFICACIONES
    public Cursor getActividadesPendientesHoy() {
        SQLiteDatabase db = getReadableDB();
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(new java.util.Date());

        String query = "SELECT * FROM " + AgendaContract.ActividadEntry.TABLE_NAME +
                " WHERE " + AgendaContract.ActividadEntry.COLUMN_FECHA + " = ?" +
                " AND " + AgendaContract.ActividadEntry.COLUMN_COMPLETADA + " = 0" +
                " AND " + AgendaContract.ActividadEntry.COLUMN_NOTIFICACION + " = 1";

        return db.rawQuery(query, new String[]{today});
    }

    public Cursor getActividadesPendientesProximas() {
        SQLiteDatabase db = getReadableDB();
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(new java.util.Date());

        String query = "SELECT * FROM " + AgendaContract.ActividadEntry.TABLE_NAME +
                " WHERE " + AgendaContract.ActividadEntry.COLUMN_FECHA + " >= ?" +
                " AND " + AgendaContract.ActividadEntry.COLUMN_COMPLETADA + " = 0" +
                " AND " + AgendaContract.ActividadEntry.COLUMN_NOTIFICACION + " = 1" +
                " ORDER BY " + AgendaContract.ActividadEntry.COLUMN_FECHA + " ASC" +
                " LIMIT 5";

        return db.rawQuery(query, new String[]{today});
    }

    // Método para agregar actividad
    public long agregarActividadCompleta(String titulo, String descripcion, String fecha, String hora,
                                         int completada, int notificacion) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(AgendaContract.ActividadEntry.COLUMN_TITULO, titulo);
        values.put(AgendaContract.ActividadEntry.COLUMN_DESCRIPCION, descripcion);
        values.put(AgendaContract.ActividadEntry.COLUMN_FECHA, fecha);
        values.put(AgendaContract.ActividadEntry.COLUMN_HORA, hora);
        values.put(AgendaContract.ActividadEntry.COLUMN_COMPLETADA, completada);
        values.put(AgendaContract.ActividadEntry.COLUMN_NOTIFICACION, notificacion);

        long newRowId = db.insert(AgendaContract.ActividadEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    // Método para actualizar
    public int actualizarActividadCompleta(long id, String titulo, String descripcion, String fecha,
                                           String hora, int completada, int notificacion) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(AgendaContract.ActividadEntry.COLUMN_TITULO, titulo);
        values.put(AgendaContract.ActividadEntry.COLUMN_DESCRIPCION, descripcion);
        values.put(AgendaContract.ActividadEntry.COLUMN_FECHA, fecha);
        values.put(AgendaContract.ActividadEntry.COLUMN_HORA, hora);
        values.put(AgendaContract.ActividadEntry.COLUMN_COMPLETADA, completada);
        values.put(AgendaContract.ActividadEntry.COLUMN_NOTIFICACION, notificacion);

        String selection = AgendaContract.ActividadEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(AgendaContract.ActividadEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return count;
    }
    // Método para agregar actividad completa CON CATEGORÍA
    public long agregarActividadCompleta(String titulo, String descripcion, String fecha, String hora,
                                         int completada, int notificacion, String categoria) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(AgendaContract.ActividadEntry.COLUMN_TITULO, titulo);
        values.put(AgendaContract.ActividadEntry.COLUMN_DESCRIPCION, descripcion);
        values.put(AgendaContract.ActividadEntry.COLUMN_FECHA, fecha);
        values.put(AgendaContract.ActividadEntry.COLUMN_HORA, hora);
        values.put(AgendaContract.ActividadEntry.COLUMN_COMPLETADA, completada);
        values.put(AgendaContract.ActividadEntry.COLUMN_NOTIFICACION, notificacion);
        values.put(AgendaContract.ActividadEntry.COLUMN_CATEGORIA, categoria); // NUEVO

        long newRowId = db.insert(AgendaContract.ActividadEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    // Método para actualizar actividad completa CON CATEGORÍA
    public int actualizarActividadCompleta(long id, String titulo, String descripcion, String fecha,
                                           String hora, int completada, int notificacion, String categoria) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(AgendaContract.ActividadEntry.COLUMN_TITULO, titulo);
        values.put(AgendaContract.ActividadEntry.COLUMN_DESCRIPCION, descripcion);
        values.put(AgendaContract.ActividadEntry.COLUMN_FECHA, fecha);
        values.put(AgendaContract.ActividadEntry.COLUMN_HORA, hora);
        values.put(AgendaContract.ActividadEntry.COLUMN_COMPLETADA, completada);
        values.put(AgendaContract.ActividadEntry.COLUMN_NOTIFICACION, notificacion);
        values.put(AgendaContract.ActividadEntry.COLUMN_CATEGORIA, categoria); // NUEVO

        String selection = AgendaContract.ActividadEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(AgendaContract.ActividadEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
        return count;
    }
}