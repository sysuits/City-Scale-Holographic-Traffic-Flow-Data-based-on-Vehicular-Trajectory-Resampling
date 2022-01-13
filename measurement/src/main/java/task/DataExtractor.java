package task;

import datatype.Trajectory;

import java.time.LocalDateTime;
import java.util.List;

public interface DataExtractor {
    List<Trajectory> getTrajectories(LocalDateTime ft, LocalDateTime tt, String ftnodeListStr);
}
