package br.com.fiap.dto;

import br.com.fiap.model.Missao;
import java.util.List;

public record MissaoResponse(int idMissao, String titulo, String descricao,
                             int pontosRecompensa, String categoria,
                             boolean ativa, List<String> dicas) {

    public static MissaoResponse from(Missao m) {
        return new MissaoResponse(m.getId(), m.getTitulo(), m.getDescricao(),
                m.getPontosRecompensa(), m.getCategoria(), m.isAtiva(), m.getDicas());
    }
}