package com.project;

import javafx.scene.paint.Color;

import java.util.*;

/**
 * The figure representation class
 * key attribute is a list of vertices in 2D space
 */
public class Figure {
    /**
     * List of vertices
     */
    List<Cell> vertices = new ArrayList<>();
    /**
     * Color of the figure
     */
    Color color;
    /**
     * Finished figure (true) has all vertices, and it is not possible to add more of them,
     * if finished is false, it is not possible to calculate symmetries, but it is possible to add new vertices
     */
    boolean finished = false;
    /**
     * If this attribute is true, them this figure was created symmetrical to some other figure, that was already drawn
     */
    boolean symmetry_from_other = false;
    /**
     * List of symmetries (null when finished == false)
     */
    Set<List<Cell>> symmetries;

    /**
     * Class constructor specifying color and start vertex.
     * @param color         color of the figure
     * @param startCell     start vertex of the figure
     */
    public Figure(Color color, Cell startCell) {
        this.color = color;
        this.vertices.add(startCell);
    }

    /**
     * Class constructor specifying color and vertices of figure to create.
     * @param color     color of the figure
     * @param cells     list of figure's vertices
     */
    public Figure(Color color, List<Cell> cells) {
        this.color = color;
        this.vertices = new ArrayList<>(List.copyOf(cells));
        this.finished = true;
        this.symmetries = getAllSymmetries();
    }

    /**
     * Adds vertex to the figure.
     * If number of vertexes is already greater than 2 and new vertex
     * is the same as a first (start) vertex, than finishes the figure, by
     * setting finished parameter to true and doesn't add vertex to the
     * figure.
     * If the new vertex is already in the figure, then do nothing
     * and return false.
     * If the figure is finished, return false
     * @param cell  a new vertex of the figure
     * @return      boolean true/false depending on success of the execution
     */
    public boolean addCell(Cell cell) {
        if (finished) return false;
        if (vertices.getFirst().equals(cell) && vertices.size() >= 3) {
            finished = true;
            symmetries = getAllSymmetries();
            return true;
        }
        if (vertices.contains(cell)) {
            return false;
        }
        vertices.add(cell);
        return true;
    }

    /**
     * @return number of vertices
     */
    public int number_of_vertices() {
        return vertices.size();
    }

    /**
     * @return penultimate vertex (if exists, otherwise -> null)
     */
    public Cell get_previous_vertex() {
        if (vertices.size() >= 2) {
            return vertices.get(vertices.size() - 2);
        }
        return null;
    }

    /**
     * @return first vertex (if exists, otherwise -> null)
     */
    public Cell get_first_vertex() {
        if (!vertices.isEmpty()) {
            return vertices.get(0);
        }
        return null;
    }

    /**
     * @return last vertex (if exists, otherwise -> null)
     */
    public Cell get_last_vertex() {
        if (!vertices.isEmpty()) {
            return vertices.get(vertices.size()-1);
        }
        return null;
    }

    /**
     * @return list of vertices
     */
    public List<Cell> get_vertices() {
        return vertices;
    }

    private int[] find_extremes(List<Cell> cells) {
        int max_width, max_height, min_width, min_height;
        max_width = cells.get(0).x;
        min_width = cells.get(0).x;
        max_height = cells.get(0).y;
        min_height = cells.get(0).y;
        for (int i = 1; i < cells.size(); i++) {
            Cell cell = cells.get(i);
            if (cell.x > max_width) max_width = cell.x;
            if (cell.y > max_height) max_height = cell.y;
            if (cell.x < min_width) min_width = cell.x;
            if (cell.y < min_height) min_height = cell.y;
        }
        return new int[]{max_width, min_width, max_height, min_height};
    }

    /**
     * Shifts all vertices to the origin of coordinates (in this project it is called normal state)
     * @param cells list of vertices
     * @return      list of normal vertices
     */
    public List<Cell> to_normal(List<Cell> cells) {
        List<Cell> normal_cells = new ArrayList<>();
        int[] extremes = find_extremes(cells);
        int min_width = extremes[1];
        int min_height = extremes[3];
        for (Cell c : cells) {
            normal_cells.add(new Cell(c.y - min_height, c.x - min_width));
        }
        return normal_cells;
    }

    /**
     * Flips horizontal all vertices
     * @param normal_cells  list of normal vertices
     * @return              list of flipped normal vertices
     */
    private List<Cell> horizontal_flip(List<Cell> normal_cells) {
        int[] extremes = find_extremes(normal_cells);
        int max_height = extremes[2];
        List<Cell> hor_flipped = new ArrayList<>();
        for (Cell c : normal_cells) {
            hor_flipped.add(new Cell(max_height - c.y, c.x));
        }
        return hor_flipped;
    }

    /**
     * Flips vertical all vertices
     * @param normal_cells  list of normal vertices
     * @return              list of flipped normal vertices
     */
    private List<Cell> vertical_flip(List<Cell> normal_cells) {
        int[] extremes = find_extremes(normal_cells);
        int max_width = extremes[0];
        List<Cell> ver_flipped = new ArrayList<>();
        for (Cell c : normal_cells) {
            ver_flipped.add(new Cell(c.y, max_width - c.x));
        }
        return ver_flipped;
    }

    /**
     * Rotates on 90 degrees all vertices
     * @param normal_cells  list of normal vertices
     * @return              list of rotated normal vertices
     */
    private List<Cell> rotate90(List<Cell> normal_cells) {
        int[] extremes = find_extremes(normal_cells);
        int max_width = extremes[0];
        List<Cell> rotated90 = new ArrayList<>();
        for (Cell c : normal_cells) {
            rotated90.add(new Cell(c.x, max_width - c.y));
        }
        return rotated90;
    }

    /**
     * Detects and deletes useless vertices, that do not pay role in the figure creation, because
     * they are lying between two vertices on the same X or Y axis
     * @param cells     list of vertices
     * @return          filtered list of vertices
     */
    private List<Cell> deleteUselessCells(List<Cell> cells) {
        if (cells.size() < 3) {
            return cells;
        }
        List<Cell> filtered_cells = new ArrayList<>();
        // deleting unnecessary vertices
        for (int i = 0; i < cells.size(); i++) {
            int prevIndex1 = (i - 1 + cells.size()) % cells.size();
            int prevIndex2 = (i - 2 + cells.size()) % cells.size();

            if ((cells.get(i).x == cells.get(prevIndex1).x && cells.get(i).x == cells.get(prevIndex2).x) ||
                    (cells.get(i).y == cells.get(prevIndex1).y && cells.get(i).y == cells.get(prevIndex2).y)) {
            } else {
                filtered_cells.add(cells.get(prevIndex1));
            }
        }
        return filtered_cells;
    }

    /**
     * Finds all possible symmetries, rotations and permutations (shifts) of the vertices of the current figure in 2D space.
     * Permutations used in case of comparing two figures that are the same, but was created in different vertex order,
     * for instance [(1,0), (0,1), (0,0)] and [(0,1), (0,0), (1,0)], the figures are the same, but order of vertices is
     * different, it was the best solution to just add all such permutations (shifts) to the set of symmetries than
     * reinvent the wheel
     * @return set of all possible symmetries, rotations and permutations (shifts) of the vertices
     */
    public Set<List<Cell>> getAllSymmetries() {
        if (!finished) return null;

        List<Cell> normal_cells = to_normal(vertices);

        List<Cell> horizontal_flipped = horizontal_flip(normal_cells);
        List<Cell> vertical_flipped = vertical_flip(normal_cells);
        List<Cell> horizontal_vertical_flipped = vertical_flip(horizontal_flipped);

        List<Cell> rotated90 = rotate90(normal_cells);
        List<Cell> rotated_horizontal_flipped = horizontal_flip(rotated90);
        List<Cell> rotated_vertical_flipped = vertical_flip(rotated90);
        List<Cell> rotated_horizontal_vertical_flipped = vertical_flip(rotated_horizontal_flipped);

        Set<List<Cell>> allSymmetries = new HashSet<>();
        allSymmetries.addAll(getAllPermutations(normal_cells));
        allSymmetries.addAll(getAllPermutations(horizontal_flipped));
        allSymmetries.addAll(getAllPermutations(vertical_flipped));
        allSymmetries.addAll(getAllPermutations(horizontal_vertical_flipped));

        allSymmetries.addAll(getAllPermutations(rotated90));
        allSymmetries.addAll(getAllPermutations(rotated_horizontal_flipped));
        allSymmetries.addAll(getAllPermutations(rotated_vertical_flipped));
        allSymmetries.addAll(getAllPermutations(rotated_horizontal_vertical_flipped));
        return allSymmetries;
    }


    /**
     * Finds all possible permutations (shifts) of the vertices.
     * Permutations used in case of comparing two figures that are the same, but was created in different vertex order,
     * for instance [(1,0), (0,1), (0,0)] and [(0,1), (0,0), (1,0)], the figures are the same, but order of vertices is
     * different
     * @param vertices  list of vertices
     * @return          all possible permutations (shifts)
     */
    private Set<List<Cell>> getAllPermutations(List<Cell> vertices) {
        Set<List<Cell>> ret = new HashSet<>();
        List<Cell> reversed = new ArrayList<>(List.copyOf(vertices));
        Collections.reverse(reversed);
        for (int i = 0; i < vertices.size(); i++) {
            List<Cell> new_perm = new ArrayList<>();
            List<Cell> new_reversed_perm = new ArrayList<>();
            for (int j = 0; j < vertices.size(); j++) {
                new_reversed_perm.add(reversed.get((j+i) % reversed.size()));
                new_perm.add(vertices.get((j+i) % vertices.size()));
            }
            ret.add(deleteUselessCells(new_perm));
            ret.add(deleteUselessCells(new_reversed_perm));
        }
        return ret;
    }

    /**
     * @return string representation of the figure
     */
    @Override
    public String toString() {
        return vertices.toString();
    }

    /**
     * @param obj   other object to compare
     * @return      true - if this object is similar to other (by existing at least one symmetry of their vertices),
     * false if not
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Figure figure = (Figure) obj;

        Set<List<Cell>> intersection = new HashSet<>(figure.symmetries);
        intersection.retainAll(symmetries);
        return !intersection.isEmpty();
    }

    /**
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(symmetries);
    }


    /**
     * Calculates the area of the figure by vertices, using Shoelace Theorem.
     * Notice: if some edges of the figure crosses another, then the function returns wrong area
     * @link <a href="https://artofproblemsolving.com/wiki/index.php?title=Shoelace_Theorem">Shoelace Theorem</a>
     * @return polygon area
     */
    public double calculatePolygonArea() {
        int n = vertices.size();
        double area = 0;

        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            area += vertices.get(i).getX() * vertices.get(j).getY();
            area -= vertices.get(j).getX() * vertices.get(i).getY();
        }

        area = Math.abs(area) / 2.0;
        return area;
    }

    public static void main(String[] args) {
        // for testing and debugging
        Figure f = new Figure(null, new Cell(1,0));
        f.addCell(new Cell(0,2));
        f.addCell(new Cell(0,1));
        f.addCell(new Cell(0,0));
        f.addCell(new Cell(1,0));

        Figure f2 = new Figure(null, new Cell(0,0));
        f2.addCell(new Cell(1,0));
        f2.addCell(new Cell(0,2));
        f2.addCell(new Cell(0,1));
        f2.addCell(new Cell(0,0));

        System.out.println(f.calculatePolygonArea());
    }
}

