# 1.1 Tvůj první program

Tvým cílem je rozumět několika řádkům, které vypisují text, a napsat podobný program sám. Pokud soubor ještě neumíš spustit, vrať se k [přípravě](00-start.md#run).

## Spusť dvě zprávy

Otevři v editoru [WakeUp.java](examples/01/WakeUp.java):

```java
void main() {
    IO.println("ARCADE");
    IO.println("Power on");
}
```

Spuštění vypíše dva řádky:

```text
ARCADE
Power on
```

Program po výpisu skončí. Žádné okno se nemusí otevřít.

## Co znamená každý zápis

| Zápis | Význam tady |
| --- | --- |
| `void main()` | Popisuje akci `main`, kterou Java při spuštění zahájí. Pro dnešek tento první řádek opisuj beze změny. `void` říká, že akce nevrací výslednou hodnotu; prázdné `()` zde neuvádějí žádné vstupy. |
| `{` a `}` | Ohraničují tělo této akce. Mezi nimi jsou kroky, které se provedou. Každá otevírací závorka potřebuje svoji zavírací. |
| `IO.println` | Připravená akce Javy pro výpis jednoho řádku. Tečka vybírá `println` z `IO`; název rozlišuje velká a malá písmena. |
| `("ARCADE")` | Závorky předávají akci text, který má vypsat. |
| `"ARCADE"` | Text je mezi dvojitými uvozovkami. Uvozovky označí jeho začátek a konec; samy se nevypíšou. |
| `;` | Ukončuje tento statement (příkaz). Obě volání `IO.println` jej potřebují. |

Celý řádek `IO.println("ARCADE");` je call (volání) připravené akce. Podobně jako tlačítko přehrávání požádá o přehrání, toto volání požádá o výpis.

Před mezerami uvnitř uvozovek má Java respekt: `"Power on"` obsahuje mezeru mezi slovy. Mezery před `IO.println` pouze pomáhají člověku vidět, které řádky patří dovnitř `main`. Používáme čtyři, aby se kód dobře četl.

## Předpověz a potom spusť

Co se vypíše, když druhou zprávu změníš na `"Ready!"`? Změň ji v souboru, ulož a ověř svoji předpověď.

<details>
<summary>Porovnej výsledek</summary>

```text
ARCADE
Ready!
```

Změnil se pouze text druhého volání. První zůstalo stejné. Výsledkem jsou pořád dva řádky.

</details>

## Úkol: probuď robota

Otevři [SecretSignal.java](practice/01/SecretSignal.java). Zatím vypisuje jediné Beep.

**Doplň další dvě volání, aby po spuštění vypsal přesně:**

```text
Beep
Beep
Ready
```

Každou zprávu vypiš samostatným voláním. První řádek `void main() {` i poslední `}` zachovej.

<details>
<summary>Nápověda: kde začít</summary>

Uvnitř `main` už máš jedno úplné volání. Můžeš celý jeho řádek zkopírovat a změnit text v uvozovkách. Nové řádky patří před zavírací `}`, aby zůstaly uvnitř akce.

</details>

<details>
<summary>Když překladač hlásí chybu</summary>

Porovnej zápis nových řádků s prvním: `IO.println`, kulaté závorky, dvojice dvojitých uvozovek a středník. Slovo `println` obsahuje písmeno l následované n, nikoli číslici 1. Piš obyčejné rovné uvozovky `"`, které používá zdrojový soubor.

</details>

## Teď napiš vlastní malý program

Vytvoř soubor `MySignal.java` ve stejné složce jako SecretSignal. Napiš začátek `void main() {`, tři vlastní zprávy a ukončení `}`. Zprávy mohou tvořit přístupový signál, krátký vtip nebo odpočet napsaný slovy. Texty v programech píšeme anglicky.

Spusť tento nový soubor stejně jako předchozí. Z terminálu hlavní složky kurzu například:

```text
java learn/practice/01/MySignal.java
```

**Tuto lekci zvládáš, když umíš přidat vlastní zprávu, vysvětlit uvozovky a středník a napsat malý spustitelný soubor.**

Pokračuj na [Pořadí příkazů](01-order.md).
