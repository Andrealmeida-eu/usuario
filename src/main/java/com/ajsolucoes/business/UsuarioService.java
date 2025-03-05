package com.ajsolucoes.business;


import com.ajsolucoes.business.converter.UsuarioConverter;
import com.ajsolucoes.business.dto.UsuarioDTO;
import com.ajsolucoes.infrastructure.entity.Usuario;
import com.ajsolucoes.infrastructure.exceptions.ConflitException;
import com.ajsolucoes.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder PasswordEncoder;

public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
    emailExiste(usuarioDTO.getEmail());
    usuarioDTO.setSenha(PasswordEncoder.encode(usuarioDTO.getSenha()));
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
}
