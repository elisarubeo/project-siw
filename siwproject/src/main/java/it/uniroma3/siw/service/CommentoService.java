package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CommentoRepository;

@Service
public class CommentoService {
	@Autowired
    private CommentoRepository commentoRepository;

    public Commento save(Commento commento) {
        return commentoRepository.save(commento);
    }

    public void deleteById(Long id) {
        commentoRepository.deleteById(id);
    }

    public Commento findById(Long id) {
        return commentoRepository.findById(id).orElse(null);
    }

    public Iterable<Commento> findAll() {
        return commentoRepository.findAll();
    }

    public Iterable<Commento> findByProdotto(Prodotto prodotto) {
        return commentoRepository.findByProdotto(prodotto);
    }

    public Optional<Commento> findByProdottoAndUser(Prodotto prodotto, User user) {
        return commentoRepository.findByProdottoAndUser(prodotto, user);
    }
}
