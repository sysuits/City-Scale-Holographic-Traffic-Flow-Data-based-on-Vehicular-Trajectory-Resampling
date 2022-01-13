package roadnetwork;

import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.ProjCoordinate;

public class CoordTransformUtils {
    private static BasicCoordinateTransform plane2latlon;
    private static BasicCoordinateTransform latlon2plane;

    public CoordTransformUtils() {
    }

    public static GeoPoint plane2latlon(GeoPoint point, GeoPoint shift) {
        ProjCoordinate tfCoordinate = new ProjCoordinate();
        plane2latlon.transform(new ProjCoordinate(point.getLocationX() + shift.getLocationX(), point.getLocationY() + shift.getLocationY()), tfCoordinate);
        return new GeoPoint(tfCoordinate.x, tfCoordinate.y, 0.0D);
    }

    public static GeoPoint latlon2plane(GeoPoint point, GeoPoint shift) {
        ProjCoordinate tfCoordinate = new ProjCoordinate();
        latlon2plane.transform(new ProjCoordinate(point.getLocationX(), point.getLocationY(), point.getLocationZ()), tfCoordinate);
        return new GeoPoint(tfCoordinate.x - shift.getLocationX(), tfCoordinate.y - shift.getLocationY(), 0.0D);
    }

    public static GeoPoint plane2latlon(GeoPoint point) {
        ProjCoordinate tfCoordinate = new ProjCoordinate();
        plane2latlon.transform(new ProjCoordinate(point.getLocationX(), point.getLocationY()), tfCoordinate);
        return new GeoPoint(tfCoordinate.x, tfCoordinate.y, 0.0D);
    }

    public static GeoPoint latlon2plane(GeoPoint point) {
        ProjCoordinate tfCoordinate = new ProjCoordinate();
        latlon2plane.transform(new ProjCoordinate(point.getLocationX(), point.getLocationY(), point.getLocationZ()), tfCoordinate);
        return new GeoPoint(tfCoordinate.x, tfCoordinate.y, point.getLocationZ());
    }

    public static GeoPoint projection(GeoPoint point, String espgSource, String espgTarget) {
        CRSFactory factory = new CRSFactory();
        CoordinateReferenceSystem source = factory.createFromName("EPSG:" + espgSource);
        CoordinateReferenceSystem taget = factory.createFromName("EPSG:" + espgTarget);
        BasicCoordinateTransform bct = new BasicCoordinateTransform(source, taget);
        ProjCoordinate tfCoordinate = new ProjCoordinate();
        bct.transform(new ProjCoordinate(point.getLocationX(), point.getLocationY(), point.getLocationZ()), tfCoordinate);
        return new GeoPoint(tfCoordinate.x, tfCoordinate.y, point.getLocationZ());
    }

    static {
        CRSFactory factory = new CRSFactory();
        CoordinateReferenceSystem latlonCrs = factory.createFromName("EPSG:4326");
        CoordinateReferenceSystem planeCrs = factory.createFromName("EPSG:2362");
        plane2latlon = new BasicCoordinateTransform(planeCrs, latlonCrs);
        latlon2plane = new BasicCoordinateTransform(latlonCrs, planeCrs);
    }
}
