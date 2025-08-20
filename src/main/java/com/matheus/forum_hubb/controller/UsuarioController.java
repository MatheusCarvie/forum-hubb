package com.matheus.forum_hubb.controller;

import com.matheus.forum_hubb.dtos.DadosCadastroUsuario;
import com.matheus.forum_hubb.model.Usuario;
import com.matheus.forum_hubb.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<Void> cadastrar(@RequestBody @Valid DadosCadastroUsuario dados) {
        // Verifica se o login já existe para evitar duplicação
        if (usuarioRepository.findByLogin(dados.login()) != null) {
            return ResponseEntity.badRequest().build();
        }

        var usuario = new Usuario();
        usuario.setLogin(dados.login());

        // Criptografa a senha antes de salvar no banco de dados
        usuario.setSenha(passwordEncoder.encode(dados.senha()));

        usuarioRepository.save(usuario);

        return ResponseEntity.ok().build();
    }
}