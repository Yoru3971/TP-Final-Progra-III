package com.viandasApp.api.Emprendimiento.service;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.repository.EmprendimientoRepository;
import com.viandasApp.api.Usuario.model.RolUsuario;
import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Usuario.service.UsuarioServiceImpl;
import jakarta.transaction.Transactional;
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

    //--------------------------Create--------------------------//
    @Transactional
    @Override
    public EmprendimientoDTO createEmprendimiento(CreateEmprendimientoDTO createEmprendimientoDTO, Usuario usuario) {

        Usuario duenioEmprendimiento = usuarioService.findEntityById(createEmprendimientoDTO.getIdUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuario no encontrado con ID: " + createEmprendimientoDTO.getIdUsuario()));

        Long duenioEmprendimientoId = duenioEmprendimiento.getId();

        if ( createEmprendimientoDTO.getTelefono().replaceFirst("^0+", "").length()< 7 ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El telefono debe tener al menos 7 digitos.");
        }

        // Verifica si ya existe un usuario con el mismo telefono
        if (emprendimientoRepository.findByTelefono(createEmprendimientoDTO.getTelefono()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con el telefono: " + createEmprendimientoDTO.getTelefono());
        }

        boolean esAdmin = usuario.getRolUsuario() == RolUsuario.ADMIN;
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuario.getId());

        if (!esDuenioDelEmprendimiento && !esAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo podés crear emprendimientos a tu nombre.");
        }

        Emprendimiento emprendimiento = DTOToEntity(createEmprendimientoDTO);
        Emprendimiento emprendimientoGuardado = emprendimientoRepository.save(emprendimiento);

        return new EmprendimientoDTO(emprendimientoGuardado);
    }

    //--------------------------Read--------------------------//
    @Override
    public List<EmprendimientoDTO> getAllEmprendimientos() {

        List<EmprendimientoDTO> emprendimientos = emprendimientoRepository.findAll().stream()
                .map(EmprendimientoDTO::new)
                .toList();

        if (emprendimientos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay emprendimientos registrados.");
        }

        return emprendimientos;
    }

    @Override
    public Optional<EmprendimientoDTO> getEmprendimientoById(Long id, Usuario usuario) {

        Emprendimiento emprendimiento = emprendimientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado con ID: " + id));

        boolean esDuenio = usuario.getRolUsuario().equals(RolUsuario.DUENO);
        boolean esDuenioDelEmprendimiento = emprendimiento.getUsuario().getId().equals(usuario.getId());

        if ( esDuenio && !esDuenioDelEmprendimiento ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para ver este emprendimiento.");
        }
        return Optional.of(new EmprendimientoDTO(emprendimiento));
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosByNombre(String nombreEmprendimiento) {

        List<EmprendimientoDTO> emprendimientos = emprendimientoRepository.findByNombreEmprendimientoContaining(nombreEmprendimiento)
                .stream()
                .map(EmprendimientoDTO::new)
                .toList();
        if (emprendimientos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron emprendimientos con el nombre: " + nombreEmprendimiento);
        }

        return emprendimientos;
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosByCiudad(String ciudad) {
        List<EmprendimientoDTO> emprendimientos = emprendimientoRepository.findByCiudad(ciudad)
                .stream()
                .map(EmprendimientoDTO::new)
                .toList();
        if (emprendimientos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron emprendimientos en la ciudad: " + ciudad);
        }

        return emprendimientos;
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosByUsuarioId(Long idUsuario, Usuario usuario) {

        Usuario usuarioEncontrado = usuarioService.findEntityById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + idUsuario));

        List<Emprendimiento> emprendimientos = emprendimientoRepository.findByUsuarioId(idUsuario);

        boolean esDuenio = usuario.getRolUsuario().equals(RolUsuario.DUENO);
        boolean esDuenioDelEmprendimiento = idUsuario.equals(usuario.getId());

        if ( esDuenio && !esDuenioDelEmprendimiento ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para ver estos emprendimientos.");
        }

        return emprendimientos
                .stream()
                .map(EmprendimientoDTO::new)
                .toList();
    }

    //--------------------------Update--------------------------//
    @Transactional
    @Override
    public Optional<EmprendimientoDTO> updateEmprendimiento(Long id, UpdateEmprendimientoDTO updateEmprendimientoDTO, Usuario usuarioLogueado) {

        Emprendimiento emprendimiento = emprendimientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado con ID: " + id));

        if ( updateEmprendimientoDTO.getTelefono().replaceFirst("^0+", "").length()< 7 ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El telefono debe tener al menos 7 digitos.");
        }

        Long duenioEmprendimientoId = emprendimiento.getUsuario().getId();

        boolean esAdmin = usuarioLogueado.getRolUsuario().equals(RolUsuario.ADMIN);
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
                        // Verifica si ya existe un usuario con el mismo telefono
                        if (emprendimientoRepository.findByTelefono(updateEmprendimientoDTO.getTelefono()).isPresent()) {
                            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con el telefono: " + updateEmprendimientoDTO.getTelefono());
                        }
                        emprendimientoExistente.setTelefono(updateEmprendimientoDTO.getTelefono());
                    }
                    if ( updateEmprendimientoDTO.getIdUsuario() != null ){

                        Usuario usuario = usuarioService.findEntityById(updateEmprendimientoDTO.getIdUsuario())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));

                        if ( !usuario.getRolUsuario().equals(RolUsuario.DUENO) ){
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo los usuarios con rol DUEÑO pueden tener emprendimientos.");
                        }

                        emprendimientoExistente.setUsuario(usuario);
                    }
                    Emprendimiento emprendimientoActualizado = emprendimientoRepository.save(emprendimientoExistente);
                    return new EmprendimientoDTO(emprendimientoActualizado);
                });
    }

    //--------------------------Delete--------------------------//
    @Transactional
    @Override
    public boolean deleteEmprendimiento(Long id, Usuario usuario) {

        Emprendimiento emprendimiento = emprendimientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emprendimiento no encontrado con ID: " + id));

        Long duenioEmprendimientoId = emprendimiento.getUsuario().getId();

        boolean esAdmin = usuario.getRolUsuario().equals(RolUsuario.ADMIN);
        boolean esDuenioDelEmprendimiento = duenioEmprendimientoId.equals(usuario.getId());

        if ( !esDuenioDelEmprendimiento && !esAdmin ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para eliminar este emprendimiento.");
        }

        if ( emprendimientoRepository.existsById(id) ){
            emprendimientoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //--------------------------Otros--------------------------//
    @Override
    public Optional<Emprendimiento> findEntityById(Long id) {

        return emprendimientoRepository.findById(id);
    }

    private Emprendimiento DTOToEntity(CreateEmprendimientoDTO createEmprendimientoDTO){

        Long id = createEmprendimientoDTO.getIdUsuario();
        Usuario usuario = usuarioService.findEntityById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));

        if ( !usuario.getRolUsuario().equals(RolUsuario.DUENO) && usuario.getRolUsuario().equals(RolUsuario.ADMIN) ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo los usuarios con rol DUEÑO/ADMIN pueden crear emprendimientos.");
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
