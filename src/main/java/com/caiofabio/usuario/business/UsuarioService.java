package com.caiofabio.usuario.business;

import com.caiofabio.usuario.business.converter.UsuarioConverter;
import com.caiofabio.usuario.business.dto.UsuarioDTO;
import com.caiofabio.usuario.infrastructure.entity.Usuario;
import com.caiofabio.usuario.infrastructure.exception.ConflictException;
import com.caiofabio.usuario.infrastructure.exception.ResourceNotFoundException;
import com.caiofabio.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ConcurrentModificationException;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuarioDTO(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public void emailExiste(String email){
        try {
            boolean existe = verificarEmailExistente(email);
            if (existe){
                throw new ConcurrentModificationException("email existente" + email);
            }
        }catch (ConflictException e){
            throw new ConflictException("email existente" + e.getCause());
        }
    }

    public boolean verificarEmailExistente(String email){

        return usuarioRepository.existsByEmail(email);
    }

    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email não encontrado" + email));
    }

    public void deletarUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }
}
