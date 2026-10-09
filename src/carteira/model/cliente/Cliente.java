package carteira.model.cliente;

import carteira.exception.CadastroInvalidoException;
import carteira.model.cartao.CartaoCredito;
import carteira.model.conta.Conta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente {
    private final String nome;
    private final String identificacao;
    private final List<Conta> contas = new ArrayList<>();
    private final List<CartaoCredito> cartoes = new ArrayList<>();

    public Cliente(String nome, String identificacao) throws CadastroInvalidoException {
        if (nome == null || nome.isBlank()) {
            throw new CadastroInvalidoException("O nome do cliente é obrigatório.");
        }
        if (identificacao == null || identificacao.isBlank()) {
            throw new CadastroInvalidoException("A identificação do cliente é obrigatória.");
        }
        this.nome = nome;
        this.identificacao = identificacao;
    }

    public void adicionarConta(Conta conta) throws CadastroInvalidoException {
        if (conta == null) {
            throw new CadastroInvalidoException("A conta do cliente é obrigatória.");
        }
        if (contas.contains(conta)) {
            throw new CadastroInvalidoException("A conta já está associada ao cliente.");
        }
        contas.add(conta);
    }

    public void adicionarCartao(CartaoCredito cartao) throws CadastroInvalidoException {
        if (cartao == null) {
            throw new CadastroInvalidoException("O cartão do cliente é obrigatório.");
        }
        if (cartoes.contains(cartao)) {
            throw new CadastroInvalidoException("O cartão já está associado ao cliente.");
        }
        cartoes.add(cartao);
    }

    public String getNome() {
        return nome;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public List<Conta> getContas() {
        return Collections.unmodifiableList(contas);
    }

    public List<CartaoCredito> getCartoes() {
        return Collections.unmodifiableList(cartoes);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %d conta(s), %d cartão(ões)",
                nome, identificacao, contas.size(), cartoes.size());
    }
}
