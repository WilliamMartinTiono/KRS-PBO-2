package com.mycompany.krs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class WindowUtil {

    // KITA PAKAI TEMA BIRU CIVITAS & PUTIH BERSIH
    public static final Color PRIMARY_COLOR = new Color(21, 101, 192);    // Biru Elegan
    public static final Color BACKGROUND_COLOR = new Color(255, 255, 255); // Putih Bersih (Agar tidak belang)

    public static void setWindow80PercentCenter(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize((int) (screenSize.width * 0.8), (int) (screenSize.height * 0.8));
        frame.setLocationRelativeTo(null);
        
        // Paksa warna frame paling belakang menjadi putih
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
    }

    public static void applyModernTheme() {
        try {
            // 1. GUNAKAN TEMA 'NIMBUS' (Bawaan Java yang modern & mendukung warna)
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

            // 2. UBAH WARNA DASAR NIMBUS
            // 'control' mengubah warna background semua panel menjadi putih
            UIManager.put("control", BACKGROUND_COLOR);
            
            // 'nimbusBase' adalah sihirnya! Ini otomatis mengubah tombol, header tabel, dan garis menjadi Biru
            UIManager.put("nimbusBase", PRIMARY_COLOR);
            
            // Warna text field dan area input
            UIManager.put("nimbusLightBackground", BACKGROUND_COLOR);

            // 3. UBAH FONT MENJADI MODERN
            Font modernFont = new Font("Segoe UI", Font.PLAIN, 14);
            UIManager.put("defaultFont", modernFont);

        } catch (Exception e) {
            System.err.println("Gagal menerapkan tema Nimbus: " + e.getMessage());
        }
    }
}