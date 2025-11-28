package com.pi.grafos.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pi.grafos.model.Usuario;
import com.pi.grafos.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registro de novo usuário
    public void cadastrarUsuario(String usuario, String rawSenha) {
        Usuario user = new Usuario();
        user.setUsuario(usuario);
        String hash = passwordEncoder.encode(rawSenha);
        user.setSenha(hash);
        user.setTipoUsuario(0);
        repository.save(user);
    }

public boolean autenticar(String usuario, String rawSenha) {
    Optional<Usuario> userOpt = repository.findByUsuario(usuario);

    if (userOpt.isPresent()) {
        Usuario user = userOpt.get();
        return passwordEncoder.matches(rawSenha, user.getSenha());

    } else {
        return false;
    }
        
    }
   
}
