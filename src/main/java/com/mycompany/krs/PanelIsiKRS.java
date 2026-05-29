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
            java.sql.ResultSet rsMhs = db.readDBSafe("SELECT nama_mhs FROM mahasiswa WHERE nim = ?", nim);
            if(rsMhs != null && rsMhs.next()) lblNama.setText(rsMhs.getString("nama_mhs"));
            
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
                
            // Kunci tombol keamanan agar tidak terjadi transaksi gaib
            btnAjukan.setEnabled(false); 
            btnAmbil.setEnabled(false);
        }
    }

    private void loadKelasTersedia() {
        modelTersedia.setRowCount(0);
        try {
            Database db = new Database();
            String sql = "SELECT k.id_kelas, mk.kode_mk, mk.nama_mk, d.nama_dosen, k.hari, k.jam, k.ruang, mk.sks, k.kuota " +
                         "FROM kelas k JOIN mata_kuliah mk ON k.kode_mk = mk.kode_mk JOIN dosen d ON k.nidn = d.nidn WHERE k.kuota > 0";
            java.sql.ResultSet rs = db.readDBSafe(sql);
            
            while(rs != null && rs.next()) {
                String idKelas = rs.getString("id_kelas");
                boolean sudahDiambil = false;
                for(int i = 0; i < modelKrs.getRowCount(); i++){
                    if(modelKrs.getValueAt(i, 0).toString().equals(idKelas)){ sudahDiambil = true; break; }
                }
                if(!sudahDiambil) {
                    modelTersedia.addRow(new Object[]{
                        idKelas, rs.getString("kode_mk"), rs.getString("nama_mk"), rs.getString("nama_dosen"),
                        rs.getString("hari"), rs.getString("jam"), rs.getString("ruang"), rs.getString("sks"), rs.getString("kuota")
                    });
                }
            }
        } catch(Exception e) {
            System.err.println("Error muat jadwal kelas: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Gagal memuat daftar kelas tersedia!\nKoneksi database terputus.", 
                "Error Sistem", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cekStatusKRS() {
        if (idPeriodeAktif == null || idPeriodeAktif.isEmpty()) return;
        
        Database db = new Database();
        try {
            // 1. Cek apakah ada KRS di periode ini (Kecuali yang ditolak, karena yang ditolak boleh revisi/isi ulang)
            String sql = "SELECT id_krs, status_validasi, total_sks FROM krs_header WHERE nim = ? AND id_periode = ? AND status_validasi != 'Ditolak'";
            java.sql.ResultSet rs = db.readDBSafe(sql, Login.getUserLogin(), idPeriodeAktif);
            
            if (rs != null && rs.next()) {
                String status = rs.getString("status_validasi");
                String idKrs = rs.getString("id_krs");
                
                // 2. Kunci Tombol agar tidak bisa memanipulasi keranjang lagi
                btnAjukan.setEnabled(false);
                btnAmbil.setEnabled(false);
                btnBatal.setEnabled(false);
                
                // 3. Tampilkan Status di Label Periode
                lblPeriode.setText(lblPeriode.getText() + " | STATUS: " + status.toUpperCase());
                lblTotalSks.setText(rs.getString("total_sks"));
                
                // 4. Tarik data kelas yang sudah diajukan ke tabel keranjang (tblKrsSaya)
                modelKrs.setRowCount(0); // Bersihkan keranjang
                String sqlDetail = "SELECT k.id_kelas, mk.kode_mk, mk.nama_mk, d.nama_dosen, k.hari, k.jam, k.ruang, mk.sks " +
                                   "FROM krs_detail kd " +
                                   "JOIN kelas k ON kd.id_kelas = k.id_kelas " +
                                   "JOIN mata_kuliah mk ON k.kode_mk = mk.kode_mk " +
                                   "JOIN dosen d ON k.nidn = d.nidn " +
                                   "WHERE kd.id_krs = ?";
                java.sql.ResultSet rsDetail = db.readDBSafe(sqlDetail, idKrs);
                
                while (rsDetail != null && rsDetail.next()) {
                    modelKrs.addRow(new Object[]{
                        rsDetail.getString("id_kelas"), rsDetail.getString("kode_mk"), 
                        rsDetail.getString("nama_mk"), rsDetail.getString("nama_dosen"), 
                        rsDetail.getString("hari"), rsDetail.getString("jam"), 
                        rsDetail.getString("ruang"), rsDetail.getString("sks")
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Error cek status KRS: " + e.getMessage());
        }
    }
    
    // FUNGSI BARU UNTUK MENAMPILKAN CATATAN PENOLAKAN
    private void cekKrsDitolak() {
        if (idPeriodeAktif == null || idPeriodeAktif.isEmpty()) return;
        
        try {
            Database db = new Database();
            String sql = "SELECT catatan_dosen FROM krs_header WHERE nim = ? AND id_periode = ? AND status_validasi = 'Ditolak'";
            java.sql.ResultSet rs = db.readDBSafe(sql, Login.getUserLogin(), idPeriodeAktif);
            
            if (rs != null && rs.next()) {
                String catatan = rs.getString("catatan_dosen");
                
                // Ubah label atas jadi warna merah peringatan
                lblPeriode.setText(lblPeriode.getText() + " | STATUS: DITOLAK (HARUS REVISI)");
                lblPeriode.setForeground(java.awt.Color.RED);
                
                // Munculkan pop-up khusus catatan dosen
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "KRS kamu sebelumnya DITOLAK oleh Dosen Wali.\n\nCatatan Dosen:\n\"" + (catatan != null && !catatan.trim().isEmpty() ? catatan : "Tidak ada catatan tambahan.") + "\"\n\nSilakan perbaiki jadwalmu dan ajukan ulang.", 
                    "KRS Ditolak", 
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("Error cek KRS ditolak: " + e.getMessage());
        }
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
        loadKelasTersedia();
        cekStatusKRS();
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
                                .addGap(1, 1, 1)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPeriode))
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
                                .addComponent(lblNim)))
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
                        .addComponent(btnBatal))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
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
        
        // 1. Simpan data baris yang mau dihapus dari keranjang ke variabel memori
        Object idKelas = modelKrs.getValueAt(baris, 0);
        Object kodeMk = modelKrs.getValueAt(baris, 1);
        Object mataKuliah = modelKrs.getValueAt(baris, 2);
        Object dosen = modelKrs.getValueAt(baris, 3);
        Object hari = modelKrs.getValueAt(baris, 4);
        Object jam = modelKrs.getValueAt(baris, 5);
        Object ruang = modelKrs.getValueAt(baris, 6);
        Object sks = modelKrs.getValueAt(baris, 7);
        
        // 2. Hapus dari tabel keranjang
        modelKrs.removeRow(baris);
        
        // 3. Ambil nilai kuota asli dari database agar akurat
        String kuotaAsli = "0";
        try {
            Database db = new Database();
            // Kueri sangat ringan karena memanggil berdasarkan Primary Key (id_kelas)
            java.sql.ResultSet rsKuota = db.readDBSafe("SELECT kuota FROM kelas WHERE id_kelas = ?", idKelas);
            if (rsKuota != null && rsKuota.next()) {
                kuotaAsli = rsKuota.getString("kuota");
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat kuota asli: " + e.getMessage());
        }

        // 4. Kembalikan data tersebut ke tabel etalase (kiri) dengan kuota yang BENAR
        modelTersedia.addRow(new Object[]{
            idKelas, kodeMk, mataKuliah, dosen, hari, jam, ruang, sks, kuotaAsli
        });
        
        // 5. Hitung ulang total SKS
        hitungSKS();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnAjukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAjukanActionPerformed
        // TODO add your handling code here:
       // 1. CEK KERANJANG KOSONG (Solusi Bug #4)
        if (modelKrs.getRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Keranjang KRS masih kosong!\nSilakan pilih mata kuliah terlebih dahulu.", 
                "Peringatan", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. CEK BATAS MAKSIMAL SKS (Solusi Bug #4)
        if (totalSks > 24) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Total SKS melebihi batas maksimal (24 SKS)!\nSKS Anda saat ini: " + totalSks + " SKS.\nSilakan batalkan beberapa mata kuliah.", 
                "Peringatan Batas SKS", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        Database db = new Database();
        try {
            // 0. MULAI TRANSAKSI DI AWAL AGAR DATA TERKUNCI DARI KLIK GANDA (Pindah ke atas!)
            if (!db.beginTransaction()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Gagal memulai transaksi sistem!"); return;
            }

            // 1. CEK STATUS KRS SEBELUMNYA (Sekarang dilindungi transaksi & FOR UPDATE)
            java.sql.ResultSet rsCek = db.readDBSafe("SELECT id_krs, status_validasi FROM krs_header WHERE nim = ? AND id_periode = ? FOR UPDATE", lblNim.getText(), idPeriodeAktif);
            
            int idKrsLama = -1;
            if (rsCek != null && rsCek.next()) {
                String status = rsCek.getString("status_validasi");
                if (status.equals("Ditolak")) {
                    idKrsLama = rsCek.getInt("id_krs"); // Simpan ID untuk direvisi
                } else {
                    db.rollback(); // Wajib rollback karena transaksi sudah dimulai!
                    javax.swing.JOptionPane.showMessageDialog(this, "Kamu SUDAH mengajukan KRS untuk periode ini.\nStatus saat ini: " + status); 
                    return;
                }
            }

            String tgl = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

            // 2. TENTUKAN APAKAH INSERT BARU ATAU UPDATE (REVISI)
            int idKrsBaru = -1;
            if (idKrsLama != -1) {
                // Jalur Revisi (KRS pernah ditolak sebelumnya)
                // TAMBAHAN: Bersihkan dulu detail lama di database agar tidak ada data hantu/ganda
                db.executeDBSafe("DELETE FROM krs_detail WHERE id_krs = ?", idKrsLama); 
                
                boolean ok = db.executeDBSafe("UPDATE krs_header SET total_sks = ?, tgl_pengajuan = ?, status_validasi = 'Menunggu' WHERE id_krs = ?", totalSks, tgl, idKrsLama);
                if (ok) idKrsBaru = idKrsLama; // Daur ulang ID yang lama
            } else {
                // Jalur Normal (Baru pertama kali submit)
                String sqlHeader = "INSERT INTO krs_header (total_sks, tgl_pengajuan, status_validasi, nim, id_periode) VALUES (?, ?, 'Menunggu', ?, ?)";
                idKrsBaru = db.insertAndGetId(sqlHeader, totalSks, tgl, lblNim.getText(), idPeriodeAktif);
            }

            // Lanjut ke penyimpanan detail kelas...
            if(idKrsBaru != -1) {
                for(int i=0; i<modelKrs.getRowCount(); i++){
                    String idKelas = modelKrs.getValueAt(i, 0).toString();
                    
                    boolean detailOk = db.executeDBSafe("INSERT INTO krs_detail (id_krs, id_kelas) VALUES (?, ?)", idKrsBaru, idKelas);
                    
                    // ---> TAMBAHKAN 'AND kuota > 0' DI SINI:
                    boolean kuotaOk = db.executeDBSafe("UPDATE kelas SET kuota = kuota - 1 WHERE id_kelas = ? AND kuota > 0", idKelas);
                    
                    if (!detailOk || !kuotaOk) {
                        throw new Exception("Gagal menyimpan jadwal. Kuota kelas ID " + idKelas + " mungkin sudah penuh direbut mahasiswa lain!");
                    }
                }   
                
                // 2. JIKA SEMUA LOOP AMAN, SIMPAN PERMANEN
                db.commit(); 
                javax.swing.JOptionPane.showMessageDialog(this, "Berhasil! KRS kamu telah diajukan dan sedang Menunggu Validasi Dosen Wali.");
                
                // 1. Kosongkan keranjang dan refresh tabel
                modelKrs.setRowCount(0);
                hitungSKS();
                loadKelasTersedia();
                
                // 2. TAMBAHAN BUG #3: Kunci semua tombol transaksi agar tidak diklik ganda
                btnAjukan.setEnabled(false);
                btnAmbil.setEnabled(false);
                btnBatal.setEnabled(false);
                
                // 3. TAMBAHAN BUG #3: Ubah teks label agar mahasiswa langsung sadar statusnya
                if (!lblPeriode.getText().contains("STATUS:")) {
                    lblPeriode.setText(lblPeriode.getText() + " | STATUS: MENUNGGU");
                }
                
            } else {
                throw new Exception("Gagal membuat formulir pengajuan KRS.");
            }
        } catch(Exception e) {
            // 3. JIKA ADA ERROR DI TENGAH JALAN, BATALKAN SEMUA (ROLLBACK)
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblNama;
    private javax.swing.JLabel lblNim;
    private javax.swing.JLabel lblPeriode;
    private javax.swing.JLabel lblTotalSks;
    private javax.swing.JTable tblKelasTersedia;
    private javax.swing.JTable tblKrsSaya;
    // End of variables declaration//GEN-END:variables
}
