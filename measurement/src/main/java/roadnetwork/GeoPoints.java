package roadnetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeoPoints {
    public GeoPoints() {
    }

    public static String toString(List<GeoPoint> points) {
        StringBuilder sb = new StringBuilder();
        points.forEach((p) -> {
            sb.append(p.toString()).append("#");
        });
        return sb.substring(0, sb.length() - 1);
    }

    public static List<GeoPoint> parse(String pointsStr) {
        List<GeoPoint> points = new ArrayList();
        String[] pointsStrArray = pointsStr.split("#");
        Arrays.stream(pointsStrArray).forEach((ps) -> {
            points.add(GeoPoint.parse(ps));
        });
        return points;
    }

    public static double calLength(List<GeoPoint> points) {
        double length = 0.0D;

        for(int i = 0; i < points.size() - 1; ++i) {
            length += ((GeoPoint)points.get(i)).distance((GeoPoint)points.get(i + 1));
        }

        return length;
    }
}
