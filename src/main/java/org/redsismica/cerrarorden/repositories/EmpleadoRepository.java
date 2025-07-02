package org.redsismica.cerrarorden.repositories;

import jakarta.persistence.EntityManager;
import org.redsismica.cerrarorden.entities.Empleado;
import org.redsismica.cerrarorden.repositories.context.DbContext;

import java.util.List;

public class EmpleadoRepository {

    // Consulta para traer todas las órdenes de inspección
    public List<Empleado> findAll() {
        EntityManager em = DbContext.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            List<Empleado> empleados = em.createQuery(
                    "SELECT e FROM Empleado e",
                    Empleado.class
            ).getResultList();
            em.getTransaction().commit();
            return empleados;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

}
