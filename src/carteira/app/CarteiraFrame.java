package carteira.app;

import carteira.controller.CarteiraController;
import carteira.exception.CarteiraException;
import carteira.model.cliente.Cliente;
import carteira.model.conta.Conta;
import carteira.model.conta.ContaCorrente;
import carteira.model.conta.ContaPoupanca;
import carteira.model.pagamento.Boleto;
import carteira.persistence.ArquivoRepository;
import carteira.persistence.AutoSaveThread;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.nio.file.Path;

public class CarteiraFrame extends JFrame {
    private static final String TELA_INICIAL = "inicial";
    private static final String TELA_CONTA = "conta";

    private final Path arquivoDados;
    private final CardLayout layout = new CardLayout();
    private final JPanel telas = new JPanel(layout);
    private CarteiraController carteira;
    private Conta contaSelecionada;
    private AutoSaveThread<CarteiraController> autoSave;
    private final JList<Conta> listaContas = new JList<>();
    private final JLabel resumoConta = new JLabel();

    public CarteiraFrame(CarteiraController carteira, Path arquivoDados) {
        super("Carteira Digital");
        this.carteira = carteira;
        this.arquivoDados = arquivoDados;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(650, 430);
        setLocationRelativeTo(null);
        telas.add(criarTelaInicial(), TELA_INICIAL);
        telas.add(criarTelaConta(), TELA_CONTA);
        add(telas);
        iniciarAutoSave();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evento) {
                fechar();
            }
        });
        atualizarListaContas();
    }

    private JPanel criarTelaInicial() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        painel.add(new JLabel("Selecione uma conta para entrar ou crie uma nova:"), BorderLayout.NORTH);
        painel.add(new JScrollPane(listaContas), BorderLayout.CENTER);

        JButton entrar = new JButton("Entrar na conta");
        entrar.addActionListener(evento -> entrarNaConta());
        JButton criar = new JButton("Criar conta");
        criar.addActionListener(evento -> criarConta());
        JButton resetar = new JButton("Resetar base");
        resetar.addActionListener(evento -> resetarBase());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botoes.add(entrar);
        botoes.add(criar);
        botoes.add(resetar);
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarTelaConta() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        painel.add(resumoConta, BorderLayout.NORTH);

        JButton depositar = new JButton("Depositar");
        depositar.addActionListener(evento -> alterarSaldo(true));
        JButton sacar = new JButton("Sacar");
        sacar.addActionListener(evento -> alterarSaldo(false));
        JButton transferir = new JButton("Transferir");
        transferir.addActionListener(evento -> transferir());
        JButton pix = new JButton("Pagar PIX");
        pix.addActionListener(evento -> pagarPix());
        JButton chavePix = new JButton("Cadastrar chave PIX");
        chavePix.addActionListener(evento -> cadastrarChavePix());
        JButton boleto = new JButton("Pagar boleto");
        boleto.addActionListener(evento -> pagarBoleto());
        JButton fechamento = new JButton("Fechar mês");
        fechamento.addActionListener(evento -> fecharMes());
        JButton extrato = new JButton("Ver extrato");
        extrato.addActionListener(evento -> mostrarExtrato());
        JButton voltar = new JButton("Voltar");
        voltar.addActionListener(evento -> {
            salvar();
            atualizarListaContas();
            layout.show(telas, TELA_INICIAL);
        });

        JPanel botoes = new JPanel(new GridLayout(4, 2, 8, 8));
        botoes.add(depositar);
        botoes.add(sacar);
        botoes.add(transferir);
        botoes.add(pix);
        botoes.add(chavePix);
        botoes.add(boleto);
        botoes.add(fechamento);
        botoes.add(extrato);
        painel.add(botoes, BorderLayout.CENTER);
        painel.add(voltar, BorderLayout.SOUTH);
        return painel;
    }

    private void atualizarListaContas() {
        listaContas.setModel(new DefaultComboBoxModel<>(
                carteira.listarContas().toArray(new Conta[0])));
    }

    private void entrarNaConta() {
        contaSelecionada = listaContas.getSelectedValue();
        if (contaSelecionada == null) {
            informar("Selecione uma conta primeiro.");
            return;
        }
        atualizarResumo();
        layout.show(telas, TELA_CONTA);
    }

    private void atualizarResumo() {
        resumoConta.setText("<html><h2>" + contaSelecionada.getTipo() + " "
                + contaSelecionada.getNumero() + "</h2><p>Titular: "
                + contaSelecionada.getTitular() + " | Saldo: R$ "
                + String.format("%.2f", contaSelecionada.getSaldo())
                + " | Chave PIX: " + (contaSelecionada.getChavePix() == null
                ? "não cadastrada" : contaSelecionada.getChavePix()) + "</p></html>");
    }

    private void criarConta() {
        JTextField numero = new JTextField();
        JTextField titular = new JTextField();
        JTextField saldo = new JTextField("0");
        JComboBox<String> tipo = new JComboBox<>(new String[]{"Conta Corrente", "Conta Poupança"});
        JPanel formulario = new JPanel(new GridLayout(0, 2, 5, 5));
        formulario.add(new JLabel("Número:"));
        formulario.add(numero);
        formulario.add(new JLabel("Titular:"));
        formulario.add(titular);
        formulario.add(new JLabel("Tipo:"));
        formulario.add(tipo);
        formulario.add(new JLabel("Saldo inicial:"));
        formulario.add(saldo);

        int opcao = JOptionPane.showConfirmDialog(this, formulario, "Criar conta",
                JOptionPane.OK_CANCEL_OPTION);
        if (opcao != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            double saldoInicial = lerValor(saldo.getText());
            Conta conta = tipo.getSelectedIndex() == 0
                    ? new ContaCorrente(numero.getText(), titular.getText(), saldoInicial, 200)
                    : new ContaPoupanca(numero.getText(), titular.getText(), saldoInicial);
            Cliente cliente = new Cliente(titular.getText(), "cliente-" + numero.getText());
            carteira.cadastrarConta(conta);
            carteira.cadastrarCliente(cliente);
            carteira.associarConta(cliente, conta);
            salvar();
            atualizarListaContas();
            informar("Conta criada com sucesso.");
        } catch (CarteiraException | NumberFormatException erro) {
            informar(erro.getMessage());
        }
    }

    private void alterarSaldo(boolean deposito) {
        String texto = JOptionPane.showInputDialog(this,
                deposito ? "Valor do depósito:" : "Valor do saque:");
        if (texto == null) {
            return;
        }
        try {
            double valor = lerValor(texto);
            if (deposito) {
                contaSelecionada.depositar(valor);
            } else {
                contaSelecionada.sacar(valor);
            }
            salvar();
            atualizarResumo();
        } catch (CarteiraException | NumberFormatException erro) {
            informar(erro.getMessage());
        }
    }

    private void transferir() {
        Conta destino = escolherConta("Escolha a conta de destino:");
        if (destino == null) {
            return;
        }
        String texto = JOptionPane.showInputDialog(this, "Valor da transferência:");
        if (texto == null) {
            return;
        }
        try {
            carteira.transferir(contaSelecionada, destino, lerValor(texto));
            salvar();
            atualizarResumo();
        } catch (CarteiraException | NumberFormatException erro) {
            informar(erro.getMessage());
        }
    }

    private void pagarPix() {
        String chave = JOptionPane.showInputDialog(this, "Chave PIX:");
        if (chave == null) {
            return;
        }
        String texto = JOptionPane.showInputDialog(this, "Valor do PIX:");
        if (texto == null) {
            return;
        }
        try {
            carteira.pagarPix(contaSelecionada, chave, lerValor(texto));
            salvar();
            atualizarResumo();
        } catch (CarteiraException | NumberFormatException erro) {
            informar(erro.getMessage());
        }
    }

    private void cadastrarChavePix() {
        String chave = JOptionPane.showInputDialog(this,
                "Informe a chave PIX (CPF, telefone, e-mail ou chave aleatória):",
                contaSelecionada.getChavePix());
        if (chave == null) {
            return;
        }
        try {
            carteira.cadastrarChavePix(contaSelecionada, chave);
            salvar();
            atualizarResumo();
            informar("Chave PIX cadastrada.");
        } catch (CarteiraException erro) {
            informar(erro.getMessage());
        }
    }

    private void pagarBoleto() {
        JTextField codigo = new JTextField();
        JTextField vencimento = new JTextField("5");
        JTextField valor = new JTextField();
        JPanel formulario = new JPanel(new GridLayout(0, 2, 5, 5));
        formulario.add(new JLabel("Código de barras:"));
        formulario.add(codigo);
        formulario.add(new JLabel("Vencimento em dias:"));
        formulario.add(vencimento);
        formulario.add(new JLabel("Valor:"));
        formulario.add(valor);
        int opcao = JOptionPane.showConfirmDialog(this, formulario, "Pagar boleto",
                JOptionPane.OK_CANCEL_OPTION);
        if (opcao != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            int dias = Integer.parseInt(vencimento.getText().trim());
            carteira.pagar(contaSelecionada,
                    new Boleto(codigo.getText(), LocalDate.now().plusDays(dias)),
                    lerValor(valor.getText()));
            salvar();
            atualizarResumo();
        } catch (CarteiraException | NumberFormatException erro) {
            informar(erro.getMessage());
        }
    }

    private void fecharMes() {
        int opcao = JOptionPane.showConfirmDialog(this,
                "Aplicar tarifa ou rendimento do mês nesta conta?",
                "Fechamento mensal", JOptionPane.YES_NO_OPTION);
        if (opcao != JOptionPane.YES_OPTION) {
            return;
        }
        contaSelecionada.fechamentoMensal();
        salvar();
        atualizarResumo();
        informar("Fechamento mensal realizado.");
    }

    private Conta escolherConta(String mensagem) {
        Conta[] contas = carteira.listarContas().stream()
                .filter(conta -> conta != contaSelecionada)
                .toArray(Conta[]::new);
        if (contas.length == 0) {
            informar("Cadastre outra conta para transferir.");
            return null;
        }
        return (Conta) JOptionPane.showInputDialog(this, mensagem, "Transferência",
                JOptionPane.PLAIN_MESSAGE, null, contas, contas[0]);
    }

    private void mostrarExtrato() {
        String extrato = String.join("\n", contaSelecionada.getExtrato());
        JOptionPane.showMessageDialog(this,
                extrato.isBlank() ? "Nenhuma operação registrada." : extrato,
                "Extrato", JOptionPane.INFORMATION_MESSAGE);
    }

    private double lerValor(String texto) {
        return Double.parseDouble(texto.trim().replace(',', '.'));
    }

    private void resetarBase() {
        int opcao = JOptionPane.showConfirmDialog(this,
                "Isso apagará todas as contas salvas. Continuar?", "Resetar base",
                JOptionPane.YES_NO_OPTION);
        if (opcao != JOptionPane.YES_OPTION) {
            return;
        }
        carteira.limpar();
        salvar();
        atualizarListaContas();
        informar("Base resetada. Nenhuma conta cadastrada.");
    }

    private void iniciarAutoSave() {
        autoSave = new AutoSaveThread<>(new ArquivoRepository<>(), arquivoDados,
                () -> carteira, 5000);
        autoSave.start();
    }

    private void salvar() {
        try {
            carteira.salvar(arquivoDados);
        } catch (CarteiraException erro) {
            informar(erro.getMessage());
        }
    }

    private void fechar() {
        autoSave.encerrar();
        salvar();
        dispose();
    }

    private void informar(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Carteira Digital",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
