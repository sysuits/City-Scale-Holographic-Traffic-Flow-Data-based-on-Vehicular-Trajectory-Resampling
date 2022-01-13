package detector;

import datatype.SimpleTrajectory;
import datatype.LoopData;
import datatype.Trajectory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoopDetector {

    public String loopId;
    public String ftNode;
    public double position;
    public double missingRate;
    public int interval;

    public LoopDetector() {

    }

    public LoopDetector(LinkedHashMap aviMap) {
        this.loopId = (String) aviMap.get("loopId");
        this.ftNode = (String) aviMap.get("ftNode");
        this.position = (double) aviMap.get("position");
        this.missingRate = (double) aviMap.get("missingRate");
        this.interval = (int) aviMap.get("interval");
    }

    public List<SimpleTrajectory> detect(List<Trajectory> dataset, long randomSeed){
        //get random missing machine
        Random rand = new Random(randomSeed);
        double missRate = (this.missingRate) / 100.0;
        //records on this link
        //pick passing records from above
        Stream<Trajectory> selectedPassedRec = dataset.stream()
                .filter(d->d.getFtnode().equals(ftNode))
                .filter(this::checkPass);
        //Randomly, from passing record calculate passing time
        List<SimpleTrajectory> res = new ArrayList<>();
        selectedPassedRec.forEach(r -> {
            if(rand.nextDouble() >= missRate){
                LocalDateTime passingTime = calPassing(r);
                SimpleTrajectory lpr = new SimpleTrajectory(r);
                lpr.passingTime = passingTime;
                res.add(lpr);
            }
        });
        //output
        return res;
    }

    public boolean checkPass(Trajectory traj){
        boolean nowAhead = (traj.getPosm() > position);
        boolean nextPassed = (traj.getNextPosm() <= position);
        return (nowAhead & nextPassed);
    }

    public LocalDateTime calPassing(Trajectory traj){
        long timeNano = traj.getTime().until(traj.getNextTime(), ChronoUnit.NANOS);//1e9 ns = 1 s
        double rate = (traj.getPosm() - position) / (traj.getPosm() - traj.getNextPosm());
        long deltaT = Math.round(((double) timeNano) * rate);
        LocalDateTime res = traj.getTime().plusNanos(deltaT);
        return res;
    }

    public String getLoopId() {
        return loopId;
    }

    public void setLoopId(String loopId) {
        this.loopId = loopId;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public List<LoopData> aggregate(List<SimpleTrajectory> lprs, LocalDateTime fTime, LocalDateTime tTime){
        List<LoopData> ltdRecords = new ArrayList<>();
        LocalDateTime ft = fTime;
        LocalDateTime tt = fTime.plusSeconds((long)interval);
        while (tt.compareTo(tTime)<0){
            LocalDateTime a = ft;
            LocalDateTime b = tt;
            List<SimpleTrajectory> subRecords = lprs.stream().filter(r ->
                    r.passingTime.compareTo(a)>=0 && r.passingTime.compareTo(b)<0)
                    .collect(Collectors.toList());
            Map<String, List<SimpleTrajectory>> laneRecordMap = subRecords.stream()
                    .collect(Collectors.groupingBy(Trajectory::getTurn));
            for(Map.Entry<String, List<SimpleTrajectory>> entry : laneRecordMap.entrySet()){
                List<SimpleTrajectory> turnRecords = entry.getValue();
                ltdRecords.add(
                        LoopData.create(turnRecords, interval)
                                .addInfo(ft,tt,entry.getKey(),ftNode)
                );
            }
            ft=ft.plusSeconds((long) interval);
            tt=tt.plusSeconds((long) interval);
        }
        return ltdRecords;
    }
}
