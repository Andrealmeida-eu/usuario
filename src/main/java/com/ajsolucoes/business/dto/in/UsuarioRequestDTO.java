package com.ajsolucoes.business.dto.in;

import com.ajsolucoes.infrastructure.entity.Usuario;
import com.ajsolucoes.infrastructure.enums.Role;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioRequestDTO {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private List<EnderecoRequestDTO> enderecos;
    private List<TelefoneRequestDTO> telefones;
    private Role role;
}
