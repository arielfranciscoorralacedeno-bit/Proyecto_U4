package vista;

import modelo.Actor;
import modelo.ContenidoAudiovisual;
import modelo.Documental;
import modelo.Investigador;
import modelo.Pelicula;
import modelo.SerieTV;
import modelo.Temporada;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class ConsolaView {

    private final ConsoleInputReader inputReader;

    public ConsolaView(ConsoleInputReader inputReader) {
        this.inputReader = Objects.requireNonNull(inputReader, "El lector de entrada no puede ser nulo.");
    }

    public void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("=== SISTEMA DE CONTENIDO AUDIOVISUAL (MVC + SOLID) ===");
        System.out.println("1. Listar contenidos audiovisuales");
        System.out.println("2. Registrar película");
        System.out.println("3. Registrar serie de TV");
        System.out.println("4. Registrar documental");
        System.out.println("5. Cargar contenidos desde CSV");
        System.out.println("6. Guardar contenidos en CSV");
        System.out.println("0. Salir");
    }

    public int leerOpcionMenu() {
        return inputReader.leerEntero("Seleccione una opción: ");
    }

    public void mostrarContenidos(List<ContenidoAudiovisual> contenidos) {
        if (contenidos == null || contenidos.isEmpty()) {
            System.out.println("No hay contenidos audiovisuales registrados.");
            return;
        }
        System.out.println();
        System.out.println("--- Catálogo de Contenidos ---");
        for (int i = 0; i < contenidos.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, contenidos.get(i));
        }
    }

    public Pelicula solicitarDatosPelicula() {
        DatosBase base = solicitarDatosBase("película");
        String director = inputReader.leerLinea("Director: ");
        int duracion = inputReader.leerEntero("Duración (minutos): ");

        Pelicula pelicula = new Pelicula(base.id, base.titulo, base.anio, base.genero, director, duracion);
        int cantidadActores = inputReader.leerEntero("Cantidad de actores a agregar: ");
        for (int i = 0; i < Math.max(0, cantidadActores); i++) {
            pelicula.agregarActor(solicitarActor(i + 1));
        }
        return pelicula;
    }

    public SerieTV solicitarDatosSerie() {
        DatosBase base = solicitarDatosBase("serie");
        String creador = inputReader.leerLinea("Creador: ");

        SerieTV serie = new SerieTV(base.id, base.titulo, base.anio, base.genero, creador);
        int cantidadTemporadas = inputReader.leerEntero("Cantidad de temporadas a agregar: ");
        for (int i = 0; i < Math.max(0, cantidadTemporadas); i++) {
            int num = inputReader.leerEntero("  Número de temporada #" + (i + 1) + ": ");
            int eps = inputReader.leerEntero("  Cantidad de episodios: ");
            int anioTemp = inputReader.leerEntero("  Año de estreno de la temporada: ");
            serie.agregarTemporada(new Temporada(num, eps, anioTemp));
        }

        int cantidadActores = inputReader.leerEntero("Cantidad de actores del elenco: ");
        for (int i = 0; i < Math.max(0, cantidadActores); i++) {
            serie.agregarActor(solicitarActor(i + 1));
        }
        return serie;
    }

    public Documental solicitarDatosDocumental() {
        DatosBase base = solicitarDatosBase("documental");
        String tema = inputReader.leerLinea("Tema principal: ");
        int duracion = inputReader.leerEntero("Duración (minutos): ");

        Documental documental = new Documental(base.id, base.titulo, base.anio, base.genero, tema, duracion);
        int cantidadInvestigadores = inputReader.leerEntero("Cantidad de investigadores a agregar: ");
        for (int i = 0; i < Math.max(0, cantidadInvestigadores); i++) {
            documental.agregarInvestigador(solicitarInvestigador(i + 1));
        }
        return documental;
    }

    private DatosBase solicitarDatosBase(String etiqueta) {
        String id = inputReader.leerLinea("ID de la " + etiqueta + ": ");
        String titulo = inputReader.leerLinea("Título: ");
        int anio = inputReader.leerEntero("Año de lanzamiento: ");
        String genero = inputReader.leerLinea("Género: ");
        return new DatosBase(id, titulo, anio, genero);
    }

    private Actor solicitarActor(int indice) {
        String id = inputReader.leerLinea("  ID actor #" + indice + ": ");
        String nombre = inputReader.leerLinea("  Nombre completo: ");
        String nacionalidad = inputReader.leerLinea("  Nacionalidad: ");
        return new Actor(id, nombre, nacionalidad);
    }

    private Investigador solicitarInvestigador(int indice) {
        String id = inputReader.leerLinea("  ID investigador #" + indice + ": ");
        String nombre = inputReader.leerLinea("  Nombre completo: ");
        String especialidad = inputReader.leerLinea("  Especialidad: ");
        return new Investigador(id, nombre, especialidad);
    }

    public Path solicitarRutaArchivo(String accion) {
        String ruta = inputReader.leerLinea("Ingrese la ruta del archivo CSV para " + accion + ": ").trim();
        return Paths.get(ruta);
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarError(String mensajeError) {
        System.err.println("Error: " + mensajeError);
    }

    private static final class DatosBase {
        private final String id;
        private final String titulo;
        private final int anio;
        private final String genero;

        private DatosBase(String id, String titulo, int anio, String genero) {
            this.id = id;
            this.titulo = titulo;
            this.anio = anio;
            this.genero = genero;
        }
    }
}