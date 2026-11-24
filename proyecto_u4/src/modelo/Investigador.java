package modelo;

import java.util.Objects;

public class Investigador implements Identifiable, CsvSerializable {

    private final String id;
    private final String nombreCompleto;
    private final String especialidad;

    public Investigador(String id, String nombreCompleto, String especialidad) {
        this.id = validarNoVacio(id, "El ID del investigador no puede estar vacío.");
        this.nombreCompleto = validarNoVacio(nombreCompleto, "El nombre del investigador no puede estar vacío.");
        this.especialidad = validarNoVacio(especialidad, "La especialidad del investigador no puede estar vacía.");
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

    public String getEspecialidad() {
        return especialidad;
    }

    @Override
    public String toCsvRow() {
        return String.join(":", id, nombreCompleto, especialidad);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Investigador)) return false;
        Investigador that = (Investigador) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s - %s [%s]", nombreCompleto, especialidad, id);
    }
}
