/*
SQLyog Community v13.3.1 (64 bit)
MySQL - 10.4.32-MariaDB : Database - krspbo2lancar
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`krspbo2lancar` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `krspbo2lancar`;

SET FOREIGN_KEY_CHECKS = 0;
/*Table structure for table `admin` */

DROP TABLE IF EXISTS `admin`;

CREATE TABLE `admin` (
  `id_admin` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id_admin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `admin` */

/*Table structure for table `prodi` */

DROP TABLE IF EXISTS `prodi`;

CREATE TABLE `prodi` (
  `id_prodi` int(11) NOT NULL,
  `nama_prodi` varchar(100) NOT NULL,
  PRIMARY KEY (`id_prodi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


/*Data for the table `prodi` */

/*Table structure for table `dosen` */

DROP TABLE IF EXISTS `dosen`;

CREATE TABLE `dosen` (
  `nidn` varchar(20) NOT NULL,
  `nama_dosen` varchar(100) NOT NULL,
  `id_prodi` int(11) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL,
  PRIMARY KEY (`nidn`),
  KEY `id_prodi` (`id_prodi`),
  KEY `id_user` (`id_user`),
  CONSTRAINT `dosen_ibfk_1` FOREIGN KEY (`id_prodi`) REFERENCES `prodi` (`id_prodi`),
  CONSTRAINT `dosen_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `admin` (`id_admin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `dosen` */

/*Table structure for table `mata_kuliah` */

DROP TABLE IF EXISTS `mata_kuliah`;

CREATE TABLE `mata_kuliah` (
  `kode_mk` varchar(20) NOT NULL,
  `nama_mk` varchar(100) NOT NULL,
  `sks` int(11) DEFAULT NULL,
  `semester` int(11) DEFAULT NULL,
  PRIMARY KEY (`kode_mk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `mata_kuliah` */

/*Table structure for table `kelas` */

DROP TABLE IF EXISTS `kelas`;

CREATE TABLE `kelas` (
  `id_kelas` int(11) NOT NULL,
  `hari` varchar(20) DEFAULT NULL,
  `jam` varchar(20) DEFAULT NULL,
  `ruang` varchar(50) DEFAULT NULL,
  `kuota` int(11) DEFAULT NULL,
  `kode_mk` varchar(20) DEFAULT NULL,
  `nidn` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_kelas`),
  KEY `kode_mk` (`kode_mk`),
  KEY `nidn` (`nidn`),
  CONSTRAINT `kelas_ibfk_1` FOREIGN KEY (`kode_mk`) REFERENCES `mata_kuliah` (`kode_mk`),
  CONSTRAINT `kelas_ibfk_2` FOREIGN KEY (`nidn`) REFERENCES `dosen` (`nidn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `kelas` */

/*Table structure for table `krs_detail` */

DROP TABLE IF EXISTS `krs_detail`;

CREATE TABLE `krs_detail` (
  `id_krs` int(11) NOT NULL,
  `id_kelas` int(11) NOT NULL,
  PRIMARY KEY (`id_krs`,`id_kelas`),
  KEY `id_kelas` (`id_kelas`),
  CONSTRAINT `krs_detail_ibfk_1` FOREIGN KEY (`id_krs`) REFERENCES `krs_header` (`id_krs`),
  CONSTRAINT `krs_detail_ibfk_2` FOREIGN KEY (`id_kelas`) REFERENCES `kelas` (`id_kelas`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `krs_detail` */

/*Table structure for table `krs_header` */

DROP TABLE IF EXISTS `krs_header`;

CREATE TABLE `krs_header` (
  `id_krs` int(11) NOT NULL,
  `total_sks` int(11) DEFAULT NULL,
  `tgl_pengajuan` date DEFAULT NULL,
  `status_validasi` varchar(20) DEFAULT NULL,
  `catatan_dosen` text DEFAULT NULL,
  `nim` varchar(20) DEFAULT NULL,
  `id_periode` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_krs`),
  KEY `nim` (`nim`),
  KEY `id_periode` (`id_periode`),
  CONSTRAINT `krs_header_ibfk_1` FOREIGN KEY (`nim`) REFERENCES `mahasiswa` (`nim`),
  CONSTRAINT `krs_header_ibfk_2` FOREIGN KEY (`id_periode`) REFERENCES `periode` (`id_periode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `krs_header` */

/*Table structure for table `mahasiswa` */

DROP TABLE IF EXISTS `mahasiswa`;

CREATE TABLE `mahasiswa` (
  `nim` varchar(20) NOT NULL,
  `nama_mhs` varchar(100) NOT NULL,
  `alamat` text DEFAULT NULL,
  `id_prodi` int(11) DEFAULT NULL,
  `id_dosen_wali` varchar(20) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL,
  PRIMARY KEY (`nim`),
  KEY `id_prodi` (`id_prodi`),
  KEY `id_dosen_wali` (`id_dosen_wali`),
  KEY `id_user` (`id_user`),
  CONSTRAINT `mahasiswa_ibfk_1` FOREIGN KEY (`id_prodi`) REFERENCES `prodi` (`id_prodi`),
  CONSTRAINT `mahasiswa_ibfk_2` FOREIGN KEY (`id_dosen_wali`) REFERENCES `dosen` (`nidn`),
  CONSTRAINT `mahasiswa_ibfk_3` FOREIGN KEY (`id_user`) REFERENCES `admin` (`id_admin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `mahasiswa` */



/*Table structure for table `mata_kuliah_prasyarat` */

DROP TABLE IF EXISTS `mata_kuliah_prasyarat`;

CREATE TABLE `mata_kuliah_prasyarat` (
  `id_prasyarat` int(11) NOT NULL,
  `id_mk_utama` varchar(20) DEFAULT NULL,
  `id_mk_prasyarat` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_prasyarat`),
  KEY `id_mk_utama` (`id_mk_utama`),
  KEY `id_mk_prasyarat` (`id_mk_prasyarat`),
  CONSTRAINT `mata_kuliah_prasyarat_ibfk_1` FOREIGN KEY (`id_mk_utama`) REFERENCES `mata_kuliah` (`kode_mk`),
  CONSTRAINT `mata_kuliah_prasyarat_ibfk_2` FOREIGN KEY (`id_mk_prasyarat`) REFERENCES `mata_kuliah` (`kode_mk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `mata_kuliah_prasyarat` */

/*Table structure for table `nilai` */

DROP TABLE IF EXISTS `nilai`;

CREATE TABLE `nilai` (
  `id_nilai` int(11) NOT NULL,
  `grade` varchar(5) DEFAULT NULL,
  `nim` varchar(20) DEFAULT NULL,
  `kode_mk` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_nilai`),
  KEY `nim` (`nim`),
  KEY `kode_mk` (`kode_mk`),
  CONSTRAINT `nilai_ibfk_1` FOREIGN KEY (`nim`) REFERENCES `mahasiswa` (`nim`),
  CONSTRAINT `nilai_ibfk_2` FOREIGN KEY (`kode_mk`) REFERENCES `mata_kuliah` (`kode_mk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `nilai` */

/*Table structure for table `pembayaran` */

DROP TABLE IF EXISTS `pembayaran`;

CREATE TABLE `pembayaran` (
  `id_bayar` int(11) NOT NULL,
  `status_Lunas` varchar(20) DEFAULT NULL,
  `id_periode` int(11) DEFAULT NULL,
  `nim` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_bayar`),
  KEY `id_periode` (`id_periode`),
  KEY `nim` (`nim`),
  CONSTRAINT `pembayaran_ibfk_1` FOREIGN KEY (`id_periode`) REFERENCES `periode` (`id_periode`),
  CONSTRAINT `pembayaran_ibfk_2` FOREIGN KEY (`nim`) REFERENCES `mahasiswa` (`nim`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `pembayaran` */

/*Table structure for table `periode` */

DROP TABLE IF EXISTS `periode`;

CREATE TABLE `periode` (
  `id_periode` int(11) NOT NULL,
  `tahun_ajaran` varchar(20) DEFAULT NULL,
  `semester` varchar(10) DEFAULT NULL,
  `status_krs` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_periode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `periode` */

SET FOREIGN_KEY_CHECKS = 1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
