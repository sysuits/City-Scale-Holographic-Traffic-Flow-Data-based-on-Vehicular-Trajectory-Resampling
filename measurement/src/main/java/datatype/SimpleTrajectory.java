package datatype;

import common.Constants;

import java.time.LocalDateTime;

public class SimpleTrajectory extends Trajectory{
    public LocalDateTime passingTime;
    public static String HEADER = "DET_ID,ROAD_ID,CID,TYPE,TIME,FNODE,TNODE,NEXTNODE,PATH,PATH_INDEX\r\n";

    public SimpleTrajectory(Trajectory traj){
        setVehID(traj.getVehID());
        setType(traj.getType());
        setSpeed(traj.getSpeed());
        setTurn(traj.getTurn());
        setFtnode(traj.getFtnode());
        setFnode(traj.getFnode());
        setTnode(traj.getTnode());
        setNextnode(traj.getNextnode());
        setPath(traj.getPath());
        setRoute(traj.getRoute());
        setPathIdx(traj.getPathIdx());
    }

    public String toRow(String kkid){
        StringBuilder sb = new StringBuilder();
        sb.append(kkid).append(",");
        sb.append(getFtnode()).append(",");
        sb.append(getVehID()).append(",");
        sb.append(getType()).append(",");
        sb.append(passingTime.format(Constants.DATETIME_OUT)).append(",");
        sb.append(getFnode()).append(",");
        sb.append(getTnode()).append(",");
        sb.append(getNextnode()).append(",");
        sb.append(getRoute()).append(",");
        sb.append(getPathIdx()).append("\r\n");
        return sb.toString();
    }
}
