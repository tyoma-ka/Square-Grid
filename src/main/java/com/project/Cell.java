package com.project;
import java.util.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * The vertex representation.
 * Yes, I know, it is wrong name (it should be Vertex or at least Point) and yes I know about save cascade renaming, but I do really afraid
 * to mess everything up, so it is written Cell, but I will call it point or vertex.
 */
public class Cell {
    int y, x;
    List<Figure> figures = new ArrayList<>();

    /**
     * Class constructor specifying y and x positions of the point.
     * @param y  height position
     * @param x  width position
     */
    public Cell(int y, int x) {
        this.y = y;
        this.x = x;
    }

    /**
     * @param obj   other object to compare
     * @return      true - both objects are instance of Cell and have equal coordinates (y,x), false - otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cell cell = (Cell) obj;
        return x == cell.x && y == cell.y;
    }

    /**
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("(%d, %d)", y, x);
    }

    /**
     * @return x value
     */
    public int getX() {
        return x;
    }

    /**
     * @return y value
     */
    public int getY() {
        return y;
    }
}
