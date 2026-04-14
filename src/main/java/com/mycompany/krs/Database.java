/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.krs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
/**
 *
 * @author User
 */
public class Database {
    String host = "jdbc:mysql://localhost:3306/krs_db";
    String username = "root";
    String password = "";

    private Connection koneksi() {
        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            connection = DriverManager.getConnection (host, username, password);
        }catch (ClassNotFoundException e){
            System.err.println("Class Not Found : " + e.getMessage());
        }catch (SQLException e) {
            System.err.println("SQL Error : " + e.getMessage());
        }
        
        return connection;
    }
    
    Object readDB (String kolom, String tabel, String kondisi){
        Connection con = koneksi();
        
        if (con !=null){
            try{
                String query = "SELECT " + kolom + " FROM " + tabel + " WHERE " + kondisi + ";";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                con.close();
                
                return rs;
            }catch (SQLException e){
                System.err.println("SQL Error : " + e.getMessage());
            }
        }
        return null;
    }
    boolean createDB (String tabel, String kolom, String nilai){
        Connection con = koneksi();
        
        if (con != null){
            try {
                // Contoh output query: INSERT INTO mahasiswa (nim, nama) VALUES ('123', 'Budi');
                String query = "INSERT INTO " + tabel + " (" + kolom + ") VALUES (" + nilai + ");";
                Statement st = con.createStatement();
                st.executeUpdate(query);
                con.close();
                
                return true; // Mengembalikan true jika insert berhasil
            } catch (SQLException e) {
                System.err.println("SQL Error : " + e.getMessage());
            }
        }
        return false; // Mengembalikan false jika insert gagal
    }
    boolean updateDB (String tabel, String nilaiUpdate, String kondisi){
        Connection con = koneksi();
        
        if (con != null){
            try {
                // Contoh output query: UPDATE mahasiswa SET nama = 'Andi' WHERE nim = '123';
                String query = "UPDATE " + tabel + " SET " + nilaiUpdate + " WHERE " + kondisi + ";";
                Statement st = con.createStatement();
                st.executeUpdate(query);
                con.close();
                
                return true; // Mengembalikan true jika update berhasil
            } catch (SQLException e) {
                System.err.println("SQL Error : " + e.getMessage());
            }
        }
        return false; // Mengembalikan false jika update gagal
    }
    boolean deleteDB (String tabel, String kondisi){
        Connection con = koneksi();
        
        if (con != null){
            try {
                // Contoh output query: DELETE FROM mahasiswa WHERE nim = '123';
                String query = "DELETE FROM " + tabel + " WHERE " + kondisi + ";";
                Statement st = con.createStatement();
                st.executeUpdate(query);
                con.close();
                
                return true; // Mengembalikan true jika delete berhasil
            } catch (SQLException e) {
                System.err.println("SQL Error : " + e.getMessage());
            }
        }
        return false; // Mengembalikan false jika delete gagal
    }
}
