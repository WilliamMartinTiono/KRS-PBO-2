/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.krs;

/**
 *
 * @author User
 */
public class PanelIsiKRS extends javax.swing.JPanel {
private String idPeriodeAktif = "";
    private int totalSks = 0;
    private javax.swing.table.DefaultTableModel modelTersedia;
    private javax.swing.table.DefaultTableModel modelKrs;

    private void setupTabel() {
        modelTersedia = new javax.swing.table.DefaultTableModel();
        modelTersedia.addColumn("ID Kelas");
        modelTersedia.addColumn("Kode MK");
        modelTersedia.addColumn("Mata Kuliah");
        modelTersedia.addColumn("Dosen");
        modelTersedia.addColumn("Hari");
        modelTersedia.addColumn("Jam");
        modelTersedia.addColumn("Ruang");
        modelTersedia.addColumn("SKS");
        modelTersedia.addColumn("Kuota");
        tblKelasTersedia.setModel(modelTersedia);

        modelKrs = new javax.swing.table.DefaultTableModel();
        modelKrs.addColumn("ID Kelas");
        modelKrs.addColumn("Kode MK");
        modelKrs.addColumn("Mata Kuliah");
        modelKrs.addColumn("Dosen");
        modelKrs.addColumn("Hari");
        modelKrs.addColumn("Jam");
        modelKrs.addColumn("Ruang");
        modelKrs.addColumn("SKS");
        tblKrsSaya.setModel(modelKrs);
    }

    private void loadDataMahasiswa() {
        String nim = Login.getUserLogin(); 
        lblNim.setText(nim);
        
        try {
            Database db = new Database();
            // FITUR BARU: Tarik data Mahasiswa sekaligus nama Dosen Walinya
            String sql = "SELECT m.nama_mhs, d.nama_dosen FROM mahasiswa m " +
                         "LEFT JOIN dosen d ON m.id_dosen_wali = d.nidn WHERE m.nim = ?";
            java.sql.ResultSet rsMhs = db.readDBSafe(sql, nim);
            if(rsMhs != null && rsMhs.next()) {
                lblNama.setText(rsMhs.getString("nama_mhs"));
                
                // Pasang ke Label yang baru saja kamu buat di Design
                String namaDosen = rsMhs.getString("nama_dosen");
                lblDosenWali.setText("Dosen Wali: " + (namaDosen != null ? namaDosen : "Belum Ditentukan"));
            }
            
            java.sql.ResultSet rsPer = db.readDBSafe("SELECT id_periode, semester, tahun_ajaran FROM periode WHERE status_krs = 'Buka'");
            if(rsPer != null && rsPer.next()) {
                idPeriodeAktif = rsPer.getString("id_periode");
                lblPeriode.setText(rsPer.getString("semester") + " " + rsPer.getString("tahun_ajaran"));
            } else {
                lblPeriode.setText("TIDAK ADA PERIODE BUKA");
                btnAjukan.setEnabled(false); btnAmbil.setEnabled(false);
            }
        } catch(Exception e) {
            System.err.println("Error muat data mahasiswa: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Gagal memuat data akademik!\nKoneksi database terputus.", 
                "Error Sistem", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
                
            btnAjukan.setEnabled(false); 
            btnAmbil.setEnabled(false);
        }
    }

    private void loadKelasTersedia() {
        modelTersedia.setRowCount(0);
        try {
            Database db = new Database();
            // Kueri Subquery yang 100% aman untuk semua versi MySQL
            String sql = "SELECT * FROM (" +
                         "  SELECT k.id_kelas, mk.kode_mk, mk.nama_mk, d.nama_dosen, k.hari, k.jam, k.ruang, mk.sks, k.kuota, " +
                         "  (SELECT COUNT(*) FROM krs_detail kd JOIN krs_header kh ON kd.id_krs = kh.id_krs WHERE kd.id_kelas = k.id_kelas AND kh.status_validasi != 'Ditolak') AS terdaftar " +
                         "  FROM kelas k " +
                         "  JOIN mata_kuliah mk ON k.kode_mk = mk.kode_mk " +
                         "  JOIN dosen d ON k.nidn = d.nidn " +
                         "  WHERE k.id_periode = ?" +
                         ") AS subquery " +
                         "WHERE (kuota - terdaftar) > 0";
            
            java.sql.ResultSet rs = db.readDBSafe(sql, idPeriodeAktif);
            
            while(rs != null && rs.next()) {
                String idKelas = rs.getString("id_kelas");
                boolean sudahDiambil = false;
                // Cek agar tidak ganda dengan keranjang
                for(int i = 0; i < modelKrs.getRowCount(); i++){
                    if(modelKrs.getValueAt(i, 0).toString().equals(idKelas)){ sudahDiambil = true; break; }
                }
                if(!sudahDiambil) {
                    int kuotaAsli = rs.getInt("kuota");
                    int terdaftar = rs.getInt("terdaftar");
                    int sisaKuota = kuotaAsli - terdaftar;

                    modelTersedia.addRow(new Object[]{
                        idKelas, rs.getString("kode_mk"), rs.getString("nama_mk"), rs.getString("nama_dosen"),
                        rs.getString("hari"), rs.getString("jam"), rs.getString("ruang"), rs.getString("sks"), 
                        sisaKuota + " (dari " + kuotaAsli + ")"
                    });
                }
            }
        } catch(Exception e) {
            System.err.println("Error muat jadwal kelas: " + e.getMessage());
        }
    }
    
    
    private void cekStatusKRS() {
        if (idPeriodeAktif == null || idPeriodeAktif.isEmpty()) return;
        Database db = new Database();
        try {
            String sql = "SELECT id_krs, status_validasi FROM krs_header WHERE nim = ? AND id_periode = ? AND status_validasi != 'Ditolak'";
            java.sql.ResultSet rs = db.readDBSafe(sql, Login.getUserLogin(), idPeriodeAktif);
            
            if (rs != null && rs.next()) {
                String status = rs.getString("status_validasi");
                String idKrs = rs.getString("id_krs");
                
                // JIKA MENUNGGU / DISETUJUI -> Kunci Mati UI-nya
                if (status.equals("Menunggu") || status.equals("Disetujui")) {
                    btnAjukan.setEnabled(false);
                    btnAmbil.setEnabled(false);
                    btnBatal.setEnabled(false);
                    if (!lblPeriode.getText().contains("STATUS:")) {
                        lblPeriode.setText(lblPeriode.getText() + " | STATUS: " + status.toUpperCase());
                    }
                }
                
                // Tarik data kelas ke keranjang HANYA YANG TIDAK DITOLAK
                modelKrs.setRowCount(0); 
                String sqlDetail = "SELECT k.id_kelas, mk.kode_mk, mk.nama_mk, d.nama_dosen, k.hari, k.jam, k.ruang, mk.sks " +
                                   "FROM krs_detail kd JOIN kelas k ON kd.id_kelas = k.id_kelas " +
                                   "JOIN mata_kuliah mk ON k.kode_mk = mk.kode_mk JOIN dosen d ON k.nidn = d.nidn " +
                                   "WHERE kd.id_krs = ? AND kd.status_detail != 'Ditolak'";
                java.sql.ResultSet rsDetail = db.readDBSafe(sqlDetail, idKrs);
                
                while (rsDetail != null && rsDetail.next()) {
                    modelKrs.addRow(new Object[]{
                        rsDetail.getString("id_kelas"), rsDetail.getString("kode_mk"), 
                        rsDetail.getString("nama_mk"), rsDetail.getString("nama_dosen"), 
                        rsDetail.getString("hari"), rsDetail.getString("jam"), 
                        rsDetail.getString("ruang"), rsDetail.getString("sks")
                    });
                }
                hitungSKS(); // Hitung ulang SKS murni dari sisa yang disetujui
            }
        } catch (Exception e) { System.err.println("Error cek status KRS: " + e.getMessage()); }
    }
    
    // FUNGSI BARU UNTUK MENAMPILKAN CATATAN PENOLAKAN
    private void cekKrsDitolak() {
        if (idPeriodeAktif == null || idPeriodeAktif.isEmpty()) return;
        try {
            Database db = new Database();
            // Cek apakah status Ditolak Total atau Sebagian Ditolak
            String sql = "SELECT catatan_dosen, status_validasi FROM krs_header WHERE nim = ? AND id_periode = ? AND (status_validasi = 'Ditolak' OR status_validasi = 'Sebagian Ditolak')";
            java.sql.ResultSet rs = db.readDBSafe(sql, Login.getUserLogin(), idPeriodeAktif);
            
            if (rs != null && rs.next()) {
                String catatan = rs.getString("catatan_dosen");
                String status = rs.getString("status_validasi");
                
                if (status.equals("Ditolak")) {
                    lblPeriode.setText(lblPeriode.getText() + " | STATUS: DITOLAK TOTAL");
                    lblPeriode.setForeground(java.awt.Color.RED);
                } else {
                    lblPeriode.setText(lblPeriode.getText() + " | STATUS: SEBAGIAN DITOLAK (REVISI)");
                    lblPeriode.setForeground(new java.awt.Color(204, 102, 0)); // Oranye
                }
                
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "KRS kamu telah divalidasi dengan status: " + status.toUpperCase() + "\n\nCatatan Dosen:\n\"" + (catatan != null && !catatan.trim().isEmpty() ? catatan : "Tidak ada catatan.") + "\"\n\nSilakan perbaiki jadwalmu.", 
                    "Validasi Dosen Wali", 
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) { System.err.println("Error cek KRS ditolak: " + e.getMessage()); }
    }

    private void hitungSKS() {
        totalSks = 0;
        for(int i = 0; i < modelKrs.getRowCount(); i++) {
            totalSks += Integer.parseInt(modelKrs.getValueAt(i, 7).toString());
        }
        lblTotalSks.setText(String.valueOf(totalSks));
    }
    
    private boolean cekBentrokWaktu(String jamBaru, String jamAda) {
        try {
            String jB = jamBaru.replace(" ", "").replace(".", ":");
            String jA = jamAda.replace(" ", "").replace(".", ":");
            
            int start1 = (Integer.parseInt(jB.split("-")[0].split(":")[0]) * 60) + Integer.parseInt(jB.split("-")[0].split(":")[1]);
            int end1 = (Integer.parseInt(jB.split("-")[1].split(":")[0]) * 60) + Integer.parseInt(jB.split("-")[1].split(":")[1]);
            
            int start2 = (Integer.parseInt(jA.split("-")[0].split(":")[0]) * 60) + Integer.parseInt(jA.split("-")[0].split(":")[1]);
            int end2 = (Integer.parseInt(jA.split("-")[1].split(":")[0]) * 60) + Integer.parseInt(jA.split("-")[1].split(":")[1]);

            return start1 < end2 && start2 < end1;
        } catch (Exception e) {
            return jamBaru.equalsIgnoreCase(jamAda);
        }
    }
    /**
     * Creates new form PanelIsiKRS
     */
    public PanelIsiKRS() {
    initComponents();
    setupTabel();

        // 1. CEK SESI LOGIN DI PINTU MASUK UTAMA
        if (Login.getUserLogin() == null || Login.getUserLogin().trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Sesi login tidak valid atau telah berakhir.\nMengalihkan ke halaman Login...", 
                "Keamanan Sistem", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            
            // 2. TUTUP MENU UTAMA & BUKA LOGIN
            javax.swing.SwingUtilities.invokeLater(() -> {
                java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(this);
                if (win != null) {
                    win.dispose(); // Tutup paksa Menu Utama
                }
                new Login().setVisible(true); // Buka kembali layar Login
            });
            
            return; // 3. HENTIKAN PROSES! (Tabel dan Data di bawah ini tidak akan di-load)
        }

        // Jika sesi aman, jalankan seperti biasa
        loadDataMahasiswa();
        cekStatusKRS();
        loadKelasTersedia();
        cekKrsDitolak();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        lblNim = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblNama = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblPeriode = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKelasTersedia = new javax.swing.JTable();
        btnAmbil = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblKrsSaya = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        lblTotalSks = new javax.swing.JLabel();
        btnAjukan = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        lblDosenWali = new javax.swing.JLabel();

        jLabel1.setText("NIM                              :");

        lblNim.setText("jLabel2");

        jLabel2.setText("Nama  Mahasiswa  :");

        lblNama.setText("jLabel3");

        jLabel3.setText("Periode Aktif              :");

        lblPeriode.setText("jLabel4");

        tblKelasTersedia.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblKelasTersedia);

        btnAmbil.setText("Ambil");
        btnAmbil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAmbilActionPerformed(evt);
            }
        });

        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        tblKrsSaya.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblKrsSaya);

        jLabel4.setText("Total SKS : ");

        lblTotalSks.setText("0");

        btnAjukan.setText("Ajukan KRS");
        btnAjukan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjukanActionPerformed(evt);
            }
        });

        jLabel5.setText("Dosen Wali                 :");

        lblDosenWali.setText("jLabel6");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAmbil)
                            .addComponent(btnBatal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTotalSks))
                            .addComponent(btnAjukan)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblNama))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblNim))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addComponent(jLabel3))
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblDosenWali)
                                    .addComponent(lblPeriode))))
                        .addGap(0, 308, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblNim))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblNama))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblPeriode))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(162, 162, 162)
                        .addComponent(btnAmbil)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBatal)
                        .addGap(0, 218, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(lblDosenWali))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblTotalSks))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAjukan)
                .addGap(27, 27, 27))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAmbilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAmbilActionPerformed
        // TODO add your handling code here:
       int baris = tblKelasTersedia.getSelectedRow();
        if(baris == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Pilih kelas di tabel kiri dulu!"); return;
        }
        
        // DEKLARASI VARIABEL (Yang sebelumnya tidak sengaja terhapus)
        String kodeMkBaru = modelTersedia.getValueAt(baris, 1).toString();
        String hariBaru = modelTersedia.getValueAt(baris, 4).toString();
        String jamBaru = modelTersedia.getValueAt(baris, 5).toString();

        // CEK BENTROK JADWAL & MATKUL KEMBAR DI KERANJANG (Dibungkus For-Loop)
        for(int i=0; i < modelKrs.getRowCount(); i++){
            // 1. Cek Matkul Kembar
            if(kodeMkBaru.equals(modelKrs.getValueAt(i, 1).toString())){
                javax.swing.JOptionPane.showMessageDialog(this, "Gagal! Mata Kuliah ini sudah kamu ambil."); return;
            }
            
            // 2. CEK BENTROK WAKTU (MENGGUNAKAN FUNGSI CERDAS)
            String hariKeranjang = modelKrs.getValueAt(i, 4).toString();
            String jamKeranjang = modelKrs.getValueAt(i, 5).toString();

            if(hariBaru.equals(hariKeranjang) && cekBentrokWaktu(jamBaru, jamKeranjang)){
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "BENTROK JADWAL!\nKelas ini beririsan waktu dengan kelas di keranjang Anda pada hari " + hariKeranjang + " jam " + jamKeranjang, 
                    "Jadwal Bentrok", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Pindahkan ke keranjang
        modelKrs.addRow(new Object[]{
            modelTersedia.getValueAt(baris, 0), modelTersedia.getValueAt(baris, 1), modelTersedia.getValueAt(baris, 2),
            modelTersedia.getValueAt(baris, 3), modelTersedia.getValueAt(baris, 4), modelTersedia.getValueAt(baris, 5),
            modelTersedia.getValueAt(baris, 6), modelTersedia.getValueAt(baris, 7)
        });
        modelTersedia.removeRow(baris); // Hapus dari etalase
        hitungSKS();
    }//GEN-LAST:event_btnAmbilActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        int baris = tblKrsSaya.getSelectedRow();
        if(baris == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Pilih mata kuliah yang akan dibatalkan!"); 
            return;
        }
        
        Object idKelas = modelKrs.getValueAt(baris, 0);
        Object kodeMk = modelKrs.getValueAt(baris, 1);
        Object mataKuliah = modelKrs.getValueAt(baris, 2);
        Object dosen = modelKrs.getValueAt(baris, 3);
        Object hari = modelKrs.getValueAt(baris, 4);
        Object jam = modelKrs.getValueAt(baris, 5);
        Object ruang = modelKrs.getValueAt(baris, 6);
        Object sks = modelKrs.getValueAt(baris, 7);

        String sisaKuotaStr = "0";

        try {
            Database db = new Database();
            // 1. CEK PENGAMAN: Batal Ditolak jika sudah ACC Dosen
            java.sql.ResultSet rsCek = db.readDBSafe("SELECT kd.status_detail FROM krs_detail kd JOIN krs_header kh ON kd.id_krs = kh.id_krs WHERE kh.nim = ? AND kh.id_periode = ? AND kd.id_kelas = ?", Login.getUserLogin(), idPeriodeAktif, idKelas);
            if (rsCek != null && rsCek.next()) {
                if ("Disetujui".equalsIgnoreCase(rsCek.getString("status_detail"))) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Akses Ditolak!\nKamu tidak bisa membatalkan mata kuliah yang sudah DISETUJUI oleh Dosen Wali.", "Peringatan", javax.swing.JOptionPane.WARNING_MESSAGE);
                    return; // Hentikan proses SEBELUM dihapus dari tabel
                }
            }

            // 2. CEK SISA KUOTA AKTUAL untuk dikembalikan ke tabel kiri
            String sqlK = "SELECT kuota, (SELECT COUNT(*) FROM krs_detail kd JOIN krs_header kh ON kd.id_krs = kh.id_krs WHERE kd.id_kelas = kelas.id_kelas AND kh.status_validasi != 'Ditolak') AS terdaftar FROM kelas WHERE id_kelas = ?";
            java.sql.ResultSet rsK = db.readDBSafe(sqlK, idKelas);
            if(rsK != null && rsK.next()){
                int k = rsK.getInt("kuota");
                int t = rsK.getInt("terdaftar");
                sisaKuotaStr = (k - t) + " (dari " + k + ")";
            }
        } catch (Exception e) { System.err.println("Error Batal: " + e.getMessage()); }

        // 3. JIKA AMAN, Hapus dari keranjang kanan
        modelKrs.removeRow(baris);
        
        // 4. Kembalikan ke etalase kiri dengan kuota aktual
        modelTersedia.addRow(new Object[]{
            idKelas, kodeMk, mataKuliah, dosen, hari, jam, ruang, sks, sisaKuotaStr
        });
        
        hitungSKS();

    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnAjukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAjukanActionPerformed
        // 1. CEK KERANJANG KOSONG
        if (modelKrs.getRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Keranjang KRS masih kosong!\nSilakan pilih mata kuliah terlebih dahulu.", 
                "Peringatan", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. CEK BATAS MAKSIMAL SKS
        if (totalSks > 24) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Total SKS melebihi batas maksimal (24 SKS)!\nSKS Anda saat ini: " + totalSks + " SKS.\nSilakan batalkan beberapa mata kuliah.", 
                "Peringatan Batas SKS", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        Database db = new Database();
        try {
            // 0. MULAI TRANSAKSI DI AWAL
            if (!db.beginTransaction()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Gagal memulai transaksi sistem!"); return;
            }

            // 1. CEK STATUS KRS SEBELUMNYA
            java.sql.ResultSet rsCek = db.readDBSafe("SELECT id_krs, status_validasi FROM krs_header WHERE nim = ? AND id_periode = ? FOR UPDATE", lblNim.getText(), idPeriodeAktif);
            
            int idKrsLama = -1;
            if (rsCek != null && rsCek.next()) {
                String status = rsCek.getString("status_validasi");
                
                // FIX: Izinkan revisi untuk status Ditolak DAN Sebagian Ditolak
                if (status.equals("Ditolak") || status.equals("Sebagian Ditolak")) {
                    idKrsLama = rsCek.getInt("id_krs"); // Simpan ID untuk direvisi
                } else {
                    db.rollback(); // Wajib rollback
                    javax.swing.JOptionPane.showMessageDialog(this, "Kamu SUDAH mengajukan KRS untuk periode ini.\nStatus saat ini: " + status.toUpperCase()); 
                    return;
                }
            }

            String tgl = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

            // 2. TENTUKAN APAKAH INSERT BARU ATAU UPDATE (REVISI)
            int idKrsBaru = -1;
            if (idKrsLama != -1) {
                // Hanya delete yang Ditolak agar database bersih. Yang Disetujui biarkan di database.
                db.executeDBSafe("DELETE FROM krs_detail WHERE id_krs = ? AND status_detail = 'Ditolak'", idKrsLama); 
                
                boolean ok = db.executeDBSafe("UPDATE krs_header SET total_sks = ?, tgl_pengajuan = ?, status_validasi = 'Menunggu' WHERE id_krs = ?", totalSks, tgl, idKrsLama);
                if (ok) idKrsBaru = idKrsLama; 
            } else {
                String sqlHeader = "INSERT INTO krs_header (total_sks, tgl_pengajuan, status_validasi, nim, id_periode) VALUES (?, ?, 'Menunggu', ?, ?)";
                idKrsBaru = db.insertAndGetId(sqlHeader, totalSks, tgl, lblNim.getText(), idPeriodeAktif);
            }

            if (idKrsBaru != -1) {
                for (int i = 0; i < modelKrs.getRowCount(); i++) {
                    String idKelas = modelKrs.getValueAt(i, 0).toString();
                    
                    // CEK APAKAH KELAS INI BARU DITAMBAHKAN ATAU SUDAH ADA
                    java.sql.ResultSet rsExist = db.readDBSafe("SELECT id_kelas FROM krs_detail WHERE id_krs = ? AND id_kelas = ?", idKrsBaru, idKelas);
                    
                    if (rsExist == null || !rsExist.next()) {
                        // Jika belum ada, Insert ke DB dan Kurangi Kuota
                        db.executeDBSafe("INSERT INTO krs_detail (id_krs, id_kelas, status_detail) VALUES (?, ?, 'Menunggu')", idKrsBaru, idKelas);
                        db.executeDBSafe("UPDATE kelas SET kuota = kuota - 1 WHERE id_kelas = ?", idKelas);
                    }
                }
                
                db.commit();
                javax.swing.JOptionPane.showMessageDialog(this, "Berhasil! KRS kamu telah diajukan dan sedang Menunggu Validasi Dosen Wali.");
                
                // 1. Kosongkan keranjang dan refresh tabel
                modelKrs.setRowCount(0);
                hitungSKS();
                loadKelasTersedia();
                
                // 2. Kunci semua tombol transaksi
                btnAjukan.setEnabled(false);
                btnAmbil.setEnabled(false);
                btnBatal.setEnabled(false);
                
                // 3. Ubah teks label
                if (!lblPeriode.getText().contains("STATUS:")) {
                    lblPeriode.setText(lblPeriode.getText() + " | STATUS: MENUNGGU");
                } else {
                    // Jika merevisi "Sebagian Ditolak", label harus kita timpa jadi Menunggu
                    lblPeriode.setText(lblPeriode.getText().split("\\|")[0] + "| STATUS: MENUNGGU");
                    lblPeriode.setForeground(new java.awt.Color(204, 102, 0)); 
                }
                
            } else {
                throw new Exception("Gagal membuat formulir pengajuan KRS.");
            }
        } catch(Exception e) {
            db.rollback();
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal Mengajukan KRS!\nSemua data dikembalikan seperti semula.\nError: " + e.getMessage());
        }
    }//GEN-LAST:event_btnAjukanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAjukan;
    private javax.swing.JButton btnAmbil;
    private javax.swing.JButton btnBatal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDosenWali;
    private javax.swing.JLabel lblNama;
    private javax.swing.JLabel lblNim;
    private javax.swing.JLabel lblPeriode;
    private javax.swing.JLabel lblTotalSks;
    private javax.swing.JTable tblKelasTersedia;
    private javax.swing.JTable tblKrsSaya;
    // End of variables declaration//GEN-END:variables
}
