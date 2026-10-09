package carteira.exception;

public class SaldoInsuficienteException extends CarteiraException {
    public SaldoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}