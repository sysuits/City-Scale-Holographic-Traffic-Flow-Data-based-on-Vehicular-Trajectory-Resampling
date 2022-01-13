package task;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class MTask implements Serializable {

    public static final int STATUS_WAITING = 1;
    public static final int STATUS_DEALING = 2;
    public static final int STATUS_PROCEEDED = 3;
    public static final int STATUS_FAILED = 4;

    private CountDownLatch countDownLatch;

    long stime;

    long etime;

    int maxCountDown;

    private Integer status;

    private LocalDateTime updatetime;

    String ftNodeStrList;

    LocalDateTime fTime;

    LocalDateTime tTime;

    boolean needFCD;

    long fcdSamplingSec;

    public MTask(LinkedHashMap properties){
        status = STATUS_WAITING;
        updatetime = LocalDateTime.now();
        ftNodeStrList = (String) properties.get("ftNodeStrList");
        fTime = ((Date) properties.get("fTime"))
                .toInstant()
                .atOffset(ZoneOffset.UTC)
                .toLocalDateTime();
        tTime = ((Date) properties.get("tTime"))
                .toInstant()
                .atOffset(ZoneOffset.UTC)
                .toLocalDateTime();
        needFCD = (boolean) properties.get("needFCD");
        fcdSamplingSec = (int) properties.get("fcdSamplingSec");
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(LocalDateTime updatetime) {
        this.updatetime = updatetime;
    }

    public static List<String> list(String idStr){
        return Arrays.asList(idStr.split(","));
    }

    public void initCountDownLatch(int num){
        setCountDownLatch(new CountDownLatch(num));
        setMaxCountDown(num);
    }

    public void countdown(){
        getCountDownLatch().countDown();
    }

    public long getCount(){
        return getCountDownLatch().getCount();
    }

    public boolean finished(){
        return getCount()<=0;
    }

    public int getProgress(){
        double r = ((double)(getMaxCountDown()-getCount())) / ((double)getMaxCountDown());
        return (int) Math.ceil(100.0*r);
    }

    public void setCountDownLatch(CountDownLatch cdl){
        this.countDownLatch = cdl;
    }

    public CountDownLatch getCountDownLatch(){
        return this.countDownLatch;
    }

    public void setMaxCountDown(int num){
        this.maxCountDown = num;
    }

    public int getMaxCountDown(){
        return this.maxCountDown;
    }

    public long getEtime() {
        return etime;
    }

    public void setETime(long et){
        this.etime = et;
    }

    public long getStime() {
        return stime;
    }

    public void setStime(long st){
        this.stime = st;
    }

    public void setEtime(long etime) {
        this.etime = etime;
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

    public boolean isNeedFCD() {
        return needFCD;
    }

    public void setNeedFCD(boolean needFCD) {
        this.needFCD = needFCD;
    }
}
