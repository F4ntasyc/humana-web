package com.humana.util;

/**
 * Utilitas parse lokasi_sesi dan hitung jarak Haversine (km).
 * Format lokasi: "lat,lng|alamat" atau "lat,lng".
 */
public final class JarakUtil {

    private JarakUtil() {}

    public static double hitungJarakKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public static double[] parseKoordinat(String lokasi) {
        if (lokasi == null || lokasi.isBlank()) return null;
        try {
            String coordPart = lokasi.contains("|") ? lokasi.split("\\|")[0] : lokasi;
            String[] coords = coordPart.split(",");
            if (coords.length < 2) return null;
            return new double[]{
                    Double.parseDouble(coords[0].trim()),
                    Double.parseDouble(coords[1].trim())
            };
        } catch (Exception e) {
            return null;
        }
    }
}
