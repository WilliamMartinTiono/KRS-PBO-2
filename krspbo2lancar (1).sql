-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 08, 2026 at 10:21 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `krspbo2lancar`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id_admin` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`id_admin`, `username`, `password`) VALUES
(1, 'admin1', '37f419999f066dcf9fa98c41cfdd1e1bac9485ad65280996e3635fea1b331fd9'),
(20, '2601', '14cb73ee51ff3deeb94dd7db4277cdd4c0b96c6fdc7c87d388b7547e8b15b0da'),
(21, '2602', '7b5e6ddd828746cae1d46d828fb95d28bace3cd5967019fbe4ea17c59a615014'),
(22, '2603', 'f565d5f66e8237dd7871f28d7390df8f3ba7b9fd17dc4329b54f21cb842ee20c'),
(23, '26001', '782ff6c9a7d076172dccb71ad7cb72420499b665a958f4fc37331a12d64ca534'),
(24, '26002', '61b7ccdb512a7d8fcba8112acc60d6c2b84924f5307ce446a436b4b369576a72'),
(25, 'testadmin', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3'),
(26, '26003', 'b8d3c92f81f5fa2abd8e3b83e0a3a4c496f8337f1f6dcb5b8a0cb809ffbacdb4');

-- --------------------------------------------------------

--
-- Table structure for table `dosen`
--

CREATE TABLE `dosen` (
  `nidn` varchar(20) NOT NULL,
  `nama_dosen` varchar(100) NOT NULL,
  `id_prodi` int(11) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `dosen`
--

INSERT INTO `dosen` (`nidn`, `nama_dosen`, `id_prodi`, `id_user`) VALUES
('2601', 'Irfan', 1, 20),
('2602', 'Persie', 3, 21),
('2603', 'Hendro', 2, 22);

-- --------------------------------------------------------

--
-- Table structure for table `kelas`
--

CREATE TABLE `kelas` (
  `id_kelas` int(11) NOT NULL,
  `hari` varchar(20) DEFAULT NULL,
  `jam` varchar(20) DEFAULT NULL,
  `ruang` varchar(50) DEFAULT NULL,
  `kuota` int(11) DEFAULT NULL,
  `id_periode` int(11) DEFAULT NULL,
  `kode_mk` varchar(20) DEFAULT NULL,
  `nidn` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `kelas`
--

INSERT INTO `kelas` (`id_kelas`, `hari`, `jam`, `ruang`, `kuota`, `id_periode`, `kode_mk`, `nidn`) VALUES
(8, 'Selasa', '06.00-07.00', '1.05', 39, 2, 'MK1002', '2603'),
(9, 'Selasa', '05.00-07.00', '1.01', 37, 2, 'MK1005', '2602'),
(10, 'Selasa', '05.00-08.00', '1.02', 39, 2, 'MK1004', '2603'),
(11, 'Senin', '15.00-17.00', '1.04', 39, 2, 'MK1004', '2601');

-- --------------------------------------------------------

--
-- Table structure for table `krs_detail`
--

CREATE TABLE `krs_detail` (
  `id_krs` int(11) NOT NULL,
  `id_kelas` int(11) NOT NULL,
  `status_detail` varchar(20) NOT NULL DEFAULT 'Menunggu',
  `catatan_mk` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `krs_detail`
--

INSERT INTO `krs_detail` (`id_krs`, `id_kelas`, `status_detail`, `catatan_mk`) VALUES
(4, 8, 'Menunggu', NULL),
(6, 9, 'Menunggu', NULL),
(7, 10, 'Menunggu', NULL),
(8, 9, 'Menunggu', NULL),
(9, 9, 'Disetujui', NULL),
(9, 11, 'Disetujui', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `krs_header`
--

CREATE TABLE `krs_header` (
  `id_krs` int(11) NOT NULL,
  `total_sks` int(11) DEFAULT NULL,
  `tgl_pengajuan` date DEFAULT NULL,
  `status_validasi` varchar(20) DEFAULT NULL,
  `catatan_dosen` text DEFAULT NULL,
  `nim` varchar(20) DEFAULT NULL,
  `id_periode` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `krs_header`
--

INSERT INTO `krs_header` (`id_krs`, `total_sks`, `tgl_pengajuan`, `status_validasi`, `catatan_dosen`, `nim`, `id_periode`) VALUES
(3, 6, '2026-05-21', 'Menunggu', NULL, NULL, 2),
(4, 3, '2026-05-29', 'Disetujui', NULL, '26001', 4),
(5, 3, '2026-05-29', 'Ditolak', NULL, '26002', 4),
(6, 3, '2026-05-29', 'Disetujui', 'pengen aja', '26003', 4),
(7, 4, '2026-06-05', 'Disetujui', NULL, '26001', 2),
(8, 3, '2026-06-05', 'Disetujui', NULL, '26003', 2),
(9, 7, '2026-06-05', 'Disetujui', '', '26002', 2);

-- --------------------------------------------------------

--
-- Table structure for table `mahasiswa`
--

CREATE TABLE `mahasiswa` (
  `nim` varchar(20) NOT NULL,
  `angkatan` year(4) DEFAULT NULL,
  `semester_aktif` tinyint(3) UNSIGNED DEFAULT 1,
  `status_mahasiswa` varchar(20) DEFAULT 'Aktif',
  `nama_mhs` varchar(100) NOT NULL,
  `alamat` text DEFAULT NULL,
  `id_prodi` int(11) DEFAULT NULL,
  `id_dosen_wali` varchar(20) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mahasiswa`
--

INSERT INTO `mahasiswa` (`nim`, `angkatan`, `semester_aktif`, `status_mahasiswa`, `nama_mhs`, `alamat`, `id_prodi`, `id_dosen_wali`, `id_user`) VALUES
('26001', NULL, 1, 'Aktif', 'William Martin Tiono', 'asd', 3, '2602', 23),
('26002', NULL, 1, 'Aktif', 'Christ', '123123123', 2, '2603', 24),
('26003', NULL, 1, 'Aktif', 'Pris', 'asd', 4, '2601', 26);

-- --------------------------------------------------------

--
-- Table structure for table `mata_kuliah`
--

CREATE TABLE `mata_kuliah` (
  `kode_mk` varchar(20) NOT NULL,
  `nama_mk` varchar(100) NOT NULL,
  `sks` int(11) DEFAULT NULL,
  `semester` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mata_kuliah`
--

INSERT INTO `mata_kuliah` (`kode_mk`, `nama_mk`, `sks`, `semester`) VALUES
('MK1001', 'PBO II', 3, 4),
('MK1002', 'DC', 3, 4),
('MK1003', 'STATIS', 3, 4),
('MK1004', 'MAKANas', 4, 4),
('MK1005', 'PERHOTAL', 3, 4);

-- --------------------------------------------------------

--
-- Table structure for table `mata_kuliah_prasyarat`
--

CREATE TABLE `mata_kuliah_prasyarat` (
  `id_prasyarat` int(11) NOT NULL,
  `id_mk_utama` varchar(20) DEFAULT NULL,
  `id_mk_prasyarat` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `nilai`
--

CREATE TABLE `nilai` (
  `id_nilai` int(11) NOT NULL,
  `grade` varchar(5) DEFAULT NULL,
  `angka_nilai` decimal(5,2) DEFAULT NULL,
  `id_periode` int(11) DEFAULT NULL,
  `nim` varchar(20) DEFAULT NULL,
  `kode_mk` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `pembayaran`
--

CREATE TABLE `pembayaran` (
  `id_bayar` int(11) NOT NULL,
  `status_lunas` varchar(20) DEFAULT 'Belum Lunas',
  `tgl_bayar` date DEFAULT NULL,
  `nominal` decimal(12,0) DEFAULT NULL,
  `keterangan` varchar(200) DEFAULT NULL,
  `id_periode` int(11) DEFAULT NULL,
  `nim` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `periode`
--

CREATE TABLE `periode` (
  `id_periode` int(11) NOT NULL,
  `tahun_ajaran` varchar(20) DEFAULT NULL,
  `semester` varchar(10) DEFAULT NULL,
  `status_krs` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `periode`
--

INSERT INTO `periode` (`id_periode`, `tahun_ajaran`, `semester`, `status_krs`) VALUES
(2, '2024/2025', 'Ganjil', 'Buka'),
(3, '2025/2026', 'Ganjil', 'Tutup'),
(4, '2026/2027', 'Ganjil', 'Tutup'),
(5, '2024/2025', 'Genap', 'Tutup'),
(6, '2025/2026', 'Genap', 'Tutup');

-- --------------------------------------------------------

--
-- Table structure for table `prodi`
--

CREATE TABLE `prodi` (
  `id_prodi` int(11) NOT NULL,
  `nama_prodi` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `prodi`
--

INSERT INTO `prodi` (`id_prodi`, `nama_prodi`) VALUES
(1, 'BD'),
(2, 'KWU'),
(3, 'STI'),
(4, 'ertre');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id_admin`);

--
-- Indexes for table `dosen`
--
ALTER TABLE `dosen`
  ADD PRIMARY KEY (`nidn`),
  ADD KEY `id_prodi` (`id_prodi`),
  ADD KEY `id_user` (`id_user`);

--
-- Indexes for table `kelas`
--
ALTER TABLE `kelas`
  ADD PRIMARY KEY (`id_kelas`),
  ADD KEY `kode_mk` (`kode_mk`),
  ADD KEY `nidn` (`nidn`),
  ADD KEY `fk_kelas_periode` (`id_periode`);

--
-- Indexes for table `krs_detail`
--
ALTER TABLE `krs_detail`
  ADD PRIMARY KEY (`id_krs`,`id_kelas`),
  ADD KEY `id_kelas` (`id_kelas`);

--
-- Indexes for table `krs_header`
--
ALTER TABLE `krs_header`
  ADD PRIMARY KEY (`id_krs`),
  ADD UNIQUE KEY `nim_2` (`nim`,`id_periode`),
  ADD UNIQUE KEY `nim_3` (`nim`,`id_periode`),
  ADD KEY `nim` (`nim`),
  ADD KEY `id_periode` (`id_periode`);

--
-- Indexes for table `mahasiswa`
--
ALTER TABLE `mahasiswa`
  ADD PRIMARY KEY (`nim`),
  ADD KEY `id_prodi` (`id_prodi`),
  ADD KEY `id_dosen_wali` (`id_dosen_wali`),
  ADD KEY `id_user` (`id_user`);

--
-- Indexes for table `mata_kuliah`
--
ALTER TABLE `mata_kuliah`
  ADD PRIMARY KEY (`kode_mk`);

--
-- Indexes for table `mata_kuliah_prasyarat`
--
ALTER TABLE `mata_kuliah_prasyarat`
  ADD PRIMARY KEY (`id_prasyarat`),
  ADD KEY `id_mk_utama` (`id_mk_utama`),
  ADD KEY `id_mk_prasyarat` (`id_mk_prasyarat`);

--
-- Indexes for table `nilai`
--
ALTER TABLE `nilai`
  ADD PRIMARY KEY (`id_nilai`),
  ADD UNIQUE KEY `uq_nilai` (`nim`,`kode_mk`,`id_periode`),
  ADD KEY `nim` (`nim`),
  ADD KEY `kode_mk` (`kode_mk`),
  ADD KEY `fk_nilai_periode` (`id_periode`);

--
-- Indexes for table `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD PRIMARY KEY (`id_bayar`),
  ADD UNIQUE KEY `uq_pembayaran` (`nim`,`id_periode`),
  ADD KEY `id_periode` (`id_periode`),
  ADD KEY `nim` (`nim`);

--
-- Indexes for table `periode`
--
ALTER TABLE `periode`
  ADD PRIMARY KEY (`id_periode`);

--
-- Indexes for table `prodi`
--
ALTER TABLE `prodi`
  ADD PRIMARY KEY (`id_prodi`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `id_admin` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `kelas`
--
ALTER TABLE `kelas`
  MODIFY `id_kelas` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `krs_header`
--
ALTER TABLE `krs_header`
  MODIFY `id_krs` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `mata_kuliah_prasyarat`
--
ALTER TABLE `mata_kuliah_prasyarat`
  MODIFY `id_prasyarat` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `periode`
--
ALTER TABLE `periode`
  MODIFY `id_periode` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `dosen`
--
ALTER TABLE `dosen`
  ADD CONSTRAINT `fk_dosen_admin` FOREIGN KEY (`id_user`) REFERENCES `admin` (`id_admin`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_dosen_prodi` FOREIGN KEY (`id_prodi`) REFERENCES `prodi` (`id_prodi`) ON DELETE SET NULL;

--
-- Constraints for table `kelas`
--
ALTER TABLE `kelas`
  ADD CONSTRAINT `fk_kelas_dosen` FOREIGN KEY (`nidn`) REFERENCES `dosen` (`nidn`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_kelas_mk` FOREIGN KEY (`kode_mk`) REFERENCES `mata_kuliah` (`kode_mk`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_kelas_periode` FOREIGN KEY (`id_periode`) REFERENCES `periode` (`id_periode`) ON DELETE SET NULL;

--
-- Constraints for table `krs_detail`
--
ALTER TABLE `krs_detail`
  ADD CONSTRAINT `fk_detail_kelas` FOREIGN KEY (`id_kelas`) REFERENCES `kelas` (`id_kelas`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_detail_krs` FOREIGN KEY (`id_krs`) REFERENCES `krs_header` (`id_krs`) ON DELETE CASCADE;

--
-- Constraints for table `krs_header`
--
ALTER TABLE `krs_header`
  ADD CONSTRAINT `fk_krs_mhs` FOREIGN KEY (`nim`) REFERENCES `mahasiswa` (`nim`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_krs_periode` FOREIGN KEY (`id_periode`) REFERENCES `periode` (`id_periode`) ON DELETE SET NULL;

--
-- Constraints for table `mahasiswa`
--
ALTER TABLE `mahasiswa`
  ADD CONSTRAINT `fk_mhs_admin` FOREIGN KEY (`id_user`) REFERENCES `admin` (`id_admin`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_mhs_dosen` FOREIGN KEY (`id_dosen_wali`) REFERENCES `dosen` (`nidn`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_mhs_prodi` FOREIGN KEY (`id_prodi`) REFERENCES `prodi` (`id_prodi`) ON DELETE SET NULL;

--
-- Constraints for table `mata_kuliah_prasyarat`
--
ALTER TABLE `mata_kuliah_prasyarat`
  ADD CONSTRAINT `fk_prasyarat_syarat` FOREIGN KEY (`id_mk_prasyarat`) REFERENCES `mata_kuliah` (`kode_mk`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_prasyarat_utama` FOREIGN KEY (`id_mk_utama`) REFERENCES `mata_kuliah` (`kode_mk`) ON DELETE CASCADE;

--
-- Constraints for table `nilai`
--
ALTER TABLE `nilai`
  ADD CONSTRAINT `fk_nilai_mhs` FOREIGN KEY (`nim`) REFERENCES `mahasiswa` (`nim`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_nilai_mk` FOREIGN KEY (`kode_mk`) REFERENCES `mata_kuliah` (`kode_mk`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_nilai_periode` FOREIGN KEY (`id_periode`) REFERENCES `periode` (`id_periode`) ON DELETE SET NULL;

--
-- Constraints for table `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD CONSTRAINT `fk_bayar_mhs` FOREIGN KEY (`nim`) REFERENCES `mahasiswa` (`nim`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_bayar_periode` FOREIGN KEY (`id_periode`) REFERENCES `periode` (`id_periode`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
