package carteira.app;

import carteira.controller.CarteiraController;
import carteira.exception.CarteiraException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.nio.file.Path;

public class Main {
    private static final Path ARQUIVO_DADOS = Path.of("data", "carteira.dat");

    public static void main(String[] args) {
        try {
            CarteiraController carteira = CarteiraController.carregar(ARQUIVO_DADOS);
            SwingUtilities.invokeLater(() ->
                    new CarteiraFrame(carteira, ARQUIVO_DADOS).setVisible(true));
        } catch (CarteiraException erro) {
            JOptionPane.showMessageDialog(null, erro.getMessage(),
                    "Erro ao carregar carteira", JOptionPane.ERROR_MESSAGE);
        }
    }
}
