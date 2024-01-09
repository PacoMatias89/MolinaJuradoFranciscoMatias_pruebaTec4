-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: agencia_turismo
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `book_flight`
--

DROP TABLE IF EXISTS `book_flight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_flight` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `destination` varchar(255) DEFAULT NULL,
  `flight_code` varchar(255) DEFAULT NULL,
  `origin` varchar(255) DEFAULT NULL,
  `peopleq` int DEFAULT NULL,
  `price` double DEFAULT NULL,
  `seat_type` varchar(255) DEFAULT NULL,
  `booking_flight_delete` bit(1) DEFAULT NULL,
  `booking_flight_date_delete` date DEFAULT NULL,
  `id_flight` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6x4k5gtip2jvyst478vxdj91a` (`id_flight`),
  CONSTRAINT `FK6x4k5gtip2jvyst478vxdj91a` FOREIGN KEY (`id_flight`) REFERENCES `flight` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_flight`
--

LOCK TABLES `book_flight` WRITE;
/*!40000 ALTER TABLE `book_flight` DISABLE KEYS */;
INSERT INTO `book_flight` VALUES (1,'2024-05-23','Barcelona','MABA-0737','Madrid',1,50,'Economy',_binary '','2024-01-09',1),(2,'2024-05-23','Barcelona','MABA-0737','Madrid',1,50,'Economy',_binary '\0',NULL,1),(3,'2024-05-23','Barcelona','MABA-0737','Madrid',1,150,'Business',_binary '\0',NULL,1);
/*!40000 ALTER TABLE `book_flight` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_flight_passengers`
--

DROP TABLE IF EXISTS `book_flight_passengers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_flight_passengers` (
  `booke_flight_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  KEY `FK4nxcwp2ky7vct2exaijowwkc6` (`user_id`),
  KEY `FKa9lahf5a7h7rr6ogj2b3h8ggi` (`booke_flight_id`),
  CONSTRAINT `FK4nxcwp2ky7vct2exaijowwkc6` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKa9lahf5a7h7rr6ogj2b3h8ggi` FOREIGN KEY (`booke_flight_id`) REFERENCES `book_flight` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_flight_passengers`
--

LOCK TABLES `book_flight_passengers` WRITE;
/*!40000 ALTER TABLE `book_flight_passengers` DISABLE KEYS */;
INSERT INTO `book_flight_passengers` VALUES (1,4),(2,5),(3,5);
/*!40000 ALTER TABLE `book_flight_passengers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_hotel`
--

DROP TABLE IF EXISTS `book_hotel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_hotel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_from` date DEFAULT NULL,
  `date_to` date DEFAULT NULL,
  `hotel_code` varchar(255) DEFAULT NULL,
  `nights` int NOT NULL,
  `peopleq` int NOT NULL,
  `price` double DEFAULT NULL,
  `room_type` varchar(255) DEFAULT NULL,
  `booking_hotel_delete` bit(1) DEFAULT NULL,
  `booking_hotel_date_delete` date DEFAULT NULL,
  `hotel_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmdalu2ngu5lpja2llhgd5wray` (`hotel_id`),
  CONSTRAINT `FKmdalu2ngu5lpja2llhgd5wray` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_hotel`
--

LOCK TABLES `book_hotel` WRITE;
/*!40000 ALTER TABLE `book_hotel` DISABLE KEYS */;
INSERT INTO `book_hotel` VALUES (1,'2024-02-23','2024-02-25','HF-0180',2,2,1500,'Triple',_binary '','2024-01-09',2),(2,'2024-02-21','2024-02-25','HF-0180',4,2,3000,'Triple',_binary '','2024-01-09',2),(3,'2024-02-21','2024-02-25','GPH-2933',4,1,2200,'Doble',_binary '','2024-01-09',3),(4,'2024-02-21','2024-02-25','GPH-2933',4,1,2200,'Doble',_binary '\0',NULL,3);
/*!40000 ALTER TABLE `book_hotel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_hotel_users`
--

DROP TABLE IF EXISTS `book_hotel_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_hotel_users` (
  `booked_hotel_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  KEY `FKeh9wl6l0gy8pfr1w1b318tvyw` (`user_id`),
  KEY `FKt956y69lmeif0vpsx4tnibuv8` (`booked_hotel_id`),
  CONSTRAINT `FKeh9wl6l0gy8pfr1w1b318tvyw` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKt956y69lmeif0vpsx4tnibuv8` FOREIGN KEY (`booked_hotel_id`) REFERENCES `book_hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_hotel_users`
--

LOCK TABLES `book_hotel_users` WRITE;
/*!40000 ALTER TABLE `book_hotel_users` DISABLE KEYS */;
INSERT INTO `book_hotel_users` VALUES (1,2),(1,6),(2,2),(2,6),(3,7),(4,7);
/*!40000 ALTER TABLE `book_hotel_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flight`
--

DROP TABLE IF EXISTS `flight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flight` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `flight_date` date DEFAULT NULL,
  `flight_destination` varchar(255) DEFAULT NULL,
  `flight_number` varchar(255) DEFAULT NULL,
  `flight_origin` varchar(255) DEFAULT NULL,
  `flight_delete` bit(1) DEFAULT NULL,
  `flight_date_delete` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flight`
--

LOCK TABLES `flight` WRITE;
/*!40000 ALTER TABLE `flight` DISABLE KEYS */;
INSERT INTO `flight` VALUES (1,'2024-05-23','Barcelona','MABA-0737','Madrid',_binary '\0',NULL),(2,'2024-03-10','Madrid','ZÚMA-5154','Zúrich',_binary '\0',NULL),(3,'2024-04-12','Nueva York','BANU-8943','Barcelona',_binary '','2024-01-08'),(4,'2024-02-10','Londres','ZÚLO-3414','Zúrich',_binary '\0',NULL),(5,'2024-04-20','Sídney','TOSÍ-2104','Tokio',_binary '\0',NULL),(6,'2024-05-25','Roma','PARO-2743','París',_binary '\0',NULL),(7,'2024-06-30','Tokio','LOTO-6364','Los Ángeles',_binary '\0',NULL),(8,'2024-08-05','Pekín','NUPE-6987','Nueva Delhi',_binary '\0',NULL),(9,'2024-09-10','Bogotá','LIBO-3442','Lima',_binary '\0',NULL),(10,'2024-10-15','Ciudad de México','RÍCI-9094','Río de Janeiro',_binary '\0',NULL),(11,'2024-11-20','Vancouver','TOVA-6200','Toronto',_binary '\0',NULL);
/*!40000 ALTER TABLE `flight` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel`
--

DROP TABLE IF EXISTS `hotel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `hotel_code` varchar(255) DEFAULT NULL,
  `is_booked` bit(1) DEFAULT NULL,
  `hotel_name` varchar(255) DEFAULT NULL,
  `hotel_place` varchar(255) DEFAULT NULL,
  `hotel_delete` bit(1) DEFAULT NULL,
  `hotel_date_delete` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel`
--

LOCK TABLES `hotel` WRITE;
/*!40000 ALTER TABLE `hotel` DISABLE KEYS */;
INSERT INTO `hotel` VALUES (1,'GPH-9629',_binary '\0','Grand Paradise Hotel','Playa del Carmen',_binary '','2024-01-08'),(2,'HF-0180',_binary '\0',' Hotel Francesco','Florencia',_binary '\0',NULL),(3,'GPH-2933',_binary '','Grand Paradise Hotel','Playa del Carmen',_binary '\0',NULL),(4,'GPH-7504',_binary '\0','Grand Paradise Hotel','Playa del Carmen',_binary '\0',NULL),(5,'SVR-3679',_binary '\0','Sunset View Resort','Bali',_binary '\0',NULL),(6,'RPI-1271',_binary '\0','Royal Plaza Inn','New York',_binary '\0',NULL),(7,'MRL-9939',_binary '\0','Mountain Retreat Lodge','Aspen',_binary '\0',NULL),(8,'OPR-7543',_binary '\0','Oceanfront Paradise Resort','Maui',_binary '\0',NULL),(9,'CLH-0769',_binary '\0','City Lights Hotel','Paris',_binary '\0',NULL),(10,'RR-7090',_binary '\0','Riverside Retreat','Kyoto',_binary '\0',NULL),(11,'GSR-7050',_binary '\0','Golden Sands Resort','Dubai',_binary '\0',NULL),(12,'MML-4830',_binary '\0','Majestic Mountain Lodge','Rocky Mountains',_binary '\0',NULL),(13,'CSH-2617',_binary '\0','Coastal Serenity Hotel','Sydney',_binary '\0',NULL);
/*!40000 ALTER TABLE `hotel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `disponibility_date_from` date DEFAULT NULL,
  `disponibility_date_to` date DEFAULT NULL,
  `room_price` double DEFAULT NULL,
  `room_type` varchar(255) DEFAULT NULL,
  `hotel_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_fdtq4rubhga537amx7apm0eel` (`hotel_id`),
  CONSTRAINT `FKdosq3ww4h9m2osim6o0lugng8` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (1,'2024-02-20','2024-02-25',550,'Doble',1),(2,'2024-02-20','2024-03-25',750,'Triple',2),(3,'2024-02-20','2024-03-25',550,'Doble',3),(4,'2024-01-20','2024-03-25',550,'individual',4),(5,'2024-03-10','2024-03-15',450,'Individual',5),(6,'2024-04-05','2024-04-10',700,'Suite',6),(7,'2024-05-15','2024-05-20',600,'Doble',7),(8,'2024-06-01','2024-06-07',750,'Suite',8),(9,'2024-07-10','2024-07-15',580,'Doble',9),(10,'2024-08-05','2024-08-10',500,'Individual',10),(11,'2024-08-15','2024-09-10',800,'Suite',11),(12,'2024-10-01','2024-10-07',620,'Doble',12),(13,'2024-11-10','2024-11-15',480,'Individual',13);
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `age` int NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `pass_port` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,35,'paco@paco.es','Molina Jurado','Francisco Matías','AB123456A'),(2,25,'migue@miguel.es','Pérez Ordoñez','Miguel','AB123456A'),(3,28,'noe@noe.es','Rienda Rodriguez','Noemi','AB123456A'),(4,20,'miguel@miguel.es','Navarro Pérez','Miguel','AB123456A'),(5,35,'ali@ali.es','Navarro Pérez','Alicia','AB123456A'),(6,24,'carlitos@carlitos.es','Jimenez Jimenez','Carlos','AB123456A'),(7,28,'noe@noe.com','Rienda Rodriguez','Noemi','AB123456A');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'agencia_turismo'
--

--
-- Dumping routines for database 'agencia_turismo'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-01-09 21:20:20
