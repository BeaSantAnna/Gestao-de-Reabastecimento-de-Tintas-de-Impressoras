package com.projeto.gerenciamento.service;

import com.projeto.gerenciamento.entity.Usuario;
import com.projeto.gerenciamento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Usuario> listarTodos() {
		return usuarioRepository.findAll();
	}

	public Usuario buscarPorUsername(String username) {
		return usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
	}

	public Usuario buscarPorId(Long id) {
		return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
	}

	public Usuario salvar(Usuario usuario) {
		if (usuario.getId() == null || !usuario.getPassword().startsWith("$2a$")) {
			usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		}
		return usuarioRepository.save(usuario);
	}

	public void deletarPorId(Long id) {
		usuarioRepository.deleteById(id);
	}

	public void codificarSenhasExistentes() {
		List<Usuario> usuarios = usuarioRepository.findAll();
		for (Usuario usuario : usuarios) {
			if (!usuario.getPassword().startsWith("$2a$")) {
				usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
				usuarioRepository.save(usuario);
			}
		}
	}

	public boolean existeUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username).isPresent();
    }
}
