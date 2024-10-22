package com.projeto.gerenciamento.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TintaService {

	public String getColor(String tinta) {
		switch (tinta) {
		case "Preto":
			return "black";
		case "Amarelo":
			return "yellow";
		case "Magenta":
			return "magenta";
		case "Ciano":
			return "cyan";
		default:
			return "white";
		}
	}
	
	public List<String> getCores() {
	    return Arrays.asList("Preto", "Amarelo", "Magenta", "Ciano");
	}

}