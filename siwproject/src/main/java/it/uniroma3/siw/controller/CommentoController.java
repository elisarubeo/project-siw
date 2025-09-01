package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.service.*;
import jakarta.validation.Valid;

@Controller
public class CommentoController {

    @Autowired
    private CommentoService commentoService;

    @Autowired
    private ProdottoService prodottoService;

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping("/prodotto/{id}/commenti")
    public String getCommentiByProdotto(@PathVariable("id") Long id, Model model) {
        Prodotto prodotto = prodottoService.findById(id);
        if (prodotto == null) {
            return "error.html";
        }
        model.addAttribute("prodotto", prodotto);
        model.addAttribute("commenti", commentoService.findByProdotto(prodotto));
        return "commenti.html";
    }

    @GetMapping("/user/prodotto/{id}/formCommento")
    public String formCommento(@PathVariable("id") Long id, Model model) {
    	Prodotto prodotto = prodottoService.findById(id);
        if (prodotto == null) return "error.html";

        User user = getCurrentUser();

        model.addAttribute("commento", new Commento());
        model.addAttribute("prodotto", prodotto);
        return "formCommento.html";
    }

    @PostMapping("/user/prodotto/{id}/commento")
    public String saveCommento(@PathVariable("id") Long id,
                                 @Valid@ModelAttribute("commento") Commento commento,
                                 BindingResult bindingResult,
                                 Model model) {
    	Prodotto prodotto = prodottoService.findById(id);
        if (prodotto == null) return "error.html";

        User user = getCurrentUser();
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("prodotto", prodotto);
            return "formCommento.html";
        }

        commento.setProdotto(prodotto);
        commento.setUser(user);
        commento.setId(null);
        commentoService.save(commento);

        return "successCommento.html";
    }

    @GetMapping("/user/commento/delete/{id}")
    public String deleteCommentoUser(@PathVariable("id") Long id) {
        Commento commento = commentoService.findById(id);
        if (commento == null) return "error.html";

        User user = getCurrentUser();

        if (commento.getUser().getId().equals(user.getId())) {
            commentoService.deleteById(id);
        }

        return "successEliminaCommento.html";
    }

    @GetMapping("/admin/commento/delete/{id}")
    public String deleteCommentoAdmin(@PathVariable("id") Long id) {
    	Commento commento = commentoService.findById(id);
        if (commento == null) return "error.html";

        commentoService.deleteById(id);
        return "successEliminaCommento.html";
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return credentialsService.getCredentials(username).getUser();
    }
}