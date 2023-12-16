module com.atoms.refurbished {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires ojdbc8;
    requires ojdbc10;
    requires java.desktop;


    opens com.atoms.refurbished to javafx.fxml;
    exports com.atoms.refurbished;
}