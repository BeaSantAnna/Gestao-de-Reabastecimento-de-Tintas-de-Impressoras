package com.projeto.gerenciamento.entity;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Modelo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nome;
	
	@OneToMany(mappedBy = "modelo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solicitacao> solicitacoes;
	
}
