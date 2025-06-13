package com.viandasApp.api.Emprendimiento.service;

import com.viandasApp.api.Emprendimiento.dto.CreateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.EmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.dto.UpdateEmprendimientoDTO;
import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Emprendimiento.repository.EmprendimientoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmprendimientoServiceImpl implements EmprendimientoService {

    private final EmprendimientoRepository emprendimientoRepository;

    public EmprendimientoServiceImpl(EmprendimientoRepository emprendimientoRepository) {

        this.emprendimientoRepository = emprendimientoRepository;
    }


    @Override
    public List<EmprendimientoDTO> getAllEmprendimientos() {
        return emprendimientoRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public Optional<EmprendimientoDTO> getEmprendimientoById(Long id) {
        return emprendimientoRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosByNombre(String nombreEmprendimiento) {
        return emprendimientoRepository.findByNombreEmprendimientoContaining(nombreEmprendimiento)
                .stream().map(this::convertToDto).toList();
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosByCiudad(String ciudad) {
        return emprendimientoRepository.findByCiudad(ciudad)
                .stream().map(this::convertToDto).toList();
    }

    @Override
    public List<EmprendimientoDTO> getEmprendimientosByUsuarioId(Long id) {
        return emprendimientoRepository.findByUsuarioId(id)
                .stream().map(this::convertToDto).toList();
    }

    @Override
    public EmprendimientoDTO createEmprendimiento(CreateEmprendimientoDTO createEmprendimientoDTO) {

        Emprendimiento emprendimiento = convertToEntity(createEmprendimientoDTO);
        Emprendimiento emprendimientoGuardado = emprendimientoRepository.save(emprendimiento);

        return convertToDto(emprendimientoGuardado);
    }

    @Override
    public Optional<EmprendimientoDTO> updateEmprendimiento(Long id, UpdateEmprendimientoDTO updateEmprendimientoDTO) {
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
                    if ( updateEmprendimientoDTO.getUsuario() != null ){
                        emprendimientoExistente.setUsuario(updateEmprendimientoDTO.getUsuario());
                    }
                    Emprendimiento emprendimientoActualizado = emprendimientoRepository.save(emprendimientoExistente);

                    return convertToDto(emprendimientoActualizado);
                });
    }

    @Override
    public boolean deleteEmprendimiento(Long id) {

        if ( emprendimientoRepository.existsById(id) ){
            emprendimientoRepository.deleteById(id);
            return true;
        }

        return false;
    }

    private EmprendimientoDTO convertToDto(Emprendimiento emprendimiento){
        return new EmprendimientoDTO(
                emprendimiento.getId(),
                emprendimiento.getNombreEmprendimiento(),
                emprendimiento.getCiudad(),
                emprendimiento.getDireccion(),
                emprendimiento.getTelefono(),
                emprendimiento.getUsuario()
        );
    }

    private Emprendimiento convertToEntity(CreateEmprendimientoDTO createEmprendimientoDTO){
        return new Emprendimiento(
                createEmprendimientoDTO.getNombreEmprendimiento(),
                createEmprendimientoDTO.getCiudad(),
                createEmprendimientoDTO.getDireccion(),
                createEmprendimientoDTO.getTelefono(),
                createEmprendimientoDTO.getUsuario()
        );
    }

    @Override
    public Optional<Emprendimiento> findEntityById(Long id) {
        return emprendimientoRepository.findById(id);
    }

}
