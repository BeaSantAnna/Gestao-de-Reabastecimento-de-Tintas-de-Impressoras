package com.projeto.gerenciamento.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projeto.gerenciamento.entity.*;
import com.projeto.gerenciamento.repository.*;

@Service
public class SolicitacaoService {

	@Autowired
	private SolicitacaoRepository solicitacaoRepository;

	@Autowired
	private UnidadeRepository unidadeRepository;

	public List<Solicitacao> listarTodas() {
		return solicitacaoRepository.findAllOrderedByData();
	}

	public void salvar(Solicitacao solicitacao) {
		solicitacaoRepository.save(solicitacao);
	}

	public List<Unidade> listarUnidades() {
		return unidadeRepository.findAll();
	}

	public List<Solicitacao> buscarPorUnidade(Unidade unidade) {
		return solicitacaoRepository.findByUnidade(unidade);
	}

	public Solicitacao buscarPorId(Long id) {
		return solicitacaoRepository.findById(id).orElse(null);
	}

	public void deletarPorId(Long id) {
		solicitacaoRepository.deleteById(id);
	}

	public List<Solicitacao> findByDataBetween(LocalDate dataInicio, LocalDate dataFim) {
		return solicitacaoRepository.findByDataBetween(dataInicio, dataFim);
	}

	public void deletarPorUnidadeId(Long unidadeId) {
        solicitacaoRepository.deleteByUnidadeId(unidadeId);
    }


	public Set<Integer> obterAnosDisponiveis() {
		return solicitacaoRepository.findAll().stream().map(solicitacao -> solicitacao.getData().getYear())
				.collect(Collectors.toSet());
	}
}
