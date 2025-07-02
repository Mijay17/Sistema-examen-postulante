module org.example.sistemaevaluacion {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens org.example to javafx.fxml;
    exports org.example;
    exports org.example.controllers;
    opens org.example.controllers to javafx.fxml;
}