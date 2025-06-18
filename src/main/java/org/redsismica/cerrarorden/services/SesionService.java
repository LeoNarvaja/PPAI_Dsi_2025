package org.redsismica.cerrarorden.services;

import org.redsismica.cerrarorden.entities.Sesion;
import org.redsismica.cerrarorden.entities.Usuario;
import org.redsismica.cerrarorden.repositories.SesionRepository;

import java.time.LocalDateTime;

public class SesionService {

    private SesionRepository sesionRepository;

    public SesionService() {
        this.sesionRepository = new SesionRepository();
    }

    public Sesion obtenerSesionActiva(String email) {
        return sesionRepository.findByEmail(email);
    }

//    private Sesion crearSesion(Usuario usuario) {
//        Sesion nuevaSesion = new Sesion();
//        nuevaSesion.setUsuario(usuario);
//        nuevaSesion.setFechaHora(LocalDateTime.now());
//        return sesionRepository.save(nuevaSesion);
//    }


}
