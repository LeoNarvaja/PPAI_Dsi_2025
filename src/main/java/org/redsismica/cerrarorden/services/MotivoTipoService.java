package org.redsismica.cerrarorden.services;

import org.redsismica.cerrarorden.entities.MotivoTipo;
import org.redsismica.cerrarorden.repositories.MotivoTipoRepository;

import java.util.List;

public class MotivoTipoService {

    private MotivoTipoRepository motivoTipoRepository;

    public MotivoTipoService() {
        this.motivoTipoRepository = new MotivoTipoRepository();
    }

    public List<MotivoTipo> obtenerMotivosTipo() {
        return this.motivoTipoRepository.findAll();
    }

}
