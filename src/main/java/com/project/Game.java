package com.project;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;
import java.util.List;


/**
 * Square grid game, where it is possible to draw shapes (figures).
 * Children could use this application to learn about symmetries: application fills equal shapes with
 * the same color and writes on the label number of symmetrical shapes, also it specify square area of the last shape.
 * If you want to change size of the game, just change SIZE parameter on the line 27 or CELL_SIZE on the line 28
 */
public class Game extends Application{
    int SIZE = 20; // number of squares
    int CELL_SIZE = 40; // size of the square

    private Canvas canvas;
    private GraphicsContext gc;
    private Label dataLabel;
    private Label symmetryLabel;
    private Label symmetryInfoLabel;
    private Label backspaceLabel;
    private int numberOfSymmetry = 0;
    private List<Figure> figures = new ArrayList<>();
    private Set<List<Cell>> allSymmetries = new HashSet<>();
    private Set<Figure> allPossibleFigures = null;
    private boolean isDrawing = false;
    private Color defined_color = null;

    private static final double TRANSPARENCY = 0.5;

    private static final List<Color> COLORS = Arrays.asList(
            Color.MEDIUMVIOLETRED.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.CORAL.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.DARKSEAGREEN.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.DEEPSKYBLUE.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.SLATEGRAY.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.CADETBLUE.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.TOMATO.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.GOLDENROD.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.MEDIUMSEAGREEN.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.DARKTURQUOISE.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.ROYALBLUE.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.DIMGRAY.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.INDIANRED.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.DARKOLIVEGREEN.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.ORANGERED.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.STEELBLUE.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.TEAL.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.SIENNA.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.DARKORANGE.deriveColor(0, 1, 1, TRANSPARENCY),
            Color.PURPLE.deriveColor(0, 1, 1, TRANSPARENCY)
    );
    static {
        Collections.shuffle(COLORS);
    }
    private static int colorIndex = 0;

    /**
     * Starts the game and initialize all GUI attributes
     * @param primaryStage  stage
     * @see Stage
     */
    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();

        canvas = new Canvas(SIZE * CELL_SIZE, SIZE * CELL_SIZE);
        gc = canvas.getGraphicsContext2D();

        drawGrid(gc);

        StackPane stackPane = new StackPane(canvas);
        gridPane.add(stackPane, 0, 0);


        ComboBox<String> color_menu = new ComboBox<>();
        color_menu.getItems().addAll("Random", "Blue", "Red");
        color_menu.setOnAction(e -> {
            if (Objects.equals(color_menu.getValue(), "Random")) {
                defined_color = null;
            } else if (Objects.equals(color_menu.getValue(), "Blue")) {
                defined_color = Color.BLUE.deriveColor(0, 1, 1, TRANSPARENCY);
            } else if (Objects.equals(color_menu.getValue(), "Red")) {
                defined_color = Color.RED.deriveColor(0, 1, 1, TRANSPARENCY);
            }
        });
        color_menu.setValue("Random");

        Label menu_label = new Label("Vyberte farbu");
        menu_label.setStyle("-fx-background-color: white; -fx-padding: 5px;");
        VBox menu = new VBox(
                menu_label, color_menu);
        menu.setAlignment(Pos.BOTTOM_CENTER);


        dataLabel = new Label("");
        dataLabel.setStyle("-fx-background-color: white; -fx-padding: 5px;");
        StackPane.setAlignment(dataLabel, Pos.TOP_RIGHT);
        stackPane.getChildren().add(dataLabel);

        symmetryLabel = new Label("");
        symmetryLabel.setStyle("-fx-background-color: white; -fx-padding: 5px;");
        StackPane.setAlignment(symmetryLabel, Pos.TOP_LEFT);
        stackPane.getChildren().add(symmetryLabel);

        backspaceLabel = new Label("Stlačením klávesu Backspace odstránite posledný útvar");
        backspaceLabel.setStyle("-fx-background-color: white; -fx-padding: 5px;");

        symmetryInfoLabel = new Label("Symetrické útvary sú maľované rovnakou farbou");
        symmetryInfoLabel.setStyle("-fx-background-color: white; -fx-padding: 5px;");
        symmetryInfoLabel.setAlignment(Pos.BOTTOM_RIGHT);

        HBox bottom_panel = new HBox(symmetryInfoLabel, menu, backspaceLabel);
        bottom_panel.setSpacing((double) (SIZE * CELL_SIZE) / 15);

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, this::onMouseMoved);
        gridPane.add(bottom_panel, 0, 1);
        gridPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> handleKeyPress(event));


        Scene scene = new Scene(gridPane, SIZE * CELL_SIZE, SIZE * CELL_SIZE + 80);
        primaryStage.setTitle("Štvorčeková sieť");
        primaryStage.setScene(scene);
        primaryStage.show();
//        allPossibleFigures = get_all_figures();
    }

    private void drawGrid(GraphicsContext gc) {
        gc.setLineWidth(1);
        gc.setStroke(Color.LIGHTGRAY);
        for (int i = 0; i <= SIZE; i++) {
            gc.strokeLine(i * CELL_SIZE, 0, i * CELL_SIZE, SIZE * CELL_SIZE);
            gc.strokeLine(0, i * CELL_SIZE, SIZE * CELL_SIZE, i * CELL_SIZE);
        }
        gc.setLineWidth(2);
    }

    private void onMouseClicked(MouseEvent event) {
        int x = (int) (Math.round(event.getX() / CELL_SIZE) * CELL_SIZE);
        int y = (int) (Math.round(event.getY() / CELL_SIZE) * CELL_SIZE);
        if (!isDrawing) {
            figures.add(
                    new Figure(
                    defined_color == null ? getNextColor() : defined_color,
                    new Cell(to_state(y), to_state(x))));
        }
        Figure current_figure = figures.get(figures.size()-1);
        if (isDrawing) {
            current_figure.addCell(new Cell(to_state(y), to_state(x)));
        }

        if (current_figure.number_of_vertices() > 2
                && Math.abs(to_app(current_figure.get_first_vertex().getX()) - x) < CELL_SIZE / 2
                && Math.abs(to_app(current_figure.get_first_vertex().getY()) - y) < CELL_SIZE / 2) {
            // Close the shape
            Cell firstVertex = current_figure.get_first_vertex();
            Cell lastVertex = current_figure.get_last_vertex();
            gc.strokeLine(to_app(lastVertex.getX()), to_app(lastVertex.getY()), to_app(firstVertex.getX()), to_app(firstVertex.getY()));
            fillFigure(current_figure);
            isDrawing = false;
            dataLabel.setText("Plocha posledného útvaru: " + current_figure.calculatePolygonArea() + " buniek²");

            Set<List<Cell>> current_symmetries = current_figure.getAllSymmetries();

            // TODO: add counting remaining unique figures to draw
//            if (allPossibleFigures.contains(current_figure)) {
//                allPossibleFigures.remove(current_figure);
//                System.out.println(allPossibleFigures.size());
//            }

            Set<List<Cell>> intersection = new HashSet<>(allSymmetries);
            intersection.retainAll(current_symmetries);
            Color current_color = null;
            if (!intersection.isEmpty()) {
                numberOfSymmetry++;
                symmetryLabel.setText("Počet symetrií: " + numberOfSymmetry);
                current_figure.symmetry_from_other = true;
                for (Figure f : figures) {
                    for (List<Cell> inter : intersection) {
                        if (f.symmetries.contains(inter)) {
                            if (current_color == null) {
                                current_color = f.color;
                            } else {
                                f.color = current_color;
                                draw_figure(f);
                            }
                            break;
                        }
                    }
                }
            }
            allSymmetries.addAll(current_symmetries);
//            for (List<Cell> vertices : current_symmetries) {
//                draw_figure_vertices(vertices, getRandomDimColor());
//            }
        } else {
            isDrawing = true;
        }
        if (current_figure.number_of_vertices() > 1 && isDrawing) {
            gc.setStroke(current_figure.color);
            Cell prevVertex = current_figure.get_previous_vertex();
            gc.strokeLine(to_app(prevVertex.getX()), to_app(prevVertex.getY()), x, y);
        }
    }

    private void onMouseMoved(MouseEvent event) {


        double x = event.getX();
        double y = event.getY();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawGrid(gc);

        // Redraw existing figures
        for (Figure f : figures) {
            draw_figure(f);
        }
        if (isDrawing) {
            Figure current_figure = figures.get(figures.size()-1);
            gc.setStroke(current_figure.color);
            Cell lastVertex = current_figure.get_last_vertex();
            gc.strokeLine(
                    to_app(lastVertex.getX()),
                    to_app(lastVertex.getY()),
                    x, y);
        }
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.BACK_SPACE) {
            if (figures.isEmpty()) {
                return;
            }
            if (!figures.get(figures.size()-1).finished) {
                isDrawing = false;
            }
            if (figures.get(figures.size()-1).symmetry_from_other) {
                numberOfSymmetry--;
                symmetryLabel.setText("Počet symetrií: " + numberOfSymmetry);
            }
            figures.remove(figures.size()-1);
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            drawGrid(gc);
            for (Figure f : figures) {
                draw_figure(f);
            }
            if (!figures.isEmpty()) {
                dataLabel.setText("Plocha posledného útvaru: " + figures.get(figures.size() - 1).calculatePolygonArea() + " buniek²");
            } else {
                dataLabel.setText("");
            }
        }
    }

    /**
     * Suppose to find all possible unique figures to draw, but still not implemented
     * @return all possible unique figures
     */
    private Set<Figure> get_all_figures() {
        Set<Figure> all_possible_figures = new HashSet<>();
        for (int x1 = 0; x1 < SIZE; x1++) {
            for (int y1 = 0; y1 < SIZE; y1++) {
                if (x1 == y1) {
                    for (int y2 = 0; y2 < SIZE+1; y2++) {
                        for (int x3 = y2; x3 < SIZE+1; x3++) {
                            List<Cell> cells = new ArrayList<>();
                            cells.add(new Cell(y1,x1));
                            cells.add(new Cell(y2, 0));
                            cells.add(new Cell(0, x3));
                            Figure triangle = new Figure(Color.BLACK, cells);
                            all_possible_figures.add(triangle);
//                            draw_figure_vertices(triangle.get_vertices(), getRandomDimColor());
                        }
                    }
                }
            }
        }

        return all_possible_figures;
    }

    private void draw_figure(Figure figure) {
        List<Cell> current_vertices = figure.get_vertices();
        for (int i = 1; i < figure.number_of_vertices(); i++) {
            Cell v1 = current_vertices.get(i - 1);
            Cell v2 = current_vertices.get(i);
            gc.setStroke(figure.color);
            gc.strokeLine(to_app(v1.getX()), to_app(v1.getY()), to_app(v2.getX()), to_app(v2.getY()));
        }
        if (figure.finished) {
            Cell first = figure.get_first_vertex();
            Cell last = figure.get_last_vertex();
            gc.setStroke(figure.color);
            gc.strokeLine(to_app(first.getX()), to_app(first.getY()), to_app(last.getX()), to_app(last.getY()));
            fillFigure(figure);
        }
    }

    private void draw_figure_vertices(List<Cell> vertices, Color color) {
        Random random = new Random();
        double randomOffset = random.nextDouble(10);
        for (int i = 1; i < vertices.size(); i++) {
            Cell v1 = vertices.get(i - 1);
            Cell v2 = vertices.get(i);
            gc.setStroke(color);
            gc.strokeLine(to_app(v1.getX())+randomOffset, to_app(v1.getY())+randomOffset, to_app(v2.getX())+randomOffset, to_app(v2.getY())+randomOffset);
        }
        Cell first = vertices.get(0);
        Cell last = vertices.get(vertices.size()-1);
        gc.strokeLine(to_app(first.getX())+randomOffset, to_app(first.getY())+randomOffset, to_app(last.getX())+randomOffset, to_app(last.getY())+randomOffset);
    }

    private static Color getNextColor() {
        Color nextColor = COLORS.get(colorIndex);
        colorIndex = (colorIndex + 1) % COLORS.size();
        return nextColor;
    }

    private static Color getRandomDimColor() {
        List<Color> dimColors = new ArrayList<>();
        dimColors.add(Color.RED);
        dimColors.add(Color.BLUE);
        dimColors.add(Color.GREEN);
        dimColors.add(Color.YELLOW);
        dimColors.add(Color.ORANGE);
        dimColors.add(Color.PURPLE);
        dimColors.add(Color.CYAN);
        dimColors.add(Color.MAGENTA);
        dimColors.add(Color.BROWN);
        dimColors.add(Color.PINK);
        dimColors.add(Color.LIME);
        dimColors.add(Color.INDIGO);
        dimColors.add(Color.GOLD);
        dimColors.add(Color.CRIMSON);
        dimColors.add(Color.AQUAMARINE);

        Random random = new Random();
        int randomIndex = random.nextInt(dimColors.size());
        return dimColors.get(randomIndex);
    }

    private void fillFigure(Figure figure) {
        List<Cell> vertices = figure.get_vertices(); // Get the vertices of the figure
        double[] xPoints = new double[vertices.size()];
        double[] yPoints = new double[vertices.size()];

        for (int i = 0; i < vertices.size(); i++) {
            Cell cell = vertices.get(i);
            xPoints[i] = to_app(cell.getX());
            yPoints[i] = to_app(cell.getY());
        }

        gc.setFill(figure.color); // Set the fill color
        gc.fillPolygon(xPoints, yPoints, vertices.size()); // Draw the filled polygon
    }

    private int to_state(int coordinate) {
        return coordinate / CELL_SIZE;
    }

    private int to_app(int coordinate) {
        return (coordinate) * CELL_SIZE;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
