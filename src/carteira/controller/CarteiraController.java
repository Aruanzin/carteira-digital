package carteira.controller;

import carteira.exception.CarteiraException;
import carteira.exception.CartaoDuplicadoException;
import carteira.exception.ContaDuplicadaException;
import carteira.exception.CadastroInvalidoException;
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

    public void cadastrarConta(Conta conta) throws CarteiraException {
        if (conta == null) {
            throw new CadastroInvalidoException("A conta é obrigatória.");
        }
        boolean numeroExistente = contas.stream()
                .anyMatch(c -> c.getNumero().equals(conta.getNumero()));
        if (numeroExistente) {
            throw new ContaDuplicadaException("Já existe uma conta com o número " + conta.getNumero() + ".");
        }
        contas.add(conta);
    }

    public void cadastrarCartao(CartaoCredito cartao) throws CarteiraException {
        if (cartao == null) {
            throw new CadastroInvalidoException("O cartão é obrigatório.");
        }
        boolean numeroExistente = cartoes.stream()
                .anyMatch(c -> c.getNumero().equals(cartao.getNumero()));
        if (numeroExistente) {
            throw new CartaoDuplicadoException("Já existe um cartão com o número " + cartao.getNumero() + ".");
        }
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