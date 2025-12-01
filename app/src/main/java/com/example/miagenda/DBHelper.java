package com.example.miagenda;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "agenda.db";
    private static final int DATABASE_VERSION = 1;

    // SQL para crear tabla contactos
    private static final String SQL_CREATE_CONTACTOS =
            "CREATE TABLE " + AgendaContract.ContactoEntry.TABLE_NAME + " (" +
                    AgendaContract.ContactoEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AgendaContract.ContactoEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    AgendaContract.ContactoEntry.COLUMN_NUMERO + " TEXT," +
                    AgendaContract.ContactoEntry.COLUMN_EMAIL + " TEXT," +
                    AgendaContract.ContactoEntry.COLUMN_NOTAS + " TEXT," +
                    AgendaContract.ContactoEntry.COLUMN_FOTO + " BLOB," +
                    AgendaContract.ContactoEntry.COLUMN_FAVORITO + " INTEGER DEFAULT 0);";

    // SQL para crear tabla notas
    private static final String SQL_CREATE_NOTAS =
            "CREATE TABLE " + AgendaContract.NotaEntry.TABLE_NAME + " (" +
                    AgendaContract.NotaEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AgendaContract.NotaEntry.COLUMN_TITULO + " TEXT NOT NULL," +
                    AgendaContract.NotaEntry.COLUMN_CONTENIDO + " TEXT);";

    // SQL para crear tabla actividades
    private static final String SQL_CREATE_ACTIVIDADES =
            "CREATE TABLE " + AgendaContract.ActividadEntry.TABLE_NAME + " (" +
                    AgendaContract.ActividadEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AgendaContract.ActividadEntry.COLUMN_TITULO + " TEXT NOT NULL," +
                    AgendaContract.ActividadEntry.COLUMN_DESCRIPCION + " TEXT," +
                    AgendaContract.ActividadEntry.COLUMN_FECHA + " TEXT," +
                    AgendaContract.ActividadEntry.COLUMN_HORA + " TEXT," +
                    AgendaContract.ActividadEntry.COLUMN_COMPLETADA + " INTEGER DEFAULT 0," +
                    AgendaContract.ActividadEntry.COLUMN_NOTIFICACION + " INTEGER DEFAULT 1," +
                    AgendaContract.ActividadEntry.COLUMN_CATEGORIA + " TEXT);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTACTOS);
        db.execSQL(SQL_CREATE_NOTAS);
        db.execSQL(SQL_CREATE_ACTIVIDADES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AgendaContract.ContactoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AgendaContract.NotaEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AgendaContract.ActividadEntry.TABLE_NAME);
        onCreate(db);
    }
}