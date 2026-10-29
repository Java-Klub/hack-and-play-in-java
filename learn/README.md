# Nauč se programovat s Hack & Play in Java

Tady postupně zjistíš, jak Java programům rozumět a jak psát vlastní. Začneš několika řádky, vyzkoušíš si novou myšlenku a potom ji použiješ v malé hře.

**Začínáš úplně od začátku? Otevři [Připrav si dílnu](00-start.md).**

Potřebuješ počítač s JDK 26 nebo novějším a NetBeans. Hlavní složku kurzu přidáš do Favorites a soubory spustíš pomocí Run File. Učebnice, příklady i hry po stažení fungují offline. Předchozí znalost programování nepotřebuješ.

## První kroky učebnicí

Postup určuje učivo. Čti jednu lekci, spusť její příklad a vyřeš navazující úkoly přímo v NetBeans. Není potřeba nejdřív přečíst celou kapitolu. Odpovědi na otázky se rozbalují zvlášť, aby sis mohl nejprve udělat vlastní předpověď.

| Kapitola | Lekce, které už můžeš projít |
| --- | --- |
| **Příprava** | [Otevři, spusť, změň, ulož](00-start.md) |
| **1. Program provádí kroky** | [Tvůj první program](01-first-program.md) → [Pořadí příkazů](01-order.md) |
| **2. Hodnoty, jména a výpočty** | [Dej hodnotě jméno](02-values.md) → [Změň uloženou hodnotu](02-change.md) → [Nech program počítat](02-calculations.md) |

Pokud už něco umíš, vyzkoušej nejprve úkol dané lekce. K vysvětlení se můžeš vrátit, když potřebuješ porozumět výsledku. Učebnice tě nehodnotí podle rychlosti.

## Jak jsou připravené soubory

- V `learn/examples` jsou úplné příklady z výkladu. Můžeš je spustit i změnit; původní znění najdeš na stránce lekce.
- V `learn/practice` jsou krátké úkoly. Upravuješ vždy soubor uvedený v zadání. Očekávaný výstup najdeš přímo v lekci.
- Ve složkách `L01` až `L12` jsou zdrojové soubory větších procvičení. Všechny postupně začleňujeme do kapitol učebnice; lekce tě pošle ke konkrétnímu programu, až znáš potřebný základ. Části připraveného programu, které zatím nepíšeš, popisují jeho komentáře.
- [Slovníček prvních zápisů](glossary.md) pomůže, když narazíš na neznámý znak nebo slovo.

Všechna procvičení patří do společné výukové cesty. Číslo složky je nyní adresa souboru; pořadí učení určují kapitoly a odkazy z právě čtené lekce.

## Kam bude učebnice pokračovat

Následující kapitoly jsou plán obsahu; jejich lekce teprve připravujeme. Postupně do nich zapracujeme všech 145 původních her, hádanek a tvořivých programů. U každého přizpůsobíme zadání, komentáře a kontrolu tomu, co už čtenář zná. Některé programy se mohou vracet s novým úkolem; obtížnost ani počet procvičení nemusí být v každé kapitole stejné.

| Kapitola | Co se naučíš |
| --- | --- |
| 3. Text a komunikace s hráčem | Načíst vstup, upravit text a převést zadané číslo. |
| 4. Rozhodování a pravidla | Zapsat podmínku, vybrat větev a odmítnout zakázaný tah. |
| 5. Vlastní metody | Pojmenovat část programu, předat jí vstupy a získat výsledek. |
| 6. Opakování a hledání | Opakovat postup, počítat a hledat pomocí cyklů. |
| 7. Více hodnot pohromadě | Pracovat s arrays (poli), jejich pozicemi a obsahem. |
| 8. Desky, mapy a sousedé | Procházet mřížku a hlídat její hranice. |
| 9. Vlastní datové typy | Použít `record` a malou `class`, rozlišit hodnotu, objekt a odkaz. |
| 10. Kolekce podle potřeby | Vybrat `List`, `Set` nebo `Map` a využít jejich operace. |
| 11. Stav hry a opakovatelné pokusy | Připravit restart, Undo, jednotlivé kroky simulace a opakovatelnou náhodu. |
| 12. Chybný vstup a soubory | Ošetřit chybu, načíst data a uchovat je mezi spuštěními. |
| 13. Program reaguje a běží v čase | Propojit vstup, aktualizaci pravidel a kreslení pomocí událostí a callbacks. |
| 14. Skládání a rozšiřování programu | Skládat malé role, využít interfaces, record patterns a pattern matching. |

## Čtení na GitHubu a offline

Na GitHubu čti soubory Markdown, například tuto stránku. Pro pohodlné čtení z disku otevři `learn/index.html` ve stažené složce. HTML stránky obsahují stejný výklad, místní odkazy a rozbalovací odpovědi; nepotřebují server ani připojení.

V českém výkladu se setkáš s anglickým termínem a případně českým vysvětlením v závorce. Jména v kódu a zprávy programů jsou anglicky, aby se stejným kódem dalo pracovat v obou jazykových verzích.
