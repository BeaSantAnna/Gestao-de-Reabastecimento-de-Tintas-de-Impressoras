package com.projeto.gerenciamento.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.projeto.gerenciamento.entity.*;
import com.projeto.gerenciamento.enums.*;
import com.projeto.gerenciamento.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/administrador")
public class AcessoAdminController {

	@Autowired
	private SolicitacaoService solicitacaoService;

	@Autowired
	private TintaService tintaService;

	@Autowired
	private UnidadeService unidadeService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ModeloService modeloService;

	// Página inicial

	@GetMapping
	public String Home(Model model, Principal principal) {
		String username = principal.getName();
		Usuario usuario = usuarioService.buscarPorUsername(username);

		List<Solicitacao> solicitacoes = solicitacaoService.listarTodas();
		List<Solicitacao> sortedSolicitacoes = solicitacoes.stream()
				.sorted(Comparator.comparing(Solicitacao::getData).reversed()).collect(Collectors.toList());

		model.addAttribute("solicitacoes", sortedSolicitacoes);
		model.addAttribute("tintaService", tintaService);
		model.addAttribute("statusColors", Status.values());
		model.addAttribute("usuarioNome", usuario.getUsername());

		return "acessos/admin/index";
	}

	// Acesso de unidades

	@GetMapping("/unidades")
	public String verPaginaInicial(Model model) {
		model.addAttribute("unidades", unidadeService.listarTodas());
		return "unidades/index";
	}

	@GetMapping("/unidades/novo")
	public String mostrarFormularioCriacao(Model model) {
		model.addAttribute("unidade", new Unidade());
		return "unidades/create";
	}

	@PostMapping("/unidades/salvar")
	public String salvarUnidade(@ModelAttribute("unidade") Unidade unidade, Model model) {
	    if (unidadeService.existeUnidadePorNome(unidade.getNome())) {
	        model.addAttribute("erro", "A unidade já existe. Escolha outro nome.");
	        return "unidades/create";
	    }
	    unidadeService.salvar(unidade);
	    return "redirect:/administrador/unidades";
	}

	@GetMapping("/unidades/editar/{id}")
	public String mostrarFormularioAtualizacao(@PathVariable(value = "id") Long id, Model model) {
	    Unidade unidade = unidadeService.buscarPorId(id);
	    if (unidade == null) {
	        model.addAttribute("erro", "Unidade não encontrada.");
	        return "unidades/error";
	    }
	    model.addAttribute("unidade", unidade);
	    return "unidades/update"; 
	}

	@PostMapping("/unidades/editar/{id}")
	public String atualizarUnidade(@PathVariable Long id, @ModelAttribute Unidade unidade, Model model) {
	    Unidade unidadeExistente = unidadeService.buscarPorId(id);
	    
	    if (unidadeExistente == null) {
	        model.addAttribute("erro", "Unidade não encontrada.");
	        return "unidades/update"; 
	    }

	    if (unidadeService.existeUnidadePorNome(unidade.getNome()) && !unidadeExistente.getNome().equals(unidade.getNome())) {
	        model.addAttribute("erro", "Já existe outra unidade com este nome.");
	        return "unidades/update";
	    }

	    unidadeExistente.setNome(unidade.getNome());
	    unidadeService.salvar(unidadeExistente);
	    return "redirect:/administrador/unidades";
	}


	
	@GetMapping("/unidades/confirmarExclusao/{id}")
	public String confirmarExclusaoUnidade(@PathVariable Long id, Model model) {
		Unidade unidade = unidadeService.buscarPorId(id);
		model.addAttribute("unidade", unidade);
		return "unidades/confirmDelete";
	}

	@GetMapping("/unidades/deletar/{id}")
	public String deletarUnidade(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	    try {
	        unidadeService.deletarPorId(id);
	        redirectAttributes.addFlashAttribute("mensagem", "Unidade excluída com sucesso!");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("mensagem", "Erro ao excluir unidade: " + e.getMessage());
	        return "unidades/confirmDelete";
	    }
	    return "redirect:/administrador/unidades";
	}


	// Acesso de modelos

	@GetMapping("/modelos")
	public String verPaginaInicialModelos(Model model) {
		model.addAttribute("modelos", modeloService.listarModelos());
		return "modelos/index";
	}

	@GetMapping("/modelos/novo")
	public String mostrarFormularioCriacaoModelos(Model model) {
		model.addAttribute("modelo", new Modelo());
		return "modelos/create";
	}

	@PostMapping("/modelos/salvar")
	public String salvarModelo(@ModelAttribute("modelo") Modelo modelo, Model model) {
	    if (modeloService.existeModeloPorNome(modelo.getNome())) {
	        model.addAttribute("erro", "O modelo já existe. Escolha outro nome.");
	        return "modelos/create";
	    }
	    modeloService.salvar(modelo);
	    return "redirect:/administrador/modelos";
	}

	@GetMapping("/modelos/editar/{id}")
	public String mostrarFormularioAtualizacaoModelos(@PathVariable(value = "id") Long id, Model model) {
		Modelo modelo = modeloService.buscarPorId(id);
		model.addAttribute("modelo", modelo);
		return "modelos/update";
	}

	@PostMapping("/modelos/editar/{id}")
	public String atualizarModelo(@PathVariable Long id, @ModelAttribute Modelo modelo, Model model) {
	    Modelo modeloExistente = modeloService.buscarPorId(id);

	    if (modeloExistente == null) {
	        model.addAttribute("erro", "Modelo não encontrado.");
	        return "modelos/update";
	    }

	    if (!modeloExistente.getNome().equals(modelo.getNome()) && modeloService.existeModeloPorNome(modelo.getNome())) {
	        model.addAttribute("erro", "O modelo já existe. Escolha outro nome.");
	        model.addAttribute("modelo", modeloExistente);
	        return "modelos/update";
	    }

	    modeloExistente.setNome(modelo.getNome());
	    modeloService.salvar(modeloExistente);
	    return "redirect:/administrador/modelos";
	}

	@GetMapping("/modelos/confirmarExclusao/{id}")
	public String confirmarExclusaoModelo(@PathVariable Long id, Model model) {
		Modelo modelo = modeloService.buscarPorId(id);
		model.addAttribute("modelo", modelo);
		return "modelos/confirmDelete";
	}

	@GetMapping("/modelos/deletar/{id}")
	public String deletarModelo(@PathVariable Long id) {
		modeloService.deletarPorId(id);
		return "redirect:/administrador/modelos";
	}

	// Acesso de solicitações

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Map.class, "niveisDeTinta", new CustomMapEditor());
	}

	@GetMapping("/solicitacoes")
	public String novaSolicitacao(Model model) {
		model.addAttribute("solicitacao", new Solicitacao());
		model.addAttribute("unidades", unidadeService.listarTodas());
		model.addAttribute("modelos", modeloService.listarModelos());
		model.addAttribute("statusOptions", Status.values());
		return "solicitacoes/admin";
	}

	@PostMapping("/solicitacoes/salvar")
	public String salvarSolicitacao(@ModelAttribute("solicitacao") Solicitacao solicitacao,
	                                 @RequestParam String niveisDeTinta) {
	    try {
	        ObjectMapper objectMapper = new ObjectMapper();
	        Map<String, Integer> niveis = objectMapper.readValue(niveisDeTinta,
	                new TypeReference<Map<String, Integer>>() {});
	        solicitacao.setNiveisDeTinta(niveis);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return "redirect:/erro";
	    }

	    solicitacaoService.salvar(solicitacao);
	    return "redirect:/administrador";
	}

	@GetMapping("/solicitacoes/atualizacao/{id}")
	public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
		Solicitacao solicitacao = solicitacaoService.buscarPorId(id);
		model.addAttribute("solicitacao", solicitacao);
		model.addAttribute("unidades", unidadeService.listarTodas());
		model.addAttribute("modelos", modeloService.listarModelos());
		model.addAttribute("tintaService", tintaService);
		model.addAttribute("statusOptions", Status.values());

		ObjectMapper objectMapper = new ObjectMapper();
		String niveisDeTintaJson = "";
		try {
			niveisDeTintaJson = objectMapper.writeValueAsString(solicitacao.getNiveisDeTinta());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		model.addAttribute("niveisDeTinta", niveisDeTintaJson);

		return "solicitacoes/update";
	}

	@PostMapping("/solicitacoes/atualizar")
	public String updateSolicitacao(@Valid @ModelAttribute("solicitacao") Solicitacao solicitacao,
			@RequestParam String niveisDeTinta, BindingResult result, Model model) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(error -> {
				System.out.println("Erro: " + error.getDefaultMessage());
			});

			model.addAttribute("unidades", unidadeService.listarTodas());
			model.addAttribute("modelos", modeloService.listarModelos());
			model.addAttribute("statusOptions", Status.values());
			model.addAttribute("niveisDeTinta", solicitacao.getNiveisDeTinta());

			return "solicitacoes/update";
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Integer> niveis = objectMapper.readValue(niveisDeTinta,
					new TypeReference<Map<String, Integer>>() {
					});
			solicitacao.setNiveisDeTinta(niveis);
		} catch (IOException e) {
			e.printStackTrace();
			return "redirect:/solicitacoes/update";
		}

		solicitacaoService.salvar(solicitacao);
		return "redirect:/administrador";
	}

	@GetMapping("/solicitacoes/deletar/{id}")
	public String deletarSolicitacao(@PathVariable Long id) {
		solicitacaoService.deletarPorId(id);
		return "redirect:/administrador";
	}

	// Acesso dos relatórios

	@GetMapping("/relatorios")
	public String listarRelatorios(Model model) {
	    List<Unidade> unidades = unidadeService.listarTodas();
	    model.addAttribute("unidades", unidades);
	    model.addAttribute("modelos", modeloService.listarModelos());
	    model.addAttribute("status", Status.values());
	    model.addAttribute("statusSelecionado", new ArrayList<>());
	    return "relatorios/admin";
	}

	@GetMapping("/relatorios/search")
	public String search(
	        @RequestParam(value = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
	        @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
	        @RequestParam(value = "unidadeId", required = false) Long unidadeId,
	        @RequestParam(value = "modelosSelecionados", required = false) List<Long> modelosSelecionados,
	        @RequestParam(value = "statusSelecionado", required = false) List<Status> statusSelecionados,                                                                            
	        Model model) {

	    List<Unidade> unidades = unidadeService.listarTodas();
	    model.addAttribute("unidades", unidades);
	    model.addAttribute("modelos", modeloService.listarModelos());
	    model.addAttribute("status", Status.values());

	    if (dataInicio == null || dataFim == null) {
	        model.addAttribute("mensagem", "Data de início e fim são obrigatórias.");
	        return "relatorios/admin";
	    }

	    List<Solicitacao> solicitacoes = solicitacaoService.findByDataBetween(dataInicio, dataFim);

	    if (statusSelecionados != null && !statusSelecionados.isEmpty()) {
	        solicitacoes = solicitacoes.stream()
	                .filter(solicitacao -> statusSelecionados.contains(solicitacao.getStatus()))
	                .collect(Collectors.toList());
	    }

	    if (unidadeId != null) {
	        solicitacoes = solicitacoes.stream()
	                .filter(solicitacao -> solicitacao.getUnidade() != null 
	                        && solicitacao.getUnidade().getId().equals(unidadeId))
	                .collect(Collectors.toList());
	    }

	    if (modelosSelecionados != null && !modelosSelecionados.isEmpty()) {
	        solicitacoes = solicitacoes.stream()
	                .filter(solicitacao -> solicitacao.getModelo() != null 
	                        && modelosSelecionados.contains(solicitacao.getModelo().getId()))
	                .collect(Collectors.toList());
	    }

	    solicitacoes = solicitacoes.stream()
	            .sorted(Comparator.comparing(Solicitacao::getData).reversed())
	            .collect(Collectors.toList());

	    model.addAttribute("solicitacoes", solicitacoes);
	    model.addAttribute("dataInicio", dataInicio);
	    model.addAttribute("dataFim", dataFim);
	    model.addAttribute("unidadeSelecionada", unidadeId);
	    model.addAttribute("modeloSelecionado", modelosSelecionados);
	    model.addAttribute("statusSelecionado", statusSelecionados);
	    model.addAttribute("tintaService", tintaService);

	    if (solicitacoes.isEmpty()) {
	        model.addAttribute("mensagem", "Não há nenhuma solicitação nesse período.");
	    }
	    
	    Map<String, Long> contagemPorUnidade = solicitacoes.stream()
	            .filter(s -> s.getUnidade() != null)
	            .collect(Collectors.groupingBy(s -> s.getUnidade().getNome(), Collectors.counting()));
	    model.addAttribute("contagemPorUnidade", contagemPorUnidade);


	    return "relatorios/admin";
	}

	@GetMapping("/relatorios/pdf")
	public void generatePdf(
	        @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
	        @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
	        @RequestParam(value = "unidadeId", required = false) Long unidadeId,
	        @RequestParam(value = "modelosSelecionados", required = false) List<Long> modelosSelecionados,
	        @RequestParam(value = "statusSelecionado", required = false) List<String> statusSelecionados, 
	        HttpServletResponse response) throws IOException {

	    List<Solicitacao> solicitacoes = solicitacaoService.findByDataBetween(dataInicio, dataFim)
	            .stream()
	            .filter(solicitacao -> unidadeId == null || (solicitacao.getUnidade() != null && solicitacao.getUnidade().getId().equals(unidadeId)))
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
	    response.setHeader("Content-Disposition", "attachment; filename=relatorios.pdf");

	    PdfWriter writer = new PdfWriter(response.getOutputStream());
	    PdfDocument pdf = new PdfDocument(writer);
	    Document document = new Document(pdf);

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    String dataInicioFormatada = dataInicio.format(formatter);
	    String dataFimFormatada = dataFim.format(formatter);

	    document.add(new Paragraph("Relatório de Solicitações de Tintas").setTextAlignment(TextAlignment.CENTER));
	    document.add(new Paragraph("Data de início: " + dataInicioFormatada));
	    document.add(new Paragraph("Data final: " + dataFimFormatada));

	    Map<String, Long> contagemPorUnidade = solicitacoes.stream()
	            .filter(s -> s.getUnidade() != null)
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
	        table.addCell(solicitacao.getModelo() != null ? solicitacao.getModelo().getNome() : "Não informado");
	        table.addCell(solicitacao.getNiveisDeTintaFormatados() != null ? solicitacao.getNiveisDeTintaFormatados() : "Desconhecido");
	        table.addCell(solicitacao.getFormattedData() != null ? solicitacao.getFormattedData() : "Desconhecida");
	        table.addCell(solicitacao.getStatus() != null ? solicitacao.getStatus().getDescricao() : "Desconhecido");
	    }

	    document.add(table);
	    document.close();
	}

	// Acesso dos usuários

	@GetMapping("/usuarios")
	public String listarUsuarios(Model model) {
		List<Usuario> usuarios = usuarioService.listarTodos();
		model.addAttribute("usuarios", usuarios);
		return "usuarios/index";
	}

	@GetMapping("/usuarios/novo")
	public String mostrarFormularioNovoUsuario(Model model) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("unidades", unidadeService.listarTodas());
		return "usuarios/create";
	}

	@PostMapping("/usuarios/salvar")
	public String salvarUsuario(@ModelAttribute("usuario") Usuario usuario, Model model) {
		if (usuarioService.existeUsuarioPorUsername(usuario.getUsername())) {
			model.addAttribute("erro", "O usuário já existe. Escolha outro nome de usuário.");
			return "usuarios/create";
		}

		usuarioService.salvar(usuario);
		return "redirect:/administrador/usuarios";
	}

	@GetMapping("/usuarios/editar/{id}")
	public String mostrarFormularioEditarUsuario(@PathVariable Long id, Model model) {
		Usuario usuario = usuarioService.buscarPorId(id);
		model.addAttribute("usuario", usuario);
		model.addAttribute("unidades", unidadeService.listarTodas());
		return "usuarios/update";
	}

	@PostMapping("/usuarios/editar/{id}")
	public String editarUsuario(@PathVariable Long id, @ModelAttribute Usuario usuario, Model model) {
	    Usuario usuarioExistente = usuarioService.buscarPorId(id);

	    if (usuarioExistente == null) {
	        model.addAttribute("erro", "Usuário não encontrado.");
	        return "usuarios/update";
	    }

	    if (!usuarioExistente.getUsername().equals(usuario.getUsername()) && usuarioService.existeUsuarioPorUsername(usuario.getUsername())) {
	        model.addAttribute("erro", "O usuário já existe. Escolha outro nome de usuário.");
	        model.addAttribute("usuario", usuarioExistente); 
	        return "usuarios/update";
	    }

	    usuarioExistente.setUsername(usuario.getUsername());
	    usuarioExistente.setRole(usuario.getRole());

	    if (usuario.getUnidade() != null) {
	        usuarioExistente.setUnidade(usuario.getUnidade());
	    }

	    if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
	        usuarioExistente.setPassword(passwordEncoder.encode(usuario.getPassword()));
	    }

	    usuarioService.salvar(usuarioExistente);

	    return "redirect:/administrador/usuarios";
	}

	@GetMapping("/usuarios/deletar/{id}")
	public String deletarUsuario(@PathVariable Long id) {
		usuarioService.deletarPorId(id);
		return "redirect:/administrador/usuarios";
	}

	@GetMapping("/usuarios/confirmarExclusao/{id}")
	public String confirmarExclusao(@PathVariable Long id, Model model) {
		Usuario usuario = usuarioService.buscarPorId(id);
		model.addAttribute("usuario", usuario);
		return "usuarios/confirmDelete";
	}
}
