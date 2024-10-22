package com.projeto.gerenciamento.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projeto.gerenciamento.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "solicitacao")
public class Solicitacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "unidade_id", nullable = false)
	private Unidade unidade;

	private String tinta;

	private List<String> tintas;

	@ManyToOne
	@JoinColumn(name = "modelo_id", nullable = false)
	private Modelo modelo;

	private Status status;

	@ElementCollection
	@JsonProperty("niveisDeTinta")
	private Map<String, Integer> niveisDeTinta = new HashMap<>();

	private LocalDate data;

	private String dataFormatada;

	public Solicitacao() {
		this.data = LocalDate.now();
	}

	public String getNiveisDeTintaFormatados() {
		return niveisDeTinta.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue() + "%")
				.collect(Collectors.joining(", "));
	}

	public String getFormattedData() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return data != null ? data.format(formatter) : "";
	}

	public String getFormattedDataForInput() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return data != null ? data.format(formatter) : "";
	}

}
