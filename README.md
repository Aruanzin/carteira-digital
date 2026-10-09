# Carteira Digital

Projeto didático em Java para evoluir uma carteira digital inspirada em produtos como o PicPay. O foco é praticar orientação a objetos com regras de contas, meios de pagamento, cartões e exceções de negócio.

## Como executar

No diretório raiz do projeto:

```powershell
Remove-Item out -Recurse -Force -ErrorAction SilentlyContinue
javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
java -cp out carteira.app.Main
```

O programa de demonstração exercita PIX, boleto, cartão de crédito, exceções, fechamento mensal e extrato.

## Organização atual

```text
src/carteira/
|-- app/          # Ponto de entrada e demonstração
|-- controller/   # Casos de uso de entrada da aplicação
|-- exception/    # Exceções de negócio do sistema
`-- model/
	|-- cartao/   # Cartões de crédito
	|-- conta/    # Conta, corrente e poupança
	`-- pagamento/ # PIX, boleto e pagamento com cartão
```

`Conta` concentra saldo e extrato. `MetodoPagamento` representa uma estratégia de pagamento, permitindo que PIX, boleto e cartão tenham regras próprias. `CarteiraController` mantém os cadastros da carteira e encaminha pagamentos para o domínio.

## Roadmap por etapas

### Etapa 1 - Fundamentos e organização (atual)

- Separar aplicação, controlador, domínio e exceções em pacotes.
- Manter contas corrente e poupança.
- Manter pagamentos via PIX, boleto e cartão de crédito.
- Registrar operações no extrato e aplicar fechamento mensal.
- Transferir valores entre contas cadastradas e consultar o limite disponível do cartão.

### Etapa 2 - Regras de cadastro (regras básicas concluídas)

- Validar titular, número da conta, limite e saldo inicial.
- Impedir contas e cartões duplicados.
- Usar exceções específicas para cadastros inválidos e duplicidades.
- Associar cartões a um cliente, em vez de mantê-los apenas em uma lista global.

### Etapa 3 - Transferências e usuários

- Criar cliente e identificação única. (concluído)
- Associar contas e cartões cadastrados ao cliente. (concluído)
- Implementar PIX entre contas da carteira. A primeira versão já possui transferência interna entre contas.
- Registrar origem, destino, data, valor e status de cada transação.

### Etapa 4 - Cartão de crédito

- Fatura por ciclo e lançamento de parcelas.
- Pagamento da fatura usando uma conta da carteira.
- A primeira versão já permite quitar a fatura integralmente usando uma conta.
- Cartões de diferentes instituições e validações de cartão.

### Etapa 5 - Investimentos

- Criar produtos de investimento e perfil de risco.
- Aplicação, resgate, rendimento e posição do cliente.
- Separar saldo disponível de saldo investido.

### Etapa 6 - Persistência e qualidade

- Substituir listas em memória por repositórios.
- Adicionar testes automatizados para regras e exceções.
- Adicionar uma API ou interface, mantendo o domínio independente da camada de entrada.

## Conceitos de POO praticados

- **Herança:** `Conta` é especializada em `ContaCorrente` e `ContaPoupanca`.
- **Polimorfismo:** `MetodoPagamento` possui implementações para PIX, boleto e cartão.
- **Encapsulamento:** saldo, fatura e extrato são alterados por operações do domínio.
- **Exceções personalizadas:** `CarteiraException` é a base das falhas de negócio.
- **Composição:** o controlador mantém contas e cartões cadastrados.

## Observações

Este é um projeto educacional. Para uma aplicação financeira real, valores monetários devem usar `BigDecimal`, as operações precisam de persistência transacional e autenticação, e as regras devem ser cobertas por testes automatizados.
