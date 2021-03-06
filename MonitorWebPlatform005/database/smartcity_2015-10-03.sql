# ************************************************************
# Sequel Pro SQL dump
# Version 4499
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.5.34)
# Database: smartcity
# Generation Time: 2015-10-03 09:48:00 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table SIDResponse
# ------------------------------------------------------------

DROP TABLE IF EXISTS `SIDResponse`;

CREATE TABLE `SIDResponse` (
  `SIDResponseID` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `profileInfoID` int(10) unsigned DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `merchant` varchar(100) DEFAULT NULL,
  `country` varchar(10) DEFAULT NULL,
  `currency` varchar(10) DEFAULT NULL,
  `reference` varchar(100) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `bank` varchar(100) DEFAULT NULL,
  `date` varchar(50) DEFAULT NULL,
  `receiptNumber` varchar(100) DEFAULT NULL,
  `transactionID` varchar(100) DEFAULT NULL,
  `consistentKey` varchar(100) DEFAULT NULL,
  `dateRegistered` datetime DEFAULT NULL,
  PRIMARY KEY (`SIDResponseID`),
  KEY `profileInfoID` (`profileInfoID`),
  KEY `dateRegistered` (`dateRegistered`),
  CONSTRAINT `sidresponse_ibfk_1` FOREIGN KEY (`profileInfoID`) REFERENCES `profileInfo` (`profileInfoID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
