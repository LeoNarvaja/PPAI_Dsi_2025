package org.redsismica.cerrarorden.services;

import org.redsismica.cerrarorden.entities.Sismografo;
import org.redsismica.cerrarorden.repositories.SismografoRepository;

import java.util.List;

public class SismografoService {

    private SismografoRepository sismografoRepository;

    public SismografoService() {
        this.sismografoRepository = new SismografoRepository();
    }

    public List<Sismografo> obtenerSismografos() {
        return this.sismografoRepository.findAll();
    }

    public void actualizarSismografo(Sismografo sismografo) {
        this.sismografoRepository.update(sismografo);
    }

}
