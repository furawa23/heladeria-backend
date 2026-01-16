package com.togamma.heladeria.controller.seguridad;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.togamma.heladeria.dto.request.seguridad.RegisterRequestDTO;
import com.togamma.heladeria.dto.request.seguridad.UsuarioRequestDTO;
import com.togamma.heladeria.dto.response.seguridad.UsuarioResponseDTO;
import com.togamma.heladeria.service.seguridad.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // 1. Crear usuario asignando sucursal explícitamente (Para Superadmin)
    @PostMapping("/superadmin")
    public ResponseEntity<UsuarioResponseDTO> crearDesdeSuperadmin(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.crearDesdeSuperadmin(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Crear usuario bajo la sucursal del usuario logueado (Para Admins de Empresa)
    @PostMapping("/empresa")
    public ResponseEntity<UsuarioResponseDTO> crearDesdeEmpresa(@RequestBody RegisterRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.crearDesdeEmpresa(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 3. Listar todos los usuarios (General)
    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listarTodos(Pageable pageable) {
        Page<UsuarioResponseDTO> response = usuarioService.listarTodos(pageable);
        return ResponseEntity.ok(response);
    }

    // 4. Listar usuarios por ID de Sucursal
    @GetMapping("/sucursal/{idSucursal}")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarPorSucursal(
            @PathVariable Long idSucursal, 
            Pageable pageable) {
        Page<UsuarioResponseDTO> response = usuarioService.listarPorSucursal(idSucursal, pageable);
        return ResponseEntity.ok(response);
    }

    // 5. Listar usuarios por ID de Empresa
    @GetMapping("/empresa/{idEmpresa}")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarPorEmpresa(
            @PathVariable Long idEmpresa, 
            Pageable pageable) {
        Page<UsuarioResponseDTO> response = usuarioService.listarPorEmpresa(idEmpresa, pageable);
        return ResponseEntity.ok(response);
    }

    // 6. Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        UsuarioResponseDTO response = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    // 7. Actualizar usuario
    // Nota: Según tu servicio, usas RegisterRequestDTO para actualizar
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id, 
            @RequestBody RegisterRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.actualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    // 8. Eliminación lógica (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // 9. Restaurar usuario eliminado
    @PutMapping("/{id}/restaurar")
    public ResponseEntity<Void> restaurar(@PathVariable Long id) {
        usuarioService.restaurar(id);
        return ResponseEntity.ok().build();
    }
}