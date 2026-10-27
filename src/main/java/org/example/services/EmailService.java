package org.example.services;

import org.example.entities.Usuario;

public interface EmailService {
    public void notificarAtraso(Usuario usuario);
}
