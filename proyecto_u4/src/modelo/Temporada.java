package modelo;

import java.util.Objects;

public class Temporada implements CsvSerializable {

    private final int numeroTemporada;
    private final int cantidadEpisodios;
    private final int anioEstreno;

    public Temporada(int numeroTemporada, int cantidadEpisodios, int anioEstreno) {
        if (numeroTemporada <= 0) {
            throw new IllegalArgumentException("El número de temporada debe ser mayor que cero.");
        }
        if (cantidadEpisodios <= 0) {
            throw new IllegalArgumentException("La cantidad de episodios debe ser mayor que cero.");
        }
        if (anioEstreno < 1888) {
            throw new IllegalArgumentException("El año de estreno no es válido.");
        }
        this.numeroTemporada = numeroTemporada;
        this.cantidadEpisodios = cantidadEpisodios;
        this.anioEstreno = anioEstreno;
    }

    public int getNumeroTemporada() {
        return numeroTemporada;
    }

    public int getCantidadEpisodios() {
        return cantidadEpisodios;
    }

    public int getAnioEstreno() {
        return anioEstreno;
    }

    @Override
    public String toCsvRow() {
        return String.join(":",
                String.valueOf(numeroTemporada),
                String.valueOf(cantidadEpisodios),
                String.valueOf(anioEstreno));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Temporada)) return false;
        Temporada temporada = (Temporada) o;
        return numeroTemporada == temporada.numeroTemporada && anioEstreno == temporada.anioEstreno;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroTemporada, anioEstreno);
    }

    @Override
    public String toString() {
        return String.format("Temporada %d (%d eps, %d)", numeroTemporada, cantidadEpisodios, anioEstreno);
    }
}
