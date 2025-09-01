package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.repository.ProdottoRepository;

@Service
public class ProdottoService {
	
	@Autowired
	ProdottoRepository prodottoRepository;
	
	@Transactional
    public Prodotto findById(Long id) {
        return prodottoRepository.findById(id).orElse(null);
    }
	
	@Transactional
    public Iterable<Prodotto> findAll() {
        return prodottoRepository.findAll();
    }
	
	@Transactional
    public void save(Prodotto prodotto) {
        prodottoRepository.save(prodotto);
    }
	
	@Transactional
    public void updateLibro(Long id, String nome, double prezzo, String descrizione) {
        Prodotto prodotto = this.findById(id);
        if (prodotto != null) {
            prodotto.setNome(nome);
            prodotto.setPrezzo(prezzo);
            prodotto.setDescrizione(descrizione);
            prodottoRepository.save(prodotto);
        }
    }
	
	@Transactional
    public void deleteById(Long id) {
        prodottoRepository.deleteById(id);
    }
	
	@Transactional
    public Iterable<Prodotto> searchByQuery(String query) {
        return prodottoRepository.findByNomeContainingIgnoreCase(query);
    }
	
	@Transactional
    public boolean existsByNome(String nome) {
        return prodottoRepository.existsByNome(nome);
    }
}
