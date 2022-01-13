package datatype;

import common.Constants;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class LoopData implements Serializable {
    public static String HEADER = "DET_ID, ROAD_ID,FTIME,TTIME,INT,COUNT,REG_COUNT,LAR_COUNT,ARTH_SPD,HARM_SPD,TURN\r\n";
    private String roadId;
    private LocalDateTime fTime;
    private LocalDateTime tTime;
    private int intervalSecs;
    private long count;
    private long regCount;
    private long larCount;
    private double arthSpd;
    private double harmSpd;
    private String turn;

    public LoopData(int interval, long count, long regular, long large, double arithmetic_mean, double harmonic_mean){
        this.intervalSecs = interval;
        this.count = count;
        this.regCount = regular;
        this.larCount = large;
        this.arthSpd = arithmetic_mean;
        this.harmSpd = harmonic_mean;
    }

    public LoopData addInfo(LocalDateTime fTime, LocalDateTime tTime, String turn, String ftNode) {
        this.fTime = fTime;
        this.tTime = tTime;
        this.turn = turn;
        this.roadId = ftNode;
        return this;
    }

    public static LoopData create(List<SimpleTrajectory> records, int interval) {
        long count = records.size();
        long regular = records.stream().filter(r->r.getType() ==2).count();
        long large = records.stream().filter(r->r.getType() ==1).count();
        double flow = 0.0;
        double arithmetic_mean = 0.0;
        double harmonic_mean = 0.0;
        if (count>0) {
            arithmetic_mean = records.stream().mapToDouble(r->r.getSpeed() / ((double)count)).sum();
            harmonic_mean = ((double)count) / records.stream().mapToDouble(r->1.0/r.getSpeed()).sum();
        }
        return new LoopData(interval, count, regular, large, arithmetic_mean, harmonic_mean);
    }

    public String toRow(String loopID){
        StringBuilder sb = new StringBuilder();
        sb.append(loopID).append(",");
        sb.append(roadId).append(",");
        sb.append(fTime.format(Constants.DATETIME_OUT)).append(",");
        sb.append(tTime.format(Constants.DATETIME_OUT)).append(",");
        sb.append(intervalSecs).append(",");
        sb.append(count).append(",");
        sb.append(regCount).append(",");
        sb.append(larCount).append(",");
        sb.append(arthSpd).append(",");
        sb.append(harmSpd).append(",");
        sb.append(turn).append("\r\n");
        return sb.toString();
    }

}
