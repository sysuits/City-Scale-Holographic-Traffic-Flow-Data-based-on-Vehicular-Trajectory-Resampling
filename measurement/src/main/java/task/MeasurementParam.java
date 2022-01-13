package task;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MeasurementParam implements Serializable {
    String ftNodeStrList;

    LocalDateTime fTime;

    LocalDateTime tTime;

    long fcdSamplingSec;

    public MeasurementParam copy() {
        MeasurementParam res = new MeasurementParam();
        res.setfTime(getfTime());
        res.settTime(gettTime());
        res.setFtNodeStrList(getFtNodeStrList());
        return res;
    }

    public String getFtNodeStrList() {
        return ftNodeStrList;
    }

    public void setFtNodeStrList(String ftNodeStrList) {
        this.ftNodeStrList = ftNodeStrList;
    }

    public LocalDateTime getfTime() {
        return fTime;
    }

    public void setfTime(LocalDateTime fTime) {
        this.fTime = fTime;
    }

    public LocalDateTime gettTime() {
        return tTime;
    }

    public void settTime(LocalDateTime tTime) {
        this.tTime = tTime;
    }

    public LocalDateTime getFTime(){
        return this.fTime;
    }

    public LocalDateTime getTTime(){
        return this.tTime;
    }

    public long getFcdSamplingSec() {
        return fcdSamplingSec;
    }

    public void setFcdSamplingSec(long fcdSamplingSec) {
        this.fcdSamplingSec = fcdSamplingSec;
    }
}
