# 1.2 Pořadí příkazů

Už umíš vypsat vlastní zprávy. Teď zjistíš, proč dva programy se stejnými kroky mohou dělat něco jiného.

## Přečti, předpověz, spusť

Otevři [MissionControl.java](examples/01/MissionControl.java). Nejdřív zkus napsat jeho výstup na papír nebo do poznámky:

```java
void main() {
    IO.println("Check power");
    IO.println("Open gate");
    IO.println("Go");
}
```

<details>
<summary>Výstup pro porovnání</summary>

```text
Check power
Open gate
Go
```

Uvnitř `main` se tato volání provedou shora dolů. První vypíše svoji zprávu a skončí; potom začne druhé a nakonec třetí.

</details>

Přesuň poslední volání před první. Co bude teď nahoře ve výstupu? Ulož a spusť. Změnil jsi pořadí kroků, jejich tři zprávy zůstaly stejné.

## Příkazy a komentáře

Comment (komentář) je zpráva pro čtenáře kódu. Java podle něj neprovádí žádnou akci. Zápis `//` označuje komentář až do konce daného řádku:

```java
void main() {
    // Say hello to the pilot.
    IO.println("Hello");
}
```

Tento program vypíše jen Hello. Řádek začínající `//` se při spuštění přeskočí. I `IO.println`, které napíšeš **za `//` na stejném řádku**, zůstane pouze komentářem.

V hrách uvidíš také řádky s `///`: jsou to vysvětlující komentáře pro tebe. Šipky u některých sekcí v editoru dovolují delší výklad sbalit. Jestli tvůj editor sbalování nenabízí, komentáře lze normálně číst jako ostatní text.

## Úkol: oprav startovací postup

Otevři [LaunchSequence.java](practice/01/LaunchSequence.java). Má všechny tři potřebné zprávy, ale ve špatném pořadí.

**Přesuň celé řádky uvnitř `main` tak, aby výstup byl:**

```text
Doors closed
Engine on
Launch
```

Každou zprávu zachovej jednou. Samotné přepsání komentáře výstup nezmění. Po úpravě předpověz výstup a porovnej ho se spuštěním.

<details>
<summary>Nápověda</summary>

Nejdřív se mají zavřít dveře. Pak může začít pracovat motor a teprve potom odstartovat loď. Přesouvej celé řádky včetně středníků.

</details>

## Použij stejnou myšlenku ve hře

Teď otevři [CafeWallLab](../L01/M01_CafeWallLab.java) ve složce `L01`. Spuštění ukáže šedou plochu: dlaždice jsou schované pod pozdějším nátěrem. Zadání na začátku souboru označuje dva řádky k přesunutí.

Tady místo výpisu textu voláš připravené kreslení. `paintTiles(g);` žádá o dlaždice a `paintBackground(g);` o šedý nátěr. `g` je dodané kreslicí pero. Tyto akce fungují; rozhoduješ o jejich pořadí. Popis `Graphics2D g` v záhlaví jen říká, jaké pero připravená část dodává. Dnes ho měnit nemusíš.

Zavři staré okno, ulož změnu a spusť soubor znovu. Výsledek má ukázat dlaždice na šedém pozadí. Opravu ověř z hlavní složky kurzu:

```text
java L01/Check.java M01_CafeWallLab.java
```

GREEN potvrzuje ověřované případy. Jestli dostaneš RED, přečti zprávu a vrať se ke dvěma kreslicím krokům.

Chceš ještě jiný obrázek? [MissingTriangle](../L01/M02_MissingTriangle.java) procvičuje stejné pořadí. Jeho zadání tě nechá sestavit kresbu ze dvou připravených volání; na výběr nepotřebuješ znát jejich vnitřní kreslicí algoritmus.

**Tuto lekci zvládáš, když předpovíš pořadí výpisů a vysvětlíš, proč pozdější kreslení může překrýt dřívější.**

Pokračuj na [Dej hodnotě jméno](02-values.md).
