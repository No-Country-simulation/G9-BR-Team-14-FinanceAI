package com.finance_ai_backend.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("usuario")
@SecurityRequirement(name = "bearerAuth") 
public class UsuarioController {

    @GetMapping()
    public String home(){
        return "cadastrado";
    }
    
}