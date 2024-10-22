package com.projeto.gerenciamento.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.projeto.gerenciamento.entity.*;
import com.projeto.gerenciamento.enums.Status;
import com.projeto.gerenciamento.service.*;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/unidade")
public class AcessoUnidadeController {

	@Autowired
	private SolicitacaoService solicitacaoService;

	@Autowired
	private TintaService tintaService;

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private ModeloService modeloService;

	/// Página inicial

	@GetMapping
	public String unidadeHome(Model model, Principal principal, @RequestParam(required = false) Long id,
			@RequestParam(required = false) String acao) {
		String username = principal.getName();
		Usuario usuario = usuarioService.buscarPorUsername(username);

		if ("solicitarCancelamento".equals(acao) && id != null) {
			Solicitacao solicitacao = solicitacaoService.buscarPorId(id);
			if (solicitacao != null) {
				solicitacao.setStatus(Status.AGUARDANDO_CANCELAMENTO);
				solicitacaoService.salvar(solicitacao);
			}
			return "redirect:/unidade";
		}

		List<Solicitacao> solicitacoes = solicitacaoService.buscarPorUnidade(usuario.getUnidade());
		List<Solicitacao> sortedSolicitacoes = solicitacoes.stream()
				.sorted(Comparator.comparing(Solicitacao::getData).reversed()).collect(Collectors.toList());

		model.addAttribute("solicitacoes", sortedSolicitacoes);
		model.addAttribute("tintaService", tintaService);
		model.addAttribute("statusColors", Status.values());
		model.addAttribute("usuarioNome", usuario.getUsername());

		return "acessos/unidade/index";
	}

	// Acesso das solicitações

	@GetMapping("/nova")
	public String novaSolicitacaoUnidade(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String username = userDetails.getUsername();
		Usuario usuario = usuarioService.buscarPorUsername(username);
		Solicitacao novaSolicitacao = new Solicitacao();

		novaSolicitacao.setUnidade(usuario.getUnidade());
		novaSolicitacao.setStatus(Status.PENDENTE);

		model.addAttribute("solicitacao", novaSolicitacao);
		model.addAttribute("modelos", modeloService.listarModelos());

		return "solicitacoes/unidade";
	}

	@PostMapping("/salvar")
	public String salvarSolicitacaoUnidade(@ModelAttribute Solicitacao solicitacao) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
	    Usuario usuario = usuarioService.buscarPorUsername(username);
	    solicitacao.setUnidade(usuario.getUnidade());

	    if (solicitacao.getModelo() != null && solicitacao.getModelo().getId() != null) {
	        Modelo modeloPersistido = modeloService.buscarPorId(solicitacao.getModelo().getId());
	        if (modeloPersistido != null) {
	            solicitacao.setModelo(modeloPersistido);
	        } else {
	            solicitacao.setModelo(null); 
	        }
	    }

	    solicitacao.setStatus(Status.PENDENTE);
	    solicitacaoService.salvar(solicitacao);

	    return "redirect:/unidade";
	}

	@PostMapping("/solicitarCancelamento/{id}")
	public String solicitarCancelamento(@PathVariable Long id) {
		Solicitacao solicitacao = solicitacaoService.buscarPorId(id);
		if (solicitacao != null) {
			solicitacao.setStatus(Status.AGUARDANDO_CANCELAMENTO);
			solicitacaoService.salvar(solicitacao);
		}
		return "redirect:/unidade";
	}

	// Acesso dos relatórios

	@GetMapping("/relatorios")
	public String listarRelatoriosUnidades(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String username = userDetails.getUsername();
		Usuario usuario = usuarioService.buscarPorUsername(username);
		model.addAttribute("unidade", usuario.getUnidade());
		model.addAttribute("modelos", modeloService.listarModelos());
		model.addAttribute("status", Status.values());
		model.addAttribute("statusSelecionado", new ArrayList<>());
		return "relatorios/unidade";
	}

	@GetMapping("/search")
	public String searchUnidade(
	        @RequestParam(value = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
	        @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
	        @RequestParam(value = "modelosSelecionados", required = false) List<Long> modelosSelecionados,
	        @RequestParam(value = "statusSelecionado", required = false) List<Status> statusSelecionados,
	        @AuthenticationPrincipal UserDetails userDetails, Model model) {

	    // Obter o usuário logado e a unidade associada
	    String username = userDetails.getUsername();
	    Usuario usuario = usuarioService.buscarPorUsername(username);
	    Unidade unidadeUsuario = usuario.getUnidade();

	    model.addAttribute("unidade", unidadeUsuario);
	    model.addAttribute("modelos", modeloService.listarModelos());
	    model.addAttribute("status", Status.values());

	    if (dataInicio == null || dataFim == null) {
	        model.addAttribute("mensagem", "Data de início e fim são obrigatórias.");
	        return "relatorios/unidade";
	    }

	    // Filtrar as solicitações pela unidade do usuário
	    List<Solicitacao> solicitacoes = solicitacaoService.findByDataBetween(dataInicio, dataFim)
	            .stream()
	            .filter(solicitacao -> solicitacao.getUnidade() != null && solicitacao.getUnidade().getId().equals(unidadeUsuario.getId()))
	            .collect(Collectors.toList());

	    if (solicitacoes.isEmpty()) {
	        model.addAttribute("mensagem", "Nenhuma solicitação encontrada.");
	        return "relatorios/unidade";
	    }

	    if (statusSelecionados != null && !statusSelecionados.isEmpty()) {
	        solicitacoes = solicitacoes.stream()
	                .filter(solicitacao -> statusSelecionados.contains(solicitacao.getStatus()))
	                .collect(Collectors.toList());
	    }

	    if (modelosSelecionados != null && !modelosSelecionados.isEmpty()) {
	        solicitacoes = solicitacoes.stream()
	                .filter(solicitacao -> solicitacao.getModelo() != null
	                        && modelosSelecionados.contains(solicitacao.getModelo().getId()))
	                .collect(Collectors.toList());
	    }

	    solicitacoes = solicitacoes.stream().sorted(Comparator.comparing(Solicitacao::getData).reversed())
	            .collect(Collectors.toList());

	    model.addAttribute("solicitacoes", solicitacoes);
	    model.addAttribute("dataInicio", dataInicio);
	    model.addAttribute("dataFim", dataFim);
	    model.addAttribute("modeloSelecionado", modelosSelecionados);
	    model.addAttribute("statusSelecionado", statusSelecionados);
	    model.addAttribute("tintaService", tintaService);
	    model.addAttribute("unidadeSelecionada", unidadeUsuario.getId());

	    if (solicitacoes.isEmpty()) {
	        model.addAttribute("mensagem", "Não há nenhuma solicitação nesse período.");
	    }
	    
	    Map<String, Long> contagemPorUnidade = solicitacoes.stream()
	            .filter(s -> s.getUnidade() != null)
	            .collect(Collectors.groupingBy(s -> s.getUnidade().getNome(), Collectors.counting()));
	    model.addAttribute("contagemPorUnidade", contagemPorUnidade);

	    return "relatorios/unidade";
	}

	@GetMapping("/relatorios/pdf")
	public void generatePdfUnidade(
	        @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
	        @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
	        @RequestParam(value = "modelosSelecionados", required = false) List<Long> modelosSelecionados,
	        @RequestParam(value = "statusSelecionado", required = false) List<String> statusSelecionados,
	        @AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) throws IOException {

	    // Buscar a unidade do usuário logado
	    String username = userDetails.getUsername();
	    Usuario usuario = usuarioService.buscarPorUsername(username);
	    Unidade unidadeUsuario = usuario.getUnidade();

	    List<Solicitacao> solicitacoes = solicitacaoService.findByDataBetween(dataInicio, dataFim)
	            .stream()
	            .filter(solicitacao -> solicitacao.getUnidade() != null && solicitacao.getUnidade().getId().equals(unidadeUsuario.getId()))
	            .filter(solicitacao -> modelosSelecionados == null || modelosSelecionados.isEmpty() || (solicitacao.getModelo() != null && modelosSelecionados.contains(solicitacao.getModelo().getId())))
	            .filter(solicitacao -> statusSelecionados == null || statusSelecionados.isEmpty() || (solicitacao.getStatus() != null && statusSelecionados.contains(solicitacao.getStatus().name())))
	            .sorted(Comparator.comparing(Solicitacao::getData).reversed())
	            .collect(Collectors.toList());

	    if (solicitacoes.isEmpty()) {
	        response.setContentType("text/plain");
	        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	        response.getWriter().write("Não é possível gerar o PDF: não há solicitações para o período especificado.");
	        return;
	    }

	    response.setContentType("application/pdf");
	    response.setHeader("Content-Disposition", "attachment; filename=relatorio.pdf");

	    try (PdfWriter writer = new PdfWriter(response.getOutputStream());
	         PdfDocument pdf = new PdfDocument(writer);
	         Document document = new Document(pdf)) {

	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        String dataInicioFormatada = dataInicio.format(formatter);
	        String dataFimFormatada = dataFim.format(formatter);

	        document.add(new Paragraph("Relatório de Solicitações de Tintas").setTextAlignment(TextAlignment.CENTER));
	        document.add(new Paragraph("Data de início: " + dataInicioFormatada));
	        document.add(new Paragraph("Data final: " + dataFimFormatada));

	        Map<String, Long> contagemPorUnidade = solicitacoes.stream().filter(s -> s.getUnidade() != null)
	                .collect(Collectors.groupingBy(s -> s.getUnidade().getNome(), Collectors.counting()));

	        document.add(new Paragraph("Contagem por Departamento").setTextAlignment(TextAlignment.LEFT));
	        for (Map.Entry<String, Long> entry : contagemPorUnidade.entrySet()) {
	            document.add(new Paragraph(entry.getKey() + ": " + entry.getValue()).setFontSize(12).setMarginBottom(5));
	        }

	        Table table = new Table(5);
	        table.addHeaderCell("Unidade");
	        table.addHeaderCell("Modelo");
	        table.addHeaderCell("Nível de tinta");
	        table.addHeaderCell("Data");
	        table.addHeaderCell("Status");

	        for (Solicitacao solicitacao : solicitacoes) {
	            table.addCell(solicitacao.getUnidade() != null ? solicitacao.getUnidade().getNome() : "Desconhecido");
	            table.addCell(solicitacao.getModelo() != null ? solicitacao.getModelo().getNome() : "Desconhecido");
	            table.addCell(solicitacao.getNiveisDeTintaFormatados() != null ? solicitacao.getNiveisDeTintaFormatados() : "Desconhecido");
	            table.addCell(solicitacao.getFormattedData() != null ? solicitacao.getFormattedData() : "Desconhecida");
	            table.addCell(solicitacao.getStatus() != null ? solicitacao.getStatus().getDescricao() : "Desconhecido");
	        }

	        document.add(table);
	    }
	}
}
