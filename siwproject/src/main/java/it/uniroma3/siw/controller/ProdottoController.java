package it.uniroma3.siw.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Categoria;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.service.CategoriaService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ProdottoService;
import jakarta.validation.Valid;

@Controller
public class ProdottoController {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private ProdottoService prodottoService;

    @Autowired
    private CategoriaService categoriaService;

    /**
     * Metodo eseguito prima di OGNI handler: aggiunge "credentials" al model
     */
    @ModelAttribute
    public void addCredentialsToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
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
     * Form nuovo prodotto (solo admin)
     */
    @GetMapping("/admin/formNewProdotto")
    public String formNewProdotto(Model model) {
        if (!model.containsAttribute("prodotto")) {
            model.addAttribute("prodotto", new Prodotto());
        }
        // Passo le categorie per la vista (checkbox)
        model.addAttribute("categorie", categoriaService.findAll());
        return "admin/formNewProdotto.html";
    }

    /**
     * Salva nuovo prodotto (solo admin)
     */
    @PostMapping("/admin/saveProdotto")
    public String saveProdotto(
            @Valid @ModelAttribute("prodotto") Prodotto prodotto,
            BindingResult bindingResult,
            @RequestParam(value = "categorieIds", required = false) List<Long> categorieIds,
            Model model) {

        // Validazioni custom
        if (prodottoService.existsByNome(prodotto.getNome())) {
            bindingResult.rejectValue("nome", "error.prodotto", "Questo prodotto esiste già");
        }
        if (categorieIds == null || categorieIds.isEmpty()) {
            bindingResult.reject("categorie", "Devi selezionare almeno una categoria");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorie", categoriaService.findAll()); // ripopola lista
            return "admin/formNewProdotto.html";
        }

        // Associa le categorie selezionate
        Set<Categoria> selezionate = new HashSet<>();
        for (Long idCat : categorieIds) {
            Categoria c = categoriaService.findById(idCat);
            if (c != null) selezionate.add(c);
        }
        prodotto.setCategorie(selezionate);

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
        if (prodotto == null) {
            return "error.html";
        }

        // Aggiorna i campi (il tuo vecchio codice non li settava)
        prodotto.setNome(nome);
        prodotto.setPrezzo(prezzo);
        prodotto.setDescrizione(descrizione);

        prodottoService.save(prodotto);
        return "admin/successUpdate.html";
    }

    /**
     * Cancella prodotto — con popup di esito su homepage (flash attributes)
     */
    @GetMapping("/admin/deleteProdotto/{id}")
    public String deleteProdotto(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            Prodotto p = prodottoService.findById(id);
            if (p == null) {
                ra.addFlashAttribute("popupType", "danger");
                ra.addFlashAttribute("popupTitle", "Prodotto inesistente");
                ra.addFlashAttribute("popupMessage", "Il prodotto che stai tentando di eliminare non esiste.");
                return "redirect:/";
            }

            prodottoService.deleteById(id);

            ra.addFlashAttribute("popupType", "success");
            ra.addFlashAttribute("popupTitle", "Eliminazione riuscita");
            ra.addFlashAttribute("popupMessage", "Prodotto eliminato correttamente.");
            return "redirect:/";

        } catch (EmptyResultDataAccessException ex) {
            ra.addFlashAttribute("popupType", "danger");
            ra.addFlashAttribute("popupTitle", "Prodotto inesistente");
            ra.addFlashAttribute("popupMessage", "Il prodotto che stai tentando di eliminare non esiste.");
            return "redirect:/";
        } catch (Exception ex) {
            ra.addFlashAttribute("popupType", "danger");
            ra.addFlashAttribute("popupTitle", "Errore");
            ra.addFlashAttribute("popupMessage", "Si è verificato un errore durante l'eliminazione del prodotto.");
            return "redirect:/";
        }
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
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

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
