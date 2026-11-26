# Carteira Digital

Projeto didático em Java para praticar orientação a objetos por meio de uma carteira digital inspirada em funcionalidades de produtos como o PicPay.

O projeto simula contas, cartões, pagamentos, transferências e PIX entre clientes. A segunda entrega mantém os conceitos de orientação a objetos da primeira e acrescenta persistência local, auto-save, tipos genéricos e uso de collections.

## Como executar

No diretório raiz do projeto, abra o PowerShell e execute:

```powershell
Remove-Item out -Recurse -Force -ErrorAction SilentlyContinue
javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
java -cp out carteira.app.Main
```

A interface gráfica permite:

- escolher uma conta já salva ou criar uma nova;
- escolher conta corrente ou poupança e informar o saldo inicial;
- depositar, sacar, transferir e pagar via PIX;
- cadastrar uma chave PIX única para a conta;
- pagar boleto com código, valor e vencimento;
- realizar o fechamento mensal da conta, aplicando tarifa ou rendimento;
- consultar o extrato;
- resetar a base pela tela inicial;
- salvar automaticamente a cada cinco segundos e ao fechar a janela.

## Segunda entrega: persistência e threads

Os dados da carteira são serializados com a API nativa do Java no arquivo
`data/carteira.dat`. Esse arquivo é criado automaticamente e fica fora do
versionamento. Ao iniciar, `Main` tenta carregá-lo; se ele ainda não existir,
a aplicação começa com uma carteira vazia. Ao terminar, a carteira é salva
novamente.

## Interface gráfica

A aplicação usa Swing, sem dependências externas. A tela inicial lista as
contas persistidas e possui os botões `Entrar na conta`, `Criar conta` e
`Resetar base`. Dentro de uma conta ficam disponíveis as operações básicas, cadastro da chave
PIX, pagamento de boleto, fechamento mensal e extrato. O botão de voltar salva
o estado visual e retorna à lista; as alterações são persistidas pelo auto-save
periódico ou no fechamento da janela.

O `AutoSaveThread` é uma thread daemon que salva a carteira a cada cinco
segundos. A gravação usa um arquivo temporário e troca atômica quando o sistema
operacional suporta esse recurso, evitando deixar o arquivo principal
incompleto em caso de interrupção. Erros de gravação são informados no
`stderr`; salvamentos automáticos concluídos são identificados no console com
o nome `carteira-auto-save`. O salvamento final ao fechar aparece com a
identificação `[ENCERRAMENTO]`. As operações não salvam imediatamente para
deixar o auto-save periódico visível durante os testes.

O roteiro da apresentação da segunda entrega está em [roteiro.md](./roteiro.md).

`ArquivoRepository<T extends Serializable>` é um repositório genérico que
concentra carregar e salvar qualquer objeto serializável. Assim, a mesma
estrutura pode ser reutilizada para outras entidades sem criar um repositório
específico para cada classe. As entidades da carteira implementam
`Serializable`, e as `List` de contas, cartões, clientes e transações continuam
representando as collections do domínio.

## Organização do projeto

```text
src/carteira/
|-- app/            # Ponto de entrada e interface gráfica
|-- controller/     # Casos de uso da carteira
|-- exception/      # Exceções de negócio
|-- persistence/    # Repositório genérico e thread de auto-save
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
- Cadastro da chave PIX da conta.
- Pagamento PIX direcionado à conta associada à chave informada, com
  transferência real entre origem e destino.
- Fechamento mensal manual, com tarifa na conta corrente ou rendimento na
  conta poupança.

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

- Separar melhor as camadas de domínio, aplicação e infraestrutura.
- Criar uma interface de terminal ou uma API.
- Adicionar autenticação e autorização.
- Implementar notificações e histórico filtrável.
