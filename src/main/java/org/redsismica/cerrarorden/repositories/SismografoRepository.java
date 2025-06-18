package org.redsismica.cerrarorden.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.redsismica.cerrarorden.entities.Sismografo;
import org.redsismica.cerrarorden.repositories.context.DbContext;

import java.util.List;

public class SismografoRepository {

    public Sismografo findById(int identificador) {
        EntityManager em = DbContext.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            Sismografo sismografo = em.createQuery(
                    "SELECT s FROM Sismografo s WHERE s.identificadorSismografo = :identificador",
                    Sismografo.class
            ).setParameter("identificador", identificador).getSingleResult();
            em.getTransaction().commit();
            return sismografo;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    // Consulta para traer todas las órdenes de inspección
    public List<Sismografo> findAll() {
        EntityManager em = DbContext.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            List<Sismografo> sismografos = em.createQuery(
                    "SELECT e FROM Sismografo e",
                    Sismografo.class
            ).getResultList();
            em.getTransaction().commit();
            return sismografos;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    public Sismografo update(Sismografo sismografo) {
        EntityManager em = DbContext.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            Sismografo sismografoActualizado = em.merge(sismografo);
            em.getTransaction().commit();
            return sismografoActualizado;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

}
