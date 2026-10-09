package carteira.model.pagamento;

import carteira.exception.BoletoVencidoException;
import carteira.exception.CarteiraException;
import carteira.model.conta.Conta;

import java.time.LocalDate;

public class Boleto extends MetodoPagamento {
    private final String codigoBarras;
    private final LocalDate vencimento;

    public Boleto(String codigoBarras, LocalDate vencimento) {
        super("Boleto " + codigoBarras);
        this.codigoBarras = codigoBarras;
        this.vencimento = vencimento;
    }

    @Override
    public void processar(Conta conta, double valor) throws CarteiraException {
        if (LocalDate.now().isAfter(vencimento)) {
            throw new BoletoVencidoException("Boleto vencido em " + vencimento + ". Pagamento recusado.");
        }
        conta.sacar(valor);
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }
}