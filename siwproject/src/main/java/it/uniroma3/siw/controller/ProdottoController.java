package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ProdottoService;
import jakarta.validation.Valid;

@Controller
public class ProdottoController {
	
	@Autowired
    private CredentialsService credentialsService;
	
	@Autowired
	private ProdottoService prodottoService;
	
	/**
     * Metodo eseguito prima di OGNI handler: aggiunge "credentials" al model
     */
    @ModelAttribute
    public void addCredentialsToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            model.addAttribute("credentials", credentials);
        } else {
            model.addAttribute("credentials", null);
        }
    }
    
    /**
     * Pagina ADMIN: lista di tutti i prodotti + pulsante aggiungi
     */
    @GetMapping("/admin/prodotti")
    public String adminProdotti(Model model) {
        model.addAttribute("prodotti", prodottoService.findAll());
        model.addAttribute("prodotto", new Prodotto());
        return "admin/prodottiAdmin.html";
    }
    
    /**
     * Salva nuovo prodotto (solo admin)
     */
    
    @GetMapping("/admin/formNewProdotto")
    public String formNewProdotto(Model model) {
        if (!model.containsAttribute("prodotto")) {
            model.addAttribute("prodotto", new Prodotto());
        }
        return "admin/formNewProdotto.html";
    }

    
    @PostMapping("/admin/saveProdotto")
    public String saveProdotto(
        @Valid @ModelAttribute("prodotto") Prodotto prodotto,
        BindingResult bindingResult,
        Model model) {
    	 if (prodottoService.existsByNome(prodotto.getNome())) {
             bindingResult.rejectValue("nome", "error.prodotto", "Questo prodotto esiste già");
         	}
        if (bindingResult.hasErrors())
            return "admin/formNewProdotto.html";
            

        prodottoService.save(prodotto);
        return "admin/successProdotto.html"; 
    }
    
    /**
     * Form di modifica prodotto
     */
    @GetMapping("/admin/formUpdateProdotto/{id}")
    public String formUpdateProdotto(@PathVariable("id") Long id, Model model) {
        Prodotto prodotto = prodottoService.findById(id);
        if (prodotto == null) {
            return "error.html";
        }
        model.addAttribute("prodotto", prodotto);
        return "admin/formUpdateProdotto.html";
    }
    
    /**
     * Salva aggiornamento prodotto
     */
    @PostMapping("/admin/updateProdotto/{id}")
    public String updateProdotto(@PathVariable("id") Long id,
                              @RequestParam String nome,
                              @RequestParam double prezzo,
                              @RequestParam String descrizione) {
    	Prodotto prodotto = prodottoService.findById(id);

        prodottoService.save(prodotto);
        return "admin/successUpdate.html";
    }
    
    /**
     * Cancella prodotto
     */
    @GetMapping("/admin/deleteProdotto/{id}")
    public String deleteProdotto(@PathVariable("id") Long id) {
        prodottoService.deleteById(id);
        return "redirect:/admin/prodotti";
    }
    
    /**
     * Lista pubblica
     */
    @GetMapping("/prodotto")
    public String getProdotti(Model model) {
        model.addAttribute("prodotti", prodottoService.findAll());
        return "prodotto.html";
    }
    
    /**
     * Dettaglio pubblico
     */
    @GetMapping("/prodotto/{id}")
    public String getProdotto(@PathVariable("id") Long id, Model model) {
        Prodotto prodotto = prodottoService.findById(id);
        if (prodotto == null) {
            return "error.html";
        }

        model.addAttribute("prodotto", prodotto);

        // Default: bottone "Aggiungi commento" NON mostrato
        boolean mostraBottoneCommento = false;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());

            // Utente con ruolo DEFAULT
            if ("DEFAULT".equals(credentials.getRole())) {
                mostraBottoneCommento = true;
            }
        }

        model.addAttribute("mostraBottoneCommento", mostraBottoneCommento);

        return "prodotto.html";
    }
    
    /**
     * Search page
     */
    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        model.addAttribute("prodotti", prodottoService.searchByQuery(query));
        return "searchResults.html";
    }
}
