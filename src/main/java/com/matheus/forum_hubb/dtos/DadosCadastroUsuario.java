package com.matheus.forum_hubb.dtos;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroUsuario(
        @NotBlank
        String login,

        @NotBlank
        String senha
) {}