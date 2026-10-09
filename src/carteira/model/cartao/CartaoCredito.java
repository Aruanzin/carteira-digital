package carteira.model.cartao;

import carteira.exception.LimiteExcedidoException;
import carteira.exception.CadastroInvalidoException;
import carteira.exception.SaldoInsuficienteException;
import carteira.exception.ValorInvalidoException;
import carteira.model.conta.Conta;

public class CartaoCredito {
    private final String numero;
    private final double limite;
    private double fatura;

    public CartaoCredito(String numero, double limite) throws CadastroInvalidoException {
        if (numero == null || numero.isBlank()) {
            throw new CadastroInvalidoException("O número do cartão é obrigatório.");
        }
        if (limite <= 0) {
            throw new CadastroInvalidoException("O limite do cartão deve ser maior que zero.");
        }
        this.numero = numero;
        this.limite = limite;
    }

    public void comprar(double valor) throws LimiteExcedidoException, ValorInvalidoException {
        validarValor(valor);
        if (fatura + valor > limite) {
            throw new LimiteExcedidoException(String.format(
                    "Limite excedido. Disponível: R$ %.2f | Compra: R$ %.2f", limite - fatura, valor));
        }
        fatura += valor;
    }

    public void pagarFatura(Conta conta) throws ValorInvalidoException, SaldoInsuficienteException {
        if (fatura == 0) {
            throw new ValorInvalidoException("A fatura já está paga.");
        }
        conta.sacar(fatura);
        fatura = 0;
    }

    public double getLimiteDisponivel() {
        return limite - fatura;
    }

    public double getFatura() {
        return fatura;
    }

    public String getNumero() {
        return numero;
    }

    private void validarValor(double valor) throws ValorInvalidoException {
        if (valor <= 0) {
            throw new ValorInvalidoException("O valor da compra deve ser maior que zero.");
        }
    }
}