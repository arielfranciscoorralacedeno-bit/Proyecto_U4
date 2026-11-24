package modelo;

import java.util.Objects;

public class Actor implements Identifiable, CsvSerializable {

    private final String id;
    private final String nombreCompleto;
    private final String nacionalidad;

    public Actor(String id, String nombreCompleto, String nacionalidad) {
        this.id = validarNoVacio(id, "El ID del actor no puede estar vacío.");
        this.nombreCompleto = validarNoVacio(nombreCompleto, "El nombre del actor no puede estar vacío.");
        this.nacionalidad = validarNoVacio(nacionalidad, "La nacionalidad del actor no puede estar vacía.");
    }

    private String validarNoVacio(String valor, String mensajeError) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return valor.trim();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    @Override
    public String toCsvRow() {
        return String.join(":", id, nombreCompleto, nacionalidad);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Actor)) return false;
        Actor actor = (Actor) o;
        return Objects.equals(id, actor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) [%s]", nombreCompleto, nacionalidad, id);
    }
}
