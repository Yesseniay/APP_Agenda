package com.example.miagenda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarma recibida para verificar actividades pendientes");

        AgendaManager dbManager = new AgendaManager(context);

        // Obtener actividades pendientes para hoy
        Cursor cursor = dbManager.getActividadesPendientesHoy();

        if (cursor != null && cursor.getCount() > 0) {
            int count = cursor.getCount();
            String message = "Tienes " + count + " actividad(es) pendiente(s) para hoy";

            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.showNotification("Actividades Pendientes", message);

            Log.d(TAG, "Notificación enviada: " + message);
        }

        if (cursor != null) {
            cursor.close();
        }


    }
}

