package controladores;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

//Variables importantes
	private static String driver = "com.mysql.jdbc.Driver";
	private static String usuario = "root";
	private static String password = "root";
	private static String url = "jdbc:mysql://localhost:3306/datos_java";
	private Connection con = null;

	public Connection getConnection() {
		try {
			con = DriverManager.getConnection(url, usuario, password);
			System.out.println("Conectado a mysql, bienvenio su payo");
		} catch (SQLException e) {
			System.out.println("Error de conexion: " + e.getMessage());
		}
		return con;
	}

	public void close() {
		try {
			if (con != null)
				con.close();
			System.out.println("Conexion cerrada");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}