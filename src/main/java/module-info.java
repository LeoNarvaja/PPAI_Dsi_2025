module org.redsismica.cerrarorden {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires org.xerial.sqlitejdbc;


    requires java.logging;
    requires jakarta.persistence;

    opens org.redsismica.cerrarorden to javafx.fxml;
    opens org.redsismica.cerrarorden.entities to javafx.base, org.hibernate.orm.core,  jakarta.persistence;
    opens org.redsismica.cerrarorden.dto to javafx.base, org.hibernate.orm.core,  jakarta.persistence;
    exports org.redsismica.cerrarorden;
    opens org.redsismica.cerrarorden.boundary to javafx.fxml;
}