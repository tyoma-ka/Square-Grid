module com.example.project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.project to javafx.fxml;
    exports com.example.project;
    exports com.project;
}