package com.caiofabio.usuario.business;

import com.caiofabio.usuario.business.converter.UsuarioConverter;
import com.caiofabio.usuario.business.dto.UsuarioDTO;
import com.caiofabio.usuario.infrastructure.entity.Usuario;
import com.caiofabio.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioConverter.paraUsuarioDTO(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

}
