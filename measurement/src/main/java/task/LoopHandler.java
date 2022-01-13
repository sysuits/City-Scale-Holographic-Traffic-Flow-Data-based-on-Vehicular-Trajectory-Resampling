package task;

import common.FileIOSetting;
import common.TxtUtils;
import datatype.SimpleTrajectory;
import datatype.LoopData;
import datatype.Trajectory;
import detector.LoopDetector;

import java.time.LocalDateTime;
import java.util.List;

public class LoopHandler extends MThread{

    public List<LoopDetector> loopDetectors;

    @Override
    public void run() {
        writer = new TxtUtils(FileIOSetting.outputDir() + "loop.csv");
        writer.writeNFlush(LoopData.HEADER);
        super.run();
        writer.closeWriter();
    }

    @Override
    void unitProcess(LocalDateTime start, LocalDateTime end) {
        //获取节段原始轨迹
//        List<Trajectory> trajUnit = originTraj(start, end);
        for (LoopDetector loop : loopDetectors) {
//            System.out.println("lp on" + loop.ftNode + ", " + start.toString() + " to " + end.toString() + " query.");
            List<Trajectory> trajUnit = extractor.getTrajectories(start, end, loop.ftNode);
            if (null==trajUnit || trajUnit.size()<=0){
                continue;
            }
            System.out.println("lp on" + loop.ftNode + ", " + start.toString() + " to " + end.toString() + " finish.");
            List<SimpleTrajectory> lprData = loop.detect(trajUnit, 0);
            List<LoopData> loopOutput = loop.aggregate(lprData, start, end);
            loopOutput.forEach(d -> writer.write(d.toRow(String.valueOf(loop.getLoopId()))));
            writer.flushBuffer();
        }
    }

    public void setLoopDetectors(List<LoopDetector> loopDetectors) {
        this.loopDetectors = loopDetectors;
    }
}
