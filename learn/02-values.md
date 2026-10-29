# 2.1 Dej hodnotě jméno

Program může uchovávat počet životů, jméno hráče nebo zvolený cíl. Když hodnotě přidělíš jméno, můžeš ji později použít vícekrát.

## Dvě pojmenované hodnoty

Otevři [Lives.java](examples/02/Lives.java):

```java
void main() {
    int lives = 3;
    String pilot = "Nova";
    IO.println(pilot);
    IO.println(lives);
}
```

Výstup je:

```text
Nova
3
```

Řádek `int lives = 3;` zavede variable (proměnnou) se jménem `lives` a počáteční hodnotou 3.

| Část | Co říká |
| --- | --- |
| `int` | Type (typ): toto jméno uchovává celé číslo. |
| `lives` | Jméno, které si pro hodnotu vybral autor. |
| `=` | Ulož následující hodnotu pod toto jméno. |
| `3` | Počáteční hodnota. |
| `;` | Konec příkazu. |

`String` je typ pro text, proto `pilot` dostává hodnotu `"Nova"`. Pro novou variable uvedeš typ i jméno. Při jejím pozdějším použití píšeš už jen jméno, například `IO.println(lives);`.

Jméno musí být napsané přesně: `lives` a `Lives` jsou dvě různá jména. V těchto příkladech použij jméno až po řádku, který ho zavádí. Přehledné anglické názvy jako `lives`, `score` nebo `pilot` pomáhají poznat, co hodnota znamená.

## Jméno a doslovný text nejsou totéž

Předpověz tyto dva výpisy, pokud má `lives` hodnotu 3:

```java
IO.println("lives");
IO.println(lives);
```

Tyto dva řádky patří dovnitř již známého `main`, za zavedení `lives`.

<details>
<summary>Odpověď</summary>

```text
lives
3
```

Uvozovky říkají „použij tyto znaky jako text“. Jméno bez uvozovek žádá hodnotu, která je pod ním uložená. Java nehledá variable uvnitř zprávy v uvozovkách.

</details>

V Lives změň jméno pilota na `Mika` a počet životů na 5. Před spuštěním si řekni, které dva řádky se změní a proč.

## Spoj zprávu a hodnotu

Znak `+` umí spojovat text. Můžeš v Lives nahradit samotný výpis životů takto:

```java
IO.println("Lives: " + lives);
```

Výsledek pro `lives` rovné 3 je `Lives: 3`. Uvnitř uvozovek je mezera za dvojtečkou. Jméno `lives` je venku, proto se použije jeho hodnota. Spojený text předá celý jeden výsledek akci `println`.

## Malý vlastní úkol

Vytvoř `PilotCard.java` ve složce `learn/practice/02`. Uvnitř `main` zaveď `String pilot` a `int lives` s vlastními hodnotami. Potom vypiš kartu na dva řádky, například:

```text
Pilot: Nova
Lives: 3
```

Zprávy sestav ze jmen `pilot` a `lives`. Potom změň jen jejich počáteční hodnoty a ověř, že obě zprávy odpovídají novým údajům.

<details>
<summary>Nápověda</summary>

Oba údaje zaveď před výpisy. Textové popisky patří do uvozovek, jména použij za `+` bez uvozovek. Vzor začátku a konce souboru najdeš v Lives.

</details>

**Tuto lekci zvládáš, když odlišíš jméno od textu v uvozovkách a pomocí jména vypíšeš uloženou hodnotu.**

Pokračuj na [Změň uloženou hodnotu](02-change.md).
