package com.togamma.heladeria.service.seguridad.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.togamma.heladeria.dto.request.seguridad.RegisterRequestDTO;
import com.togamma.heladeria.dto.request.seguridad.UsuarioRequestDTO;
import com.togamma.heladeria.dto.response.seguridad.UsuarioResponseDTO;
import com.togamma.heladeria.model.seguridad.Rol;
import com.togamma.heladeria.model.seguridad.Sucursal;
import com.togamma.heladeria.model.seguridad.Usuario;
import com.togamma.heladeria.repository.seguridad.SucursalRepository;
import com.togamma.heladeria.repository.seguridad.UsuarioRepository;
import com.togamma.heladeria.service.seguridad.UsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponseDTO crearDesdeSuperadmin(UsuarioRequestDTO dto) {
        if (usuarioRepository.findByUsername(dto.username()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        Sucursal sucursal = sucursalRepository.findById(dto.idSucursal())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + dto.idSucursal()));

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.username());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(Rol.valueOf(dto.rol())); 
        usuario.setSucursal(sucursal);

        Usuario guardado = usuarioRepository.save(usuario);
        return mapToResponse(guardado);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crearDesdeEmpresa(RegisterRequestDTO dto) {

        if (usuarioRepository.findByUsername(dto.username()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuarioLogueado = usuarioRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("No se encontró al usuario de la sesión actual"));
        
        Sucursal sucursalActual = usuarioLogueado.getSucursal();

        if (sucursalActual == null) {
             throw new RuntimeException("El usuario logueado no pertenece a ninguna sucursal");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.username());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(Rol.valueOf(dto.rol()));
                usuario.setSucursal(sucursalActual); 

        Usuario guardado = usuarioRepository.save(usuario);
        return mapToResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarPorSucursal(Long idSucursal, Pageable pageable) {
        return usuarioRepository.findBySucursalId(idSucursal, pageable)
        .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarPorEmpresa(Long idEmpresa, Pageable pageable) {
        return usuarioRepository.findBySucursalEmpresaId(idEmpresa, pageable)
        .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapToResponse(usuario);
    }

    @Override
    public UsuarioResponseDTO actualizar(Long id, RegisterRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrado"));

        usuario.setUsername(dto.username());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(Rol.valueOf(dto.rol())); 

        return mapToResponse(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void restaurar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        usuario.setDeletedAt(null);
        usuarioRepository.save(usuario);
    }

    private UsuarioResponseDTO mapToResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
            usuario.getCreatedAt(),
            usuario.getUpdatedAt(),
            usuario.getId(),
            usuario.getUsername(),
            usuario.getRol().name(),
            usuario.getEmpresa() != null ? usuario.getEmpresa().getRazonSocial() : null,
            usuario.getSucursal() != null ? usuario.getSucursal().getNombre() : null
        );
    }
}
