package com.example.miagenda;

import android.graphics.Color;
import java.util.HashMap;
import java.util.Map;

public class ColorApiHelper {
    private static final Map<String, String> CATEGORY_COLORS = new HashMap<>();

    static {
        // Colores predefinidos para categorías comunes
        CATEGORY_COLORS.put("Trabajo", "#FF6B6B");
        CATEGORY_COLORS.put("Personal", "#51CF66");
        CATEGORY_COLORS.put("Estudio", "#339AF0");
        CATEGORY_COLORS.put("Salud", "#FF922B");
        CATEGORY_COLORS.put("Compras", "#CC5DE8");
        CATEGORY_COLORS.put("Familia", "#F06595");
        CATEGORY_COLORS.put("Urgente", "#F03E3E");
        CATEGORY_COLORS.put("General", "#868E96");
    }

    public static String getColorForCategory(String category) {
        return CATEGORY_COLORS.getOrDefault(category, generateRandomColor());
    }

    public static String[] getAvailableCategories() {
        return new String[]{
                "Trabajo", "Personal", "Estudio", "Salud",
                "Compras", "Familia", "Urgente", "General"
        };
    }

    private static String generateRandomColor() {
        // Genera un color
        int color = Color.rgb(
                (int) (Math.random() * 256),
                (int) (Math.random() * 256),
                (int) (Math.random() * 256)
        );
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static int getColorInt(String hexColor) {
        return Color.parseColor(hexColor);
    }
    static {
        CATEGORY_COLORS.put("Trabajo", "#FF6B6B");
        CATEGORY_COLORS.put("Personal", "#51CF66");
        CATEGORY_COLORS.put("Estudio", "#339AF0");
        CATEGORY_COLORS.put("Salud", "#FF922B");
        CATEGORY_COLORS.put("Compras", "#CC5DE8");
        CATEGORY_COLORS.put("Familia", "#F06595");
        CATEGORY_COLORS.put("Urgente", "#F03E3E");
        CATEGORY_COLORS.put("General", "#868E96");
        CATEGORY_COLORS.put("Deportes", "#20C997");
        CATEGORY_COLORS.put("Viajes", "#FD7E14");
    }

}
