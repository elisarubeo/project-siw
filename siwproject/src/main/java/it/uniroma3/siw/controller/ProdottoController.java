package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ProdottoService;

@Controller
public class ProdottoController {
	
	@Autowired
    private CredentialsService credentialsService;
	
	@Autowired
	private ProdottoService prodottoService;

}
