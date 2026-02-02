package com.viandasApp.api.Emprendimiento.dto;

import com.viandasApp.api.Emprendimiento.model.Emprendimiento;
import com.viandasApp.api.Usuario.dto.UsuarioDTO;
import com.viandasApp.api.Usuario.model.Usuario;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
public class EmprendimientoAdminDTO extends RepresentationModel<EmprendimientoAdminDTO> {
    private Long id;
    private String nombreEmprendimiento;
    private String imagenUrl;
    private String ciudad;
    private String direccion;
    private String telefono;
    private Boolean estaDisponible;
    private UsuarioDTO dueno;
    private LocalDateTime fechaEliminacion;

    private static final Pattern PATRON_ELIMINADO = Pattern.compile("^Emprendimiento Eliminado_\\d+_");

    public EmprendimientoAdminDTO(Emprendimiento emprendimiento) {
        this.id = emprendimiento.getId();
        this.imagenUrl = emprendimiento.getImagenUrl();
        this.estaDisponible = emprendimiento.getEstaDisponible();
        this.fechaEliminacion = emprendimiento.getDeletedAt();
        this.dueno = new UsuarioDTO(emprendimiento.getUsuario());

        if (emprendimiento.getDeletedAt() != null) {
            this.nombreEmprendimiento = limpiarTexto(emprendimiento.getNombreEmprendimiento());
            this.ciudad = limpiarTexto(emprendimiento.getCiudad());
            this.direccion = limpiarTexto(emprendimiento.getDireccion());
            this.telefono = limpiarTexto(emprendimiento.getTelefono());
        } else {
            this.nombreEmprendimiento = emprendimiento.getNombreEmprendimiento();
            this.ciudad = emprendimiento.getCiudad();
            this.direccion = emprendimiento.getDireccion();
            this.telefono = emprendimiento.getTelefono();
        }

        Usuario usuarioEntity = emprendimiento.getUsuario();
        this.dueno = new UsuarioDTO(usuarioEntity);

        if (usuarioEntity.getDeletedAt() != null) {
            this.dueno.setNombreCompleto(limpiarDatoUsuario(usuarioEntity.getNombreCompleto(), "usuario_borrado_"));
            this.dueno.setEmail(limpiarDatoUsuario(usuarioEntity.getEmail(), "usuario_borrado_"));
            this.dueno.setTelefono(limpiarDatoUsuario(usuarioEntity.getTelefono(), "borrado_"));
        }
    }

    private String limpiarTexto(String textoSucio) {
        if (textoSucio == null) return null;
        return PATRON_ELIMINADO.matcher(textoSucio).replaceFirst("");
    }

    private String limpiarDatoUsuario(String datoSucio, String prefijoBase) {
        if (datoSucio == null) return "";
        String regex = "^" + prefijoBase + "\\d+_";
        return datoSucio.replaceFirst(regex, "");
    }
}
