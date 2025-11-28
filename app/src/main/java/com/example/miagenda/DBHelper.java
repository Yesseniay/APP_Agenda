package com.example.miagenda;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.miagenda.AgendaContract.ContactoEntry;
import com.example.miagenda.AgendaContract.NotaEntry;
import com.example.miagenda.AgendaContract.ActividadEntry;
;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MiAgenda.db";

    private static final String SQL_CREATE_CONTACTOS =
            "CREATE TABLE " + ContactoEntry.TABLE_NAME + " (" +
                    ContactoEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    ContactoEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    ContactoEntry.COLUMN_NUMERO + " TEXT UNIQUE," +
                    ContactoEntry.COLUMN_EMAIL + " TEXT UNIQUE," +
                    ContactoEntry.COLUMN_NOTAS + " TEXT"+
                    ContactoEntry.COLUMN_FOTO  + "BLOB);";

    private static final String SQL_CREATE_NOTAS =
            "CREATE TABLE " + NotaEntry.TABLE_NAME + " (" +
                    NotaEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    NotaEntry.COLUMN_TITULO + " TEXT NOT NULL," +
                    NotaEntry.COLUMN_CONTENIDO + " TEXT)";
    public static final String SQL_CREATE_ACTIVIDAD =
            "CREATE TABLE " + ActividadEntry.TABLE_NAME + " (" +
                    ActividadEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    ActividadEntry.COLUMN_TITULO + " TEXT NOT NULL," +
                    ActividadEntry.COLUMN_DESCRIPCION + " TEXT," +
                    ActividadEntry.COLUMN_FECHA + " TEXT NOT NULL,"+
                    ActividadEntry.COLUMN_HORA + " TEXT," +
                    ActividadEntry.COLUMN_COMPLETADA + " INTEGER DEFAULT 0," +
                    ActividadEntry.COLUMN_NOTIFICACION + " INTEGER DEFAULT 1"+
                    ActividadEntry.COLUMN_CATEGORIA + " TEXT DEFAULT 'General');";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTACTOS);
        db.execSQL(SQL_CREATE_NOTAS);
        db.execSQL(SQL_CREATE_ACTIVIDAD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ContactoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NotaEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ActividadEntry.TABLE_NAME);
        onCreate(db);
    }
}
