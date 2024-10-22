package com.projeto.gerenciamento.enums;

public enum Status {
    PENDENTE("Pendente", "yellow"), 
    CONCLUIDO("Concluído", "green"),
    CANCELADO("Cancelado", "red"),
    AGUARDANDO_CANCELAMENTO("Aguardando cancelamento", "orange"); 

    private final String descricao;
    private final String cor;

    Status(String descricao, String cor) {
        this.descricao = descricao;
        this.cor = cor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCor() {
        return cor;
    }
}
