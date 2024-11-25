-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: tripdb
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `locationtrack`
--

DROP TABLE IF EXISTS `locationtrack`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `locationtrack` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `createdat` datetime(6) DEFAULT NULL,
  `locationlat` double DEFAULT NULL,
  `locationlong` double DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcu07h4hxikwstg2anve3facqf` (`user_id`),
  CONSTRAINT `FKcu07h4hxikwstg2anve3facqf` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `locationtrack`
--

LOCK TABLES `locationtrack` WRITE;
/*!40000 ALTER TABLE `locationtrack` DISABLE KEYS */;
INSERT INTO `locationtrack` VALUES (1,'2024-11-25 15:56:01.709773',32,32.1213123,NULL);
/*!40000 ALTER TABLE `locationtrack` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_token`
--

DROP TABLE IF EXISTS `password_reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `expiry_date` datetime(6) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKf90ivichjaokvmovxpnlm5nin` (`user_id`),
  CONSTRAINT `FK83nsrttkwkb6ym0anu051mtxn` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_token`
--

LOCK TABLES `password_reset_token` WRITE;
/*!40000 ALTER TABLE `password_reset_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `role_name` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `UK716hgxp60ym1lifrdgp67xt5k` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'2024-10-03 10:37:25.000000','ROLE_SYSTEM_ADMIN',NULL),(2,'2024-10-03 10:37:25.000000','ROLE_ORG_ADMIN',NULL),(3,'2024-10-03 10:37:25.000000','ROLE_USER',NULL);
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transporterinfo`
--

DROP TABLE IF EXISTS `transporterinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transporterinfo` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `createdat` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phoneno` varchar(255) DEFAULT NULL,
  `updateadat` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transporterinfo`
--

LOCK TABLES `transporterinfo` WRITE;
/*!40000 ALTER TABLE `transporterinfo` DISABLE KEYS */;
INSERT INTO `transporterinfo` VALUES (1,'dhaka','2024-11-25 15:55:58.768463','opu.anirban@gmail.com',' opu','0133255429','2024-11-25 15:55:58.769464');
/*!40000 ALTER TABLE `transporterinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tripinfo`
--

DROP TABLE IF EXISTS `tripinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tripinfo` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `createdat` datetime(6) DEFAULT NULL,
  `customername` varchar(255) DEFAULT NULL,
  `droplat` double DEFAULT NULL,
  `droplocation` varchar(255) DEFAULT NULL,
  `droplong` double DEFAULT NULL,
  `pickuplat` double DEFAULT NULL,
  `pickuplocation` varchar(255) DEFAULT NULL,
  `pickuplong` double DEFAULT NULL,
  `status` varchar(255) DEFAULT 'unassigned ',
  `transporteraddtime` datetime(6) DEFAULT NULL,
  `updateadat` datetime(6) DEFAULT NULL,
  `transporter_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKofh5oi9dtm9hnau70tjnmxi0n` (`transporter_id`),
  KEY `FKax9tslj0y6s3esxrxhvw708ms` (`user_id`),
  CONSTRAINT `FKax9tslj0y6s3esxrxhvw708ms` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKofh5oi9dtm9hnau70tjnmxi0n` FOREIGN KEY (`transporter_id`) REFERENCES `transporterinfo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tripinfo`
--

LOCK TABLES `tripinfo` WRITE;
/*!40000 ALTER TABLE `tripinfo` DISABLE KEYS */;
INSERT INTO `tripinfo` VALUES (1,'2024-11-25 15:55:46.174233','test',34.88888882,'new22222',34.88888882,34.88888882,'new22',34.88888882,'Bokked','2024-11-25 15:56:19.000000','2024-11-25 15:56:19.000000',1,1);
/*!40000 ALTER TABLE `tripinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(500) DEFAULT NULL,
  `business_email` varchar(255) DEFAULT NULL,
  `business_mobile_number` varchar(255) DEFAULT NULL,
  `business_name` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `organization_name` varchar(255) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `phone_no` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `token_created_at` datetime(6) DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `updatead_at` datetime(6) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `parent_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKin4n4lbhkic2cl1ghgbmvd74a` (`organization_name`),
  UNIQUE KEY `UKsixq0fu6tsxv2xt6l8nivm4wm` (`phone_no`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  KEY `FK4ty2qb76gscl4oi0yugg5gywc` (`parent_id`),
  CONSTRAINT `FK4ty2qb76gscl4oi0yugg5gywc` FOREIGN KEY (`parent_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'test',NULL,NULL,NULL,'2024-11-25 15:55:36.396527','opu.anirban@gmail.com',_binary '\0',NULL,'$2a$10$Byq2Amx3jXOqSxdFz0MbkO/qf.Wu.ILfGAzRZ1UeHHf7.9w5DBsIy','0133255429',NULL,NULL,NULL,'2024-11-25 15:55:36.396527','opu.anirban@gmail.com',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_roles`
--

DROP TABLE IF EXISTS `users_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users_roles` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FKj6m8fwv7oqv74fcehir1a9ffy` (`role_id`),
  CONSTRAINT `FK2o0jvgh89lemvvo17cbqvdxaa` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKj6m8fwv7oqv74fcehir1a9ffy` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_roles`
--

LOCK TABLES `users_roles` WRITE;
/*!40000 ALTER TABLE `users_roles` DISABLE KEYS */;
INSERT INTO `users_roles` VALUES (1,1);
/*!40000 ALTER TABLE `users_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'tripdb'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-25 15:58:54
