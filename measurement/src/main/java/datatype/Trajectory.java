package datatype;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Trajectory implements Serializable {

    private String vehID;

    private Integer type;

    private LocalDateTime time;

    private double posm;

    private double speed;

    private String turn;

    private String ftnode;

    private String fnode;

    private String tnode;

    private String nextnode;

    private String path;

    private String route;

    private String pathIdx;

    private LocalDateTime nextTime;

    private double nextPosm;

    public Trajectory(){}

    public Trajectory(ResultSet rs){
        try {
            this.vehID = rs.getString("hphm");
            this.type = rs.getInt("hpzl");
            this.time = rs.getTimestamp("time").toLocalDateTime();
            this.posm = rs.getDouble("posm");
            this.speed = rs.getDouble("speed");
            this.turn = rs.getString("turn");
            this.ftnode = rs.getString("ftnode");
            this.fnode = rs.getString("fnode");
            this.tnode = rs.getString("tnode");
            this.nextnode = rs.getString("nextnode");
            this.path = rs.getString("path");
            this.route = rs.getString("route");
            this.pathIdx = rs.getString("path_idx");
            this.nextTime = rs.getTimestamp("next_time").toLocalDateTime();
            this.nextPosm = rs.getDouble("next_posm");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public LocalDateTime getNextTime() {
        return nextTime;
    }

    public void setNextTime(LocalDateTime nextTime) {
        this.nextTime = nextTime;
    }

    public double getPosm() {
        return posm;
    }

    public void setPosm(double posm) {
        this.posm = posm;
    }

    public double getNextPosm() {
        return nextPosm;
    }

    public void setNextPosm(double nextPosm) {
        this.nextPosm = nextPosm;
    }

    public String getFtnode() {
        return ftnode;
    }

    public void setFtnode(String ftnode) {
        this.ftnode = ftnode;
    }

    public String getFnode() {
        return fnode;
    }

    public void setFnode(String fnode) {
        this.fnode = fnode;
    }

    public String getTnode() {
        return tnode;
    }

    public void setTnode(String tnode) {
        this.tnode = tnode;
    }

    public String getVehID() {
        return vehID;
    }

    public void setVehID(String vehID) {
        this.vehID = vehID;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getNextnode() {
        return nextnode;
    }

    public void setNextnode(String nextnode) {
        this.nextnode = nextnode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getPathIdx() {
        return pathIdx;
    }

    public void setPathIdx(String pathIdx) {
        this.pathIdx = pathIdx;
    }

    public double timeRange(){
        return (double) time.until(nextTime, ChronoUnit.SECONDS);
    }

    public double disRange(){
        return posm - nextPosm;
    }

    public double linearDis(double rate){
        return disRange()*rate + nextPosm;
    }


}
