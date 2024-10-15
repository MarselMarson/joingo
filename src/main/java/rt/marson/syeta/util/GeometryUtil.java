package rt.marson.syeta.util;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import rt.marson.syeta.dto.google.places.LatLngDto;

public class GeometryUtil {

    public static final int SRID = 4326; //LatLng
    public static final int RADIUS = 70_000;
    private static WKTReader wktReader = new WKTReader();

    private static Geometry wktToGeometry(String wellKnownText) throws ParseException {
        Geometry geometry;

        try {
            geometry = wktReader.read(wellKnownText);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage());
        }
        System.out.println("###geometry :" + geometry);
        return geometry;
    }
    public static Point parseLocation(double x, double y) throws ParseException {
        Geometry geometry = GeometryUtil.wktToGeometry(String.format("POINT (%s %s)", x, y));
        Point p = (Point) geometry;
        p.setSRID(SRID);
        return p;
    }

    public static LatLngDto stringToLatLng(String locationLatLng) {
        String[] parts = locationLatLng.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Неверный формат координат");
        }

        try {
            double lat = Double.parseDouble(parts[0].trim());
            double lng = Double.parseDouble(parts[1].trim());

            return new LatLngDto(lat, lng);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат чисел для координат");
        }
    }
}
