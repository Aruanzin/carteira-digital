package carteira.app;

import carteira.controller.CarteiraController;
import carteira.exception.CarteiraException;
import carteira.model.cartao.CartaoCredito;
import carteira.model.conta.Conta;
import carteira.model.conta.ContaCorrente;
import carteira.model.conta.ContaPoupanca;
import carteira.model.pagamento.Boleto;
import carteira.model.pagamento.MetodoPagamento;
import carteira.model.pagamento.PagamentoCartao;
import carteira.model.pagamento.Pix;

import java.time.LocalDate;

public class Main {
    private static void tentar(CarteiraController carteira, String titulo, Conta conta,
                               MetodoPagamento metodo, double valor) {
        System.out.println("\n>> " + titulo);
        try {
            carteira.pagar(conta, metodo, valor);
            System.out.printf("  OK. Saldo atual: R$ %.2f%n", conta.getSaldo());
        } catch (CarteiraException e) {
            System.out.println("  ERRO (" + e.getClass().getSimpleName() + "): " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        CarteiraController carteira = new CarteiraController();
        Conta corrente = new ContaCorrente("001", "Ana", 500, 200);
        Conta poupanca = new ContaPoupanca("002", "Ana", 1000);
        CartaoCredito cartao = new CartaoCredito("1234-5678", 1000);
        carteira.cadastrarConta(corrente);
        carteira.cadastrarConta(poupanca);
        carteira.cadastrarCartao(cartao);

        System.out.println("=== 1. Polimorfismo nos pagamentos ===");
        tentar(carteira, "PIX válido", corrente, new Pix("ana@email.com"), 100);
        tentar(carteira, "Boleto em dia", corrente, new Boleto("23790.12345", LocalDate.now().plusDays(5)), 150);
        tentar(carteira, "Cartão 3x", corrente, new PagamentoCartao(cartao, 3), 300);

        System.out.println("\n=== 2. Exceções personalizadas ===");
        tentar(carteira, "PIX com chave vazia", corrente, new Pix(""), 50);
        tentar(carteira, "Boleto vencido", corrente, new Boleto("23790.99999", LocalDate.now().minusDays(1)), 80);
        tentar(carteira, "Valor negativo", corrente, new Pix("12345678901"), -10);
        tentar(carteira, "Sem saldo (nem cheque especial)", corrente, new Pix("12345678901"), 5000);
        tentar(carteira, "Limite do cartão estourado", corrente, new PagamentoCartao(cartao, 1), 900);

        System.out.println("\n=== Transferência entre contas ===");
        try {
            carteira.transferir(corrente, poupanca, 50);
            System.out.printf("  Transferência OK. Corrente: R$ %.2f | Poupança: R$ %.2f%n",
                    corrente.getSaldo(), poupanca.getSaldo());
        } catch (CarteiraException e) {
            System.out.println("  ERRO: " + e.getMessage());
        }

        System.out.println("\n=== Pagamento de fatura ===");
        try {
            cartao.pagarFatura(corrente);
            System.out.printf("  Fatura paga. Saldo: R$ %.2f | Limite disponível: R$ %.2f%n",
                    corrente.getSaldo(), cartao.getLimiteDisponivel());
        } catch (CarteiraException e) {
            System.out.println("  ERRO: " + e.getMessage());
        }

        System.out.println("\n=== 3. Polimorfismo nas contas (fechamento do mês) ===");
        for (Conta conta : carteira.listarContas()) {
            conta.fechamentoMensal();
            System.out.println("  " + conta);
        }

        System.out.println("\n=== Extrato da conta corrente ===");
        corrente.getExtrato().forEach(linha -> System.out.println("  - " + linha));
    }
}