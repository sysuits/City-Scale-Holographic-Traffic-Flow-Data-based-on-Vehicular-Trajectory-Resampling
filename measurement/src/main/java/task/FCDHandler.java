package task;

import common.Constants;
import common.FileIOSetting;
import common.TxtUtils;
import datatype.Trajectory;
import roadnetwork.CenterRoad;
import roadnetwork.CoordTransformUtils;
import roadnetwork.GeoPoint;
import roadnetwork.GeoPoints;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FCDHandler extends MThread {

    //待插入时间段
    List<LocalDateTime> times;

    //Header
    String FCD_HEADER = "CID,TYPE,TIME,LON,LAT,SPD,TURN,DIS,ROADID\r\n";

    @Override
    public void run() {
        writer = new TxtUtils(FileIOSetting.outputDir() +"fcd.csv");
        writer.writeNFlush(FCD_HEADER);
        super.run();
        writer.closeWriter();
    }


    public void initFcdCondition() {
        this.times = generateTimeSeries(task.getfTime(), task.gettTime(), task.getFcdSamplingSec());
    }

    @Override
    void unitProcess(LocalDateTime start, LocalDateTime end) {
        //获取节段原始轨迹
        List<Trajectory> trajUnit = originTraj(start, end);
        //截取插值时刻
        List<LocalDateTime> unitTimeSecs = times.stream()
                .filter(t-> start.isBefore(t)&&!t.isAfter(end))
                .collect(Collectors.toList());
        //插值
        trajUnit.parallelStream().forEach(traj -> interpolate(traj, unitTimeSecs));
    }

    void interpolate(Trajectory traj, List<LocalDateTime> unitTimeSecs){
        int dir = 1;
        CenterRoad link = CenterRoad.getLinkMap().get(traj.getFtnode());
        if (link==null){
            String[] ftnode = traj.getFtnode().split("_");
            link = CenterRoad.getLinkMap().get(ftnode[1]+"_"+ftnode[0]);
            dir = -1;
        }
        if (traj.getPosm()<= GeoPoints.calLength(link.getGeoPoints()) && traj.timeRange()>=0){
            Stream<LocalDateTime> intersecs = unitTimeSecs.stream().filter(t->traj.getTime().isBefore(t)&&!t.isAfter(traj.getNextTime()));
            CenterRoad finalLink = link;
            int finalDir = dir;
            intersecs.forEach(t-> writer.write(makePoint(t,traj, finalLink, finalDir)));
            writer.flushBuffer();
        }
    }

    String makePoint(LocalDateTime t, Trajectory traj, CenterRoad link, int dir){
        double rate = traj.timeRange()==0 ?
                1 :
                ((double) t.until(traj.getNextTime(), ChronoUnit.SECONDS)) / traj.timeRange();
        double dis = traj.linearDis(rate);
        double dsp = dir>0 ? link.getLength() - dis : dis;
        List<GeoPoint> points = link.getGeoPoints();
        double l = 0;
        int i = 0;
        while (l<=dsp && i<points.size()-1){
            l += points.get(i).distance(points.get(i+1));
            i++;
        }
        try {
            GeoPoint p = points.get(i).add(points.get(i).derectionVector(points.get(i-1)).times(l-dsp));
            GeoPoint pout = CoordTransformUtils.plane2latlon(p);
            String out = traj.getVehID() + "," +
                    traj.getType() + "," +
                    t.format(Constants.DATETIME_FORMAT) + "," +
                    pout.getLocationX() + "," +
                    pout.getLocationY() + "," +
                    traj.getSpeed() + "," +
                    traj.getTurn() + "," +
                    dis + "," +
                    traj.getFtnode() + "\r\n";
            return out;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return "";
        }
    }
}
