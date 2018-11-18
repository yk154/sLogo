package view.turtle;

import java.util.List;

public interface IconLocationListener {
    void hasMoved(List<Coordinate> locationLog, int id);
    void hasSpun(double newAngle, int id);
}
