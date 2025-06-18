package org.redsismica.cerrarorden.repositories;

import jakarta.persistence.EntityManager;
import org.redsismica.cerrarorden.entities.OrdenInspeccion;
import org.redsismica.cerrarorden.repositories.context.DbContext;

import java.util.List;

public class OrdenInspeccionRepository  {

    public OrdenInspeccion findByNumeroOrden(int numeroOrden) {
        EntityManager em = DbContext.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            OrdenInspeccion orden = em.createQuery(
                    "SELECT s FROM OrdenInspeccion s WHERE s.numeroOrden = :numeroOrden",
                    OrdenInspeccion.class
            ).setParameter("numeroOrden", numeroOrden).getSingleResult();
            em.getTransaction().commit();
            return orden;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    // Consulta para traer todas las órdenes de inspección
    public List<OrdenInspeccion> findAll() {
        EntityManager em = DbContext.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            List<OrdenInspeccion> ordenes = em.createQuery(
                    "SELECT e FROM OrdenInspeccion e",
                    OrdenInspeccion.class
            ).getResultList();
            em.getTransaction().commit();
            return ordenes;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    public OrdenInspeccion update(OrdenInspeccion OrdenInspeccion) {
        EntityManager em = DbContext.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            OrdenInspeccion OrdenInspeccionActualizada = em.merge(OrdenInspeccion);
            em.getTransaction().commit();
            return OrdenInspeccionActualizada;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

}
