package com.projeto.gerenciamento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.projeto.gerenciamento.entity.Unidade;

@Repository
public interface UnidadeRepository extends JpaRepository<Unidade, Long> {
	boolean existsByNome(String nome);
}
