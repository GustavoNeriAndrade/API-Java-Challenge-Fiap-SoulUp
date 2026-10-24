package br.com.fiap.dto;

import br.com.fiap.model.UsuarioMissao;
import java.time.LocalDateTime;

public record UsuarioMissaoResponse(int idUsuario, int idMissao,
                                    LocalDateTime dataConclusao, int pontosGanhos) {

    public static UsuarioMissaoResponse from(UsuarioMissao um) {
        return new UsuarioMissaoResponse(um.getIdUsuario(), um.getIdMissao(),
                um.getDataConclusao(), um.getPontosGanhos());
    }
}
