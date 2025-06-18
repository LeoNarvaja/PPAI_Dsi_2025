package org.redsismica.cerrarorden.repositories.context;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DbContext {
    private static DbContext instance;
    private EntityManagerFactory entityManagerFactory;

    private DbContext() {
        entityManagerFactory = Persistence.createEntityManagerFactory("cierreOrden");
    }

    public static synchronized DbContext getInstance() {
        if (instance == null) {
            instance = new DbContext();
        }
        return instance;
    }

    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

}
