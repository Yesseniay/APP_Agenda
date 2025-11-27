package com.example.miagenda;

import android.provider.BaseColumns;

public final class AgendaContract {
    private AgendaContract() {}

    // TABLA DE CONTACTOS
    public static class ContactoEntry implements BaseColumns {
        public static final String TABLE_NAME = "contactos";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "nombre";
        public static final String COLUMN_NUMERO = "numero";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_NOTAS = "notas";
    }

    // TABLA DE NOTAS
    public static class NotaEntry implements BaseColumns {
        public static final String TABLE_NAME = "notas";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_TITULO = "titulo";
        public static final String COLUMN_CONTENIDO = "contenido";
    }

    // Nueva Tabla: Actividades
    public static class ActividadEntry implements BaseColumns {
        public static final String TABLE_NAME = "actividades";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_TITULO = "titulo";
        public static final String COLUMN_DESCRIPCION = "descripcion";
        public static final String COLUMN_FECHA = "fecha"; // Formato YYYY-MM-DD
    }



}