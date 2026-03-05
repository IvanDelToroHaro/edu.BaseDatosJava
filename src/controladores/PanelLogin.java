package controladores;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class PanelLogin extends JFrame {

	private static final long serialVersionUID = 1L;
	public static String usuario;
	private JPanel contentPane;
	private JTextField cajaUsu;
	private JTextField cajaPaswd;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PanelLogin frame = new PanelLogin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */

	public static ConexionDB db = new ConexionDB();
	public static Connection con = db.getConnection();

	// Variables--------------------------------------------------------------------------------------------------------//
	int contador= 0;
	private JTextField txtRol;
	// -----------------------------------------------------------------------------------------------------------------//
	public PanelLogin() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 414, 239);
		contentPane.add(panel);
		panel.setLayout(null);

		JLabel textoLogin = new JLabel("LOGIN");
		textoLogin.setBounds(166, 0, 81, 40);
		textoLogin.setFont(new Font("Tahoma", Font.PLAIN, 26));
		panel.add(textoLogin);

		cajaUsu = new JTextField();
		cajaUsu.setText("Usuario");
		cajaUsu.setBounds(65, 76, 86, 20);
		panel.add(cajaUsu);
		cajaUsu.setColumns(10);

		cajaPaswd = new JTextField();
		cajaPaswd.setText("Contraseña");
		cajaPaswd.setBounds(161, 76, 86, 20);
		cajaPaswd.setColumns(10);
		panel.add(cajaPaswd);

		JButton botonEnviar = new JButton("Login");
		botonEnviar.setBounds(161, 163, 86, 23);
		botonEnviar.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {

		        usuario = cajaUsu.getText();
		        String password = cajaPaswd.getText();

		        try {

		            String sql = "SELECT * FROM usuarios WHERE usuario = ? AND contraseña = ?";
		            PreparedStatement ps = con.prepareStatement(sql);

		            ps.setString(1, usuario);
		            ps.setString(2, password);

		            ResultSet rs = ps.executeQuery();

		            if (rs.next()) {

		                // Usuario encontrado → login correcto
		                JOptionPane.showMessageDialog(null, "Login correcto");
		                contador = 0; // Reiniciamos intentos

		                PanelDos panelDos = new PanelDos();
		                panelDos.setVisible(true);
		                dispose();

		            } else {

		                // No existe en la base de datos
		                contador++;

		                JOptionPane.showMessageDialog(null, 
		                    "Usuario o contraseña incorrectos\nIntento " + contador + " de 3");

		                if (contador >= 3) {
		                    botonEnviar.setEnabled(false);
		                    JOptionPane.showMessageDialog(null, 
		                        "Has superado el número máximo de intentos");
		                }
		            }

		            rs.close();
		            ps.close();

		        } catch (Exception ex) {
		            ex.printStackTrace();
		            JOptionPane.showMessageDialog(null, 
		                "Error al conectar con la base de datos");
		        }
		    }
		});
		panel.add(botonEnviar);
		
		txtRol = new JTextField();
		txtRol.setText("Rol");
		txtRol.setBounds(257, 76, 86, 20);
		panel.add(txtRol);
		txtRol.setColumns(10);

	}
}
