package it.uniroma3.siw.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
	
	@Transactional
    public void addSimile(Long idProdotto, Long idSimile) {
        Prodotto a = prodottoRepository.findByIdWithSimili(idProdotto)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato: " + idProdotto));
        Prodotto b = prodottoRepository.findById(idSimile)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato: " + idSimile));
        a.addSimile(b);
        // salva solo l’owning; flush automatico a fine transazione
        prodottoRepository.save(a);
    }

    @Transactional
    public void removeSimile(Long idProdotto, Long idSimile) {
        Prodotto a = prodottoRepository.findByIdWithSimili(idProdotto)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato: " + idProdotto));
        Prodotto b = prodottoRepository.findById(idSimile)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato: " + idSimile));
        a.removeSimile(b);
        prodottoRepository.save(a);
    }

    /**
     * Sostituisce l’insieme dei “simili” (in modo simmetrico) con l’elenco dato.
     * Utile per form con checkbox.
     */
    @Transactional
    public void replaceSimili(Long idProdotto, Collection<Long> nuoviSimiliIds) {
        Prodotto a = prodottoRepository.findByIdWithSimili(idProdotto)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato: " + idProdotto));

        // calcola target set
        Set<Long> targetIds = nuoviSimiliIds == null ? Set.of() : new HashSet<>(nuoviSimiliIds);
        targetIds.remove(a.getId()); // niente self

        // stato attuale (unione simili + simileDi)
        Set<Prodotto> attuali = a.getTuttiISimili();

        // rimuovi quelli non più selezionati
        for (Prodotto cur : new HashSet<>(attuali)) {
            if (!targetIds.contains(cur.getId())) {
                a.removeSimile(cur); // mantiene simmetria
            }
        }

        // aggiungi i nuovi
        for (Long tid : targetIds) {
            Prodotto b = prodottoRepository.findById(tid)
                    .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato: " + tid));
            if (!attuali.contains(b)) {
                a.addSimile(b);
            }
        }

        prodottoRepository.save(a);
    }

    /**
     * Delete sicuro: stacca i “simili” simmetricamente prima di rimuovere il prodotto.
     */
    @Transactional
    public void deleteByIdSafely(Long id) {
        Prodotto p = prodottoRepository.findByIdWithSimili(id).orElse(null);
        if (p != null) {
            for (Prodotto other : new HashSet<>(p.getTuttiISimili())) {
                p.removeSimile(other);
            }
            prodottoRepository.save(p);
        }
        prodottoRepository.deleteById(id);
    }
}
