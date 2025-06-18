package org.redsismica.cerrarorden.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.redsismica.cerrarorden.entities.EstacionSismologica;
import org.redsismica.cerrarorden.entities.Estado;
import org.redsismica.cerrarorden.entities.OrdenInspeccion;
import org.redsismica.cerrarorden.repositories.context.DbContext;

import java.util.List;

public class EstadoRepository {

    // Consulta para traer todas las órdenes de inspección
    public List<Estado> findAll() {
        EntityManager em = DbContext.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            List<Estado> estados = em.createQuery(
                    "SELECT e FROM Estado e",
                    Estado.class
            ).getResultList();
            em.getTransaction().commit();
            return estados;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

}
