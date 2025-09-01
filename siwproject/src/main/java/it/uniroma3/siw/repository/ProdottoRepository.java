package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Prodotto;

public interface ProdottoRepository extends CrudRepository<Prodotto, Long>{
	
	public boolean existsByNome(String nome);
    
    List<Prodotto> findByNomeContainingIgnoreCase(String query);
}
