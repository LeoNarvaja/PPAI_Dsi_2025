package org.redsismica.cerrarorden.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.redsismica.cerrarorden.entities.MotivoTipo;
import org.redsismica.cerrarorden.repositories.context.DbContext;

import java.util.List;

public class MotivoTipoRepository {

    // Consulta para traer todas las órdenes de inspección
    public List<MotivoTipo> findAll() {
        EntityManager em = DbContext.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            List<MotivoTipo> motivosTipo = em.createQuery(
                    "SELECT e FROM MotivoTipo e",
                    MotivoTipo.class
            ).getResultList();
            em.getTransaction().commit();
            return motivosTipo;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
