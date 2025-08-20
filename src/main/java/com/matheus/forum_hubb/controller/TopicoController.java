package com.matheus.forum_hubb.controller;

import com.matheus.forum_hubb.dtos.DadosAtualizacaoTopico;
import com.matheus.forum_hubb.dtos.DadosCadastroTopico;
import com.matheus.forum_hubb.dtos.DadosDetalhamentoTopico;
import com.matheus.forum_hubb.model.Topico;
import com.matheus.forum_hubb.repository.TopicoRepository;
import com.matheus.forum_hubb.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoTopico> cadastrar(@RequestBody @Valid DadosCadastroTopico dados, UriComponentsBuilder uriBuilder) {
        if (topicoRepository.existsByTituloAndMensagem(dados.titulo(), dados.mensagem())) {
            return ResponseEntity.badRequest().build();
        }

        var autor = usuarioRepository.findById(dados.autorId()).orElseThrow(() -> new IllegalArgumentException("Autor não encontrado"));
        var topico = new Topico(null, dados.titulo(), dados.mensagem(), null, null, autor, dados.curso());

        topicoRepository.save(topico);

        URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoTopico(topico));
    }

    @GetMapping
    public ResponseEntity<List<DadosDetalhamentoTopico>> listar() {
        var topicos = topicoRepository.findAll().stream().map(DadosDetalhamentoTopico::new).toList();
        return ResponseEntity.ok(topicos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoTopico> detalhar(@PathVariable Long id) {
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        if (topicoOptional.isPresent()) {
            return ResponseEntity.ok(new DadosDetalhamentoTopico(topicoOptional.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoTopico> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoTopico dados) {
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        if (topicoOptional.isPresent()) {
            var topico = topicoOptional.get();

            if (dados.titulo() != null) topico.setTitulo(dados.titulo());
            if (dados.mensagem() != null) topico.setMensagem(dados.mensagem());
            if (dados.curso() != null) topico.setCurso(dados.curso());

            topicoRepository.save(topico);
            return ResponseEntity.ok(new DadosDetalhamentoTopico(topico));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (topicoRepository.existsById(id)) {
            topicoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}