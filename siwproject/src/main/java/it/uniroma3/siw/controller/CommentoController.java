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

        model.addAttribute("commento", new Commento());
        model.addAttribute("prodotto", prodotto);
        return "formCommento.html";
    }

    @PostMapping("/user/prodotto/{id}/commento")
    public String saveCommento(@PathVariable("id") Long id,
                               @Valid @ModelAttribute("commento") Commento commento,
                               BindingResult bindingResult,
                               Model model) {
        Prodotto prodotto = prodottoService.findById(id);
        if (prodotto == null) return "error.html";

        if (commento.getTesto() != null) {
            commento.setTesto(commento.getTesto().trim());
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("prodotto", prodotto);
            return "formCommento.html";
        }

        User user = getCurrentUser();
        commento.setProdotto(prodotto);
        commento.setUser(user);
        commento.setId(null);
        commentoService.save(commento);

        return "redirect:/prodotto/" + prodotto.getId();
    }


    @GetMapping("/user/commento/edit/{id}")
    public String editCommentoForm(@PathVariable("id") Long id, Model model) {
        Commento commento = commentoService.findById(id);
        if (commento == null) return "error.html";

        // deve essere l'autore e avere ruolo DEFAULT
        User current = getCurrentUser();
        String role = getCurrentRole();
        if (current == null || commento.getUser() == null ||
            !commento.getUser().getId().equals(current.getId()) ||
            !"DEFAULT".equals(role)) {
            Long pid = commento.getProdotto() != null ? commento.getProdotto().getId() : null;
            return (pid != null) ? "redirect:/prodotto/" + pid : "redirect:/";
        }

        model.addAttribute("commento", commento);
        model.addAttribute("prodotto", commento.getProdotto());
        return "formEditCommento.html";
    }

    @PostMapping("/user/commento/update/{id}")
    public String updateCommento(@PathVariable("id") Long id,
                                 @ModelAttribute("commento") Commento form,
                                 BindingResult bindingResult,
                                 Model model) {
        Commento existing = commentoService.findById(id);
        if (existing == null) return "error.html";

        Long pid = (existing.getProdotto() != null) ? existing.getProdotto().getId() : null;

        // permessi: autore + DEFAULT
        User current = getCurrentUser();
        String role = getCurrentRole();
        if (current == null || existing.getUser() == null ||
            !existing.getUser().getId().equals(current.getId()) ||
            !"DEFAULT".equals(role)) {
            return (pid != null) ? "redirect:/prodotto/" + pid : "redirect:/";
        }

        // normalizza e valida solo il testo
        String nuovoTesto = form.getTesto() != null ? form.getTesto().trim() : null;
        if (nuovoTesto == null || nuovoTesto.isBlank()) {
            bindingResult.rejectValue("testo", "commento.testo.blank", "Il testo è obbligatorio");
        }

        if (bindingResult.hasErrors()) {
            // mostro di nuovo il form con gli errori
            existing.setTesto(nuovoTesto); // per mantenere quanto digitato
            model.addAttribute("commento", existing);
            model.addAttribute("prodotto", existing.getProdotto());
            return "formEditCommento.html";
        }

        existing.setTesto(nuovoTesto);
        commentoService.save(existing);

        return (pid != null) ? "redirect:/prodotto/" + pid : "redirect:/";
    }

    /* ==================================================================== */

    @GetMapping("/user/commento/delete/{id}")
    public String deleteCommentoUser(@PathVariable("id") Long id) {
        Commento commento = commentoService.findById(id);
        if (commento == null) return "error.html";

        Long productId = commento.getProdotto() != null ? commento.getProdotto().getId() : null;

        User user = getCurrentUser();
        if (commento.getUser() != null && user != null &&
            commento.getUser().getId().equals(user.getId())) {
            commentoService.deleteById(id);
        }

        return (productId != null) ? "redirect:/prodotto/" + productId : "redirect:/";
    }

    @GetMapping("/admin/commento/delete/{id}")
    public String deleteCommentoAdmin(@PathVariable("id") Long id) {
        Commento commento = commentoService.findById(id);
        if (commento == null) return "error.html";

        Long productId = commento.getProdotto() != null ? commento.getProdotto().getId() : null;

        commentoService.deleteById(id);

        return (productId != null) ? "redirect:/prodotto/" + productId : "redirect:/";
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return credentialsService.getCredentials(username).getUser();
    }

    private String getCurrentRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return credentialsService.getCredentials(username).getRole();
    }
}
