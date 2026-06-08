/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.krs;

/**
 *
 * @author User
 */
public class PanelKelas extends javax.swing.JPanel {

    /**
     * Creates new form PanelKelas
     */
    public PanelKelas() {
        initComponents();
        
        // PANGGIL 2 FUNGSI INI DI SINI:
        loadComboBox(); // Untuk mengisi data Matkul dan Dosen
        tampilData();   // Untuk mengisi data Tabel di bawah
    }
// Variabel penampung ID Kelas saat baris tabel diklik
    private String idKelasTerpilih = "";

    private void loadComboBox() {
        try {
            Database db = new Database();
            // 1. Load Matkul
            java.sql.ResultSet rsMk = db.readDBSafe("SELECT kode_mk, nama_mk FROM mata_kuliah");
            cbMatkul.removeAllItems();
            while (rsMk != null && rsMk.next()) { cbMatkul.addItem(rsMk.getString("kode_mk") + " - " + rsMk.getString("nama_mk")); }

            // 2. Load Dosen
            java.sql.ResultSet rsDosen = db.readDBSafe("SELECT nidn, nama_dosen FROM dosen");
            cbDosen.removeAllItems();
            while (rsDosen != null && rsDosen.next()) { cbDosen.addItem(rsDosen.getString("nidn") + " - " + rsDosen.getString("nama_dosen")); }
            
            // 3. FITUR BARU: Load Periode
            java.sql.ResultSet rsPeriode = db.readDBSafe("SELECT id_periode, semester, tahun_ajaran FROM periode ORDER BY id_periode DESC");
            cbPeriode.removeAllItems();
            while (rsPeriode != null && rsPeriode.next()) { 
                cbPeriode.addItem(rsPeriode.getString("id_periode") + "-" + rsPeriode.getString("semester") + " " + rsPeriode.getString("tahun_ajaran")); 
            }
        } catch (Exception e) { System.err.println("Terjadi Error: " + e.getMessage()); }
    }

    private void tampilData() {
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel();
        model.addColumn("ID Kelas"); model.addColumn("Mata Kuliah"); model.addColumn("Dosen");
        model.addColumn("Ruang"); model.addColumn("Hari"); model.addColumn("Jam"); model.addColumn("Kuota"); model.addColumn("Periode");
        tblKelas.setModel(model);
        try {
            Database db = new Database();
            // Triple JOIN: Menggabungkan tabel kelas, mata_kuliah, dosen, dan periode
            String sql = "SELECT k.id_kelas, k.kode_mk, mk.nama_mk, k.nidn, d.nama_dosen, " +
                         "k.ruang, k.hari, k.jam, k.kuota, k.id_periode, p.semester, p.tahun_ajaran " +
                         "FROM kelas k " +
                         "JOIN mata_kuliah mk ON k.kode_mk = mk.kode_mk " +
                         "JOIN dosen d ON k.nidn = d.nidn " +
                         "LEFT JOIN periode p ON k.id_periode = p.id_periode " +
                         "ORDER BY k.id_kelas DESC";
                         
            java.sql.ResultSet rs = db.readDBSafe(sql);
            while (rs != null && rs.next()) {
                // Rangkai teks Mata Kuliah
                String matkulTampil = rs.getString("kode_mk") + " - " + rs.getString("nama_mk");
                // Rangkai teks Dosen
                String dosenTampil = rs.getString("nidn") + " - " + rs.getString("nama_dosen");
                // Rangkai teks Periode
                String periodeTampil = rs.getString("id_periode") != null ? 
                                       rs.getString("id_periode") + "-" + rs.getString("semester") + " " + rs.getString("tahun_ajaran") : 
                                       "Belum Diatur";

                model.addRow(new Object[]{ 
                    rs.getString("id_kelas"), 
                    matkulTampil, 
                    dosenTampil, 
                    rs.getString("ruang"), 
                    rs.getString("hari"), 
                    rs.getString("jam"), 
                    rs.getString("kuota"),
                    periodeTampil
                });
            }
        } catch (Exception e) { System.err.println("Terjadi Error di tampilData kelas: " + e.getMessage()); }
    }
    // FUNGSI KHUSUS UNTUK MEMBACA IRISAN WAKTU
    private boolean cekBentrokWaktu(String jamBaru, String jamAda) {
        try {
            // Bersihkan spasi dan ubah titik jadi pemisah jam:menit
            String jB = jamBaru.replace(" ", "").replace(".", ":");
            String jA = jamAda.replace(" ", "").replace(".", ":");
            
            // Konversi ke total menit agar gampang dihitung (misal 08:30 = 8*60 + 30 = 510)
            int start1 = (Integer.parseInt(jB.split("-")[0].split(":")[0]) * 60) + Integer.parseInt(jB.split("-")[0].split(":")[1]);
            int end1 = (Integer.parseInt(jB.split("-")[1].split(":")[0]) * 60) + Integer.parseInt(jB.split("-")[1].split(":")[1]);
            
            int start2 = (Integer.parseInt(jA.split("-")[0].split(":")[0]) * 60) + Integer.parseInt(jA.split("-")[0].split(":")[1]);
            int end2 = (Integer.parseInt(jA.split("-")[1].split(":")[0]) * 60) + Integer.parseInt(jA.split("-")[1].split(":")[1]);

            // Rumus irisan waktu: Mulai_1 lebih awal dari Selesai_2 DAN Mulai_2 lebih awal dari Selesai_1
            return start1 < end2 && start2 < end1;
        } catch (Exception e) {
            // Kalau admin ketiknya ngawur (ga pakai strip "-"), balik ke pengecekan teks biasa
            return jamBaru.equalsIgnoreCase(jamAda);
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

        jLabel1 = new javax.swing.JLabel();
        cbMatkul = new javax.swing.JComboBox<>();
        cbDosen = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtFilterMatkul = new javax.swing.JTextField();
        txtFilterDosen = new javax.swing.JTextField();
        txtRuang = new javax.swing.JTextField();
        txtJam = new javax.swing.JTextField();
        txtKuota = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cbHari = new javax.swing.JComboBox<>();
        btnSimpan = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKelas = new javax.swing.JTable();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        cbPeriode = new javax.swing.JComboBox<>();

        jLabel1.setText("Kelola Kelas");

        cbMatkul.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbDosen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Mata Kuliah");

        jLabel3.setText("Dosen");

        jLabel4.setText("Filter :");

        jLabel5.setText("Filter :");

        txtFilterMatkul.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilterMatkulActionPerformed(evt);
            }
        });
        txtFilterMatkul.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterMatkulKeyReleased(evt);
            }
        });

        txtFilterDosen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFilterDosenKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterDosenKeyReleased(evt);
            }
        });

        txtRuang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRuangActionPerformed(evt);
            }
        });

        jLabel6.setText("Ruangan");

        jLabel7.setText("Jam");

        jLabel8.setText("Kuota");

        jLabel9.setText("Hari");

        cbHari.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "Senin", "Selasa", "Rabu", "Kamis", "Jumat" }));

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        tblKelas.setModel(new javax.swing.table.DefaultTableModel(
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
        tblKelas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKelasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblKelas);

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        jLabel10.setText("Periode");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(171, 171, 171)
                                .addComponent(jLabel1))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnSimpan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnHapus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnReset)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCari))
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbPeriode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(cbMatkul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jLabel4)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtFilterMatkul, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(cbDosen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jLabel5)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtFilterDosen))
                                        .addComponent(txtRuang)
                                        .addComponent(txtJam)
                                        .addComponent(txtKuota))
                                    .addComponent(cbHari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbMatkul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(txtFilterMatkul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbDosen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(txtFilterDosen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRuang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtJam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtKuota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cbPeriode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(cbHari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCari))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(btnHapus)
                    .addComponent(btnReset)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
// 1. VALIDASI COMBOBOX
        if (cbMatkul.getSelectedItem() == null || cbDosen.getSelectedItem() == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Data Mata Kuliah dan Dosen belum tersedia atau belum dipilih!", "Peringatan", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 1.5 FITUR BARU: VALIDASI COMBOBOX PERIODE
        if (cbPeriode.getSelectedItem() == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Data Periode belum tersedia! Buat periode terlebih dahulu.", "Peringatan", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String kodeMk = cbMatkul.getSelectedItem().toString().split(" - ")[0];
        String nidn = cbDosen.getSelectedItem().toString().split(" - ")[0];
        String idPeriode = cbPeriode.getSelectedItem().toString().split("-")[0]; // Ambil ID Periode
        
        String ruang = txtRuang.getText().trim(); 
        String jam = txtJam.getText().trim();
        String hari = cbHari.getSelectedItem().toString(); 
        String kuotaStr = txtKuota.getText().trim();

        // 2. VALIDASI INPUT KOSONG
        if (ruang.isEmpty() || jam.isEmpty() || kuotaStr.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Kolom Ruang, Jam, dan Kuota tidak boleh kosong!", "Peringatan", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 3. VALIDASI FORMAT JAM
        if (!jam.matches("^\\d{2}[:.]\\d{2}\\s*-\\s*\\d{2}[:.]\\d{2}$")) {
            javax.swing.JOptionPane.showMessageDialog(this, "Format Jam salah! Gunakan: 08:00 - 10:30", "Peringatan Format", javax.swing.JOptionPane.WARNING_MESSAGE); 
            return;
        }

        // 4. VALIDASI KUOTA (Harus Angka & Lebih dari 0)
        int kuota;
        try { 
            kuota = Integer.parseInt(kuotaStr); 
            if (kuota <= 0) {
                javax.swing.JOptionPane.showMessageDialog(this, "Jumlah kuota kelas harus lebih dari 0!", "Validasi Kuota", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) { 
            javax.swing.JOptionPane.showMessageDialog(this, "Kolom Kuota harus diisi dengan angka bulat!", "Peringatan Format", javax.swing.JOptionPane.WARNING_MESSAGE);
            return; 
        }

        // --- KODINGAN DATABASE ---
        Database db = new Database();
        try {
            // SATPAM ANTI-BENTROK YANG LEBIH CERDAS (Ditambah id_periode agar beda semester boleh pakai ruang/dosen di jam sama)
            String queryRuang = "SELECT jam FROM kelas WHERE ruang = ? AND hari = ? AND id_periode = ?";
            String queryDosen = "SELECT jam FROM kelas WHERE nidn = ? AND hari = ? AND id_periode = ?";
            java.sql.ResultSet rsRuang, rsDosen;

            if (btnSimpan.getText().equals("Ubah Data")) {
                queryRuang += " AND id_kelas != ?"; queryDosen += " AND id_kelas != ?";
                rsRuang = db.readDBSafe(queryRuang, ruang, hari, idPeriode, idKelasTerpilih);
                rsDosen = db.readDBSafe(queryDosen, nidn, hari, idPeriode, idKelasTerpilih);
            } else {
                rsRuang = db.readDBSafe(queryRuang, ruang, hari, idPeriode);
                rsDosen = db.readDBSafe(queryDosen, nidn, hari, idPeriode);
            }

            while (rsRuang != null && rsRuang.next()) {
                if (cekBentrokWaktu(jam, rsRuang.getString("jam"))) {
                    javax.swing.JOptionPane.showMessageDialog(this, "BENTROK! Ruang dipakai jam " + rsRuang.getString("jam") + " pada periode ini"); return;
                }
            }
            while (rsDosen != null && rsDosen.next()) {
                if (cekBentrokWaktu(jam, rsDosen.getString("jam"))) {
                    javax.swing.JOptionPane.showMessageDialog(this, "BENTROK! Dosen mengajar jam " + rsDosen.getString("jam") + " pada periode ini"); return;
                }
            }

            // EKSEKUSI SIMPAN / UBAH
            if (btnSimpan.getText().equals("Simpan")) {
                // Kueri INSERT baru (ditambah id_periode di akhir)
                if (db.executeDBSafe("INSERT INTO kelas (kode_mk, nidn, ruang, hari, jam, kuota, id_periode) VALUES (?, ?, ?, ?, ?, ?, ?)", kodeMk, nidn, ruang, hari, jam, kuota, idPeriode)) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Kelas Dibuka!"); btnResetActionPerformed(evt);
                }
            } else {
                // 1. FIX BUG #8: CEK JUMLAH MAHASISWA YANG SUDAH TERDAFTAR
                java.sql.ResultSet rsTerdaftar = db.readDBSafe("SELECT COUNT(id_krs) AS terdaftar FROM krs_detail WHERE id_kelas = ?", idKelasTerpilih);
                int jumlahTerdaftar = 0;
                
                if (rsTerdaftar != null && rsTerdaftar.next()) {
                    jumlahTerdaftar = rsTerdaftar.getInt("terdaftar");
                }
                
                // 2. BLOKIR JIKA KUOTA BARU LEBIH KECIL DARI YANG SUDAH DAFTAR
                if (kuota < jumlahTerdaftar) {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "GAGAL MENGUBAH KUOTA!\nKelas ini sudah diisi oleh " + jumlahTerdaftar + " mahasiswa.\nKuota baru (" + kuota + ") tidak boleh lebih kecil dari jumlah mahasiswa yang sudah terdaftar.", 
                        "Validasi Kapasitas", 
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                    return; // Hentikan proses update
                }

                // 3. JIKA AMAN, LANJUTKAN UPDATE (Kueri ditambah id_periode)
                if (db.executeDBSafe("UPDATE kelas SET kode_mk=?, nidn=?, ruang=?, hari=?, jam=?, kuota=?, id_periode=? WHERE id_kelas=?", kodeMk, nidn, ruang, hari, jam, kuota, idPeriode, idKelasTerpilih)) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Kelas Diperbarui!"); btnResetActionPerformed(evt);
                }
            }
        } catch (Exception e) { System.err.println("Terjadi Error: " + e.getMessage()); }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void txtRuangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRuangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRuangActionPerformed

    private void tblKelasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKelasMouseClicked
        int baris = tblKelas.getSelectedRow();
        if (baris != -1) {
            idKelasTerpilih = tblKelas.getValueAt(baris, 0).toString();
            
            // Set ComboBox (Indeks 1 dan 2 langsung di-set karena teksnya sudah sama persis!)
            cbMatkul.setSelectedItem(tblKelas.getValueAt(baris, 1).toString());
            cbDosen.setSelectedItem(tblKelas.getValueAt(baris, 2).toString());
            
            // Set Teks Biasa
            txtRuang.setText(tblKelas.getValueAt(baris, 3).toString());
            cbHari.setSelectedItem(tblKelas.getValueAt(baris, 4).toString());
            txtJam.setText(tblKelas.getValueAt(baris, 5).toString());
            txtKuota.setText(tblKelas.getValueAt(baris, 6).toString());
            
            // Set ComboBox Periode (Indeks ke-7)
            if (tblKelas.getColumnCount() > 7 && tblKelas.getValueAt(baris, 7) != null) {
                cbPeriode.setSelectedItem(tblKelas.getValueAt(baris, 7).toString());
            }
            
            // Ubah tombol jadi "Ubah Data"
            btnSimpan.setText("Ubah Data");
        }
    }//GEN-LAST:event_tblKelasMouseClicked

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        txtRuang.setText("");
        txtJam.setText("");
        txtKuota.setText("");
        txtCari.setText("");
        txtFilterMatkul.setText("");
        txtFilterDosen.setText("");
        
        if (cbHari.getItemCount() > 0) cbHari.setSelectedIndex(0);
        idKelasTerpilih = "";
        
        loadComboBox();
        tampilData();
        tblKelas.clearSelection();
        btnSimpan.setText("Simpan");
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
       if (idKelasTerpilih.isEmpty()) return;
        if (javax.swing.JOptionPane.showConfirmDialog(this, "Yakin Hapus?", "Konfirmasi", 0) == 0) {
            Database db = new Database();
            if (db.executeDBSafe("DELETE FROM kelas WHERE id_kelas = ?", idKelasTerpilih)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Jadwal dihapus!"); btnResetActionPerformed(evt);
            }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        String keyword = "%" + txtCari.getText().trim() + "%";
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tblKelas.getModel();
        model.setRowCount(0); 
        
        try {
            Database db = new Database();
            // Kueri pencarian yang lebih pintar (bisa cari nama dosen/matkul)
            String sql = "SELECT k.id_kelas, k.kode_mk, mk.nama_mk, k.nidn, d.nama_dosen, " +
                         "k.ruang, k.hari, k.jam, k.kuota, k.id_periode, p.semester, p.tahun_ajaran " +
                         "FROM kelas k " +
                         "JOIN mata_kuliah mk ON k.kode_mk = mk.kode_mk " +
                         "JOIN dosen d ON k.nidn = d.nidn " +
                         "LEFT JOIN periode p ON k.id_periode = p.id_periode " +
                         "WHERE mk.nama_mk LIKE ? OR d.nama_dosen LIKE ? OR k.ruang LIKE ?";
                         
            java.sql.ResultSet rs = db.readDBSafe(sql, keyword, keyword, keyword);
            
            while (rs != null && rs.next()) {
                String matkulTampil = rs.getString("kode_mk") + " - " + rs.getString("nama_mk");
                String dosenTampil = rs.getString("nidn") + " - " + rs.getString("nama_dosen");
                String periodeTampil = rs.getString("id_periode") != null ? 
                                       rs.getString("id_periode") + "-" + rs.getString("semester") + " " + rs.getString("tahun_ajaran") : 
                                       "Belum Diatur";

                model.addRow(new Object[]{ 
                    rs.getString("id_kelas"), matkulTampil, dosenTampil, 
                    rs.getString("ruang"), rs.getString("hari"), 
                    rs.getString("jam"), rs.getString("kuota"), periodeTampil
                });
            }
            if (model.getRowCount() == 0) tampilData();
        } catch (Exception e) { System.err.println("Terjadi Error di pencarian kelas: " + e.getMessage()); }
    }//GEN-LAST:event_btnCariActionPerformed

    private void txtFilterMatkulKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterMatkulKeyReleased
        // TODO add your handling code here:
     String keyword = "%" + txtFilterMatkul.getText().trim() + "%";
        try {
            Database db = new Database();
            java.sql.ResultSet rs = db.readDBSafe("SELECT kode_mk, nama_mk FROM mata_kuliah WHERE nama_mk LIKE ? OR kode_mk LIKE ?", keyword, keyword);
            cbMatkul.removeAllItems();
            while (rs != null && rs.next()) { cbMatkul.addItem(rs.getString("kode_mk") + " - " + rs.getString("nama_mk")); }
        } catch (Exception e) { System.err.println("Terjadi Error: " + e.getMessage()); }
    }//GEN-LAST:event_txtFilterMatkulKeyReleased

    private void txtFilterDosenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterDosenKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterDosenKeyPressed

    private void txtFilterDosenKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterDosenKeyReleased
        // TODO add your handling code here:
        String keyword = "%" + txtFilterDosen.getText().trim() + "%";
        try {
            Database db = new Database();
            java.sql.ResultSet rs = db.readDBSafe("SELECT nidn, nama_dosen FROM dosen WHERE nama_dosen LIKE ?", keyword);
            cbDosen.removeAllItems();
            while (rs != null && rs.next()) { cbDosen.addItem(rs.getString("nidn") + " - " + rs.getString("nama_dosen")); }
        } catch (Exception e) { System.err.println("Terjadi Error: " + e.getMessage()); }
    }//GEN-LAST:event_txtFilterDosenKeyReleased

    private void txtFilterMatkulActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilterMatkulActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterMatkulActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox<String> cbDosen;
    private javax.swing.JComboBox<String> cbHari;
    private javax.swing.JComboBox<String> cbMatkul;
    private javax.swing.JComboBox<String> cbPeriode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblKelas;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtFilterDosen;
    private javax.swing.JTextField txtFilterMatkul;
    private javax.swing.JTextField txtJam;
    private javax.swing.JTextField txtKuota;
    private javax.swing.JTextField txtRuang;
    // End of variables declaration//GEN-END:variables
}
