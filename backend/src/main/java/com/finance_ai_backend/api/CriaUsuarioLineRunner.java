package com.finance_ai_backend.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataIntegrityViolationException;

import com.finance_ai_backend.api.domain.dtos.UsuarioMinimoParaCadastroDTO;
import com.finance_ai_backend.api.services.UsuarioService;

@Component
public class CriaUsuarioLineRunner implements CommandLineRunner {
    private final UsuarioService usuarioService;
    
    @Value("${admin-application:false}")
    private boolean adminApplication;

    @Value("${admin-application-username:}")
    private String adminUsername;

    @Value("${admin-application-password:}")
    private String adminPassword;

    public CriaUsuarioLineRunner(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!adminApplication) return;
        try{
            usuarioService.cadastraUsuario(
                UsuarioMinimoParaCadastroDTO
                .builder()
                .email("admin@admin.com")
                .username(adminUsername)
                .senha(adminPassword)
                .build()
            );
            System.out.println("Usuário administrativo cadastrado");
        }catch(DataIntegrityViolationException e){
            System.out.println("Usuário administrativo já cadastrado");
        }
        

    }
}