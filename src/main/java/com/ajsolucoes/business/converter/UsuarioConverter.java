package com.ajsolucoes.business.converter;

import com.ajsolucoes.business.dto.in.EnderecoRequestDTO;
import com.ajsolucoes.business.dto.in.TelefoneRequestDTO;
import com.ajsolucoes.business.dto.in.UsuarioRequestDTO;
import com.ajsolucoes.business.dto.out.EnderecoResponseDTO;
import com.ajsolucoes.business.dto.out.TelefoneResponseDTO;
import com.ajsolucoes.business.dto.out.UsuarioResponseDTO;
import com.ajsolucoes.infrastructure.entity.Endereco;
import com.ajsolucoes.infrastructure.entity.Telefone;
import com.ajsolucoes.infrastructure.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UsuarioConverter {

    //CONVERTE DE DTO PARA ENTITY
    public Usuario paraUsuario(UsuarioRequestDTO usuarioDTO){
        return Usuario.builder()
                .nome(usuarioDTO.getNome())
                .email(usuarioDTO.getEmail())
                .senha(usuarioDTO.getSenha())
                .role(usuarioDTO.getRole())
                .enderecos(paraListaEndereco(usuarioDTO.getEnderecos()))
                .telefones(paraListatelefones(usuarioDTO.getTelefones()))
                .build();
    }

    public List<Endereco> paraListaEndereco(List<EnderecoRequestDTO> enderecoDTOS) {
        List<Endereco> enderecos = new ArrayList<>();

        for (EnderecoRequestDTO enderecoDTO : enderecoDTOS){
            enderecos.add(paraEndereco(enderecoDTO));
        }

        return enderecos;
    }

    public Endereco paraEndereco(EnderecoRequestDTO enderecoDTO){
        return Endereco.builder()
                .rua(enderecoDTO.getRua())
                .numero(enderecoDTO.getNumero())
                .cidade(enderecoDTO.getCidade())
                .complemento(enderecoDTO.getComplemento())
                .cep(enderecoDTO.getCep())
                .estado(enderecoDTO.getEstado())
                .build();
    }

    public List<Telefone> paraListatelefones(List<TelefoneRequestDTO> telefoneDTOS){
        return telefoneDTOS.stream().map(this::paraTelefone).toList();
    }

    public Telefone paraTelefone(TelefoneRequestDTO telefoneDTO){
        return Telefone.builder()
                .numero(telefoneDTO.getNumero())
                .ddd(telefoneDTO.getDdd())
                .build();
    }


    //CONVERTE DE ENTITY PARA DTO
    public UsuarioResponseDTO paraUsuarioDTO(Usuario usuarioDTO){
        return UsuarioResponseDTO
                .builder()
                .nome(usuarioDTO.getNome())
                .email(usuarioDTO.getEmail())
                .senha(usuarioDTO.getSenha())
                .role(usuarioDTO.getRole())
                .enderecos(paraListaEnderecoDTO(usuarioDTO.getEnderecos()))
                .telefones(paraListatelefonesDTO(usuarioDTO.getTelefones()))
                .build();
    }

    public List<EnderecoResponseDTO> paraListaEnderecoDTO(List<Endereco> enderecoDTOS) {
        List<EnderecoResponseDTO> enderecos = new ArrayList<>();

        for (Endereco enderecoDTO : enderecoDTOS){
            enderecos.add(paraEnderecoDTO(enderecoDTO));
        }

        return enderecos;
    }

    public EnderecoResponseDTO paraEnderecoDTO(Endereco endereco){
        return EnderecoResponseDTO.builder()
                .rua(endereco.getRua())
                .numero(endereco.getNumero())
                .cidade(endereco.getCidade())
                .complemento(endereco.getComplemento())
                .cep(endereco.getCep())
                .estado(endereco.getEstado())
                .build();
    }

    public List<TelefoneResponseDTO> paraListatelefonesDTO(List<Telefone> telefoneDTOS){
        return telefoneDTOS.stream().map(this::paraTelefoneDTO).toList();
    }

    public TelefoneResponseDTO paraTelefoneDTO(Telefone telefone){
        return TelefoneResponseDTO.builder()
                .numero(telefone.getNumero())
                .ddd(telefone.getDdd())
                .build();
    }


    public Usuario updatedUsuario(UsuarioRequestDTO usuarioDTO, Usuario entity) {
        return Usuario.builder()
                .nome(usuarioDTO.getNome() != null ? usuarioDTO.getNome() : entity.getNome())
                .id(entity.getId())
                .senha(usuarioDTO.getSenha() != null ? usuarioDTO.getSenha() : entity.getSenha())
                .email(usuarioDTO.getEmail() != null ? usuarioDTO.getEmail() : entity.getEmail())
                .role(usuarioDTO.getRole() != null ? usuarioDTO.getRole() : entity.getRole())
                .enderecos(entity.getEnderecos())
                .telefones(entity.getTelefones())
                .build();
    }

    public Endereco updatedEndereco(EnderecoRequestDTO dto, Endereco entity){
        return Endereco.builder()
                .id(entity.getId())
                .rua(dto.getRua() != null ? dto.getRua() : entity.getRua())
                .numero(dto.getNumero() != null ? dto.getNumero() : entity.getNumero())
                .complemento(dto.getComplemento() != null ? dto.getComplemento() : entity.getComplemento())
                .cidade(dto.getCidade() != null ? dto.getCidade() : entity.getCidade())
                .estado(dto.getEstado() != null ? dto.getEstado() : entity.getEstado())
                .cep(dto.getCep() != null ? dto.getCep() : entity.getCep())
                .build();
    }

    public Telefone updatedTelefone(TelefoneRequestDTO dto, Telefone entity){
        return Telefone.builder()
                .id(entity.getId())
                .numero(dto.getNumero() != null ? dto.getNumero() : entity.getNumero())
                .ddd(dto.getDdd() != null ? dto.getDdd() : entity.getDdd())
                .build();
    }

    public Endereco paraEnderecoEntity(EnderecoRequestDTO dto, Long idUsuario){
        return Endereco.builder()
                .rua(dto.getRua())
                .cidade(dto.getCidade())
                .cep(dto.getCep())
                .complemento(dto.getComplemento())
                .estado(dto.getEstado())
                .numero(dto.getNumero())
                .usuario_id(idUsuario)
                .build();
    }

    public Telefone paraTelefoneEntity(TelefoneRequestDTO dto, Long idUsuario){
        return Telefone.builder()
                .numero(dto.getNumero())
                .ddd(dto.getDdd())
                .usuario_id(idUsuario)
                .build();
    }


}
