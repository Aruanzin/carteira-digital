# Carteira Digital

Projeto didático em Java para praticar orientação a objetos por meio de uma carteira digital inspirada em funcionalidades de produtos como o PicPay.

O projeto simula contas, cartões, pagamentos, transferências e PIX entre clientes. O foco desta primeira entrega é demonstrar classes, herança, polimorfismo, encapsulamento e tratamento de erros por meio de exceções personalizadas.

## Como executar

No diretório raiz do projeto, abra o PowerShell e execute:

```powershell
Remove-Item out -Recurse -Force -ErrorAction SilentlyContinue
javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
java -cp out carteira.app.Main
```

O programa de demonstração exercita:

- cadastro de contas, cartões e clientes;
- rejeição de cadastros duplicados;
- pagamentos via PIX, boleto e cartão de crédito;
- tratamento de valores inválidos, PIX inválido, boleto vencido e saldo insuficiente;
- transferências entre contas;
- PIX entre clientes;
- pagamento integral da fatura;
- fechamento mensal de contas;
- consulta do extrato.

## Organização do projeto

```text
src/carteira/
|-- app/            # Ponto de entrada e demonstração
|-- controller/     # Casos de uso da carteira
|-- exception/      # Exceções de negócio
`-- model/
    |-- cartao/    # Cartões de crédito
    |-- cliente/   # Clientes e seus produtos
    |-- conta/     # Conta, conta corrente e conta poupança
    |-- pagamento/ # PIX, boleto e pagamento com cartão
    `-- transacao/ # Transações e seus status
```

`CarteiraController` coordena os cadastros e as operações da carteira. `Conta` concentra as regras de saldo e extrato. `MetodoPagamento` define uma estratégia comum para PIX, boleto e cartão. `Cliente` agrupa contas e cartões pertencentes ao mesmo usuário. `Transacao` representa um PIX com seus participantes, valor, descrição, data e status.

## Funcionalidades implementadas

### Contas

- Conta abstrata com saldo, saque, depósito, pagamento e extrato.
- `ContaCorrente` com limite de cheque especial e tarifa mensal.
- `ContaPoupanca` com rendimento no fechamento mensal.
- Transferência entre contas com validação de origem, destino e saldo.

### Pagamentos

- PIX com validação de chave.
- Pagamento de boleto com validação de vencimento.
- Compra com cartão de crédito.
- Validação de parcelas.
- Consulta de limite disponível.
- Pagamento integral da fatura usando uma conta.

### Cadastro e clientes

- Cadastro de contas, cartões e clientes.
- Validação de nome, identificação, número, limite e saldo inicial.
- Prevenção de contas, cartões e clientes duplicados.
- Associação de contas e cartões a clientes.
- Impedimento de associação de um mesmo produto a mais de um cliente.

### PIX e transações

- PIX entre clientes cadastrados.
- Escolha explícita da conta de origem e da conta de destino.
- Validação de pertencimento das contas aos clientes.
- Registro de remetente, destinatário, valor, descrição, data e status.
- Status `PENDENTE`, `CONCLUIDA` e `RECUSADA`.

## Conceitos de POO praticados

- **Classes:** representação de contas, clientes, cartões, pagamentos e transações.
- **Herança:** `ContaCorrente` e `ContaPoupanca` especializam `Conta`.
- **Polimorfismo:** cada implementação de `MetodoPagamento` possui sua própria regra de processamento.
- **Abstração:** `Conta` e `MetodoPagamento` definem comportamentos comuns.
- **Encapsulamento:** saldo, fatura, extratos e coleções são protegidos por métodos do domínio.
- **Composição:** clientes possuem contas e cartões; a carteira mantém seus objetos cadastrados.
- **Exceções personalizadas:** erros de negócio são representados por subclasses de `CarteiraException`.

## Diagrama de classe

![Diagrama](./public/Diagrama%20de%20classe.png)

## O que ainda pode ser explorado:

### Cartão de crédito

- Criar lançamentos individuais de compras.
- Implementar parcelamento real.
- Organizar fatura por ciclo e data de vencimento.
- Permitir pagamento parcial da fatura.
- Associar cada cartão diretamente ao seu cliente.

### Funcionalidades inspiradas em carteiras digitais

- QR Code para cobrança.
- Solicitação de dinheiro.
- PIX agendado.
- Recarga de celular.
- Pagamento de contas.
- Cashback.
- Cofrinhos e investimentos.

### Qualidade e testes

- Criar testes automatizados para regras e exceções.
- Cobrir transferências, PIX recusado, duplicidade e pagamento de fatura.
- Substituir `double` por `BigDecimal` para valores monetários.
- Melhorar mensagens e validações de cadastro.

### Evolução da aplicação

- Adicionar persistência por meio de repositórios.
- Separar melhor as camadas de domínio, aplicação e infraestrutura.
- Criar uma interface de terminal ou uma API.
- Adicionar autenticação e autorização.
- Implementar notificações e histórico filtrável.


