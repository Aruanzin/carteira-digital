package carteira.model.pagamento;

import carteira.exception.CarteiraException;
import carteira.exception.ValorInvalidoException;
import carteira.model.cartao.CartaoCredito;
import carteira.model.conta.Conta;

public class PagamentoCartao extends MetodoPagamento {
    private final CartaoCredito cartao;
    private final int parcelas;

    public PagamentoCartao(CartaoCredito cartao, int parcelas) {
        super("Cartão de crédito em " + parcelas + "x");
        this.cartao = cartao;
        this.parcelas = parcelas;
    }

    @Override
    public void processar(Conta conta, double valor) throws CarteiraException {
        if (parcelas < 1) {
            throw new ValorInvalidoException("Número de parcelas inválido.");
        }
        cartao.comprar(valor);
    }
}