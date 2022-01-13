package roadnetwork;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;
import org.postgis.Geometry;
import org.postgis.MultiLineString;
import org.postgis.PGgeometry;

import java.util.HashMap;
import java.util.List;

@CsvDataType
public class CenterRoad {

    @CsvField(pos = 1)
    public String id;

    @CsvField(pos = 2)
    public int nlane;

    @CsvField(pos = 3)
    public String turn;

    @CsvField(pos = 4)
    public String dnroad;

    @CsvField(pos = 5)
    public String biGeomStr;

    public Geometry geom;

    public List<GeoPoint> GeoPoints;

    public MultiLineString lineString;

    private double length;

    public int flowdir;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public List<GeoPoint> getGeoPoints() {
        return GeoPoints;
    }

    public void setGeoPoints(List<GeoPoint> geoPoints) {
        GeoPoints = geoPoints;
    }

    private static HashMap<String, CenterRoad> LINK_MAP;

    public static HashMap<String, CenterRoad> getLinkMap(){
        if (LINK_MAP==null){
            LINK_MAP = new HashMap<>();
        }
        return LINK_MAP;
    }
}
