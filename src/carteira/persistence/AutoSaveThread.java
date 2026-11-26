package carteira.persistence;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.function.Supplier;

public class AutoSaveThread<T extends Serializable> extends Thread {
    private final ArquivoRepository<T> repositorio;
    private final Path arquivo;
    private final Supplier<T> fonte;
    private final long intervaloMillis;
    private volatile boolean executando = true;

    public AutoSaveThread(ArquivoRepository<T> repositorio, Path arquivo,
                          Supplier<T> fonte, long intervaloMillis) {
        super("carteira-auto-save");
        this.repositorio = repositorio;
        this.arquivo = arquivo;
        this.fonte = fonte;
        this.intervaloMillis = intervaloMillis;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (executando) {
            try {
                Thread.sleep(intervaloMillis);
                if (executando) {
                    T objeto = fonte.get();
                    synchronized (objeto) {
                        repositorio.salvar(arquivo, objeto);
                    }
                    System.out.printf("[%s] Auto-save concluído às %s: %s%n",
                            getName(), LocalTime.now().withNano(0), arquivo);
                }
            } catch (InterruptedException e) {
                if (executando) {
                    System.err.println("Auto-save interrompido: " + e.getMessage());
                }
                Thread.currentThread().interrupt();
                return;
            } catch (IOException e) {
                System.err.println("Falha no auto-save: " + e.getMessage());
            }
        }
    }

    public void encerrar() {
        executando = false;
        interrupt();
    }
}
