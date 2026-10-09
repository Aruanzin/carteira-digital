package carteira.model.pagamento;

import carteira.exception.ChavePixInvalidaException;
import carteira.exception.CarteiraException;
import carteira.model.conta.Conta;

public class Pix extends MetodoPagamento {
    private final String chave;

    public Pix(String chave) {
        super("PIX para " + chave);
        this.chave = chave;
    }

    private boolean chaveValida() {
        if (chave == null || chave.isBlank()) return false;
        if (chave.matches("\\d{11}")) return true;
        if (chave.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) return true;
        if (chave.matches("\\+?\\d{10,13}")) return true;
        return chave.matches("[0-9a-fA-F-]{32,36}");
    }

    @Override
    public void processar(Conta conta, double valor) throws CarteiraException {
        if (!chaveValida()) {
            throw new ChavePixInvalidaException("Chave PIX inválida: '" + chave + "'");
        }
        conta.sacar(valor);
    }
}