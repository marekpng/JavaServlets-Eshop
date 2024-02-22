-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hostiteľ: 127.0.0.1
-- Čas generovania: Št 22.Feb 2024, 23:14
-- Verzia serveru: 10.4.27-MariaDB
-- Verzia PHP: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databáza: `eshop`
--

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `kosik`
--

CREATE TABLE `kosik` (
  `ID` int(11) NOT NULL,
  `ID_pouzivatela` int(11) NOT NULL,
  `ID_tovaru` int(11) NOT NULL,
  `cena` decimal(10,2) NOT NULL,
  `ks` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Sťahujem dáta pre tabuľku `kosik`
--

INSERT INTO `kosik` (`ID`, `ID_pouzivatela`, `ID_tovaru`, `cena`, `ks`) VALUES
(38, 7, 1, '18.00', 3),
(51, 10, 1, '18.00', 1),
(52, 10, 2, '2.00', 1),
(53, 10, 6, '29.99', 1);

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `obj_polozky`
--

CREATE TABLE `obj_polozky` (
  `ID` int(11) NOT NULL,
  `ID_objednavky` int(11) NOT NULL,
  `ID_tovaru` int(11) NOT NULL,
  `cena` decimal(10,2) NOT NULL,
  `ks` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Sťahujem dáta pre tabuľku `obj_polozky`
--

INSERT INTO `obj_polozky` (`ID`, `ID_objednavky`, `ID_tovaru`, `cena`, `ks`) VALUES
(16, 12, 2, '1.80', 1),
(15, 11, 2, '1.80', 2),
(14, 10, 2, '1.80', 2),
(13, 9, 1, '16.20', 2),
(12, 9, 2, '1.80', 2),
(17, 13, 2, '1.80', 1),
(18, 14, 2, '1.80', 3),
(28, 20, 5, '15.99', 5),
(27, 19, 2, '2.00', 1),
(26, 19, 5, '15.99', 1),
(25, 18, 1, '18.00', 1),
(29, 21, 6, '26.99', 1),
(30, 21, 1, '16.20', 1),
(35, 25, 1, '18.00', 1),
(37, 27, 1, '18.00', 1),
(38, 28, 2, '2.00', 4);

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `obj_zoznam`
--

CREATE TABLE `obj_zoznam` (
  `ID` int(11) NOT NULL,
  `obj_cislo` varchar(20) NOT NULL,
  `datum_objednavky` date NOT NULL,
  `ID_pouzivatela` int(11) NOT NULL,
  `suma` decimal(10,2) NOT NULL,
  `stav` varchar(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Sťahujem dáta pre tabuľku `obj_zoznam`
--

INSERT INTO `obj_zoznam` (`ID`, `obj_cislo`, `datum_objednavky`, `ID_pouzivatela`, `suma`, `stav`) VALUES
(12, 'OBJ1703099025851', '2023-12-20', 3, '1.80', 'Odoslane'),
(11, 'OBJ1703098890461', '2023-12-20', 3, '3.60', 'zaplatene'),
(10, 'OBJ1703098772248', '2023-12-20', 3, '3.60', 'spracuva sa'),
(9, 'OBJ1703098646387', '2023-12-20', 3, '36.00', 'spracuva sa'),
(13, 'OBJ1703099108472', '2023-12-20', 3, '1.80', 'spracuva sa'),
(14, 'OBJ1703099114586', '2023-12-20', 3, '5.40', 'spracuva sa'),
(18, 'OBJ1703154496129', '2023-12-21', 5, '18.00', 'spracuva sa'),
(19, 'OBJ1703154522333', '2023-12-21', 5, '17.99', 'spracuva sa'),
(20, 'OBJ1703154543296', '2023-12-21', 5, '79.95', 'zaplatene'),
(21, 'OBJ1703156700270', '2023-12-21', 3, '43.19', 'odoslane'),
(25, 'OBJ1703782731730', '2023-12-28', 9, '18.00', 'zaplatene'),
(27, 'OBJ1708637456777', '2024-02-22', 10, '18.00', 'spracuva sa'),
(28, 'OBJ1708637562568', '2024-02-22', 10, '8.00', 'spracuva sa');

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `sklad`
--

CREATE TABLE `sklad` (
  `ID` int(11) NOT NULL,
  `nazov` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `ks` int(11) NOT NULL,
  `cena` decimal(10,2) NOT NULL,
  `image` varchar(255) NOT NULL,
  `popis` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Sťahujem dáta pre tabuľku `sklad`
--

INSERT INTO `sklad` (`ID`, `nazov`, `ks`, `cena`, `image`, `popis`) VALUES
(1, 'nohavice', 1, '18.00', 'https://img.freepik.com/free-photo/pants_1203-8310.jpg?w=360&t=st=1703020309~exp=1703020909~hmac=2587a40287f60e133e8e24dbb07122f4d8be7303478bd91773bee706a46efae7', 'asdfasdf adsf asdf asd fads fas dfas df'),
(2, 'ponožky', 140, '2.00', 'https://img.freepik.com/free-photo/woman-wearing-plain-white-color-socks_53876-102871.jpg?w=740&t=st=1703020587~exp=1703021187~hmac=69fb080d88a6f5675afd7d88ab9602b07e54673860bf8b99a177834e16c17a81', 'asdfasfasfasfasfadsfsdf'),
(5, 'tricko', 0, '15.99', 'https://img.freepik.com/free-psd/mens-tri-blend-crew-tee-mockup_126278-130.jpg?w=740&t=st=1703020628~exp=1703021228~hmac=eea0ace43e5d4a86a6d31b88d25b1b1ae325b6c3bafcd26e4e27a527c000b96e', 'Super tricko '),
(6, 'mikina', 5, '29.99', 'https://img.freepik.com/free-psd/jerzees-pullover-hooded-sweatshirt-mockup-01_126278-94.jpg?w=740&t=st=1703020661~exp=1703021261~hmac=0bcf0d4b0f565d8393b9df0a6b88899616c3159bef56ace7c40124b7972e00ff', 'Super mikina');

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `users`
--

CREATE TABLE `users` (
  `ID` int(11) NOT NULL,
  `login` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `passwd` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `adresa` varchar(50) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `zlava` int(11) NOT NULL,
  `meno` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `priezvisko` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `poznamky` text CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `je_admin` tinyint(1) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Sťahujem dáta pre tabuľku `users`
--

INSERT INTO `users` (`ID`, `login`, `passwd`, `adresa`, `zlava`, `meno`, `priezvisko`, `poznamky`, `je_admin`) VALUES
(1, 'asd@gmail.com', '123', 'Zeleninova 4, Nitra', 20, 'Jan', 'Zelezny', 'tester', 0),
(2, 'jmrkva@ukf.sk', '123', 'Zahrada 11', 3, 'Jozef', 'Mrkva', 'druhý tester', 0),
(3, 'test', '123', 'jkrala', 10, 'Marek', 'Jaros', 'asdfasfasfadsfa', 1),
(5, 'test@test', '123', '123', 0, 'Frantisek', 'Benicky', '132', 0),
(7, 'marek@gmail.com', '123', 'Madrid', 0, 'Ferdinand', 'Mláčka', 'ja som fero mrkef', 0),
(8, 'pokus@pokus', 'asd', 'Toronto', 0, 'Dusan', 'Vlk', 'Ja som dusky vlk', 1),
(9, 'm@m', 'asd', 'lovca', 0, 'Veronika', 'Jarabovičová', 'ahojojojojojo\r\n', 0),
(10, 'user@user', '1', 'Ruzinov', 0, 'Michaela', 'Višňovská', 'spolahlivy zakaznik', 0),
(11, 'admin@admin', '1', 'Bratislava stare mesto', 0, 'Admin', 'Admin', 'toto je admin account', 1);

--
-- Kľúče pre exportované tabuľky
--

--
-- Indexy pre tabuľku `kosik`
--
ALTER TABLE `kosik`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `obj_polozky`
--
ALTER TABLE `obj_polozky`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `obj_zoznam`
--
ALTER TABLE `obj_zoznam`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `sklad`
--
ALTER TABLE `sklad`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT pre exportované tabuľky
--

--
-- AUTO_INCREMENT pre tabuľku `kosik`
--
ALTER TABLE `kosik`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=54;

--
-- AUTO_INCREMENT pre tabuľku `obj_polozky`
--
ALTER TABLE `obj_polozky`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT pre tabuľku `obj_zoznam`
--
ALTER TABLE `obj_zoznam`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT pre tabuľku `sklad`
--
ALTER TABLE `sklad`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pre tabuľku `users`
--
ALTER TABLE `users`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
