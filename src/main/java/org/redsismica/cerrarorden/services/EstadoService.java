package org.redsismica.cerrarorden.services;

import org.redsismica.cerrarorden.entities.Estado;
import org.redsismica.cerrarorden.repositories.EstadoRepository;

import java.util.List;

public class EstadoService {

    private EstadoRepository estadoRepository;

    public EstadoService() {
        this.estadoRepository = new EstadoRepository();
    }

    public List<Estado> obtenerEstados() {
        return this.estadoRepository.findAll();
    }

}
