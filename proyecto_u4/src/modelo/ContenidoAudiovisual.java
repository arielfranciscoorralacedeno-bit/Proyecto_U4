package modelo;

import java.util.Objects;

public abstract class ContenidoAudiovisual implements Identifiable, CsvSerializable {

    private final String id;
    private final String titulo;
    private final int anioLanzamiento;
    private final String genero;

    protected ContenidoAudiovisual(String id, String titulo, int anioLanzamiento, String genero) {
        this.id = validarNoVacio(id, "El ID del contenido no puede estar vacío.");
        this.titulo = validarNoVacio(titulo, "El título del contenido no puede estar vacío.");
        if (anioLanzamiento < 1888) {
            throw new IllegalArgumentException("El año de lanzamiento debe ser igual o posterior a 1888.");
        }
        this.anioLanzamiento = anioLanzamiento;
        this.genero = validarNoVacio(genero, "El género del contenido no puede estar vacío.");
    }

    protected static String validarNoVacio(String valor, String mensajeError) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return valor.trim();
    }

    protected static int validarPositivo(int valor, String mensajeError) {
        if (valor <= 0) {
            throw new IllegalArgumentException(mensajeError);
        }
        return valor;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getAnioLanzamiento() {
        return anioLanzamiento;
    }

    public String getGenero() {
        return genero;
    }

    public abstract String getTipoContenido();

    public abstract String resumenDetalle();

    protected abstract String serializarCamposEspecificos();

    @Override
    public String toCsvRow() {
        return String.join(";",
                getTipoContenido(),
                id,
                titulo,
                String.valueOf(anioLanzamiento),
                genero,
                serializarCamposEspecificos());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContenidoAudiovisual)) return false;
        ContenidoAudiovisual that = (ContenidoAudiovisual) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d) | Género: %s | ID: %s -> %s",
                getTipoContenido(), titulo, anioLanzamiento, genero, id, resumenDetalle());
    }
}
