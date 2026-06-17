/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.krs;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.*;
/**
 *
 * @author User
 */
public class PanelLaporan extends javax.swing.JPanel {

    /**
     * Creates new form PanelLaporan
     */
    public PanelLaporan() {
        initComponents();
        setupTabel();
        loadPeriode();
    }
    // Import model tabel
    private javax.swing.table.DefaultTableModel modelKrs, modelBelum, modelKapasitas, modelRuang;

    private void setupTabel() {
        // 1. Model Tabel KRS
        modelKrs = new javax.swing.table.DefaultTableModel(new String[]{"NIM", "Nama Mahasiswa", "Total SKS", "Tgl Pengajuan", "Status"}, 0);
        tblKrs.setModel(modelKrs);
        
            // 2. Model Tabel Belum KRS (Tadi yang ini hilang tidak sengaja terhapus)
        modelBelum = new javax.swing.table.DefaultTableModel(new String[]{"NIM", "Nama Mahasiswa", "Dosen Wali"}, 0);
        tblBelumKrs.setModel(modelBelum);
        
        // 3. Model Tabel Kapasitas Kelas
        modelKapasitas = new javax.swing.table.DefaultTableModel(new String[]{"Mata Kuliah", "Dosen", "Hari", "Jam", "Kuota Total", "Terisi", "Sisa Slot"}, 0);
        tblKapasitas.setModel(modelKapasitas);
        
        // 4. Model Tabel Penggunaan Ruang
        modelRuang = new javax.swing.table.DefaultTableModel(new String[]{"Hari", "Jam", "Ruang", "Mata Kuliah", "Dosen"}, 0);
        tblRuang.setModel(modelRuang);
    }

    private void loadPeriode() {
        try {
            Database db = new Database();
            java.sql.ResultSet rs = db.readDBSafe("SELECT id_periode, semester, tahun_ajaran FROM periode ORDER BY id_periode DESC");
            cbPeriode.removeAllItems();
            while (rs != null && rs.next()) {
                cbPeriode.addItem(rs.getString("id_periode") + "-" + rs.getString("semester") + " " + rs.getString("tahun_ajaran"));
            }
        } catch (Exception e) { System.err.println("Gagal load periode: " + e.getMessage()); }
    }

   private void tampilkanSemuaLaporan() {
        if (cbPeriode.getSelectedItem() == null) return;
        String idPeriode = cbPeriode.getSelectedItem().toString().split("-")[0];
        Database db = new Database();
        java.sql.ResultSet rs;

        try {
            // --- 1. LAPORAN KRS MASUK ---
            modelKrs.setRowCount(0);
            String sqlKrs = "SELECT k.nim, m.nama_mhs, k.total_sks, k.tgl_pengajuan, k.status_validasi " +
                            "FROM krs_header k JOIN mahasiswa m ON k.nim = m.nim " +
                            "WHERE k.id_periode = ? ORDER BY k.status_validasi, k.tgl_pengajuan";
            rs = db.readDBSafe(sqlKrs, idPeriode);
            while (rs != null && rs.next()) {
                modelKrs.addRow(new Object[]{rs.getString("nim"), rs.getString("nama_mhs"), rs.getString("total_sks"), rs.getString("tgl_pengajuan"), rs.getString("status_validasi")});
            }

            // --- 2. LAPORAN MAHASISWA BELUM KRS (Fix Bug SQL NULL) ---
            modelBelum.setRowCount(0);
            String sqlBelum = "SELECT m.nim, m.nama_mhs, d.nama_dosen AS dosen_wali FROM mahasiswa m " +
                              "LEFT JOIN dosen d ON m.id_dosen_wali = d.nidn " +
                              "WHERE m.nim NOT IN (SELECT nim FROM krs_header WHERE id_periode = ? AND nim IS NOT NULL) ORDER BY m.nama_mhs";
            rs = db.readDBSafe(sqlBelum, idPeriode);
            while (rs != null && rs.next()) {
                modelBelum.addRow(new Object[]{rs.getString("nim"), rs.getString("nama_mhs"), rs.getString("dosen_wali")});
            }

            // --- 3. LAPORAN KAPASITAS KELAS ---
            modelKapasitas.setRowCount(0);
            String sqlKapasitas = "SELECT mk.nama_mk, d.nama_dosen, k.hari, k.jam, k.kuota, " +
                                  "(SELECT COUNT(*) FROM krs_detail kd JOIN krs_header kh ON kd.id_krs = kh.id_krs WHERE kd.id_kelas = k.id_kelas AND kh.status_validasi != 'Ditolak') AS terisi " +
                                  "FROM kelas k JOIN mata_kuliah mk ON k.kode_mk = mk.kode_mk JOIN dosen d ON k.nidn = d.nidn " +
                                  "WHERE k.id_periode = ? ORDER BY terisi DESC";
            rs = db.readDBSafe(sqlKapasitas, idPeriode);
            while (rs != null && rs.next()) {
                int kuota = rs.getInt("kuota");
                int terisi = rs.getInt("terisi");
                int sisa = kuota - terisi;
                modelKapasitas.addRow(new Object[]{rs.getString("nama_mk"), rs.getString("nama_dosen"), rs.getString("hari"), rs.getString("jam"), kuota, terisi, sisa});
            }

            // --- 4. LAPORAN PENGGUNAAN RUANG ---
            modelRuang.setRowCount(0);
            String sqlRuang = "SELECT k.hari, k.jam, k.ruang, mk.nama_mk, d.nama_dosen " +
                              "FROM kelas k JOIN mata_kuliah mk ON k.kode_mk = mk.kode_mk JOIN dosen d ON k.nidn = d.nidn " +
                              "WHERE k.id_periode = ? ORDER BY FIELD(k.hari,'Senin','Selasa','Rabu','Kamis','Jumat','Sabtu'), k.jam";
            rs = db.readDBSafe(sqlRuang, idPeriode);
            while (rs != null && rs.next()) {
                modelRuang.addRow(new Object[]{rs.getString("hari"), rs.getString("jam"), rs.getString("ruang"), rs.getString("nama_mk"), rs.getString("nama_dosen")});
            }

        } catch (Exception e) {
            System.err.println("Gagal memuat laporan: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memuat laporan.");
        }
    
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cbPeriode = new javax.swing.JComboBox<>();
        btnTampil = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKrs = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBelumKrs = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblKapasitas = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblRuang = new javax.swing.JTable();
        btnCetakAdmin = new javax.swing.JButton();

        cbPeriode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnTampil.setText("Tampilkan");
        btnTampil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilActionPerformed(evt);
            }
        });

        jLabel1.setText("Laporan");

        tblKrs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblKrs);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("KRS", jPanel1);

        tblBelumKrs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblBelumKrs);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Belum KRS", jPanel2);

        tblKapasitas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tblKapasitas);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Daftar Kelas", jPanel3);

        tblRuang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(tblRuang);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Jadwal Kelas", jPanel4);

        btnCetakAdmin.setText("Cetak");
        btnCetakAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakAdminActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(cbPeriode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnTampil)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCetakAdmin))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(314, 314, 314)
                                .addComponent(jLabel1)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbPeriode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTampil)
                    .addComponent(btnCetakAdmin))
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnTampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilActionPerformed
        // TODO add your handling code here:
        tampilkanSemuaLaporan();
    }//GEN-LAST:event_btnTampilActionPerformed

    private void btnCetakAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakAdminActionPerformed
      if (cbPeriode.getSelectedItem() == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Pilih periode terlebih dahulu!");
            return;
        }

        String idPeriode = cbPeriode.getSelectedItem().toString().split("-")[0];

        try {
            // 1. CEK KONEKSI DATABASE
            Database db = new Database();
            if (db.getConnection() == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "GAGAL: Koneksi Database terputus atau bernilai null!");
                return;
            }
            
            // 2. SIAPKAN PARAMETER
            java.util.HashMap<String, Object> parameter = new java.util.HashMap<>();
            parameter.put("p_id_periode", idPeriode);
            
            // 3. CARI LOKASI FILE (Khusus Proyek Maven)
            java.io.File file = new java.io.File("src/main/java/com/mycompany/krs/LaporanKrsAdmin.jrxml");
            
            // Jika tidak ada di folder Maven, coba di folder biasa
            if (!file.exists()) {
                file = new java.io.File("src/com/mycompany/krs/LaporanKrsAdmin.jrxml");
            }
            
            // Jika benar-benar tidak ada
            if (!file.exists()) {
                javax.swing.JOptionPane.showMessageDialog(this, "GAGAL: File LaporanKrsAdmin.jrxml tidak ditemukan!\nPastikan file sudah dibuat.");
                return;
            }
            
            // 4. PROSES BACA FILE
            java.io.InputStream fileJrxml = new java.io.FileInputStream(file);

            // 5. MESIN COMPILE & CETAK
            net.sf.jasperreports.engine.JasperReport jasperReport = net.sf.jasperreports.engine.JasperCompileManager.compileReport(fileJrxml);
            net.sf.jasperreports.engine.JasperPrint jasperPrint = net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, parameter, db.getConnection());
            
            // 6. TAMPILKAN
            net.sf.jasperreports.view.JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            // Tampilkan error SANGAT DETAIL ke layar
            String namaError = e.toString();
            String barisError = (e.getStackTrace().length > 0) ? e.getStackTrace()[0].toString() : "Tidak diketahui";
            javax.swing.JOptionPane.showMessageDialog(this, "Terjadi Error Detail:\n" + namaError + "\n\nLokasi: " + barisError);
        }
    }//GEN-LAST:event_btnCetakAdminActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCetakAdmin;
    private javax.swing.JButton btnTampil;
    private javax.swing.JComboBox<String> cbPeriode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tblBelumKrs;
    private javax.swing.JTable tblKapasitas;
    private javax.swing.JTable tblKrs;
    private javax.swing.JTable tblRuang;
    // End of variables declaration//GEN-END:variables
}
