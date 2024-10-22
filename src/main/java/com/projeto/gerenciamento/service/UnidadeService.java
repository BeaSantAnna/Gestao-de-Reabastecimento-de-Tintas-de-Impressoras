package com.projeto.gerenciamento.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projeto.gerenciamento.entity.Unidade;
import com.projeto.gerenciamento.repository.UnidadeRepository;

@Service
public class UnidadeService {

	@Autowired
	private UnidadeRepository unidadeRepository;

	@Autowired
	private SolicitacaoService solicitacaoService;

	public List<Unidade> listarTodas() {
		return unidadeRepository.findAll();
	}

	public void salvar(Unidade unidade) {
		unidadeRepository.save(unidade);
	}

	public void salvarAtualizar(Unidade unidade) {
		unidadeRepository.save(unidade);
	}

	public Unidade buscarPorId(Long id) {
		return unidadeRepository.findById(id).orElse(null);
	}

	public void deletarPorId(Long id) {
		solicitacaoService.deletarPorUnidadeId(id);
		unidadeRepository.deleteById(id);
	}

	public boolean existeUnidadePorNome(String nome) {
		return unidadeRepository.existsByNome(nome);
	}
}
