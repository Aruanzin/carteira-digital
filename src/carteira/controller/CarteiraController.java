package carteira.controller;

import carteira.exception.CarteiraException;
import carteira.exception.CartaoDuplicadoException;
import carteira.exception.ContaDuplicadaException;
import carteira.exception.CadastroInvalidoException;
import carteira.exception.ClienteDuplicadoException;
import carteira.exception.ChavePixInvalidaException;
import carteira.exception.ValorInvalidoException;
import carteira.model.cartao.CartaoCredito;
import carteira.model.cliente.Cliente;
import carteira.model.conta.Conta;
import carteira.model.pagamento.MetodoPagamento;
import carteira.model.transacao.Transacao;
import carteira.persistence.ArquivoRepository;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarteiraController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final ArquivoRepository<CarteiraController> REPOSITORIO = new ArquivoRepository<>();
    private final List<Conta> contas = new ArrayList<>();
    private final List<CartaoCredito> cartoes = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Transacao> transacoes = new ArrayList<>();

    public synchronized void salvar(Path arquivo) throws CarteiraException {
        try {
            REPOSITORIO.salvar(arquivo, this);
        } catch (IOException e) {
            throw new carteira.exception.PersistenciaException(
                    "Não foi possível salvar a carteira em " + arquivo + ".", e);
        }
    }

    public static CarteiraController carregar(Path arquivo) throws CarteiraException {
        try {
            return REPOSITORIO.carregar(arquivo).orElseGet(CarteiraController::new);
        } catch (IOException | ClassNotFoundException e) {
            throw new carteira.exception.PersistenciaException(
                    "Não foi possível carregar a carteira de " + arquivo + ".", e);
        }
    }

    public synchronized void cadastrarCliente(Cliente cliente) throws CarteiraException {
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

    public synchronized void cadastrarConta(Conta conta) throws CarteiraException {
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

    public synchronized void cadastrarCartao(CartaoCredito cartao) throws CarteiraException {
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

    public synchronized void associarConta(Cliente cliente, Conta conta) throws CarteiraException {
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

    public synchronized void associarCartao(Cliente cliente, CartaoCredito cartao) throws CarteiraException {
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

    public synchronized void pagar(Conta conta, MetodoPagamento metodo, double valor) throws CarteiraException {
        conta.pagar(metodo, valor);
    }

    public synchronized void transferir(Conta origem, Conta destino, double valor) throws CarteiraException {
        if (origem == null) {
            throw new ValorInvalidoException("A conta de origem é obrigatória.");
        }
        origem.transferirPara(destino, valor);
    }

    public synchronized void cadastrarChavePix(Conta conta, String chave)
            throws CarteiraException {
        if (conta == null || !contas.contains(conta)) {
            throw new CadastroInvalidoException("A conta precisa estar cadastrada na carteira.");
        }
        boolean chaveEmUso = contas.stream()
                .anyMatch(c -> chave != null && chave.equals(c.getChavePix()));
        if (chaveEmUso && !chave.equals(conta.getChavePix())) {
            throw new ChavePixInvalidaException("Esta chave PIX já está associada a outra conta.");
        }
        conta.registrarChavePix(chave);
    }

    public synchronized void pagarPix(Conta origem, String chave, double valor)
            throws CarteiraException {
        Conta destino = contas.stream()
                .filter(conta -> chave != null && chave.equals(conta.getChavePix()))
                .findFirst()
                .orElseThrow(() -> new ChavePixInvalidaException(
                        "Nenhuma conta encontrada para a chave PIX informada."));
        transferir(origem, destino, valor);
    }

    public synchronized Transacao enviarPix(Cliente remetente, Conta contaOrigem,
                               Cliente destinatario, Conta contaDestino,
                               double valor, String descricao) throws CarteiraException {
        validarClienteCadastrado(remetente);
        validarClienteCadastrado(destinatario);
        validarContaDoCliente(remetente, contaOrigem, "A conta de origem");
        validarContaDoCliente(destinatario, contaDestino, "A conta de destino");

        Transacao transacao = new Transacao(remetente, destinatario, valor, descricao);
        transacoes.add(transacao);
        try {
            contaOrigem.transferirPara(contaDestino, valor);
            transacao.concluir();
            return transacao;
        } catch (CarteiraException e) {
            transacao.recusar();
            throw e;
        }
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

    public List<Transacao> listarTransacoes() {
        return Collections.unmodifiableList(transacoes);
    }

    public synchronized void limpar() {
        contas.clear();
        cartoes.clear();
        clientes.clear();
        transacoes.clear();
    }

    private void validarClienteCadastrado(Cliente cliente) throws CadastroInvalidoException {
        if (cliente == null || !clientes.contains(cliente)) {
            throw new CadastroInvalidoException("O cliente precisa estar cadastrado na carteira.");
        }
    }

    private void validarContaDoCliente(Cliente cliente, Conta conta, String descricao)
            throws CadastroInvalidoException {
        if (conta == null || !cliente.getContas().contains(conta)) {
            throw new CadastroInvalidoException(descricao + " não pertence ao cliente informado.");
        }
    }
}