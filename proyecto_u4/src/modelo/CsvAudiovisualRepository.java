package modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CsvAudiovisualRepository implements AudiovisualRepository {

    private static final String SEPARADOR_CAMPOS = ";";
    private static final String CABECERA_CSV =
            "TIPO;ID;TITULO;ANIO;GENERO;CAMPO_EXTRA_1;CAMPO_EXTRA_2;DETALLE_RELACIONES";

    @Override
    public List<ContenidoAudiovisual> cargarDesdeArchivo(Path rutaArchivo) throws DataPersistenceException {
        Objects.requireNonNull(rutaArchivo, "La ruta del archivo no puede ser nula.");
        if (!Files.exists(rutaArchivo)) {
            throw new DataPersistenceException("No se encontró el archivo CSV: " + rutaArchivo);
        }

        List<ContenidoAudiovisual> contenidos = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(rutaArchivo, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                String limpia = linea.trim();
                if (limpia.isEmpty() || esCabecera(limpia)) {
                    continue;
                }
                contenidos.add(parsearLinea(limpia, numeroLinea));
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Error de E/S al leer el archivo CSV: " + rutaArchivo, e);
        }
        return contenidos;
    }

    @Override
    public void guardarEnArchivo(Path rutaArchivo, List<ContenidoAudiovisual> contenidos)
            throws DataPersistenceException {
        Objects.requireNonNull(rutaArchivo, "La ruta del archivo no puede ser nula.");
        Objects.requireNonNull(contenidos, "La lista de contenidos no puede ser nula.");

        try {
            if (rutaArchivo.getParent() != null) {
                Files.createDirectories(rutaArchivo.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(rutaArchivo, StandardCharsets.UTF_8)) {
                writer.write(CABECERA_CSV);
                writer.newLine();
                for (ContenidoAudiovisual contenido : contenidos) {
                    writer.write(contenido.toCsvRow());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new DataPersistenceException("Error de E/S al guardar el archivo CSV: " + rutaArchivo, e);
        }
    }

    private boolean esCabecera(String linea) {
        return linea.startsWith("TIPO;ID;TITULO");
    }

    private ContenidoAudiovisual parsearLinea(String linea, int numeroLinea) throws DataPersistenceException {
        String[] partes = linea.split(SEPARADOR_CAMPOS, -1);
        if (partes.length < 8) {
            throw new DataPersistenceException(
                    String.format("Formato CSV inválido en línea %d: se esperaban al menos 8 columnas.", numeroLinea));
        }

        String tipo = partes[0].trim().toUpperCase();
        String id = partes[1].trim();
        String titulo = partes[2].trim();
        int anio = parsearEntero(partes[3].trim(), "año de lanzamiento", numeroLinea);
        String genero = partes[4].trim();
        String extra1 = partes[5].trim();
        String extra2 = partes[6].trim();
        String relaciones = partes[7].trim();

        try {
            switch (tipo) {
                case "PELICULA":
                    return construirPelicula(id, titulo, anio, genero, extra1, extra2, relaciones, numeroLinea);
                case "SERIE":
                    return construirSerie(id, titulo, anio, genero, extra1, relaciones, numeroLinea);
                case "DOCUMENTAL":
                    return construirDocumental(id, titulo, anio, genero, extra1, extra2, relaciones, numeroLinea);
                default:
                    throw new DataPersistenceException(
                            String.format("Tipo de contenido no soportado '%s' en línea %d.", tipo, numeroLinea));
            }
        } catch (IllegalArgumentException e) {
            throw new DataPersistenceException(
                    String.format("Datos inválidos en línea %d: %s", numeroLinea, e.getMessage()), e);
        }
    }

    private Pelicula construirPelicula(String id, String titulo, int anio, String genero,
                                      String director, String duracionStr, String relaciones, int numeroLinea)
            throws DataPersistenceException {
        int duracion = parsearEntero(duracionStr, "duración de película", numeroLinea);
        Pelicula pelicula = new Pelicula(id, titulo, anio, genero, director, duracion);
        for (Actor actor : parsearActores(relaciones, numeroLinea)) {
            pelicula.agregarActor(actor);
        }
        return pelicula;
    }

    private SerieTV construirSerie(String id, String titulo, int anio, String genero,
                                   String creador, String relaciones, int numeroLinea)
            throws DataPersistenceException {
        SerieTV serie = new SerieTV(id, titulo, anio, genero, creador);
        if (!relaciones.isEmpty()) {
            String[] bloques = relaciones.split("\\|", -1);
            String bloqueTemporadas = bloques[0].trim();
            String bloqueActores = bloques.length > 1 ? bloques[1].trim() : "";

            for (Temporada temporada : parsearTemporadas(bloqueTemporadas, numeroLinea)) {
                serie.agregarTemporada(temporada);
            }
            for (Actor actor : parsearActores(bloqueActores, numeroLinea)) {
                serie.agregarActor(actor);
            }
        }
        return serie;
    }

    private Documental construirDocumental(String id, String titulo, int anio, String genero,
                                           String tema, String duracionStr, String relaciones, int numeroLinea)
            throws DataPersistenceException {
        int duracion = parsearEntero(duracionStr, "duración de documental", numeroLinea);
        Documental documental = new Documental(id, titulo, anio, genero, tema, duracion);
        for (Investigador investigador : parsearInvestigadores(relaciones, numeroLinea)) {
            documental.agregarInvestigador(investigador);
        }
        return documental;
    }

    private List<Actor> parsearActores(String textoActores, int numeroLinea) throws DataPersistenceException {
        List<Actor> actores = new ArrayList<>();
        if (textoActores == null || textoActores.isEmpty()) {
            return actores;
        }
        for (String item : textoActores.split("\\|")) {
            if (item.trim().isEmpty()) {
                continue;
            }
            String[] datos = item.split(":", -1);
            if (datos.length < 3) {
                throw new DataPersistenceException(
                        String.format("Formato de actor inválido en CSV (línea %d): %s", numeroLinea, item));
            }
            actores.add(new Actor(datos[0].trim(), datos[1].trim(), datos[2].trim()));
        }
        return actores;
    }

    private List<Temporada> parsearTemporadas(String textoTemporadas, int numeroLinea)
            throws DataPersistenceException {
        List<Temporada> temporadas = new ArrayList<>();
        if (textoTemporadas == null || textoTemporadas.isEmpty()) {
            return temporadas;
        }
        for (String item : textoTemporadas.split("#")) {
            if (item.trim().isEmpty()) {
                continue;
            }
            String[] datos = item.split(":", -1);
            if (datos.length < 3) {
                throw new DataPersistenceException(
                        String.format("Formato de temporada inválido en CSV (línea %d): %s", numeroLinea, item));
            }
            int numTemp = parsearEntero(datos[0].trim(), "número de temporada", numeroLinea);
            int episodios = parsearEntero(datos[1].trim(), "cantidad de episodios", numeroLinea);
            int anioEstreno = parsearEntero(datos[2].trim(), "año de estreno", numeroLinea);
            temporadas.add(new Temporada(numTemp, episodios, anioEstreno));
        }
        return temporadas;
    }

    private List<Investigador> parsearInvestigadores(String textoInvestigadores, int numeroLinea)
            throws DataPersistenceException {
        List<Investigador> investigadores = new ArrayList<>();
        if (textoInvestigadores == null || textoInvestigadores.isEmpty()) {
            return investigadores;
        }
        for (String item : textoInvestigadores.split("\\|")) {
            if (item.trim().isEmpty()) {
                continue;
            }
            String[] datos = item.split(":", -1);
            if (datos.length < 3) {
                throw new DataPersistenceException(
                        String.format("Formato de investigador inválido en CSV (línea %d): %s", numeroLinea, item));
            }
            investigadores.add(new Investigador(datos[0].trim(), datos[1].trim(), datos[2].trim()));
        }
        return investigadores;
    }

    private int parsearEntero(String valor, String campo, int linea) throws DataPersistenceException {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new DataPersistenceException(
                    String.format("Valor numérico inválido para '%s' ('%s') en línea %d.", campo, valor, linea), e);
        }
    }
}