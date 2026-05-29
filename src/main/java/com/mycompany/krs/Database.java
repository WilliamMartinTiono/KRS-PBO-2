/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.krs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 *
 * @author User
 */
public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/krspbo2lancar"; // Pastikan nama DB benar
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection connection = null;
    
    // FUNGSI TRANSAKSI DATABASE
    public boolean beginTransaction() {
        try { 
            getConnection().setAutoCommit(false); 
            return true; 
        } catch (SQLException e) { 
            e.printStackTrace();
            return false; 
        }
    }

    public boolean commit() {
        try { 
            getConnection().commit(); 
            getConnection().setAutoCommit(true); 
            return true; 
        } catch (SQLException e) { 
            e.printStackTrace();
            rollback(); 
            return false; 
        }
    }

    public void rollback() {
        try { 
            getConnection().rollback(); 
            getConnection().setAutoCommit(true); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }
    public static Connection getConnection() {
try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASS);
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Gagal koneksi database: " + e.getMessage());
            return null;
        }
    }

    // FUNGSI BACA DATA AMAN (Mengatasi SQL Injection)
    // Cara pakai: db.readDBSafe("SELECT * FROM tabel WHERE nama LIKE ?", "%" + kataKunci + "%");
    public ResultSet readDBSafe(String query, Object... params) {
        Connection conn = getConnection();
        if (conn == null) {
            javax.swing.JOptionPane.showMessageDialog(null, "Tidak dapat terhubung ke database!\nPastikan MySQL (XAMPP) sudah berjalan.", "Error Koneksi", javax.swing.JOptionPane.ERROR_MESSAGE);
            return null;
        }
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // FUNGSI EKSEKUSI AMAN (INSERT, UPDATE, DELETE & Null Crash)
    public boolean executeDBSafe(String query, Object... params) {
        Connection conn = getConnection();
        if (conn == null) {
            javax.swing.JOptionPane.showMessageDialog(null, "Tidak dapat terhubung ke database!\nPastikan MySQL (XAMPP) sudah berjalan.", "Error Koneksi", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // FUNGSI INSERT & AMBIL ID (Mengatasi Race Condition & Null Crash)
    public int insertAndGetId(String query, Object... params) {
        Connection conn = getConnection();
        if (conn == null) {
            javax.swing.JOptionPane.showMessageDialog(null, "Tidak dapat terhubung ke database!\nPastikan MySQL (XAMPP) sudah berjalan.", "Error Koneksi", javax.swing.JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        try {
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static String hashPassword(String password) {
        try {
            // FIX #5: Tambahkan 'Salt' Rahasia. 
            // Ini akan ditempelkan ke password user sebelum dienkripsi.
            String salt = "SIAKAD_SECRET_2026!"; 
            String saltedPassword = password + salt;
            
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(saltedPassword.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            // FIX #6: JANGAN PERNAH kembalikan password asli. Paksa sistem berhenti.
            throw new RuntimeException("Fatal Error: Sistem Enkripsi Kriptografi Gagal!", e);
        }
    }
   
}
