package carteira.model.transacao;

import carteira.exception.CadastroInvalidoException;
import carteira.model.cliente.Cliente;

import java.time.LocalDateTime;

public class Transacao {
    private final Cliente remetente;
    private final Cliente destinatario;
    private final double valor;
    private final String descricao;
    private final LocalDateTime data;
    private StatusTransacao status;

    public Transacao(Cliente remetente, Cliente destinatario, double valor, String descricao)
            throws CadastroInvalidoException {
        if (remetente == null || destinatario == null) {
            throw new CadastroInvalidoException("Remetente e destinatário são obrigatórios.");
        }
        if (valor <= 0) {
            throw new CadastroInvalidoException("O valor da transação deve ser maior que zero.");
        }
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.valor = valor;
        this.descricao = descricao == null || descricao.isBlank() ? "PIX" : descricao;
        this.data = LocalDateTime.now();
        this.status = StatusTransacao.PENDENTE;
    }

    public void concluir() {
        status = StatusTransacao.CONCLUIDA;
    }

    public void recusar() {
        status = StatusTransacao.RECUSADA;
    }

    public Cliente getRemetente() {
        return remetente;
    }

    public Cliente getDestinatario() {
        return destinatario;
    }

    public double getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getData() {
        return data;
    }

    public StatusTransacao getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("PIX de %s para %s | R$ %.2f | %s | %s",
                remetente.getNome(), destinatario.getNome(), valor, status, descricao);
    }
}
