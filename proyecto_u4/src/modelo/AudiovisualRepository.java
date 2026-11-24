package modelo;

import java.nio.file.Path;
import java.util.List;

public interface AudiovisualRepository {

    List<ContenidoAudiovisual> cargarDesdeArchivo(Path rutaArchivo) throws DataPersistenceException;

    void guardarEnArchivo(Path rutaArchivo, List<ContenidoAudiovisual> contenidos) throws DataPersistenceException;
}
