package br.com.feiraviva.dto;

import java.util.List;

public record CategoriaArvoreDTO(
        Long id, String nome, String descricao,
        Long categoriaPaiId, boolean folha,
        List<CategoriaArvoreDTO> subcategorias) { }