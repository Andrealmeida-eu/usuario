package com.ajsolucoes.controller;


import com.ajsolucoes.business.UsuarioService;
import com.ajsolucoes.business.dto.in.EnderecoRequestDTO;
import com.ajsolucoes.business.dto.in.TelefoneRequestDTO;
import com.ajsolucoes.business.dto.in.UsuarioRequestDTO;
import com.ajsolucoes.business.dto.out.EnderecoResponseDTO;
import com.ajsolucoes.business.dto.out.TelefoneResponseDTO;
import com.ajsolucoes.business.dto.out.UsuarioResponseDTO;
import com.ajsolucoes.infrastructure.entity.Usuario;
import com.ajsolucoes.infrastructure.repository.UsuarioRepository;
import com.ajsolucoes.infrastructure.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor

public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;


    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> salvaUsuario(@RequestBody UsuarioRequestDTO usuarioDTO){
          return ResponseEntity.ok(usuarioService.salvaUsuario(usuarioDTO));
    }

    @PostMapping ("/login")
    public String login(@RequestBody UsuarioRequestDTO usuarioDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(), usuarioDTO.getSenha())
        );

        // Agora busca o usuário completo no banco
        Usuario usuario = usuarioRepository.findByEmail(usuarioDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Gera e retorna o token JWT
        return "Bearer " + jwtUtil.generateToken(usuario);
    }


    @GetMapping("/buscar-por-email")
    public ResponseEntity<UsuarioResponseDTO> buscaUsuarioPorEmail(@RequestParam("email") String email){

        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletaUsuarioPorEmail(@PathVariable String email){
        usuarioService.deletaUsuarioPorEmail(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<UsuarioResponseDTO> atualizaDadosUsuario(@RequestBody UsuarioRequestDTO dto,
                                                                  @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.atualizaDadosUsuario(token, dto));
    }

    @PutMapping("/endereco")
    public ResponseEntity<EnderecoResponseDTO> atualizaEndereco (@RequestBody EnderecoRequestDTO dto,
                                                                 @RequestParam("id") Long id){
        return ResponseEntity.ok(usuarioService.atualizaEndereco(id,dto));
    }

    @PutMapping("/telefone")
    public ResponseEntity<TelefoneResponseDTO> atualizaTelefone (@RequestBody TelefoneRequestDTO dto,
                                                                 @RequestParam("id") Long id){
        return ResponseEntity.ok(usuarioService.atualizaTelefone(id,dto));
    }

    @PostMapping("/endereco")
    public ResponseEntity<EnderecoResponseDTO> cadastraEndereco (@RequestBody EnderecoRequestDTO dto,
                                                                @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.cadastraEndereco(token, dto));
    }

    @PostMapping("/telefone")
    public ResponseEntity<TelefoneResponseDTO> cadastraTelefone (@RequestBody TelefoneRequestDTO dto,
                                                                @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.cadastraTelefone(token, dto));
    }
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodosAlunos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

}
