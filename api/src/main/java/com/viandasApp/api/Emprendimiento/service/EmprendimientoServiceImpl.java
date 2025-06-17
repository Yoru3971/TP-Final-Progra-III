package com.viandasApp.api.Emprendimiento.service;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.repository.EmprendimientoRepository;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioServiceImpl;
import com.viandasApp.api.Vianda.model.Vianda;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmprendimientoServiceImpl implements EmprendimientoService {

    private final EmprendimientoRepository emprendimientoRepository;
    private final UsuarioServiceImpl usuarioService;


    @Override
    public EmprendimientoDTO createEmprendimiento(CreateEmprendimientoDTO createEmprendimientoDTO, Usuario usuario) {

        Long duenioEmprendimientoId = createEmprendimientoDTO.getIdUsuario();

        boolean esAdmin = usuario.getRolUsuario() == RolUsuario.ADMIN;
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuario.getId());

        if (!esDuenioDelEmprendimiento && !esAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo podes crear emprendimientos a tu nombre.");
        }

        Emprendimiento emprendimiento = DTOToEntity(createEmprendimientoDTO);
        Emprendimiento emprendimientoGuardado = emprendimientoRepository.save(emprendimiento);

        return new EmprendimientoDTO(emprendimientoGuardado);
    }

    @Override
    public Optional<EmprendimientoDTO> updateEmprendimiento(Long id, UpdateEmprendimientoDTO updateEmprendimientoDTO, Usuario usuarioLogueado) {
        Optional<Emprendimiento> optionalEmprendimiento = emprendimientoRepository.findById(id);

        if (optionalEmprendimiento.isEmpty()) return Optional.empty();

        Long duenioEmprendimientoId = optionalEmprendimiento.get().getUsuario().getId();

        boolean esAdmin = usuarioLogueado.getRolUsuario() == RolUsuario.ADMIN;
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuarioLogueado.getId());

        if (!esDuenioDelEmprendimiento && !esAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para editar este emprendimiento.");
        }

        return emprendimientoRepository.findById(id)
                .map(emprendimientoExistente -> {
                    if ( updateEmprendimientoDTO.getNombreEmprendimiento() != null ){
                        emprendimientoExistente.setNombreEmprendimiento(updateEmprendimientoDTO.getNombreEmprendimiento());
                    }
                    if ( updateEmprendimientoDTO.getCiudad() != null ){
                        emprendimientoExistente.setCiudad(updateEmprendimientoDTO.getCiudad());
                    }
                    if ( updateEmprendimientoDTO.getDireccion() != null ){
                        emprendimientoExistente.setDireccion(updateEmprendimientoDTO.getDireccion());
                    }
                    if ( updateEmprendimientoDTO.getTelefono() != null ){
                        emprendimientoExistente.setTelefono(updateEmprendimientoDTO.getTelefono());
                    }
                    if ( updateEmprendimientoDTO.getIdUsuario() != null ){

                        Usuario usuario = usuarioService.findEntityById(updateEmprendimientoDTO.getIdUsuario())
                                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

                        if ( usuario.getRolUsuario() != RolUsuario.DUENO ){
                            throw new RuntimeException("Solo los usuarios con rol DUEÑO pueden tener emprendimientos.");
                        }

                        emprendimientoExistente.setUsuario(usuario);
                    }
                    Emprendimiento emprendimientoActualizado = emprendimientoRepository.save(emprendimientoExistente);

                    return new EmprendimientoDTO(emprendimientoActualizado);
                });
    }

    @Override
    public boolean deleteEmprendimiento(Long id, Usuario usuario) {

        Optional<Emprendimiento> optionalEmprendimiento = emprendimientoRepository.findById(id);

        if (optionalEmprendimiento.isEmpty()) return false;

        Long duenioEmprendimientoId = optionalEmprendimiento.get().getUsuario().getId();

        boolean esAdmin = usuario.getRolUsuario() == RolUsuario.ADMIN;
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuario.getId());

        if (!esDuenioDelEmprendimiento && !esAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para eliminar este emprendimiento.");
        }

        if ( emprendimientoRepository.existsById(id) ){
            emprendimientoRepository.deleteById(id);
            return true;
        }

        return false;
    }

    @Override
    public List<EmprendimientoDTO> getAllEmprendimientos() {
        return emprendimientoRepository.findAll().stream()
                .map(EmprendimientoDTO::new)
                .toList();
    }

    @Override
    public Optional<EmprendimientoDTO> getEmprendimientoById(Long id) {
        return emprendimientoRepository.findById(id)
                .map(EmprendimientoDTO::new);
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosByNombre(String nombreEmprendimiento) {
        return emprendimientoRepository.findByNombreEmprendimientoContaining(nombreEmprendimiento)
                .stream().map(EmprendimientoDTO::new).toList();
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosByCiudad(String ciudad) {
        return emprendimientoRepository.findByCiudad(ciudad)
                .stream().map(EmprendimientoDTO::new).toList();
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosByUsuarioId(Long id) {
        return emprendimientoRepository.findByUsuarioId(id)
                .stream().map(EmprendimientoDTO::new).toList();
    }

    @Override
    public Optional<Emprendimiento> findEntityById(Long id) {

        return emprendimientoRepository.findById(id);
    }


    private Emprendimiento DTOToEntity(CreateEmprendimientoDTO createEmprendimientoDTO){

        Long id = createEmprendimientoDTO.getIdUsuario();
        Usuario usuario = usuarioService.findEntityById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

        if ( usuario.getRolUsuario() != RolUsuario.DUENO ){
            throw new RuntimeException("Solo los usuarios con rol DUEÑO pueden crear emprendimientos.");
        }

        return new Emprendimiento(
                createEmprendimientoDTO.getNombreEmprendimiento(),
                createEmprendimientoDTO.getCiudad(),
                createEmprendimientoDTO.getDireccion(),
                createEmprendimientoDTO.getTelefono(),
                usuario
        );
    }


}
