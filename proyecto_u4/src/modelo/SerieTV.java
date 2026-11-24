package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SerieTV extends ContenidoAudiovisual {

    private final String creador;
    private final List<Temporada> temporadas;
    private final List<Actor> elenco;

    public SerieTV(String id, String titulo, int anioLanzamiento, String genero, String creador) {
        super(id, titulo, anioLanzamiento, genero);
        this.creador = validarNoVacio(creador, "El creador de la serie no puede estar vacío.");
        this.temporadas = new ArrayList<>();
        this.elenco = new ArrayList<>();
    }

    public String getCreador() {
        return creador;
    }

    public void agregarTemporada(Temporada temporada) {
        Objects.requireNonNull(temporada, "La temporada no puede ser nula.");
        if (!temporadas.contains(temporada)) {
            temporadas.add(temporada);
        }
    }

    public void agregarActor(Actor actor) {
        Objects.requireNonNull(actor, "El actor no puede ser nulo.");
        if (!elenco.contains(actor)) {
            elenco.add(actor);
        }
    }

    public List<Temporada> getTemporadas() {
        return Collections.unmodifiableList(temporadas);
    }

    public List<Actor> getElenco() {
        return Collections.unmodifiableList(elenco);
    }

    @Override
    public String getTipoContenido() {
        return "SERIE";
    }

    @Override
    public String resumenDetalle() {
        String actoresStr = elenco.stream()
                .map(Actor::getNombreCompleto)
                .collect(Collectors.joining(", "));
        if (actoresStr.isEmpty()) {
            actoresStr = "Sin elenco registrado";
        }
        return String.format("Creador: %s | Temporadas: %d | Elenco: %s",
                creador, temporadas.size(), actoresStr);
    }

    @Override
    protected String serializarCamposEspecificos() {
        String tempsSerializadas = temporadas.stream()
                .map(Temporada::toCsvRow)
                .collect(Collectors.joining("#"));
        String elencoSerializado = elenco.stream()
                .map(Actor::toCsvRow)
                .collect(Collectors.joining("|"));
        String detalleCombinado = tempsSerializadas + "|" + elencoSerializado;
        return String.join(";", creador, "", detalleCombinado);
    }
}
