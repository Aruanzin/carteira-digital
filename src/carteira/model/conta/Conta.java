package carteira.model.conta;

import carteira.exception.CarteiraException;
import carteira.exception.SaldoInsuficienteException;
import carteira.exception.ValorInvalidoException;
import carteira.model.pagamento.MetodoPagamento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Conta {
    protected final String numero;
    protected final String titular;
    protected double saldo;
    private final List<String> extrato = new ArrayList<>();

    protected Conta(String numero, String titular, double saldoInicial) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    public void depositar(double valor) throws ValorInvalidoException {
        validarValor(valor);
        saldo += valor;
        registrar("Depósito de R$ " + String.format("%.2f", valor));
    }

    public void sacar(double valor) throws ValorInvalidoException, SaldoInsuficienteException {
        validarValor(valor);
        if (valor > getSaldoDisponivel()) {
            throw new SaldoInsuficienteException(String.format(
                    "Saldo insuficiente. Disponível: R$ %.2f | Pedido: R$ %.2f",
                    getSaldoDisponivel(), valor));
        }
        saldo -= valor;
        registrar("Saque de R$ " + String.format("%.2f", valor));
    }

    public void pagar(MetodoPagamento metodo, double valor) throws CarteiraException {
        validarValor(valor);
        if (metodo == null) {
            throw new ValorInvalidoException("O método de pagamento é obrigatório.");
        }
        metodo.processar(this, valor);
        registrar(metodo.getDescricao() + " de R$ " + String.format("%.2f", valor));
    }

    public void transferirPara(Conta destino, double valor) throws CarteiraException {
        if (destino == null) {
            throw new ValorInvalidoException("A conta de destino é obrigatória.");
        }
        if (destino == this) {
            throw new ValorInvalidoException("A conta de origem deve ser diferente da conta de destino.");
        }
        validarValor(valor);
        sacar(valor);
        try {
            destino.depositar(valor);
        } catch (CarteiraException e) {
            depositar(valor);
            throw e;
        }
        registrar("Transferência enviada para " + destino.getNumero()
                + " de R$ " + String.format("%.2f", valor));
        destino.registrar("Transferência recebida de " + numero
                + " de R$ " + String.format("%.2f", valor));
    }

    protected void validarValor(double valor) throws ValorInvalidoException {
        if (valor <= 0) {
            throw new ValorInvalidoException("O valor deve ser maior que zero.");
        }
    }

    protected void registrar(String linha) {
        extrato.add(linha);
    }

    public double getSaldoDisponivel() {
        return saldo;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getNumero() {
        return numero;
    }

    public String getTitular() {
        return titular;
    }

    public List<String> getExtrato() {
        return Collections.unmodifiableList(extrato);
    }

    public abstract String getTipo();

    public abstract void fechamentoMensal();

    @Override
    public String toString() {
        return String.format("[%s %s] %s | saldo: R$ %.2f", getTipo(), numero, titular, saldo);
    }
}