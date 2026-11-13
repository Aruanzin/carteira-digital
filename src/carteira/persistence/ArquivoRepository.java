package carteira.persistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class ArquivoRepository<T extends Serializable> {
    public void salvar(Path arquivo, T objeto) throws IOException {
        Path pasta = arquivo.toAbsolutePath().getParent();
        if (pasta != null) {
            Files.createDirectories(pasta);
        }

        Path temporario = arquivo.resolveSibling(arquivo.getFileName() + ".tmp");
        try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(temporario))) {
            saida.writeObject(objeto);
        }
        try {
            Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException e) {
            Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<T> carregar(Path arquivo) throws IOException, ClassNotFoundException {
        if (Files.notExists(arquivo)) {
            return Optional.empty();
        }
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(arquivo))) {
            return Optional.of((T) entrada.readObject());
        }
    }
}
