# Připrav si dílnu

V této lekci otevřeš kurz v NetBeans, spustíš první soubor a změníš jeho zprávu. Zápis programu rozebereme v další lekci.

## Co potřebuješ

- **JDK 26 nebo novější** a NetBeans s podporou Java source file launcheru. JDK překládá a spouští kód. V NetBeans musí být pro spouštění těchto souborů vybrané toto JDK. Pokud prostředí ještě nemáš, připrav jej před prací offline; základ instalace JDK popisuje oficiální stránka [Getting Started with Java](https://dev.java/learn/getting-started/).
- **Staženou a rozbalenou složku celého kurzu.** V její hlavní složce leží `README.md`, `learn` a složky se zdrojovými soubory. Samotný náhled souboru na GitHubu ke spuštění nestačí.

## Přidej celou složku do Favorites

1. V NetBeans zvol **Window → Favorites**.
2. V okně Favorites klikni pravým tlačítkem do volného místa a zvol **Add to Favorites…**.
3. Vyber hlavní složku rozbaleného kurzu, tu s `README.md`. Přidej ji a rozbal její strom.

Favorites ukazuje skutečné soubory na disku. Úprava souboru v NetBeans mění právě tuto staženou kopii. Celou učebnici a všechny její programy tak najdeš v jednom stromu. Projekt zakládat nemusíš: Java source file launcher umí spouštět `.java` soubory přímo.

<a id="run"></a>
## Spusť první program

1. Ve Favorites rozbal `learn`, potom `examples` a `01`.
2. Dvojklikem otevři **WakeUp.java**.
3. Zvol **Run → Run File** (`Shift+F6`). Tato volba spouští otevřený soubor.
4. V okně **Output** najdi:

   ```text
   ARCADE
   Power on
   ```

Když Output nevidíš, otevři **Window → Output**. Program vypsal dvě zprávy a skončil. Je v pořádku, že se neotevřelo žádné herní okno. Pozdější grafické programy otevřou vlastní okno.

## Změň, ulož, spusť

V otevřeném WakeUp změň text uvnitř uvozovek z `ARCADE` na `HELLO`. Zvol **File → Save** a znovu **Run → Run File**. První zpráva v Output má být HELLO. Potom můžeš původní text vrátit.

Save uloží upravený text do souboru. Další spuštění načte tuto uloženou verzi. Již otevřené herní okno se samo nepřepíše: před novým spuštěním ho zavři.

Další příklady i úkoly otevřeš stejným způsobem ve Favorites. Lekce vždy uvede cestu, například `learn/practice/01/SecretSignal.java`: postupně rozbalíš `learn`, `practice` a `01`, otevřeš soubor a použiješ Run File.

Když si v lekci vytváříš vlastní soubor, zachovej koncovku `.java`, nikoli `.java.txt`. Jeho jméno potom vybereš v editoru stejně jako u připravených souborů.

<a id="checks"></a>
## Ověř úkol přes Run File

Krátké úkoly v učebnici mají přímo uvedený očekávaný výstup. Spustíš je a porovnáš výsledek. Pro větší programy je v jejich složce připravený soubor **Check.java**.

Až tě lekce pošle k první hře, ověříš ji takto:

1. Ve Favorites vyber **L01/Check.java**. Klikni na něj pravým tlačítkem a zvol **Properties**.
2. V sekci **Run Single File** nastav **Program Arguments** na `M01_CafeWallLab.java`. Tím vybíráš jednu úlohu ke kontrole. Použij přesně jméno uvedené v lekci, včetně `.java`.
3. Otevři Check.java a použij **Run → Run File**. Výsledek najdeš v Output.

Při kontrole další úlohy ve stejné složce změníš jen Program Arguments. NetBeans si tuto vlastnost pamatuje; samotný zdrojový kód Check.java neměň. **Prázdné Program Arguments spustí kontroly všech programů v této složce**, včetně těch, které jsi zatím neřešil.

- **GREEN**: ověřované případy zadání prošly.
- **RED**: některý případ zatím nesouhlasí. Přečti zprávu za názvem programu.
- **Compiler error**: chyba zápisu zabránila překladu kódu.

Na začátku může být úloha RED, přestože její program běží. Jejím cílem může být oprava nebo nové pravidlo. Pro další hraní zase otevři soubor daného programu a spusť Run File.

<a id="errors"></a>
## Když se něco nepovede

| Co vidíš | Co zkusit |
| --- | --- |
| V Output se objevil jiný program. | Otevři správný `.java` soubor a použij Run File. Ověř také, že upravuješ svou staženou kopii. |
| Run File nejde použít nebo Java nerozumí `void main()` či `IO`. | Ověř, že má NetBeans pro spouštění souborů dostupné JDK 26 nebo novější. Výběr najdeš u souboru v Properties → Run Single File → JDK. |
| Výpis obsahuje `error:` a číslo řádku. | Compiler (překladač) nerozumí zápisu. Začni prvním hlášením; zkontroluj uvozovky, závorky a středníky u označeného místa. |
| Po změně stále vidíš starý výsledek. | Ulož soubor a spusť jej znovu. Původní herní okno nejprve zavři. |
| Kontrola vypsala mnoho nesplněných úloh. | V Properties souboru Check.java nastav Program Arguments na jeden soubor podle aktuální lekce. |

Oprav nejprve první chybu ve výpisu a program spusť znovu. **Edit → Undo** umí vrátit poslední změnu.

<details>
<summary>Spouštění z terminálu, pokud ho chceš použít</summary>

Stejné soubory umí spustit přímo JDK i mimo NetBeans. Z terminálu v hlavní složce kurzu:

```text
java learn/examples/01/WakeUp.java
java L01/Check.java M01_CafeWallLab.java
```

Za `java` píšeš cestu k vybranému souboru včetně `.java`. Za cestou k Check.java je volitelně jméno jednoho kontrolovaného programu. Tuto možnost k průchodu učebnicí nepotřebuješ.

</details>

**Můžeš pokračovat, když dokážeš najít WakeUp ve Favorites, spustit ho a po uložení změny vidět novou zprávu.**

Pokračuj na [Tvůj první program](01-first-program.md).
