package carteira.controller;

import carteira.exception.CarteiraException;
import carteira.exception.ValorInvalidoException;
import carteira.model.cartao.CartaoCredito;
import carteira.model.conta.Conta;
import carteira.model.pagamento.MetodoPagamento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarteiraController {
    private final List<Conta> contas = new ArrayList<>();
    private final List<CartaoCredito> cartoes = new ArrayList<>();

    public void cadastrarConta(Conta conta) {
        contas.add(conta);
    }

    public void cadastrarCartao(CartaoCredito cartao) {
        cartoes.add(cartao);
    }

    public void pagar(Conta conta, MetodoPagamento metodo, double valor) throws CarteiraException {
        conta.pagar(metodo, valor);
    }

    public void transferir(Conta origem, Conta destino, double valor) throws CarteiraException {
        if (origem == null) {
            throw new ValorInvalidoException("A conta de origem é obrigatória.");
        }
        origem.transferirPara(destino, valor);
    }

    public List<Conta> listarContas() {
        return Collections.unmodifiableList(contas);
    }

    public List<CartaoCredito> listarCartoes() {
        return Collections.unmodifiableList(cartoes);
    }
}