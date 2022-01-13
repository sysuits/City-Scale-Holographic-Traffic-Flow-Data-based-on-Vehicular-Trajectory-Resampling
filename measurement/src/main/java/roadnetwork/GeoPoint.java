package roadnetwork;

import java.util.Arrays;
import java.util.List;

public class GeoPoint {
    public static final double POINT_EPSILON = 0.1D;
    private double[] locCoods = new double[3];
    private float[] locCoodsf;

    public GeoPoint() {
    }

    public GeoPoint(double x, double y) {
        this.locCoods[0] = x;
        this.locCoods[1] = y;
        this.locCoods[2] = 0.0D;
    }

    public GeoPoint(double x, double y, double z) {
        this.locCoods[0] = x;
        this.locCoods[1] = y;
        this.locCoods[2] = z;
    }

    public GeoPoint(GeoPoint gPoint) {
        this.locCoods[0] = gPoint.locCoods[0];
        this.locCoods[1] = gPoint.locCoods[1];
        this.locCoods[2] = gPoint.locCoods[2];
    }

    public GeoPoint(GeoPoint spt, GeoPoint ept, double r) {
        for(int i = 0; i < 3; ++i) {
            this.locCoods[i] = r * spt.locCoods[i] + (1.0D - r) * ept.locCoods[i];
        }

    }

    public GeoPoint(double[] xyz) {
        for(int i = 0; i < xyz.length; ++i) {
            this.locCoods[i] = xyz[i];
        }

    }

    public void setLocCoods(double x, double y, double z) {
        this.locCoods[0] = x;
        this.locCoods[1] = y;
        this.locCoods[2] = z;
    }

    public void setLocCoods(double[] xyz) {
        if (xyz.length > 3) {
            System.out.println("Error: check the length of array");
        } else {
            this.locCoods[0] = xyz[0];
            this.locCoods[1] = xyz[1];
            this.locCoods[2] = xyz[2];
        }
    }

    public GeoPoint intermediate(GeoPoint p, double r) {
        return new GeoPoint((1.0D - r) * this.locCoods[0] + r * p.locCoods[0], (1.0D - r) * this.locCoods[1] + r * p.locCoods[1], (1.0D - r) * this.locCoods[2] + r * p.locCoods[2]);
    }

    public double getLocationX() {
        return this.locCoods[0];
    }

    public double getLocationY() {
        return this.locCoods[1];
    }

    public double getLocationZ() {
        return this.locCoods[2];
    }

    public double[] getLocCoods() {
        return this.locCoods;
    }

    public double distanceSquared(GeoPoint p) {
        double e_diff = p.locCoods[0] - this.locCoods[0];
        double n_diff = p.locCoods[1] - this.locCoods[1];
        double h_diff = p.locCoods[2] - this.locCoods[2];
        return n_diff * n_diff + e_diff * e_diff + h_diff * h_diff;
    }

    public double distance(GeoPoint p) {
        return Math.sqrt(this.distanceSquared(p));
    }

    public boolean equal(GeoPoint p, double epsilon) {
        return Math.abs(this.locCoods[0] - p.locCoods[0]) < epsilon && Math.abs(this.locCoods[1] - p.locCoods[1]) < epsilon && Math.abs(this.locCoods[2] - p.locCoods[2]) < epsilon;
    }

    public boolean equal(GeoPoint p) {
        return Math.abs(this.locCoods[0] - p.locCoods[0]) < 1.0E-9D && Math.abs(this.locCoods[1] - p.locCoods[1]) < 1.0E-9D && Math.abs(this.locCoods[2] - p.locCoods[2]) < 1.0E-9D;
    }

    public double angle(GeoPoint pnt) {
        double dx = pnt.locCoods[0] - this.locCoods[0];
        double dy = pnt.locCoods[1] - this.locCoods[1];
        double dis = dx * dx + dy * dy;
        double alpha;
        if (dis > 1.0E-10D) {
            alpha = Math.acos(dx / Math.sqrt(dis));
            if (dy < 0.0D) {
                alpha = 6.283185307179586D - alpha;
            }
        } else {
            alpha = 0.0D;
        }

        return alpha;
    }

    public GeoPoint bearing(double offset, double alpha) {
        return new GeoPoint(this.locCoods[0] + offset * Math.cos(alpha), this.locCoods[1] + offset * Math.sin(alpha));
    }

    public float[] getLocCoodsf() {
        if (this.locCoodsf == null) {
            this.locCoodsf = new float[3];

            for(int i = 0; i < 3; ++i) {
                this.locCoodsf[i] = (float)this.locCoods[i];
            }
        }

        return this.locCoodsf;
    }

    public GeoPoint derectionVector(GeoPoint endPoint) {
        GeoPoint d = endPoint.minus(this);
        return d.times(1.0D / this.distance(endPoint));
    }

    public GeoPoint add(GeoPoint b) {
        return new GeoPoint(this.getLocationX() + b.getLocationX(), this.getLocationY() + b.getLocationY(), this.getLocationZ() + b.getLocationZ());
    }

    public GeoPoint minus(GeoPoint b) {
        return new GeoPoint(this.getLocationX() - b.getLocationX(), this.getLocationY() - b.getLocationY(), this.getLocationZ() - b.getLocationZ());
    }

    public GeoPoint times(double x) {
        return new GeoPoint(this.locCoods[0] * x, this.locCoods[1] * x, this.locCoods[2] * x);
    }

    public double cross(GeoPoint b) {
        return this.getLocationX() * b.getLocationY() - this.getLocationY() * b.getLocationX();
    }

    public double dot(GeoPoint b) {
        return this.getLocationX() * b.getLocationX() + this.getLocationY() * b.getLocationY();
    }

    public String toString() {
        return Arrays.toString(this.locCoods);
    }

    public static GeoPoint parse(String arrayStr) {
        String[] coords = arrayStr.replace("[", "").replace("]", "").split(",");
        return new GeoPoint(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));
    }

    public static GeoPoint center(List<GeoPoint> points) {
        double meanX = points.stream().mapToDouble(GeoPoint::getLocationX).average().getAsDouble();
        double meanY = points.stream().mapToDouble(GeoPoint::getLocationY).average().getAsDouble();
        double meanZ = points.stream().mapToDouble(GeoPoint::getLocationZ).average().getAsDouble();
        return new GeoPoint(meanX, meanY, meanZ);
    }
}
