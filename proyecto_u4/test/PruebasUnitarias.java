import controlador.AudiovisualController;
import modelo.Actor;
import modelo.AudiovisualRepository;
import modelo.ContenidoAudiovisual;
import modelo.CsvAudiovisualRepository;
import modelo.DataPersistenceException;
import modelo.Documental;
import modelo.Investigador;
import modelo.Pelicula;
import modelo.SerieTV;
import modelo.Temporada;
import vista.VentanaPrincipalView;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PruebasUnitarias {

    public static void main(String[] args) throws Exception {
        probarModelos();
        probarPersistenciaCsv();
        probarControladorSinVentana();
        System.out.println("TODAS LAS PRUEBAS UNITARIAS PASARON CORRECTAMENTE.");
    }

    private static void probarModelos() {
        Pelicula pelicula = new Pelicula("MOV-10", "Interestelar", 2014, "Ciencia Ficcion", "Christopher Nolan", 169);
        pelicula.agregarActor(new Actor("ACT-10", "Matthew McConaughey", "Estadounidense"));
        assertEquals("PELICULA", pelicula.getTipoContenido());
        assertEquals(1, pelicula.getActoresPrincipales().size());

        SerieTV serie = new SerieTV("SER-10", "Breaking Bad", 2008, "Drama", "Vince Gilligan");
        serie.agregarTemporada(new Temporada(1, 7, 2008));
        serie.agregarActor(new Actor("ACT-20", "Bryan Cranston", "Estadounidense"));
        assertEquals(1, serie.getTemporadas().size());
        assertEquals(1, serie.getElenco().size());

        Documental doc = new Documental("DOC-10", "Cosmos", 2014, "Ciencia", "Astronomia", 45);
        doc.agregarInvestigador(new Investigador("INV-10", "Carl Sagan", "Astrofisica"));
        assertEquals(1, doc.getInvestigadores().size());

        assertThrowsIllegalArgument(() -> new Pelicula("", "Titulo", 2020, "Accion", "Director", 90));
        assertThrowsIllegalArgument(() -> new Pelicula("MOV-99", "Titulo", 1800, "Accion", "Director", 90));
    }

    private static void probarPersistenciaCsv() throws Exception {
        Path tempDir = Files.createTempDirectory("u4_test");
        Path archivoCsv = tempDir.resolve("catalogo.csv");
        CsvAudiovisualRepository repo = new CsvAudiovisualRepository();

        Pelicula p = new Pelicula("MOV-1", "Matrix", 1999, "Ciencia Ficcion", "Lana y Lilly Wachowski", 136);
        p.agregarActor(new Actor("ACT-1", "Keanu Reeves", "Canadiense"));
        repo.guardarEnArchivo(archivoCsv, List.of(p));

        List<ContenidoAudiovisual> cargados = repo.cargarDesdeArchivo(archivoCsv);
        assertEquals(1, cargados.size());
        assertEquals("MOV-1", cargados.get(0).getId());

        assertThrowsPersistence(() -> repo.cargarDesdeArchivo(tempDir.resolve("inexistente.csv")));
    }

    private static void probarControladorSinVentana() throws Exception {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return;
        }
        Path tempDir = Files.createTempDirectory("u4_ctrl_test");
        Path archivo = tempDir.resolve("ctrl.csv");
        VentanaPrincipalView view = new VentanaPrincipalView();
        AudiovisualRepository repo = new CsvAudiovisualRepository();
        AudiovisualController ctrl = new AudiovisualController(repo, view, null);

        ctrl.registrarContenido(new Pelicula("MOV-50", "Duna", 2021, "Ciencia Ficcion", "Denis Villeneuve", 155));
        ctrl.guardarContenidos(archivo);

        AudiovisualController ctrl2 = new AudiovisualController(repo, view, null);
        ctrl2.cargarContenidos(archivo);
        assertEquals(1, ctrl2.obtenerContenidos().size());
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Esperado: " + expected + ", pero fue: " + actual);
        }
    }

    private static void assertThrowsIllegalArgument(Runnable action) {
        try {
            action.run();
            throw new AssertionError("Se esperaba IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @FunctionalInterface
    private interface PersistenceAction {
        void run() throws DataPersistenceException;
    }

    private static void assertThrowsPersistence(PersistenceAction action) {
        try {
            action.run();
            throw new AssertionError("Se esperaba DataPersistenceException");
        } catch (DataPersistenceException ignored) {
        }
    }
}