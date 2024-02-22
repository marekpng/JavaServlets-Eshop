# E-shop aplikácia
Tento projekt predstavuje jednoduchý e-shop, ktorý umožňuje používateľom prehliadať produkty, pridávať ich do košíka a vykonávať objednávky.

## Funkcionalita
Registrácia a prihlásenie

Používatelia môžu sa registrovať s novým účtom alebo sa prihlásiť do existujúceho účtu.

Zabezpečuje sa jedinečnosť emailových adries pre každého používateľa.
## Produkty a košík

Zobrazenie produktov z databázy.

Možnosť pridávať produkty do košíka a upravovať ich množstvo.

Produkty sú pridávané do košíka s pomocou synchronized metód na zabezpečenie konzistencie skladových zásob.
## Objednávky

Používatelia môžu vykonávať objednávky z položiek v ich košíku.

Každá objednávka je zaznamenaná v databáze s príslušnými informáciami o používateľovi, dátumoch a položkách objednávky.
## Správa objednávok

Administračné rozhranie umožňuje administrátorom prehliadať všetky objednávky, meniť ich stav a odstraňovať nevyžiadané objednávky.

Prístup k administračnému rozhraniu majú len používatelia s príslušnými právami.
## Skladové zásoby

Udržiavanie aktuálnych skladových zásob a synchronizácia s objednávkami zabezpečujú, že produkty nie sú predané, keď nie sú na sklade.
## Technické detaily
Aplikácia je implementovaná v Jave s využitím servletov a JSP pre prezentačnú vrstvu.

Databáza (napr. MySQL, MariaDB) sa používa na ukladanie informácií o používateľoch, produktoch, objednávkach a skladových zásobách.

Synchronizácia je použitá na správne spracovanie prístupu k zdieľaným zdrojom, ako je košík a sklad.

Tento projekt demonštruje základné funkcie e-shopu a zároveň implementuje niekoľko dôležitých aspektov ako správa objednávok, synchronizácia pre viacrozmerné použitie a administrácia. Nájdete tu tiež funkcie zabezpečenia, ako je overovanie prihlasovacích údajov a obmedzenie prístupu k administračnému rozhraniu.

Pre spustenie aplikácie je potrebné nasadiť ju na servlet kontajner, ako je napríklad Apache Tomcat. Pred spustením aplikácie sa uistite, že máte správne nastavené pripojenie k databáze a vytvorenú databázu so všetkými potrebnými tabuľkami.

#### V branchi "databaza" je mysql súbor pomocou ktorého môžete vygenerovať údaje a tabuľky.

## #Snippet
## USER rozhranie
## Produkty (USER)
![ProduktyUser](https://github.com/marekpng/JavaServlets-Eshop/assets/76039661/edbe6cfa-e74f-484f-b1ee-a0882c48d325)

## Košík (USER)
![KosikUser](https://github.com/marekpng/JavaServlets-Eshop/assets/76039661/b6b9edf6-8bfc-4a35-94a2-ae811941417e)

## Zoznam mojich objednávok (USER)
![ZoznamObjednavokUser](https://github.com/marekpng/JavaServlets-Eshop/assets/76039661/03328b1e-f7f7-4df3-b2e1-477901676fe6)

## ADMIN rozhranie
## Produkty (ADMIN)
![produktyAdmin](https://github.com/marekpng/JavaServlets-Eshop/assets/76039661/2ac1ffb8-1d24-4bb8-9470-135b09f90b11)

## Zoznam objednávok (ADMIN)
![ZoznamObjednavokAdmin](https://github.com/marekpng/JavaServlets-Eshop/assets/76039661/5dd9d397-5f55-4fba-8a06-0f1c5f576207)

## Zoznam userov (ADMIN)
![ZoznamUserovAdmin](https://github.com/marekpng/JavaServlets-Eshop/assets/76039661/e4c0582c-02c5-48c3-bfdf-23e18fc397cc)

## Záver
Toto bol krátky snippet, pre plnohodnotný používateľský zážitok si stiahnite celý projekt, verím že sa Vám bude páčiť ;)

