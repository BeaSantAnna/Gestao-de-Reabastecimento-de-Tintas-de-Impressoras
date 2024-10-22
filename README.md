
# Gestão de Reabastecimento de Tintas de Impressora
Este projeto foi desenvolvido durante meu estágio na Câmara Bragança Paulista, com o objetivo de gerenciar os tanques de tinta das impressoras de toda a corporação, proporcionando um melhor controle e eficiência.

## Funcionalidades
- Cadastro de Usuários, edição, exclusão e visualização (Tanto com administrador quanto por unidade)
- Cadastro de Modelos, edição e exclusão e visualização (Impressoras)
- Cadastro de Unidades, edição, exclusão e visualização (departamentos dentro de uma empresa)
- Fazer solicitações para preencher os tanques das impressoras
- Emitir relatórios com base em datas (inicial e final), unidades, modelos e status
- Gerar relatórios em PDF
  
## Tecnologias Utilizadas

- **Linguagem:** Java.
- **Framework:** Spring Framework.
- **Frontend:** HTML, CSS e JavaScript.
- **Biblioteca:** iText.
- **Banco de dados:** MySQL.
- **Bibliotecas de Ícones:** Font Awesome.

- ## Como Usar

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/Gerenciamento-de-Tintas-de-Impressora.git](https://github.com/BeaSantAnna/Gestao-de-Reabastecimento-de-Tintas-de-Impressoras.git)

2. Altere o propriets:
   ```bash
   # Banco de dados
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.datasource.url=jdbc:mysql://localhost:3306/gerenciamento
spring.datasource.username=root 
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
