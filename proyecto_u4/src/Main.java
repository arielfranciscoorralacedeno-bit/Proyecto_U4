import controlador.AudiovisualController;
import modelo.AudiovisualRepository;
import modelo.CsvAudiovisualRepository;
import vista.VentanaPrincipalView;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.SwingUtilities;

public class Main {

    public static final Path RUTA_CSV_POR_DEFECTO = Paths.get("data", "contenidos.csv");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AudiovisualRepository repository = new CsvAudiovisualRepository();
                VentanaPrincipalView view = new VentanaPrincipalView();
                AudiovisualController controller = new AudiovisualController(repository, view, RUTA_CSV_POR_DEFECTO);
                controller.iniciar();
            }
        });
    }
}