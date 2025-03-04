package com.ajsolucoes.business;


import com.ajsolucoes.business.converter.UsuarioConverter;
import com.ajsolucoes.business.dto.UsuarioDTO;
import com.ajsolucoes.infrastructure.entity.Usuario;
import com.ajsolucoes.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
    Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
    return usuarioConverter.paraUsuarioDTO(
            usuarioRepository.save(usuario));
}
}
