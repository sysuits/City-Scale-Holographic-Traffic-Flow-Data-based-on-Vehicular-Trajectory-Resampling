import common.FileIOSetting;
import detector.LoopDetector;
import net.sf.jsefa.Deserializer;
import net.sf.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;
import org.apache.commons.cli.*;
import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.ProjCoordinate;
import org.postgis.Geometry;
import org.postgis.MultiLineString;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.yaml.snakeyaml.Yaml;
import roadnetwork.CenterRoad;
import roadnetwork.GeoPoint;
import roadnetwork.GeoPoints;
import task.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class CmdRunner {
    MTask task;

    public void process(HashMap<String, Object> properties){
        task.initCountDownLatch(3);
        task.setStatus(MTask.STATUS_DEALING);
        task.setStime(System.currentTimeMillis());

        FileIOSetting.mkResDir((String)properties.get("outputDir"));

        List<CenterRoad> crs = filterRoad(task);
        if (crs==null || crs.size()<=0){
            return;
        }

//        List<AVIDetector> aviDetectors = filterAVI(properties.get("avi"));
//        if (aviDetectors.size()>0){
//            LPRHandler lprHandler = new LPRHandler();
//            lprHandler.setTask(task);
//            lprHandler.setDetectors(aviDetectors);
//            lprHandler.start();
//        }
//        else {
//            task.countdown();
//        }
        task.countdown();

//        List<LoopDetector> loopDetectors = filterLoop(properties.get("loop"));
        List<LoopDetector> loopDetectors = allLoop();
        if (loopDetectors.size()>0){
            LoopHandler loopHandler = new LoopHandler();
            loopHandler.setTask(task);
            loopHandler.setLoopDetectors(loopDetectors);
            loopHandler.start();
        }
        else {
            task.countdown();
        }

        if (task.isNeedFCD()){
            FCDHandler fcdHandler = new FCDHandler();
            fcdHandler.setTask(task);
            fcdHandler.initFcdCondition();
            fcdHandler.start();
        }
        else {
            task.countdown();
        }

    }

    public List<LoopDetector> allLoop(){
        List<LoopDetector> ans = new ArrayList<>();
        for (CenterRoad c : CenterRoad.getLinkMap().values()) {
            LoopDetector tmp = new LoopDetector();
            tmp.loopId = "lp_"+c.id;
            tmp.ftNode = c.id;
            tmp.missingRate = 0.0;
            tmp.interval = 300;
            tmp.position = c.getLength() / 2.0;
            ans.add(tmp);
        }
        return ans;
    }

    public List<CenterRoad> filterRoad(MTask task){
        String tmp = task.getFtNodeStrList();
        List<CenterRoad> rds = new ArrayList<>();
        if (tmp != null && !"".equals(tmp)){
            String[] roadIDs = tmp.split(",");
            if (roadIDs.length>0){
                for (String roadID : roadIDs) {
                    CenterRoad c = CenterRoad.getLinkMap().get(roadID);
                    if (c != null) {
                        rds.add(c);
                    }
                }
            }
        }
        if (rds.size()>0){
            return rds;
        }
        else {
            return null;
        }
    }

    public List<LoopDetector> filterLoop(Object loopList){
        List<LoopDetector> loopDetectors = new ArrayList<>();
        if (null!=loopList && (loopList instanceof ArrayList)){
            for (Object loop : (ArrayList) loopList) {
                if (loop instanceof LinkedHashMap){
                    LoopDetector lpd = new LoopDetector((LinkedHashMap) loop);
                    loopDetectors.add(lpd);
                }
            }
        }
        return loopDetectors;
    }

    public GeoPoint changeLatlng2XY(Point point){
        ProjCoordinate tfCoordinate = new ProjCoordinate();
        getTransform().transform(new ProjCoordinate(point.getX(),point.getY()), tfCoordinate);
        return new GeoPoint(tfCoordinate.x, tfCoordinate.y);
    }

    static BasicCoordinateTransform latlon2plane;
    public BasicCoordinateTransform getTransform(){
        if (latlon2plane==null){
            CRSFactory factory = new CRSFactory();
            CoordinateReferenceSystem latlonCrs = factory.createFromName("EPSG:4326");
            CoordinateReferenceSystem planeCrs = factory.createFromName("EPSG:2362");
            latlon2plane = new BasicCoordinateTransform(latlonCrs, planeCrs);
        }
        return latlon2plane;
    }

    public void initCenterRoads(){
        {
            List<CenterRoad> roads = readRd(); //rdMapper.getRoads();
            for (CenterRoad road: roads) {
                road.flowdir = 1;
                Geometry geom = null;
                try {
                    geom = PGgeometry.geomFromString(road.biGeomStr);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                road.geom = geom;
                MultiLineString lines = (MultiLineString) geom;
                road.lineString = lines;
                List<GeoPoint> ps = new ArrayList<>();
                for (int i = 0; i < lines.numPoints(); i++) {
                    ps.add(changeLatlng2XY(lines.getPoint(i)));
                }
                double len2 = GeoPoints.calLength(ps);
                road.setGeoPoints(ps);
                road.setLength(len2);
                CenterRoad.getLinkMap().put(road.id, road);
            }
        }
    }

    public static List<CenterRoad> readRd(){
        List<CenterRoad> ans = new ArrayList<>();
        CsvConfiguration config = new CsvConfiguration();
        config.setFieldDelimiter(',');
        config.setLineFilter(new HeaderAndFooterFilter(1, false, false));
        Deserializer deserializer = CsvIOFactory.createFactory(config, CenterRoad.class)
                .createDeserializer();
        Reader reader = null;
        try {
            reader = new FileReader("data/road.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ans;
        }
        deserializer.open(reader);
        while (deserializer.hasNext()) {
            CenterRoad r = deserializer.next();
            ans.add(r);
        }
        deserializer.close(true);
        return ans;
    }

    public static void printErr(Options options) {
        String header = "please check the correct param\n\n";
        String footer = "\nPlease report issues at https://github.com/sysuits/City-Scale-Holographic-Traffic-Flow-Data-based-on-Vehicular-Trajectory-Resampling/issues";

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("OsLauncher java -jar measurement.jar ", header, options, footer, true);
    }

    public static void printHelp(Options options){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("OsLauncher java -jar measurement.jar ", options);
    }

    public static void main(String[] args) throws FileNotFoundException {

        CommandLine commandLine;
        String pFile;

        Option conf = Option.builder("c")
                .required(false)
                .desc("Specifies the relative or absolute path to the properties file")
                .longOpt("config")
                .build();
        Option help = Option.builder("h")
                .required(false)
                .desc("Print this message")
                .longOpt("help")
                .build();
        Options options = new Options();
        options.addOption(conf);
        options.addOption(help);

        CommandLineParser parser = new DefaultParser();

        try {
            commandLine = parser.parse(options, args);
            if (commandLine.hasOption("h")) {
                printHelp(options);
                System.exit(0);
            }

            if (commandLine.hasOption("c"))
            {
                pFile = commandLine.getOptionValue("c");
                CmdRunner runner = new CmdRunner();
                Yaml yaml = new Yaml();
                InputStream is = new FileInputStream(pFile);
                HashMap<String, Object> properties = yaml.loadAs(is, HashMap.class);
                SQLReader.TRAJ_DIR = (String) properties.get("TRAJ_DIR");
                runner.task = new MTask((LinkedHashMap) properties.get("task"));
                runner.initCenterRoads();
                runner.process(properties);
                runner.task.getCountDownLatch().await();
            }
        } catch (Exception e) {
            System.out.println("Exceptions occurred.");
            System.out.println(e.getMessage());
            printErr(options);
        }

//        CmdRunner runner = new CmdRunner();
//        Yaml yaml = new Yaml();
//        InputStream is = new FileInputStream(args[0]);
//        HashMap<String, Object> properties = yaml.loadAs(is, HashMap.class);
//        SQLReader.TRAJ_DIR = (String) properties.get("TRAJ_DIR");
//        runner.task = new MTask((LinkedHashMap) properties.get("task"));
//        runner.initCenterRoads();
//        runner.process(properties);
//        try{
//            runner.task.getCountDownLatch().await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        finally {
//            System.out.println("finished.");
//        }

    }
}
