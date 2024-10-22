package com.projeto.gerenciamento.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projeto.gerenciamento.entity.Modelo;
import com.projeto.gerenciamento.repository.ModeloRepository;

@Service
public class ModeloService {
	
	@Autowired
    private ModeloRepository modeloRepository;

	public List<Modelo> listarModelos() {
	    return modeloRepository.findAll();
	}

    public void salvar(Modelo modelo) {
        modeloRepository.save(modelo);
    }

    public Modelo buscarPorId(Long id) {
        return modeloRepository.findById(id).orElse(null); 
    }

    public void deletarPorId(Long id) {
        modeloRepository.deleteById(id);
    }

    public boolean existeModeloPorNome(String nome) {
        return modeloRepository.existsByNome(nome);
    }
}
