package controladores;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class PanelDos extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    // Campos de texto globales para usar en varios paneles
    private JTextField txtUsuario;
    private JTextField txtDepartamento;
    private JTextField txtPrecio;
    private JTextField txtCantidad;
    private JTextField txtProducto;
    private JTextField txtDescuento;

    // Formato de fecha y hora de login
    LocalDateTime fechaLogin = LocalDateTime.now();
    DateTimeFormatter formateo = DateTimeFormatter.ofPattern("dd MM yyyy HH:mm:ss");
    String formateoFechaLogin = fechaLogin.format(formateo);

    /**
     * Método main para lanzar la aplicación
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                PanelDos frame = new PanelDos();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Constructor de la ventana principal
     */
    public PanelDos() {
        // Configuración de la ventana principal
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Usuario: " + PanelLogin.usuario + "          Acceso: " + formateoFechaLogin);
        setBounds(100, 100, 600, 400);

        // Panel principal con border layout
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        // Pestañas principales
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Inicio", crearPanelInicio());        // Panel de bienvenida
        tabbedPane.addTab("Usuarios", crearPanelUsuarios());    // Panel para gestión de empleados
        tabbedPane.addTab("Almacen", crearPanelProductos());    // Panel para gestión de productos
        tabbedPane.addTab("Stock Pais", crearPanelStockPais()); // Panel para stock filtrado por país

        // Agregamos las pestañas al panel principal
        contentPane.add(tabbedPane, BorderLayout.CENTER);
    }

    // ------------------------- PANEL STOCK POR PAIS -------------------------
    private JPanel crearPanelStockPais() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // JTextPane para listar los productos filtrados por país
        JTextPane textPane = new JTextPane();
        textPane.setBounds(10, 11, 300, 200);
        panel.add(textPane);

        // JLabel que muestra el stock total acumulado del país
        JLabel lblTotal = new JLabel("Total stock: 0 kg");
        lblTotal.setBounds(10, 220, 200, 20);
        panel.add(lblTotal);

        // ComboBox estático de países para filtrar
        JComboBox<String> comboPais = new JComboBox<>();
        comboPais.setBounds(350, 30, 150, 25);
        panel.add(comboPais);
        comboPais.addItem("España");
        comboPais.addItem("Inglaterra");
        comboPais.addItem("Alemania");

        // JLabel que mostrará el almacén con más unidades dentro del país seleccionado
        JLabel lblMayor = new JLabel("Almacén con más unidades:");
        lblMayor.setBounds(350, 70, 300, 20);
        panel.add(lblMayor);

        // JTextPane para mostrar los almacenes vacíos del país seleccionado
        JTextPane textVacios = new JTextPane();
        textVacios.setBounds(350, 110, 250, 100);
        panel.add(textVacios);

        // Botón para listar productos, stock total y almacenes vacíos según país seleccionado
        JButton btnListar = new JButton("Listar por país");
        btnListar.setBounds(10, 250, 150, 25);
        panel.add(btnListar);

        // Acción del botón Listar
        btnListar.addActionListener(e -> {
            String pais = (String) comboPais.getSelectedItem();

            try {
                // ---------------- Listar productos del país y calcular stock total ----------------
                String sql = "SELECT producto, cantidad FROM productos WHERE pais = ?";
                PreparedStatement ps = PanelLogin.con.prepareStatement(sql);
                ps.setString(1, pais);
                ResultSet rs = ps.executeQuery();

                textPane.setText("");      // Limpiar texto previo
                double totalStock = 0;     // Inicializar total de stock

                while (rs.next()) {
                    String producto = rs.getString("producto");
                    double cantidad = rs.getDouble("cantidad");
                    totalStock += cantidad;
                    textPane.setText(textPane.getText() + producto + " - " + cantidad + " kg\n");
                }

                rs.close();
                ps.close();

                lblTotal.setText("Total stock: " + totalStock + " kg"); // Actualizar JLabel total

                // ---------------- Mostrar almacén con más unidades ----------------
                String sqlMayor = "SELECT id_almacen, SUM(cantidad) AS total_unidades " +
                                  "FROM productos WHERE pais = ? " +
                                  "GROUP BY id_almacen ORDER BY total_unidades DESC LIMIT 1";
                PreparedStatement psMayor = PanelLogin.con.prepareStatement(sqlMayor);
                psMayor.setString(1, pais);
                ResultSet rsMayor = psMayor.executeQuery();

                if (rsMayor.next()) {
                    int idAlmacen = rsMayor.getInt("id_almacen");
                    double unidades = rsMayor.getDouble("total_unidades");
                    lblMayor.setText("Almacén con más unidades: " + idAlmacen + " (" + unidades + " kg)");
                } else {
                    lblMayor.setText("No hay productos en este país");
                }

                rsMayor.close();
                psMayor.close();

                // ---------------- Mostrar almacenes vacíos ----------------
                String sqlVacios = "SELECT id, nombre FROM almacen WHERE pais = ? " +
                                   "AND id NOT IN (SELECT DISTINCT id_almacen FROM productos WHERE pais = ?)";
                PreparedStatement psVacios = PanelLogin.con.prepareStatement(sqlVacios);
                psVacios.setString(1, pais);
                psVacios.setString(2, pais);
                ResultSet rsVacios = psVacios.executeQuery();

                textVacios.setText("");  // Limpiar JTextPane
                while (rsVacios.next()) {
                    textVacios.setText(textVacios.getText() +
                        "Almacén vacío: " + rsVacios.getInt("id") + " - " + rsVacios.getString("nombre") + "\n");
                }

                rsVacios.close();
                psVacios.close();

            } catch (SQLException ex) {
                textPane.setText("Error al obtener datos: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        return panel;
    }

    // ------------------------- PANEL INICIO -------------------------
    private JPanel crearPanelInicio() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Mensaje de bienvenida
        JLabel textLogin = new JLabel("Login Correcto", JLabel.CENTER);
        textLogin.setFont(new Font("Tahoma", Font.PLAIN, 30));

        panel.add(textLogin, BorderLayout.CENTER);
        return panel;
    }

    // ------------------------- PANEL USUARIOS -------------------------
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // JTextPane para mostrar empleados
        JTextPane textPane = new JTextPane();
        textPane.setBounds(10, 11, 222, 179);
        panel.add(textPane);

        // Botón Listar empleados
        JButton botonListar = new JButton("Listar");
        botonListar.setBounds(32, 201, 89, 23);
        botonListar.addActionListener(e -> {
            String sql = "SELECT empleado, departamento FROM empleados ORDER BY empleado ASC LIMIT 10";
            try {
                PreparedStatement ps = PanelLogin.con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                textPane.setText("");
                while (rs.next()) {
                    textPane.setText(textPane.getText() +
                        rs.getString("empleado") + " - " + rs.getString("departamento") + "\n");
                }
                rs.close();
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                textPane.setText("Error al obtener los datos.");
            }
        });
        panel.add(botonListar);

        // Botón eliminar empleado (solo admin)
        JButton Eliminar = new JButton("Eliminar");
        Eliminar.setBounds(466, 130, 86, 20);
        Eliminar.addActionListener(e -> {
            String empleado = txtUsuario.getText();
            String departamento = txtDepartamento.getText();
            String sql = "DELETE FROM empleados WHERE empleado = ? AND departamento = ?";
            try {
                PreparedStatement ps = PanelLogin.con.prepareStatement(sql);
                if (PanelLogin.usuario.equals("admin")) {
                    ps.setString(1, empleado);
                    ps.setString(2, departamento);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(null, "No tiene permisos");
                    return;
                }
                int resultado = ps.executeUpdate();
                if (resultado > 0) {
                    javax.swing.JOptionPane.showMessageDialog(null, "¡Datos eliminados correctamente!");
                    txtUsuario.setText("");
                    txtDepartamento.setText("");
                } else {
                    javax.swing.JOptionPane.showMessageDialog(null, "No se encontró el usuario para eliminar.");
                }
            } catch (SQLException e2) {
                javax.swing.JOptionPane.showMessageDialog(null, "Error al eliminar: " + e2.getMessage());
                e2.printStackTrace();
            }
        });
        panel.add(Eliminar);

        // Botón agregar empleado (solo admin)
        JButton Enviar = new JButton("Enviar");
        Enviar.setBounds(370, 130, 86, 20);
        Enviar.addActionListener(e -> {
            String empleado = txtUsuario.getText();
            String departamento = txtDepartamento.getText();
            String sql = "INSERT INTO empleados (empleado, departamento) VALUES (?, ?)";
            try {
                PreparedStatement ps = PanelLogin.con.prepareStatement(sql);
                if (PanelLogin.usuario.equals("admin")) {
                    ps.setString(1, empleado);
                    ps.setString(2, departamento);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(null, "No tiene permisos");
                    return;
                }
                int resultado = ps.executeUpdate();
                if (resultado > 0) {
                    javax.swing.JOptionPane.showMessageDialog(null, "¡Datos insertados correctamente!");
                    txtUsuario.setText("");
                    txtDepartamento.setText("");
                }
            } catch (SQLException e2) {
                javax.swing.JOptionPane.showMessageDialog(null, "Error al insertar: " + e2.getMessage());
                e2.printStackTrace();
            }
        });
        panel.add(Enviar);

        // Campos de texto para usuario y departamento
        txtUsuario = new JTextField("Usuario");
        txtUsuario.setBounds(370, 99, 86, 20);
        panel.add(txtUsuario);
        txtUsuario.setColumns(10);

        txtDepartamento = new JTextField("Departamento");
        txtDepartamento.setBounds(466, 99, 86, 20);
        panel.add(txtDepartamento);
        txtDepartamento.setColumns(10);

        return panel;
    }

    // ------------------------- PANEL PRODUCTOS -------------------------
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // JTextPane para mostrar productos
        JTextPane textPane = new JTextPane();
        textPane.setBounds(10, 11, 250, 200);
        panel.add(textPane);

        // Botón listar productos
        JButton botonListar = new JButton("Listar");
        botonListar.setBounds(10, 220, 90, 25);
        botonListar.addActionListener(e -> {
            String sql = "SELECT producto, precio, cantidad, pais, id_almacen FROM productos ORDER BY producto ASC";
            try {
                PreparedStatement ps = PanelLogin.con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                textPane.setText("");
                while (rs.next()) {
                    textPane.setText(textPane.getText() +
                        rs.getString("producto") + " -- " +
                        rs.getDouble("precio") + "€ -- " +
                        rs.getDouble("cantidad") + "kg -- " +
                        rs.getString("pais") + " -- Almacén: " +
                        rs.getInt("id_almacen") + "\n");
                }
                rs.close();
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                textPane.setText("Error al obtener los datos.");
            }
        });
        panel.add(botonListar);

        // Campos para agregar o eliminar producto
        txtProducto = new JTextField("Producto nombre");
        txtProducto.setBounds(280, 20, 120, 25);
        panel.add(txtProducto);

        txtPrecio = new JTextField("Precio en €");
        txtPrecio.setBounds(410, 20, 80, 25);
        panel.add(txtPrecio);

        txtCantidad = new JTextField("Cantidad en kg");
        txtCantidad.setBounds(500, 20, 80, 25);
        panel.add(txtCantidad);

        // ComboBox estático de almacenes (ID - Nombre - País)
        JComboBox<String> comboAlmacenes = new JComboBox<>();
        comboAlmacenes.setBounds(280, 55, 300, 25);
        panel.add(comboAlmacenes);
        comboAlmacenes.addItem("1 - Madrid (España)");
        comboAlmacenes.addItem("2 - London (Inglaterra)");
        comboAlmacenes.addItem("3 - Berlin (Alemania)");

        // Botón agregar producto
        JButton btnInsertar = new JButton("Enviar");
        btnInsertar.setBounds(280, 90, 100, 25);
        panel.add(btnInsertar);
        btnInsertar.addActionListener(e -> {
            String producto = txtProducto.getText();
            double precioDouble;
            double cantidadDouble;
            try {
                precioDouble = Double.parseDouble(txtPrecio.getText());
                cantidadDouble = Double.parseDouble(txtCantidad.getText());
            } catch (NumberFormatException ex) {
                javax.swing.JOptionPane.showMessageDialog(null, "Precio o cantidad no válidos.");
                return;
            }

            // Obtener ID del almacén seleccionado
            String seleccionado = (String) comboAlmacenes.getSelectedItem();
            int idAlmacen = Integer.parseInt(seleccionado.split(" - ")[0]);

            // Determinar país del almacén según selección
            String paisAlmacen = "";
            if (seleccionado.contains("España")) paisAlmacen = "España";
            else if (seleccionado.contains("Inglaterra")) paisAlmacen = "Inglaterra";
            else if (seleccionado.contains("Alemania")) paisAlmacen = "Alemania";

            try {
                String sqlInsert = "INSERT INTO productos (producto, precio, cantidad, pais, id_almacen) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psInsert = PanelLogin.con.prepareStatement(sqlInsert);
                psInsert.setString(1, producto);
                psInsert.setDouble(2, precioDouble);
                psInsert.setDouble(3, cantidadDouble);
                psInsert.setString(4, paisAlmacen);
                psInsert.setInt(5, idAlmacen);

                int resultado = psInsert.executeUpdate();
                psInsert.close();

                if (resultado > 0) {
                    javax.swing.JOptionPane.showMessageDialog(null, "¡Producto insertado correctamente!");
                    txtProducto.setText("");
                    txtPrecio.setText("");
                    txtCantidad.setText("");
                }

            } catch (SQLException ex) {
                javax.swing.JOptionPane.showMessageDialog(null, "Error al insertar: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Botón eliminar producto (solo necesita el nombre del producto)
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(390, 90, 100, 25);
        panel.add(btnEliminar);
        btnEliminar.addActionListener(e -> {
            String producto = txtProducto.getText().trim();
            if (producto.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(null, "Ingrese el nombre del producto a eliminar.");
                return;
            }
            try {
                String sqlDelete = "DELETE FROM productos WHERE producto = ?";
                PreparedStatement psDelete = PanelLogin.con.prepareStatement(sqlDelete);
                psDelete.setString(1, producto);
                int resultado = psDelete.executeUpdate();
                psDelete.close();

                if (resultado > 0) {
                    javax.swing.JOptionPane.showMessageDialog(null, "Producto eliminado correctamente.");
                    txtProducto.setText("");
                } else {
                    javax.swing.JOptionPane.showMessageDialog(null, "No se encontró ningún producto con ese nombre.");
                }
            } catch (SQLException ex) {
                javax.swing.JOptionPane.showMessageDialog(null, "Error al eliminar: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
     // Campo para indicar el porcentaje de descuento
        txtDescuento = new JTextField("Descuento %");
        txtDescuento.setBounds(280, 125, 80, 25);
        panel.add(txtDescuento);

        // Botón aplicar descuento
        JButton btnDescuento = new JButton("Aplicar descuento");
        btnDescuento.setBounds(370, 125, 150, 25);
        panel.add(btnDescuento);

        btnDescuento.addActionListener(e -> {
            String porcentajeStr = txtDescuento.getText();
            String producto = txtProducto.getText();
            double porcentaje;

            // Validar que el porcentaje sea un número
            try {
                porcentaje = Double.parseDouble(porcentajeStr);
                if (porcentaje <= 0 || porcentaje > 100) {
                    javax.swing.JOptionPane.showMessageDialog(null, "Ingrese un porcentaje entre 0 y 100.");
                    return;
                }
            } catch (NumberFormatException ex) {
                javax.swing.JOptionPane.showMessageDialog(null, "Porcentaje no válido.");
                return;
            }

            try {
                // SQL para aplicar descuento: precio = precio * (1 - porcentaje/100)
                String sqlUpdate;
                PreparedStatement ps;

                if (!producto.isEmpty()) {
                    // Descuento solo para producto específico
                    sqlUpdate = "UPDATE productos SET precio = precio * (1 - ?/100) WHERE producto = ?";
                    ps = PanelLogin.con.prepareStatement(sqlUpdate);
                    ps.setDouble(1, porcentaje);
                    ps.setString(2, producto);
                } else {
                    // Descuento a todos los productos
                    sqlUpdate = "UPDATE productos SET precio = precio * (1 - ?/100)";
                    ps = PanelLogin.con.prepareStatement(sqlUpdate);
                    ps.setDouble(1, porcentaje);
                }

                int resultado = ps.executeUpdate();
                ps.close();

                if (resultado > 0) {
                    javax.swing.JOptionPane.showMessageDialog(null, "Descuento aplicado correctamente.");
                    txtDescuento.setText("");
                } else {
                    javax.swing.JOptionPane.showMessageDialog(null, "No se aplicó el descuento. Verifique el nombre del producto.");
                }

            } catch (SQLException ex) {
                javax.swing.JOptionPane.showMessageDialog(null, "Error al aplicar descuento: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        return panel;
    }
}