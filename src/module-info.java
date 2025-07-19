module com.uni.cookoff {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.boot.autoconfigure;
    requires spring.boot;


    opens com.uni.cookoff to javafx.fxml;
    exports com.uni.cookoff;
}