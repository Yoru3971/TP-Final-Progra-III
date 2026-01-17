package com.viandasApp.api.Vianda.service;

import com.viandasApp.api.Usuario.model.Usuario;
import com.viandasApp.api.Vianda.dto.*;
import com.viandasApp.api.Vianda.model.Vianda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ViandaService {
    //--------------------------Create--------------------------//
    ViandaDTO createVianda(ViandaCreateDTO viandaDto, Usuario usuario);

    //--------------------------Read--------------------------//
    Page<ViandaDTO> getViandasByEmprendimiento(FiltroViandaDTO filtroViandaDTO, Long idEmprendimiento, Usuario usuario, boolean incluirEliminadas, Pageable pageable);
    Page<ViandaDTO> getViandasDisponiblesByEmprendimiento(FiltroViandaDTO filtroViandaDTO, Long idEmprendimiento, Pageable pageable);
    Optional<ViandaDTO> findViandaById(Long id, Usuario usuario);
    Optional<ViandaDTO> findViandaByIdPublic(Long id);
    Page<ViandaAdminDTO> getAllViandasForAdmin(Pageable pageable);

    //--------------------------Update--------------------------//
    Optional<ViandaDTO> updateVianda(Long id, ViandaUpdateDTO viandaDto, Usuario usuario);
    ViandaDTO updateImagenVianda(Long id, MultipartFile image, Usuario usuarioLogueado);

    //--------------------------Delete--------------------------//
    boolean deleteVianda(Long id, Usuario usuario);

    //--------------------------Otros--------------------------//
    Optional<Vianda> findEntityViandaById(Long id);
}
