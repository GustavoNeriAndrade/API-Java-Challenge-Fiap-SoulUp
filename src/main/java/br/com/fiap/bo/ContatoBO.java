package br.com.fiap.bo;

import br.com.fiap.dao.ContatoDAO;
import br.com.fiap.dto.ContatoRequest;
import br.com.fiap.model.Contato;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
public class ContatoBO {

    private final ContatoDAO contatoDAO;

    public ContatoBO(ContatoDAO contatoDAO) {
        this.contatoDAO = contatoDAO;
    }

    public Contato registrar(ContatoRequest req) throws SQLException {
        Contato contato = new Contato(0, req.nome().trim(), req.email().trim().toLowerCase(),
                req.mensagem().trim(), LocalDateTime.now());
        contatoDAO.inserir(contato);
        return contato;
    }
}