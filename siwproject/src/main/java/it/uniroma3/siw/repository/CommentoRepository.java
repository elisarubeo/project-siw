package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.model.User;

public interface CommentoRepository extends CrudRepository<Commento, Long> {

    List<Commento> findByProdotto(Prodotto prodotto);

    Optional<Commento> findByProdottoAndUser(Prodotto prodotto, User user);
}