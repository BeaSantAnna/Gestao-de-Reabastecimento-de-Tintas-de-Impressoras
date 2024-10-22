package com.projeto.gerenciamento.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.projeto.gerenciamento.entity.Modelo;
import com.projeto.gerenciamento.entity.Solicitacao;
import com.projeto.gerenciamento.entity.Unidade;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

	@Query("SELECT s FROM Solicitacao s ORDER BY s.data DESC")
	List<Solicitacao> findAllOrderedByData();

	@Query("SELECT s FROM Solicitacao s WHERE s.data BETWEEN :startDate AND :endDate AND s.modelo = :modelo ORDER BY s.data DESC")
	List<Solicitacao> findByDataBetweenAndModelo(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("modelo") Modelo modelo);

    @Query("SELECT s FROM Solicitacao s WHERE MONTH(s.data) = ?1 AND YEAR(s.data) = ?2 AND s.unidade.id = ?3")
    List<Solicitacao> findByMesAndAnoAndUnidade(int mes, int ano, Long unidadeId);

    @Query("SELECT s FROM Solicitacao s WHERE MONTH(s.data) = ?1 AND YEAR(s.data) = ?2")
    List<Solicitacao> findByMesAndAno(int mes, int ano);
    
    @Query("SELECT s FROM Solicitacao s WHERE s.data BETWEEN :startDate AND :endDate AND s.unidade = :unidade ORDER BY s.data DESC")
    List<Solicitacao> findByDataBetweenAndUnidade(LocalDate startDate, LocalDate endDate, Unidade unidade);
	
	List<Solicitacao> findByDataBetween(LocalDate startDate, LocalDate endDate);

    List<Solicitacao> findByUnidade(Unidade unidade);

    void deleteByUnidadeId(Long unidadeId);
}
