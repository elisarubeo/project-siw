package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
// oppure: import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.model.Categoria;

public interface CategoriaRepository extends CrudRepository<Categoria, Long> {
	
    boolean existsByNome(String nome);
    
    boolean existsByNomeIgnoreCase(String nome);

    List<Categoria> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT DISTINCT c FROM Categoria c LEFT JOIN FETCH c.prodotti")
    List<Categoria> findAllWithProdotti();
    
    boolean existsByIdAndProdottiIsNotEmpty(Long id);
}
