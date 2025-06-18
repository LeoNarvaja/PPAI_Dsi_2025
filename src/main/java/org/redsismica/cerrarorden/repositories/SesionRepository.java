package org.redsismica.cerrarorden.repositories;

import jakarta.persistence.EntityManager;
import org.redsismica.cerrarorden.entities.Sesion;
import org.redsismica.cerrarorden.repositories.context.DbContext;


public class SesionRepository {

    // Consulta para traer todas las órdenes de inspección
    public Sesion findByEmail(String email) {
        EntityManager em = DbContext.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            Sesion sesion = em.createQuery(
                    "SELECT s FROM Sesion s WHERE s.usuario.empleado.email = :email",
                    Sesion.class
            ).setParameter("email", email).getSingleResult();
            em.getTransaction().commit();
            return sesion;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

}
