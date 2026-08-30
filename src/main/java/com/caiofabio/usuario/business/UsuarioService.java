package com.caiofabio.usuario.business;

import com.caiofabio.usuario.business.converter.UsuarioConverter;
import com.caiofabio.usuario.business.dto.EnderecoDTO;
import com.caiofabio.usuario.business.dto.TelefoneDTO;
import com.caiofabio.usuario.business.dto.UsuarioDTO;
import com.caiofabio.usuario.infrastructure.entity.Endereco;
import com.caiofabio.usuario.infrastructure.entity.Telefone;
import com.caiofabio.usuario.infrastructure.entity.Usuario;
import com.caiofabio.usuario.infrastructure.exception.ConflictException;
import com.caiofabio.usuario.infrastructure.exception.ResourceNotFoundException;
import com.caiofabio.usuario.infrastructure.repository.EnderecoRepository;
import com.caiofabio.usuario.infrastructure.repository.TelefoneRepository;
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
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
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

    public UsuarioDTO buscarUsuarioPorEmail(String email){
        try {
            return usuarioConverter.paraUsuarioDTO(usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Email não encontrado: " + email)));
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException("Email não encontrado: " + email);
        }

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

    public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO){

        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(() ->
                new ResourceNotFoundException("Id nao encontrado" + idEndereco));

        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO, entity);

        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }

    public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO dto){

        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow( () ->
                new ResourceNotFoundException("Telefone nao encontrado" + idTelefone));

        Telefone telefone = usuarioConverter.updateTelefone(dto, entity);
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }

    public EnderecoDTO cadastraEndereco(String token, EnderecoDTO dto){
        String email = jwtUtil.extrairEmailDoToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow( () ->
                new ResourceNotFoundException("Email nao encontrado: " + email ));

        Endereco endereco = usuarioConverter.paraEnderecoEntity(dto, usuario.getId());
        Endereco enderecoEntity = enderecoRepository.save(endereco);
        return usuarioConverter.paraEnderecoDTO(enderecoEntity);
    }

    public TelefoneDTO cadastraTelefone(String token, TelefoneDTO dto){
        String email = jwtUtil.extrairEmailDoToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow( () ->
                new ResourceNotFoundException("Email nao encontrado: " + email ));

        Telefone telefone = usuarioConverter.paraTelefoneEntity(dto, usuario.getId());
        Telefone telefoneEntity = telefoneRepository.save(telefone);
        return usuarioConverter.paraTelefoneDTO(telefoneEntity);
    }
}
