package controlador;

import modelo.AudiovisualRepository;
import modelo.ContenidoAudiovisual;
import modelo.DataPersistenceException;
import vista.VentanaPrincipalView;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class AudiovisualController {

    private final AudiovisualRepository repository;
    private final VentanaPrincipalView view;
    private final Path rutaInicial;
    private final List<ContenidoAudiovisual> contenidos;

    public AudiovisualController(AudiovisualRepository repository, VentanaPrincipalView view, Path rutaInicial) {
        this.repository = Objects.requireNonNull(repository, "El repositorio no puede ser nulo.");
        this.view = Objects.requireNonNull(view, "La vista no puede ser nula.");
        this.rutaInicial = rutaInicial;
        this.contenidos = new ArrayList<>();
        conectarEventosVista();
    }

    private void conectarEventosVista() {
        view.setOnRegistrarContenido(new Consumer<ContenidoAudiovisual>() {
            @Override
            public void accept(ContenidoAudiovisual contenido) {
                registrarContenido(contenido);
                view.actualizarTabla(contenidos);
                view.mostrarMensaje("Contenido registrado correctamente.");
            }
        });

        view.setOnCargarCsv(new Consumer<File>() {
            @Override
            public void accept(File archivo) {
                try {
                    cargarContenidos(archivo.toPath());
                    view.actualizarTabla(contenidos);
                    view.mostrarMensaje("Datos cargados desde: " + archivo.getName());
                } catch (DataPersistenceException e) {
                    view.mostrarError(e.getMessage());
                }
            }
        });

        view.setOnGuardarCsv(new Consumer<File>() {
            @Override
            public void accept(File archivo) {
                try {
                    guardarContenidos(archivo.toPath());
                    view.mostrarMensaje("Datos guardados en: " + archivo.getName());
                } catch (DataPersistenceException e) {
                    view.mostrarError(e.getMessage());
                }
            }
        });
    }

    public void iniciar() {
        view.mostrar();
        if (rutaInicial != null) {
            try {
                cargarContenidos(rutaInicial);
                view.actualizarTabla(contenidos);
            } catch (DataPersistenceException e) {
                view.mostrarMensaje("Aviso inicial: " + e.getMessage());
            }
        }
    }

    public void registrarContenido(ContenidoAudiovisual contenido) {
        Objects.requireNonNull(contenido, "El contenido audiovisual no puede ser nulo.");
        contenidos.removeIf(item -> item.getId().equals(contenido.getId()));
        contenidos.add(contenido);
    }

    public void cargarContenidos(Path rutaArchivo) throws DataPersistenceException {
        List<ContenidoAudiovisual> cargados = repository.cargarDesdeArchivo(rutaArchivo);
        contenidos.clear();
        contenidos.addAll(cargados);
    }

    public void guardarContenidos(Path rutaArchivo) throws DataPersistenceException {
        repository.guardarEnArchivo(rutaArchivo, contenidos);
    }

    public List<ContenidoAudiovisual> obtenerContenidos() {
        return Collections.unmodifiableList(contenidos);
    }
}