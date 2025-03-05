package com.ajsolucoes.business;


import com.ajsolucoes.business.converter.UsuarioConverter;
import com.ajsolucoes.business.dto.UsuarioDTO;
import com.ajsolucoes.infrastructure.entity.Usuario;
import com.ajsolucoes.infrastructure.exceptions.ConflitException;
import com.ajsolucoes.infrastructure.exceptions.ResourceNotfoundException;
import com.ajsolucoes.infrastructure.repository.UsuarioRepository;
import com.ajsolucoes.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
    emailExiste(usuarioDTO.getEmail());
    usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
    Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
    return usuarioConverter.paraUsuarioDTO(
            usuarioRepository.save(usuario));
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

    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotfoundException("Email nao encontrado" + email));
    }

    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto){

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
}
