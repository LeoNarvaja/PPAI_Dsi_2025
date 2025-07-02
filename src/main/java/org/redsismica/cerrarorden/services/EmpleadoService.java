package org.redsismica.cerrarorden.services;

import org.redsismica.cerrarorden.entities.Empleado;
import org.redsismica.cerrarorden.repositories.EmpleadoRepository;

import java.util.List;

public class EmpleadoService {

    private EmpleadoRepository empleadoRepository;

    public EmpleadoService() {
        this.empleadoRepository = new EmpleadoRepository();
    }

    public List<Empleado> obtenerEmpleados() {
        return this.empleadoRepository.findAll();
    }

}
