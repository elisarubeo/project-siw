package it.uniroma3.siw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Categoria;
import it.uniroma3.siw.repository.CategoriaRepository;

@Service
public class CategoriaService {
	@Autowired
    private CategoriaRepository categoriaRepository;

    public Iterable<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    public Categoria findById(Long id) {
        Optional<Categoria> result = categoriaRepository.findById(id);
        return result.orElse(null);
    }

    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }

    public boolean existsByName(String nome) {
        return categoriaRepository.existsByNome(nome);
    }
    
    public List<Categoria> searchByName(String query) {
        return categoriaRepository.findByNomeContainingIgnoreCase(query);
    }
    
    public List<Categoria> findAllWithProdotti() {
        return categoriaRepository.findAllWithProdotti();
    }
}
