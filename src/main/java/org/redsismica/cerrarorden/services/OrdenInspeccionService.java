package org.redsismica.cerrarorden.services;

import org.redsismica.cerrarorden.entities.OrdenInspeccion;
import org.redsismica.cerrarorden.entities.Sismografo;
import org.redsismica.cerrarorden.repositories.OrdenInspeccionRepository;

import java.util.List;

public class OrdenInspeccionService {

    private final OrdenInspeccionRepository ordenInspeccionRepository;

    public OrdenInspeccionService() {
        this.ordenInspeccionRepository = new OrdenInspeccionRepository();
    }

    public List<OrdenInspeccion> listarOrdenesInspeccion() {
        return ordenInspeccionRepository.findAll();
    }

    public OrdenInspeccion actualizarOrdenInspeccion(OrdenInspeccion ordenInspeccion) {
        return this.ordenInspeccionRepository.update(ordenInspeccion);
    }

}
