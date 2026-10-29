# 2.2 Změň uloženou hodnotu

Počáteční počet životů není celý příběh hry. Po nehodě má být menší. Variable (proměnná) může postupně uchovávat různé hodnoty.

## Sleduj jednu změnu

Otevři [Energy.java](examples/02/Energy.java):

```java
void main() {
    int energy = 5;
    IO.println(energy);
    energy = energy - 2;
    IO.println(energy);
}
```

Předpověz výstup, potom spusť program.

<details>
<summary>Výstup a průchod</summary>

```text
5
3
```

| Krok | Uložená hodnota `energy` |
| --- | --- |
| `int energy = 5;` | 5 |
| První výpis | 5, výpis ji nemění |
| `energy = energy - 2;` | 3 |
| Druhý výpis | 3 |

</details>

Na řádku `energy = energy - 2;` Java nejprve přečte pravou stranu. Z dosavadních 5 odečte 2. Teprve potom uloží výsledek 3 pod jméno `energy`. Tomu říkáme assignment (přiřazení).

`=` zde není matematické tvrzení o věčné rovnosti. Je to pokyn změnit uloženou hodnotu. Při změně už znovu nepíšeš `int`: nové jméno bylo zavedeno výše.

Co se vypíše, když počáteční hodnota bude 9? Předpověď ověř. Potom vrať počáteční hodnotu na 5 a přesuň řádek se změnou až za oba výpisy. Předpověz a ověř také tento výstup.

<details>
<summary>Porovnej předpovědi</summary>

S počáteční hodnotou 9 a změnou na původním místě dostaneš 9 a 7. Pokud z původního programu přesuneš změnu až za oba výpisy, oba vypíšou 5. Java nezmění už vypsaný řádek zpětně.

</details>

## Úkol: zaplať za otevření dveří

Otevři [EnergyCounter.java](practice/02/EnergyCounter.java). Má dvakrát vypsat energii: před otevřením dveří a po něm. Dveře spotřebují 3 jednotky, ale připravený program zatím nechá stejnou hodnotu.

**Změň přiřazení mezi výpisy tak, aby odečetlo 3 od právě uložené energie.** Při počáteční hodnotě 8 čekej:

```text
8
5
```

Potom změň jen počáteční hodnotu na 14. Výsledek musí být 14 a 11. Tím ověříš, že program skutečně počítá z dosavadní hodnoty.

<details>
<summary>Nápověda</summary>

V Energy už máš stejný druh změny. Pravá strana čte uloženou energii a odečítá cenu; levá uloží výsledek zpět. Nepiš pevný konečný počet, protože pro jiný začátek musí vyjít jiný konec.

</details>

## Když hodnotu čte více míst

[SharedSettings.java](examples/02/SharedSettings.java) má nastavení před `main`:

```java
int goal = 6;

void main() {
    IO.println("Target: " + goal);
    IO.println("Win at: " + goal);
}
```

Obě zprávy čtou stejné `goal`. Změň jeho počáteční hodnotu na 4 a ověř oba výpisy.

Hodnota zapsaná takto mimo `main` může být dostupná také dalším akcím téhož programu. V hrách se tak sdílí například nastavení cíle. Podrobněji jeho životnost prozkoumáme při psaní vlastních metod. Pro tuto změnu stačí najít pojmenovanou hodnotu a sledovat její čtení.

## Vyber si procvičení ve hře

- [HigherOrLower](../L01/M06_HigherOrLower.java): najdi společné nastavení cíle a zkrať karetní výzvu. Hledáš v programu; nápovědy v jeho souboru tě postupně navedou.
- [SteppingFeet](../L01/M07_SteppingFeet.java): uprav druhé přiřazení a vrať oba obdélníky do obrázku. Tady už máš označené místo k úpravě.

Každé zadání obsahuje vlastní cílovou hodnotu. Pro kontrolu HigherOrLower otevři `L01/Check.java`, nastav Program Arguments na `M06_HigherOrLower.java` a spusť Run File. Pro SteppingFeet použij `M07_SteppingFeet.java`. Postup nastavení je v [přípravě](00-start.md#checks).

**Tuto lekci zvládáš, když vysvětlíš čtení staré hodnoty před uložením nové a správně předpovíš několik přiřazení za sebou.**

Pokračuj na [Nech program počítat](02-calculations.md).
