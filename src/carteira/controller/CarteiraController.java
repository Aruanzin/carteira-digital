package carteira.controller;

import carteira.exception.CarteiraException;
import carteira.exception.CartaoDuplicadoException;
import carteira.exception.ContaDuplicadaException;
import carteira.exception.CadastroInvalidoException;
import carteira.exception.ClienteDuplicadoException;
import carteira.exception.ValorInvalidoException;
import carteira.model.cartao.CartaoCredito;
import carteira.model.cliente.Cliente;
import carteira.model.conta.Conta;
import carteira.model.pagamento.MetodoPagamento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarteiraController {
    private final List<Conta> contas = new ArrayList<>();
    private final List<CartaoCredito> cartoes = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();

    public void cadastrarCliente(Cliente cliente) throws CarteiraException {
        if (cliente == null) {
            throw new CadastroInvalidoException("O cliente é obrigatório.");
        }
        boolean identificacaoExistente = clientes.stream()
                .anyMatch(c -> c.getIdentificacao().equals(cliente.getIdentificacao()));
        if (identificacaoExistente) {
            throw new ClienteDuplicadoException(
                    "Já existe um cliente com a identificação " + cliente.getIdentificacao() + ".");
        }
        clientes.add(cliente);
    }

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

    public void associarConta(Cliente cliente, Conta conta) throws CarteiraException {
        validarClienteCadastrado(cliente);
        if (!contas.contains(conta)) {
            throw new CadastroInvalidoException("A conta precisa estar cadastrada na carteira.");
        }
        boolean contaAssociada = clientes.stream()
                .anyMatch(c -> c.getContas().contains(conta));
        if (contaAssociada) {
            throw new CadastroInvalidoException("A conta já está associada a um cliente.");
        }
        cliente.adicionarConta(conta);
    }

    public void associarCartao(Cliente cliente, CartaoCredito cartao) throws CarteiraException {
        validarClienteCadastrado(cliente);
        if (!cartoes.contains(cartao)) {
            throw new CadastroInvalidoException("O cartão precisa estar cadastrado na carteira.");
        }
        boolean cartaoAssociado = clientes.stream()
                .anyMatch(c -> c.getCartoes().contains(cartao));
        if (cartaoAssociado) {
            throw new CadastroInvalidoException("O cartão já está associado a um cliente.");
        }
        cliente.adicionarCartao(cartao);
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

    public List<Cliente> listarClientes() {
        return Collections.unmodifiableList(clientes);
    }

    private void validarClienteCadastrado(Cliente cliente) throws CadastroInvalidoException {
        if (cliente == null || !clientes.contains(cliente)) {
            throw new CadastroInvalidoException("O cliente precisa estar cadastrado na carteira.");
        }
    }
}