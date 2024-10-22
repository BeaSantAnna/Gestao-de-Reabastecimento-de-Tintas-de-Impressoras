package com.projeto.gerenciamento.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.projeto.gerenciamento.entity.Modelo;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {
	boolean existsByNome(String nome);

	List<Modelo> findAll();
}
