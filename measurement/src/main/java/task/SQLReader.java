package task;

import common.Constants;
import datatype.Trajectory;
import org.sqlite.mc.SQLiteMCConfig;
import task.DataExtractor;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SQLReader {
    public static String TRAJ_DIR = "data/trajectories/";

    protected List<Trajectory> getTrajectories(LocalDateTime ft, LocalDateTime tt, String ftnodeListStr) {
        String[] ftn = ftnodeListStr.split(",");
        if (ftn.length<=0){
            return null;
        }
        List<Trajectory> trajectories = new ArrayList<>();
        LocalDateTime startTime = ft;
        LocalDateTime endTime = startTime.plusDays(1).with(LocalTime.parse("00:00:00"));
        if (tt.isBefore(endTime)){
            endTime = tt;
        }
        while (startTime.isBefore(tt)){
            String dateStr = startTime.format(Constants.DATE);
            for (String n : ftn) {
                String fileName = TRAJ_DIR + n + "_" + dateStr + ".db";
                List<Trajectory> tmp = getFromSQLFile(fileName, startTime, endTime);
                if (tmp!=null && tmp.size()>0){
                    trajectories.addAll(tmp);
                }
            }
            startTime = endTime;
            endTime = startTime.plusDays(1).with(LocalTime.parse("03:00:00"));
            if (tt.isBefore(endTime)){
                endTime = tt;
            }
        }
        return trajectories;
    }

    public List<Trajectory> getFromSQLFile(String fileName, LocalDateTime ft, LocalDateTime tt){
        List<Trajectory> trajectories = new ArrayList<>();
        try {
            Connection connection = new SQLiteMCConfig()
                    .withKey("demokey")
                    .createConnection("jdbc:sqlite:file:"+fileName);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            String sql = "select * from traj_sysu where time between Datetime('%s') and Datetime('%s');";
            sql = String.format(sql,
                    ft.format(Constants.DATETIME_FORMAT),
                    tt.format(Constants.DATETIME_FORMAT));
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                Trajectory t = new Trajectory(rs);
                trajectories.add(t);
            }
        } catch (SQLException throwables) {
//            throwables.printStackTrace();
            return null;
        }
        return trajectories.size() > 0 ? trajectories : null;
    }

}
