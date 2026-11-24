package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Documental extends ContenidoAudiovisual {

    private final String temaPrincipal;
    private final int duracionMinutos;
    private final List<Investigador> investigadores;

    public Documental(String id, String titulo, int anioLanzamiento, String genero,
                      String temaPrincipal, int duracionMinutos) {
        super(id, titulo, anioLanzamiento, genero);
        this.temaPrincipal = validarNoVacio(temaPrincipal, "El tema principal no puede estar vacío.");
        this.duracionMinutos = validarPositivo(duracionMinutos, "La duración debe ser mayor que cero.");
        this.investigadores = new ArrayList<>();
    }

    public String getTemaPrincipal() {
        return temaPrincipal;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void agregarInvestigador(Investigador investigador) {
        Objects.requireNonNull(investigador, "El investigador no puede ser nulo.");
        if (!investigadores.contains(investigador)) {
            investigadores.add(investigador);
        }
    }

    public List<Investigador> getInvestigadores() {
        return Collections.unmodifiableList(investigadores);
    }

    @Override
    public String getTipoContenido() {
        return "DOCUMENTAL";
    }

    @Override
    public String resumenDetalle() {
        String invStr = investigadores.stream()
                .map(Investigador::getNombreCompleto)
                .collect(Collectors.joining(", "));
        if (invStr.isEmpty()) {
            invStr = "Sin investigadores registrados";
        }
        return String.format("Tema: %s | Duración: %d min | Investigadores: %s",
                temaPrincipal, duracionMinutos, invStr);
    }

    @Override
    protected String serializarCamposEspecificos() {
        String invSerializados = investigadores.stream()
                .map(Investigador::toCsvRow)
                .collect(Collectors.joining("|"));
        return String.join(";", temaPrincipal, String.valueOf(duracionMinutos), invSerializados);
    }
}
