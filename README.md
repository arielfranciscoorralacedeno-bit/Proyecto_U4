# Sistema de Gestión de Catálogo Audiovisual (MVC + POO + CSV)

Aplicación de escritorio en Java con interfaz **Swing** estructurada bajo el patrón **Modelo-Vista-Controlador (MVC)**, principios **SOLID** y persistencia de datos en archivos **CSV**, desarrollada para la Unidad 4 de Programación Orientada a Objetos.

## Arquitectura (MVC)

* **Modelo (`src`):** Clases del dominio (`ContenidoAudiovisual`, `Pelicula`, `SerieTV` / `SerieDeTV`, `Documental`, `Actor`, `Temporada`, `Investigador`), interfaces y repositorio en CSV (`AudiovisualRepository`, `CsvAudiovisualRepository`, `DataPersistenceException`).
* **Vista (`src`):** Interfaz gráfica en Swing (`VentanaPrincipalView`) encargada de mostrar la tabla de contenidos, el detalle del registro seleccionado, las acciones de carga/guardado/registro y los mensajes al usuario.
* **Controlador (`src`):** Coordinación del flujo (`AudiovisualController` y `Main`) entre la vista y el modelo, incluyendo validación, manejo de excepciones y actualización de la interfaz.

## Estructura del Proyecto

* `src/` — Código fuente del modelo, la vista y el controlador.
* `test/` — Pruebas unitarias (`PruebasUnitarias.java`).
* `data/contenidos.csv` — Archivo CSV con el catálogo inicial y los datos persistidos.
* `bin/` — Clases compiladas (generadas por el entorno/IDE).
* `.project`, `.classpath`, `.gitignore` — Archivos de configuración del proyecto y control de versiones.

## Ejecución y Uso

1. Abre el proyecto en tu IDE o sitúate en la **carpeta raíz del proyecto** (`hoy`).
2. Ejecuta la clase principal (`Main`) para iniciar la ventana Swing.
3. Al abrirse, se cargan los contenidos iniciales desde `data/contenidos.csv`.
4. Usa los controles de la interfaz para registrar nuevos contenidos, inspeccionar detalles y cargar o guardar cambios en CSV.

## Pruebas Unitarias

Ejecuta las pruebas ubicadas en `test/` (`PruebasUnitarias.java`) para verificar el comportamiento del dominio, la persistencia en CSV y la coordinación del controlador.
