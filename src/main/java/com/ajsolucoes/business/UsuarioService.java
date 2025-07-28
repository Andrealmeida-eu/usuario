package com.ajsolucoes.business;


import com.ajsolucoes.business.converter.UsuarioConverter;
import com.ajsolucoes.business.dto.in.EnderecoRequestDTO;
import com.ajsolucoes.business.dto.in.TelefoneRequestDTO;
import com.ajsolucoes.business.dto.in.UsuarioRequestDTO;
import com.ajsolucoes.business.dto.out.EnderecoResponseDTO;
import com.ajsolucoes.business.dto.out.TelefoneResponseDTO;
import com.ajsolucoes.business.dto.out.UsuarioResponseDTO;
import com.ajsolucoes.infrastructure.entity.Endereco;
import com.ajsolucoes.infrastructure.entity.Telefone;
import com.ajsolucoes.infrastructure.entity.Usuario;
import com.ajsolucoes.infrastructure.enums.Role;
import com.ajsolucoes.infrastructure.exceptions.ConflitException;
import com.ajsolucoes.infrastructure.exceptions.ResourceNotfoundException;
import com.ajsolucoes.infrastructure.repository.EnderecoRepository;
import com.ajsolucoes.infrastructure.repository.TelefoneRepository;
import com.ajsolucoes.infrastructure.repository.UsuarioRepository;

import com.ajsolucoes.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;


public UsuarioResponseDTO salvaUsuario(UsuarioRequestDTO usuarioDTO){
    emailExiste(usuarioDTO.getEmail());

    // Definir role padrão como CLIENTE se não for especificado
    if (usuarioDTO.getRole() == null) {
        usuarioDTO.setRole(Role.CLIENTE);
    }

    usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
    Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
    return usuarioConverter.paraUsuarioDTO(
            usuarioRepository.save(usuario));
}

    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponseDTO salvaAdmin(UsuarioRequestDTO usuarioDTO) {
        usuarioDTO.setRole(Role.ADMIN);
        return salvaUsuario(usuarioDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioResponseDTO> listarTodosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuarioConverter::paraUsuarioDTO)
                .collect(Collectors.toList());
    }

public void emailExiste(String email) {
        try {
            boolean existe = verificaEmailExistente(email);
            if (existe) {
                throw new ConflitException("Email já cadastrado" + email);
            }
        } catch (ConflitException e) {
            throw new ConflitException("Email já cadastrado" + e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public UsuarioResponseDTO buscarUsuarioPorEmail(String email){
       try {
           return usuarioConverter.paraUsuarioDTO(
                   usuarioRepository.findByEmail(email)
                           .orElseThrow(
                   () -> new ResourceNotfoundException("Email nao encontrado" + email)
                           )
           );
       } catch (ResourceNotfoundException e){
           throw new ResourceNotfoundException("Email nao encontrado "+ email);
       }
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioResponseDTO atualizaDadosUsuario(String token, UsuarioRequestDTO dto){

        //buscamos o email do usuario atraves do token (tirar a obrigatoriedade do email)
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        //criptografia na senha
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);
        //Buscamos os dados no banco
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotfoundException("Email nao encontrado"));
        //Mesclou os dados que recebemos dto com os dados do banco
        Usuario usuario = usuarioConverter.updatedUsuario(dto, usuarioEntity);
        //salvou os dados do usuario convertido e depois converteu o retorno para dto
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));


    }

    public EnderecoResponseDTO atualizaEndereco(Long idEndereco, EnderecoRequestDTO enderecoDTO){
        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(() ->
                new ResourceNotfoundException("Id nao encontrado" + idEndereco));

        Endereco endereco = usuarioConverter.updatedEndereco(enderecoDTO, entity);

        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }

    public TelefoneResponseDTO atualizaTelefone(Long idTelefone, TelefoneRequestDTO telefoneDTO){

        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(()->
                new ResourceNotfoundException("id nao encontrado" + idTelefone));

        Telefone telefone = usuarioConverter.updatedTelefone(telefoneDTO, entity);

        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }

    public EnderecoResponseDTO cadastraEndereco(String token, EnderecoRequestDTO dto){

        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(()->
               new ResourceNotfoundException("Email nao encontrado " + email));

        Endereco endereco = usuarioConverter.paraEnderecoEntity(dto, usuario.getId());
        Endereco enderecoEntity = enderecoRepository.save(endereco);

        return usuarioConverter.paraEnderecoDTO(enderecoEntity);

    }

    public TelefoneResponseDTO cadastraTelefone(String token, TelefoneRequestDTO dto){

        String email = jwtUtil.extrairEmailToken(token);
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(()->
                new ResourceNotfoundException("Email nao encontrado " + email));

        Telefone telefone = usuarioConverter.paraTelefoneEntity(dto, usuario.getId());
        Telefone telefoneEntity = telefoneRepository.save(telefone);

        return usuarioConverter.paraTelefoneDTO(telefoneEntity);

    }

    public List<UsuarioResponseDTO> listarTodos () {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioConverter::paraUsuarioDTO)  // Usando o construtor especial
                .collect(Collectors.toList());
    }
}
