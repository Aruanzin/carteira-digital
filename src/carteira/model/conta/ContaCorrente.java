package carteira.model.conta;

public class ContaCorrente extends Conta {
    private static final double TARIFA_MENSAL = 10.0;
    private final double limiteChequeEspecial;

    public ContaCorrente(String numero, String titular, double saldoInicial, double limite) {
        super(numero, titular, saldoInicial);
        this.limiteChequeEspecial = limite;
    }

    @Override
    public double getSaldoDisponivel() {
        return saldo + limiteChequeEspecial;
    }

    @Override
    public String getTipo() {
        return "Conta Corrente";
    }

    @Override
    public void fechamentoMensal() {
        saldo -= TARIFA_MENSAL;
        registrar("Tarifa mensal de R$ " + TARIFA_MENSAL);
    }
}