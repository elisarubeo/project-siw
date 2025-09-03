package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Prodotto;

public interface ProdottoRepository extends CrudRepository<Prodotto, Long>{
	
	public boolean existsByNome(String nome);
    
    List<Prodotto> findByNomeContainingIgnoreCase(String query);
    
    @Query("""
            select p from Prodotto p
            left join fetch p.simili
            where p.id = :id
            """)
     Optional<Prodotto> findByIdWithSimili(@Param("id") Long id);
}
