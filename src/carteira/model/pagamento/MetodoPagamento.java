package carteira.model.pagamento;

import carteira.exception.CarteiraException;
import carteira.model.conta.Conta;

public abstract class MetodoPagamento {
    private final String descricao;

    protected MetodoPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public abstract void processar(Conta conta, double valor) throws CarteiraException;
}