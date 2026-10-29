# 2.3 Nech program počítat

Stejný kód může vypočítat výsledek pro různé počty hráčů, energii nebo skóre. Místo ručně napsané odpovědi sestavíš expression (výraz), který odpověď vypočítá.

## Základní výpočty

Následující výrazy můžeš vložit do `IO.println(...)` uvnitř známého `main`:

| Výraz | Výsledek | Význam |
| --- | --- | --- |
| `7 + 2` | `9` | Sečti. |
| `7 - 2` | `5` | Odečti. |
| `7 * 2` | `14` | Vynásob; pro násobení piš hvězdičku. |
| `7 / 2` | `3` | Vyděl dvě celá čísla; desetinná část se zahodí směrem k nule. |
| `7 % 2` | `1` | Vrať zbytek po celočíselném dělení. |

Násobení a dělení mají přednost před sčítáním a odčítáním. `2 + 3 * 4` dává 14. Závorkami vybereš jiný postup: `(2 + 3) * 4` dává 20. Stejnou přednost mají `/`, `*` a `%`; mezi nimi se postupuje zleva doprava.

Jména lze použít místo čísel: `energy - cost` počítá z jejich právě uložených hodnot.

## Celá a desetinná čísla

`int` uchovává celá čísla. `double` umí také desetinnou část. V kódu používáme desetinnou **tečku**, například `2.5`.

Otevři [Sharing.java](examples/02/Sharing.java):

```java
void main() {
    double share = 5 / 2.0;
    IO.println(share);
}
```

Výsledek je `2.5`. Operace s desetinným číslem `2.0` použije desetinné dělení.

Teď předpověz, co se stane, když změníš pouze `2.0` na `2`.

<details>
<summary>Proč může vyjít překvapivý výsledek</summary>

Vypíše se `2.0`. Pravá strana `5 / 2` nejprve vydělí dvě celá čísla a vytvoří celé číslo 2. Až to se uloží do `double` jako 2.0. Samotné `double` vlevo ztracenou polovinu nevrátí.

</details>

Obnov původní `2.0`. U dělení hlídej také dělitele: nulou v těchto příkladech nedělíme. `double` ukládá některé desetinné hodnoty přibližně; k penězům nebo přesným desetinným výpočtům se vrátíme později. Pro dnešní rychlosti a rozdělení energie nám poslouží.

## Úkol: rozděl energii paprsku

Otevři [BeamSplit.java](practice/02/BeamSplit.java). Máš `double energy = 9;` a `int beams = 2;`.

**Uprav výpočet `perBeam`, aby rozdělil energii rovným dílem mezi paprsky.** Výstup má být `4.5`. Využij obě jména, aby se program dal použít také s jiným nastavením.

Potom změň pouze energii na 10 a počet paprsků na 4. Očekávej `2.5`. `energy` je už `double`, proto výpočet s jeho hodnotou zachová desetinnou část.

<details>
<summary>Nápověda</summary>

Ptej se, kterou operací zjistíš rovný díl. Celkové množství i počet dílů už mají jména. Jejich hodnoty neopisuj přímo do výpočtu.

</details>

## Číselný součet a spojení textu

V Sharing můžeš původní výpis nahradit těmito dvěma řádky uvnitř `main`. Nejdřív předpověz jejich výstup:

```java
IO.println("Score: " + 3 + 2);
IO.println("Score: " + (3 + 2));
```

<details>
<summary>Odpověď</summary>

```text
Score: 32
Score: 5
```

V prvním případě už `"Score: " + 3` vytváří text, ke kterému se připojí další 2. Závorky ve druhém řádku nejprve vypočítají číselný součet 5 a potom jej připojí ke zprávě.

</details>

## Úkol: sestav skóre pilota

Otevři [Scoreboard.java](practice/02/Scoreboard.java). Pilot má na začátku skóre 10.

**Přidej 5 bodů do `score` a nahraď dva výpisy jediným řádkem**, který sestaví zprávu z `pilot`, dvojtečky s mezerou a aktuálního `score`:

```text
Nova: 15
```

Při jiné počáteční hodnotě 2 a jménu Mika má stejný postup vypsat `Mika: 7`. Pro přidání bodů použij přiřazení; neskládej pouze zprávu s pevnou odpovědí.

<details>
<summary>Nápověda</summary>

Nejdřív změň uložené skóre. Potom využij spojování textu z lekce 2.1. Textem je jen oddělovač `": "`; jména hodnot do uvozovek nepatří.

</details>

## Použij čísla v obrázku

[SunflowerSpiral](../L01/M08_SunflowerSpiral.java) má připravený výpočet obrázku a jediný úhel k nalezení. Procvičíš čtení pojmenované desetinné hodnoty a její změnu. Vnitřní trigonometrické výpočty pro tuto misi psát nemusíš; popisuje je připravená část souboru.

**Kapitolu zvládáš, když umíš napsat krátký program s vlastními hodnotami, změnit je výpočtem a vysvětlit rozdíl mezi `5 / 2` a `5 / 2.0`.**

Další kapitola bude o textu a vstupu hráče. Zatím můžeš pokračovat vlastním pokusem: v novém malém programu spočítej odměnu pro různé počty hráčů a vypiš ji s popiskem. Každou předpověď porovnej se skutečným spuštěním.

[Zpět na obsah učebnice](README.md)
