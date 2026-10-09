package carteira.model.conta;

import carteira.exception.CadastroInvalidoException;

public class ContaPoupanca extends Conta {
    private static final double TAXA_RENDIMENTO = 0.005;

    public ContaPoupanca(String numero, String titular, double saldoInicial)
            throws CadastroInvalidoException {
        super(numero, titular, saldoInicial);
    }

    @Override
    public String getTipo() {
        return "Conta Poupança";
    }

    @Override
    public void fechamentoMensal() {
        double rendimento = saldo * TAXA_RENDIMENTO;
        saldo += rendimento;
        registrar(String.format("Rendimento de R$ %.2f", rendimento));
    }
}