package carteira.exception;

public class LimiteExcedidoException extends CarteiraException {
    public LimiteExcedidoException(String mensagem) {
        super(mensagem);
    }
}