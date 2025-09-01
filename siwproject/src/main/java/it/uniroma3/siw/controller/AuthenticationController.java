package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Prodotto;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ProdottoService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private ProdottoService prodottoService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        Credentials credentials = new Credentials();
        credentials.setUser(new it.uniroma3.siw.model.User());
        model.addAttribute("credentials", credentials);
        return "formRegisterUser";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("credentials") Credentials credentials,
            BindingResult bindingResult,
            Model model) {

        if (credentialsService.existsByUsername(credentials.getUsername())) {
            bindingResult.rejectValue("username", "error.credentials", "Username già esistente");
        }

        if (bindingResult.hasErrors()) {
            return "formRegisterUser";
        }

        credentials.setRole(Credentials.DEFAULT_ROLE);
        credentialsService.saveCredentials(credentials);
        model.addAttribute("user", credentials.getUser());
        return "registrationSuccessful";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "formLogin";
    }

    @GetMapping("/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            model.addAttribute("credentials", credentials);
        } else {
            model.addAttribute("credentials", null);
        }

        Iterable<Prodotto> allProdotti = prodottoService.findAll();
        model.addAttribute("prodotti", allProdotti);

        return "homepage";
    }

    @GetMapping("/success")
    public String defaultAfterLogin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            model.addAttribute("credentials", credentials);
        } else {
            model.addAttribute("credentials", null);
        }

        Iterable<Prodotto> allProdotti = prodottoService.findAll();
        model.addAttribute("prodotti", allProdotti);

        return "homepage";
    }

}
