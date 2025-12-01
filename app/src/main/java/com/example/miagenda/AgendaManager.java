package com.example.miagenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    // --- CRUD CONTACTOS ---

    // MÉTODO CORREGIDO: Usando las constantes correctas
    public long agregarContacto(String nombre, String numero, String email,
                                String notas, int favorito, byte[] foto) {
        SQLiteDatabase db = this.getWritableDB();  // CORREGIDO: getWritableDB() no getWritableDatabase()
        ContentValues values = new ContentValues();

        // CORREGIDO: Usando las constantes de AgendaContract
        values.put(AgendaContract.ContactoEntry.COLUMN_NAME, nombre);
        values.put(AgendaContract.ContactoEntry.COLUMN_NUMERO, numero);
        values.put(AgendaContract.ContactoEntry.COLUMN_EMAIL, email != null ? email : "");
        values.put(AgendaContract.ContactoEntry.COLUMN_NOTAS, notas != null ? notas : "");
        values.put(AgendaContract.ContactoEntry.COLUMN_FAVORITO, favorito);  // CORREGIDO: favorito no favorite

        if (foto != null && foto.length > 0) {
            values.put(AgendaContract.ContactoEntry.COLUMN_FOTO, foto);
        } else {
            values.putNull(AgendaContract.ContactoEntry.COLUMN_FOTO);
        }

        long id = db.insert(AgendaContract.ContactoEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // Método para buscar contactos
    public Cursor buscarContactos(String query) {
        SQLiteDatabase db = getReadableDB();
        String selection = null;
        String[] selectionArgs = null;

        if (query != null && !query.isEmpty()) {
            String likeQuery = "%" + query + "%";
            selection = AgendaContract.ContactoEntry.COLUMN_NAME + " LIKE ? OR " +
                    AgendaContract.ContactoEntry.COLUMN_NUMERO + " LIKE ? OR " +
                    AgendaContract.ContactoEntry.COLUMN_EMAIL + " LIKE ?";
            selectionArgs = new String[]{likeQuery, likeQuery, likeQuery};
        }

        String sortOrder = AgendaContract.ContactoEntry.COLUMN_NAME + " ASC";

        return db.query(AgendaContract.ContactoEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, sortOrder);
    }

    // Método para obtener TODOS los contactos
    public Cursor obtenerContactos() {
        SQLiteDatabase db = getReadableDB();
        String sortOrder = AgendaContract.ContactoEntry.COLUMN_NAME + " ASC";
        return db.query(AgendaContract.ContactoEntry.TABLE_NAME,
                null, null, null, null, null, sortOrder);
    }

    // Método para obtener un contacto por ID
    public Cursor obtenerContactoPorId(long id) {
        SQLiteDatabase db = getWritableDB();
        return db.query(AgendaContract.ContactoEntry.TABLE_NAME,
                null,
                AgendaContract.ContactoEntry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    // Método para actualizar los contactos
    public int actualizarContacto(long id, String nombre, String numero,
                                  String email, String notas) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();

        values.put(AgendaContract.ContactoEntry.COLUMN_NAME, nombre);
        values.put(AgendaContract.ContactoEntry.COLUMN_NUMERO, numero);
        values.put(AgendaContract.ContactoEntry.COLUMN_EMAIL, email);
        values.put(AgendaContract.ContactoEntry.COLUMN_NOTAS, notas);

        String selection = AgendaContract.ContactoEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(AgendaContract.ContactoEntry.TABLE_NAME,
                values, selection, selectionArgs);
        db.close();
        return count;
    }

    // Método para actualizar favorito (Corazón)
    public boolean actualizarFavorito(long id, int favorito) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(AgendaContract.ContactoEntry.COLUMN_FAVORITO, favorito);

        int rowsAffected = db.update(AgendaContract.ContactoEntry.TABLE_NAME,
                values,
                AgendaContract.ContactoEntry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    // Método para eliminar los contactos
    public int eliminarContacto(long id) {
        SQLiteDatabase db = getWritableDB();
        String selection = AgendaContract.ContactoEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        int deletedRows = db.delete(AgendaContract.ContactoEntry.TABLE_NAME,
                selection, selectionArgs);
        db.close();
        return deletedRows;
    }

    // --- CRUD NOTAS ---

    public long agregarNota(String titulo, String contenido) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(AgendaContract.NotaEntry.COLUMN_TITULO, titulo);
        values.put(AgendaContract.NotaEntry.COLUMN_CONTENIDO, contenido);
        long newRowId = db.insert(AgendaContract.NotaEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    public Cursor buscarNotas(String query) {
        SQLiteDatabase db = getReadableDB();
        String selection = null;
        String[] selectionArgs = null;

        if (query != null && !query.isEmpty()) {
            String likeQuery = "%" + query + "%";
            selection = AgendaContract.NotaEntry.COLUMN_TITULO + " LIKE ? OR " +
                    AgendaContract.NotaEntry.COLUMN_CONTENIDO + " LIKE ?";
            selectionArgs = new String[]{likeQuery, likeQuery};
        }

        String sortOrder = AgendaContract.NotaEntry.COLUMN_ID + " DESC";

        return db.query(AgendaContract.NotaEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, sortOrder);
    }

    public int actualizarNota(long id, String titulo, String contenido) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(AgendaContract.NotaEntry.COLUMN_TITULO, titulo);
        values.put(AgendaContract.NotaEntry.COLUMN_CONTENIDO, contenido);

        String selection = AgendaContract.NotaEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(AgendaContract.NotaEntry.TABLE_NAME,
                values, selection, selectionArgs);
        db.close();
        return count;
    }

    public int eliminarNota(long id) {
        SQLiteDatabase db = getWritableDB();
        String selection = AgendaContract.NotaEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        int deletedRows = db.delete(AgendaContract.NotaEntry.TABLE_NAME,
                selection, selectionArgs);
        db.close();
        return deletedRows;
    }

    // --- CRUD ACTIVIDADES ---

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

    public Cursor buscarActividadesPorFecha(String fecha) {
        SQLiteDatabase db = getReadableDB();
        String selection = AgendaContract.ActividadEntry.COLUMN_FECHA + " = ?";
        String[] selectionArgs = { fecha };
        String sortOrder = AgendaContract.ActividadEntry.COLUMN_TITULO + " ASC";

        return db.query(AgendaContract.ActividadEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, sortOrder);
    }

    public Cursor getActividadPorId(long id) {
        SQLiteDatabase db = getReadableDB();
        String selection = AgendaContract.ActividadEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        return db.query(AgendaContract.ActividadEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, null);
    }

    public int actualizarActividad(long id, String titulo, String descripcion, String fecha) {
        SQLiteDatabase db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(AgendaContract.ActividadEntry.COLUMN_TITULO, titulo);
        values.put(AgendaContract.ActividadEntry.COLUMN_DESCRIPCION, descripcion);
        values.put(AgendaContract.ActividadEntry.COLUMN_FECHA, fecha);

        String selection = AgendaContract.ActividadEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(AgendaContract.ActividadEntry.TABLE_NAME,
                values, selection, selectionArgs);
        db.close();
        return count;
    }

    public int eliminarActividad(long id) {
        SQLiteDatabase db = getWritableDB();
        String selection = AgendaContract.ActividadEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        int deletedRows = db.delete(AgendaContract.ActividadEntry.TABLE_NAME,
                selection, selectionArgs);
        db.close();
        return deletedRows;
    }

    // --- MÉTODOS PARA NOTIFICACIONES ---

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

    // Método para agregar actividad completa (Sobrecarga 1)
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

    // Método para agregar actividad completa CON CATEGORÍA (Sobrecarga 2)
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
        values.put(AgendaContract.ActividadEntry.COLUMN_CATEGORIA, categoria);

        long newRowId = db.insert(AgendaContract.ActividadEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    // Método para actualizar actividad completa
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

        String selection = AgendaContract.ActividadEntry.COLUMN_ID + " = ?";  // CORREGIDO: COLUMN_ID no _ID
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(AgendaContract.ActividadEntry.TABLE_NAME,
                values, selection, selectionArgs);
        db.close();
        return count;
    }

    // Método para actualizar actividad completa CON CATEGORÍA (Sobrecarga 2)
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
        values.put(AgendaContract.ActividadEntry.COLUMN_CATEGORIA, categoria);

        String selection = AgendaContract.ActividadEntry.COLUMN_ID + " = ?";  // CORREGIDO: COLUMN_ID no _ID
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(AgendaContract.ActividadEntry.TABLE_NAME,
                values, selection, selectionArgs);
        db.close();
        return count;
    }
}