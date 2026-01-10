package com.togamma.heladeria.repository.seguridad;

import java.util.Optional;

import com.togamma.heladeria.model.seguridad.Usuario;
import com.togamma.heladeria.repository.BaseRepository;

public interface UsuarioRepository extends BaseRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
}
