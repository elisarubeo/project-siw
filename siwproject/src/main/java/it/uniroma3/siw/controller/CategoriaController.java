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

import it.uniroma3.siw.model.Categoria;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CategoriaService;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.validation.Valid;

@Controller
public class CategoriaController {
	@Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CredentialsService credentialsService;

    /**
     * Aggiunge sempre le credenziali dell'utente loggato al model
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
     * Pagina pubblica di elenco categorie
     */
    @GetMapping("/categoria")
    public String getCategoria(Model model) {
        model.addAttribute("categorie", categoriaService.findAll());
        return "categorie.html";
    }

    /**
     * Pagina pubblica dettaglio categoria
     */
    @GetMapping("/categoria/{id}")
    public String getCategoria(@PathVariable("id") Long id, Model model) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria == null) {
            return "error.html";
        }
        model.addAttribute("categoria", categoria);
        return "categoria.html";
    }

    /**
     * Pagina ADMIN: lista di tutte le categorie + pulsante aggiungi
     */
    @GetMapping("/admin/categorie")
    public String adminCategorie(Model model) {
    	model.addAttribute("categorie", categoriaService.findAllWithProdotti());
        model.addAttribute("categoria", new Categoria());
        return "admin/categorieAdmin.html";
    }

    /**
     * Form per creare una nuova categoria
     */
    @GetMapping("/admin/formNewCategoria")
    public String formNewCategoria(@RequestParam(value = "from", required = false) String from, Model model) {
        if (!model.containsAttribute("categoria")) {
            model.addAttribute("categoria", new Categoria());
        }
        model.addAttribute("from", from); // lo passo per il bottone "Torna indietro"
        return "admin/formNewCategoria.html";
    }

    @PostMapping("/admin/saveCategoria")
    public String saveCategoria(
            @Valid @ModelAttribute("categoria") Categoria categoria,
            BindingResult bindingResult,
            @RequestParam(value = "from", required = false) String from,
            Model model) {

        if (categoriaService.existsByName(categoria.getNome())) {
            bindingResult.rejectValue("nome", "error.categoria", "Questa categoria esiste già");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("from", from);
            return "admin/formNewCategoria.html";
        }

        categoriaService.save(categoria);

        // Se provengo dalla creazione prodotto, posso tornare lì:
        if ("new-product".equals(from)) {
            return "redirect:/admin/formNewProdotto";
        }

        return "admin/successCategoria.html";
    }
    

    /**
     * Form per eliminare una categoria
     */
    @GetMapping("/admin/deleteCategoria/{id}")
    public String deleteCategoria(@PathVariable("id") Long id, Model model) {
        Categoria categoria = categoriaService.findById(id);

        if (categoria == null) {
            return "error.html";
        }

        if (!categoria.getProdotti().isEmpty()) {
            model.addAttribute("messaggioErrore", "Non puoi eliminare una categoria con prodotti associati.");
            return "erroreEliminaCategoria.html";
        }

        categoriaService.deleteById(id);
        return "redirect:/admin/categorie";
    }
}
