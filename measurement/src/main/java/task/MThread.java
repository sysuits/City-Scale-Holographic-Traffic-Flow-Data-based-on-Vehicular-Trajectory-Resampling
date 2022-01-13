package task;

import com.google.inject.Inject;
import common.FileIOSetting;
import common.TxtUtils;
import datatype.Trajectory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public abstract class MThread extends Thread{
    public MTask task;
    public TxtUtils writer;
    public SQLReader extractor;


    @Override
    public void run() {
        long st = System.currentTimeMillis();
        int nTask = folkNum();
        CountDownLatch countDownLatch = new CountDownLatch(nTask);
        extractor = new SQLReader();
        for (int i = 0; i < nTask; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    if (getTask().getStatus()!= MTask.STATUS_FAILED){
                        miniRun(finalI);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    System.out.println("mini run failed.");
                    updateTaskStatus(MTask.STATUS_FAILED);
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        try {
            countDownLatch.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            long et = System.currentTimeMillis();
            String rep = String.format("%s from measurement finished in：%d ms.",
                    getClass().getName(),
                    (et-st));
            System.out.println(rep);
            getTask().countdown();
            if (getTask().finished()){
                FileIOSetting.pack2dnDir();
                if (getTask().getStatus()!=MTask.STATUS_FAILED){
                    updateTaskStatus(MTask.STATUS_PROCEEDED);
                }
                else {
                    updateTaskStatus(MTask.STATUS_FAILED);
                }
                long etime = System.currentTimeMillis();
                getTask().setEtime(etime);
                String timer = String.format("measurement status %d totally costs：%d ms.",
                        getTask().getStatus(),
                        (etime-getTask().getStime()));
                System.out.println(timer);
            }
        }
    }

    public void miniRun(int index) throws Exception {
        LocalDateTime ft = task.getfTime();
        LocalDateTime tt = task.gettTime();
        //起始日，对齐ft
        LocalDateTime start = ft.plusDays(index).truncatedTo(ChronoUnit.DAYS);
        if (ft.isAfter(start)){
            start = ft;
        }
        //结束日，对齐tt
        LocalDateTime end = ft.plusDays(index+1).truncatedTo(ChronoUnit.DAYS);
        if (tt.isBefore(end)){
            end = tt;
        }
        unitProcess(start, end);
    }

    public void updateTaskStatus(int taskStatus){
        getTask().setStatus(taskStatus);
        getTask().setUpdatetime(LocalDateTime.now());
    }

    public int folkNum() {
        LocalDateTime ft = task.getfTime();
        LocalDateTime tt = task.gettTime();
        //limit to 1 month task
        int x = (int) Math.min(30, ft.until(tt, ChronoUnit.DAYS));
        return 1 + x;
    }

    protected List<Trajectory> originTraj(LocalDateTime start, LocalDateTime end){
        List<Trajectory> trajUnit = extractor.getTrajectories(start, end, task.ftNodeStrList);
        return trajUnit;
    }

    public static List<LocalDateTime> generateTimeSeries(LocalDateTime ft, LocalDateTime tt, long interval){
        List<LocalDateTime> timeSeries = new ArrayList<>();
        LocalDateTime sampledTime = ft;
        while (!sampledTime.isAfter(tt)){
            timeSeries.add(sampledTime);
            sampledTime = sampledTime.plusSeconds(interval);
        }
        return timeSeries;
    }

    public MTask getTask() {
        return this.task;
    }

    public MThread setTask(MTask task){
        this.task = task;
        return this;
    }

    abstract void unitProcess(LocalDateTime start, LocalDateTime end);

}
