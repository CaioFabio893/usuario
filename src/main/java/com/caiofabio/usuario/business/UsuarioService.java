package com.caiofabio.usuario.business;

import com.caiofabio.usuario.business.converter.UsuarioConverter;
import com.caiofabio.usuario.business.dto.UsuarioDTO;
import com.caiofabio.usuario.infrastructure.entity.Usuario;
import com.caiofabio.usuario.infrastructure.exception.ConflictException;
import com.caiofabio.usuario.infrastructure.exception.ResourceNotFoundException;
import com.caiofabio.usuario.infrastructure.repository.UsuarioRepository;
import com.caiofabio.usuario.infrastructure.security.JwtUtil;
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
    private final JwtUtil jwtUtil;

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

    public UsuarioDTO atualizarUsuario(String token, UsuarioDTO dto){
        //bucas email do usuario atraves do token(tira a obrigatoriade de passar o emial)
        String email = jwtUtil.extrairEmailDoToken(token.substring(7));

        //criptografia de senha
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

        // buscou os dados do usuario no banco de dados
       Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
               new ResourceNotFoundException("Email não localizado" + email));

       // mesclou os dados que recebemos na requisiçao DTO com os dados dos banco de dados
       Usuario usuario =usuarioConverter.updateUsuario(dto, usuarioEntity);


       // salvo os dados do usuario convertido e depois peagou o retorno e converteu para UsuarioDTO
       return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

}
