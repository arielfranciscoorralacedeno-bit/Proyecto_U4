package vista;

import modelo.Actor;
import modelo.ContenidoAudiovisual;
import modelo.Documental;
import modelo.Investigador;
import modelo.Pelicula;
import modelo.SerieTV;
import modelo.Temporada;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class VentanaPrincipalView {

    private final JFrame frame;
    private final DefaultTableModel tableModel;
    private final JTable tablaContenidos;
    private final JTextArea areaDetalles;
    private List<ContenidoAudiovisual> contenidosActuales;

    private Consumer<ContenidoAudiovisual> onRegistrarContenido;
    private Consumer<File> onCargarCsv;
    private Consumer<File> onGuardarCsv;

    public VentanaPrincipalView() {
        this.contenidosActuales = new ArrayList<>();
        this.frame = new JFrame("Sistema de Contenido Audiovisual - Unidad 4 (MVC + SOLID)");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(980, 600);
        this.frame.setLocationRelativeTo(null);
        this.frame.setLayout(new BorderLayout(10, 10));

        String[] columnas = {"Tipo", "ID", "Título", "Año", "Género", "Resumen"};
        this.tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.tablaContenidos = new JTable(tableModel);
        this.tablaContenidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.areaDetalles = new JTextArea(6, 50);
        this.areaDetalles.setEditable(false);
        this.areaDetalles.setLineWrap(true);
        this.areaDetalles.setWrapStyleWord(true);
        this.areaDetalles.setBorder(BorderFactory.createTitledBorder("Detalle del Elemento Seleccionado"));

        configurarEventosSeleccion();
        frame.add(construirPanelSuperior(), BorderLayout.NORTH);
        frame.add(construirPanelCentral(), BorderLayout.CENTER);
        frame.add(construirPanelInferior(), BorderLayout.SOUTH);
    }

    public void setOnRegistrarContenido(Consumer<ContenidoAudiovisual> listener) {
        this.onRegistrarContenido = listener;
    }

    public void setOnCargarCsv(Consumer<File> listener) {
        this.onCargarCsv = listener;
    }

    public void setOnGuardarCsv(Consumer<File> listener) {
        this.onGuardarCsv = listener;
    }

    public void mostrar() {
        frame.setVisible(true);
    }

    public void actualizarTabla(List<ContenidoAudiovisual> contenidos) {
        this.contenidosActuales = contenidos != null ? new ArrayList<>(contenidos) : new ArrayList<>();
        tableModel.setRowCount(0);
        for (ContenidoAudiovisual item : this.contenidosActuales) {
            tableModel.addRow(new Object[]{
                item.getTipoContenido(),
                item.getId(),
                item.getTitulo(),
                item.getAnioLanzamiento(),
                item.getGenero(),
                item.resumenDetalle()
            });
        }
        areaDetalles.setText("");
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(frame, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String error) {
        JOptionPane.showMessageDialog(frame, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel construirPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Acciones del Catálogo"));

        JButton btnNuevo = new JButton("Registrar Contenido");
        JButton btnCargar = new JButton("Cargar CSV");
        JButton btnGuardar = new JButton("Guardar CSV");

        btnNuevo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirDialogoRegistro();
            }
        });

        btnCargar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionarArchivo(true);
            }
        });

        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionarArchivo(false);
            }
        });

        panel.add(btnNuevo);
        panel.add(btnCargar);
        panel.add(btnGuardar);
        return panel;
    }

    private JScrollPane construirPanelCentral() {
        JScrollPane scrollPane = new JScrollPane(tablaContenidos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Contenidos Audiovisuales"));
        return scrollPane;
    }

    private JPanel construirPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 140));
        panel.add(new JScrollPane(areaDetalles), BorderLayout.CENTER);
        return panel;
    }

    private void configurarEventosSeleccion() {
        tablaContenidos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int fila = tablaContenidos.getSelectedRow();
                    if (fila >= 0 && fila < contenidosActuales.size()) {
                        areaDetalles.setText(contenidosActuales.get(fila).toString());
                    }
                }
            }
        });
    }

    private void seleccionarArchivo(boolean abrirParaLeer) {
        JFileChooser chooser = new JFileChooser();
        int resultado = abrirParaLeer ? chooser.showOpenDialog(frame) : chooser.showSaveDialog(frame);
        if (resultado == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
            File archivo = chooser.getSelectedFile();
            if (abrirParaLeer && onCargarCsv != null) {
                onCargarCsv.accept(archivo);
            } else if (!abrirParaLeer && onGuardarCsv != null) {
                onGuardarCsv.accept(archivo);
            }
        }
    }

    private void abrirDialogoRegistro() {
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"PELICULA", "SERIE", "DOCUMENTAL"});
        JTextField txtId = new JTextField();
        JTextField txtTitulo = new JTextField();
        JTextField txtAnio = new JTextField("2024");
        JTextField txtGenero = new JTextField();
        JTextField txtCampo1 = new JTextField();
        JTextField txtCampo2 = new JTextField("120");
        JTextField txtRelacion = new JTextField();

        JLabel lblCampo1 = new JLabel("Director:");
        JLabel lblCampo2 = new JLabel("Duración (min):");
        JLabel lblRelacion = new JLabel("Actor (id:nombre:nac):");

        comboTipo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipo = (String) comboTipo.getSelectedItem();
                if ("PELICULA".equals(tipo)) {
                    lblCampo1.setText("Director:");
                    lblCampo2.setText("Duración (min):");
                    lblCampo2.setVisible(true);
                    txtCampo2.setVisible(true);
                    lblRelacion.setText("Actor (id:nombre:nac):");
                } else if ("SERIE".equals(tipo)) {
                    lblCampo1.setText("Creador:");
                    lblCampo2.setVisible(false);
                    txtCampo2.setVisible(false);
                    lblRelacion.setText("Temporada (num:eps:anio):");
                } else {
                    lblCampo1.setText("Tema principal:");
                    lblCampo2.setText("Duración (min):");
                    lblCampo2.setVisible(true);
                    txtCampo2.setVisible(true);
                    lblRelacion.setText("Investigador (id:nombre:esp):");
                }
            }
        });

        JPanel formulario = new JPanel(new GridLayout(0, 2, 6, 6));
        formulario.add(new JLabel("Tipo de contenido:"));
        formulario.add(comboTipo);
        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Título:"));
        formulario.add(txtTitulo);
        formulario.add(new JLabel("Año lanzamiento:"));
        formulario.add(txtAnio);
        formulario.add(new JLabel("Género:"));
        formulario.add(txtGenero);
        formulario.add(lblCampo1);
        formulario.add(txtCampo1);
        formulario.add(lblCampo2);
        formulario.add(txtCampo2);
        formulario.add(lblRelacion);
        formulario.add(txtRelacion);

        int opcion = JOptionPane.showConfirmDialog(
                frame, formulario, "Registrar Nuevo Contenido", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                ContenidoAudiovisual nuevo = construirDesdeFormulario(
                        (String) comboTipo.getSelectedItem(),
                        txtId.getText(),
                        txtTitulo.getText(),
                        txtAnio.getText(),
                        txtGenero.getText(),
                        txtCampo1.getText(),
                        txtCampo2.getText(),
                        txtRelacion.getText());
                if (onRegistrarContenido != null) {
                    onRegistrarContenido.accept(nuevo);
                }
            } catch (IllegalArgumentException ex) {
                mostrarError(ex.getMessage());
            }
        }
    }

    private ContenidoAudiovisual construirDesdeFormulario(
            String tipo, String id, String titulo, String anioStr, String genero,
            String campo1, String campo2Str, String relacion) {
        int anio = Integer.parseInt(anioStr.trim());
        if ("PELICULA".equals(tipo)) {
            int duracion = Integer.parseInt(campo2Str.trim());
            Pelicula pelicula = new Pelicula(id, titulo, anio, genero, campo1, duracion);
            if (!relacion.trim().isEmpty()) {
                String[] d = relacion.trim().split(":");
                if (d.length >= 3) {
                    pelicula.agregarActor(new Actor(d[0].trim(), d[1].trim(), d[2].trim()));
                }
            }
            return pelicula;
        }
        if ("SERIE".equals(tipo)) {
            SerieTV serie = new SerieTV(id, titulo, anio, genero, campo1);
            if (!relacion.trim().isEmpty()) {
                String[] d = relacion.trim().split(":");
                if (d.length >= 3) {
                    serie.agregarTemporada(new Temporada(
                            Integer.parseInt(d[0].trim()),
                            Integer.parseInt(d[1].trim()),
                            Integer.parseInt(d[2].trim())));
                }
            }
            return serie;
        }
        int duracion = Integer.parseInt(campo2Str.trim());
        Documental doc = new Documental(id, titulo, anio, genero, campo1, duracion);
        if (!relacion.trim().isEmpty()) {
            String[] d = relacion.trim().split(":");
            if (d.length >= 3) {
                doc.agregarInvestigador(new Investigador(d[0].trim(), d[1].trim(), d[2].trim()));
            }
        }
        return doc;
    }
}
