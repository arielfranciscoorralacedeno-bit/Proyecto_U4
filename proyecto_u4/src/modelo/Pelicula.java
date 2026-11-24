package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Pelicula extends ContenidoAudiovisual {

    private final String director;
    private final int duracionMinutos;
    private final List<Actor> actoresPrincipales;

    public Pelicula(String id, String titulo, int anioLanzamiento, String genero,
                    String director, int duracionMinutos) {
        super(id, titulo, anioLanzamiento, genero);
        this.director = validarNoVacio(director, "El director no puede estar vacío.");
        this.duracionMinutos = validarPositivo(duracionMinutos, "La duración debe ser mayor que cero.");
        this.actoresPrincipales = new ArrayList<>();
    }

    public String getDirector() {
        return director;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void agregarActor(Actor actor) {
        Objects.requireNonNull(actor, "El actor no puede ser nulo.");
        if (!actoresPrincipales.contains(actor)) {
            actoresPrincipales.add(actor);
        }
    }

    public List<Actor> getActoresPrincipales() {
        return Collections.unmodifiableList(actoresPrincipales);
    }

    @Override
    public String getTipoContenido() {
        return "PELICULA";
    }

    @Override
    public String resumenDetalle() {
        String nombresActores = actoresPrincipales.stream()
                .map(Actor::getNombreCompleto)
                .collect(Collectors.joining(", "));
        if (nombresActores.isEmpty()) {
            nombresActores = "Sin actores registrados";
        }
        return String.format("Dir: %s | Duración: %d min | Actores: %s",
                director, duracionMinutos, nombresActores);
    }

    @Override
    protected String serializarCamposEspecificos() {
        String actoresSerializados = actoresPrincipales.stream()
                .map(Actor::toCsvRow)
                .collect(Collectors.joining("|"));
        return String.join(";", director, String.valueOf(duracionMinutos), actoresSerializados);
    }
}
