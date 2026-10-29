# Připrav si dílnu

V této lekci poprvé spustíš připravený soubor a zjistíš, kde uvidíš výsledek. Zápis programu rozebereme v další lekci.

## Co potřebuješ

1. **JDK 26 nebo novější.** Obsahuje program `java`, který tvůj kód přeloží a spustí. Samotný editor Javu nenahrazuje. Pokud JDK ještě nemáš, před prací offline si jej nainstaluj; základní postup najdeš na oficiální stránce [Getting Started with Java](https://dev.java/learn/getting-started/).
2. **Staženou složku celého kurzu.** V její hlavní složce leží `README.md`, `learn`, `L01` a další hry. ZIP nejprve rozbal. Jednotlivý náhled souboru na GitHubu ke spuštění celého kurzu nestačí.
3. **Editor pro Java soubory.** Můžeš použít NetBeans, IntelliJ IDEA, VS Code nebo textový editor. Soubory v kurzu jsou běžné `.java` soubory, které umí spouštět přímo JDK.

První programy pouze vypisují text. Po vypsání skončí. Je v pořádku, když se neotevře žádné herní okno.

<a id="run"></a>
## První spuštění z terminálu

Terminal (terminál) je okno pro zadávání příkazů počítači. Může být samostatné nebo součástí editoru. Následující postup funguje i tehdy, když editor zatím nemá nastavené spouštění Javy.

1. Otevři terminál **v hlavní složce rozbaleného kurzu**. Ve Windows lze v Průzkumníku kliknout do adresního řádku této složky, napsat `cmd` a potvrdit Enter. Na macOS lze u složky ve Finderu použít službu **New Terminal at Folder**; v Linuxu obvykle nabídku **Open in Terminal**.
2. Ověř dostupnost Javy:

   ```text
   java --version
   ```

   Ve výpisu musí být verze 26 nebo vyšší. Pokud příkaz není nalezen nebo ukazuje starší verzi, vyřeš nejprve výběr či instalaci JDK. U editoru a terminálu mohou být vybrané různé JDK.

3. Spusť připravený soubor:

   ```text
   java learn/examples/01/WakeUp.java
   ```

   Správný výsledek je:

   ```text
   ARCADE
   Power on
   ```

   Potom se vrátí místo pro další příkaz. Program svoji práci dokončil.

Pokud Java hlásí, že soubor nemůže najít, ověř složku terminálu. V ní mají být přímo adresáře `learn` a `L01`. Celý kurz můžeš mít v libovolně pojmenované složce.

### Jak spustíš další soubory

Za `java` píšeš cestu k vybranému souboru z hlavní složky kurzu, včetně koncovky `.java`. Pro první krátký úkol tedy použiješ:

```text
java learn/practice/01/SecretSignal.java
```

U dalšího příkladu či úkolu změníš celou cestu podle souboru uvedeného v lekci. Terminál přitom zůstává v hlavní složce kurzu. V editoru se spouštěním Javy místo toho vybereš odpovídající soubor.

## Otevření a změna souboru

V editoru otevři soubor `learn/examples/01/WakeUp.java`. **Soubor má končit `.java`, nikoli `.java.txt`.** Pokud jej otevřeš odkazem v prohlížeči, uvidíš jeho text nebo se stáhne; pro úpravy jej otevři ve svém editoru.

Změň v uvozovkách `ARCADE` na `HELLO`, ulož a znovu spusť stejný soubor. První vypsaný řádek se má změnit na HELLO. Potom můžeš původní text vrátit.

`Save` uloží upravený text do souboru. Další spuštění načte tuto uloženou verzi. Již otevřené herní okno se samo nepřepíše: před novým spuštěním ho zavři.

## Stejná práce v NetBeans

Pokud používáš NetBeans s JDK 26 nebo novějším, můžeš program spouštět přímo z editoru:

1. Zvol **File → Open File…** a otevři `learn/examples/01/WakeUp.java` ze stažené složky kurzu. Projekt vytvářet nemusíš.
2. Po úpravě zvol **File → Save**.
3. Zvol **Run → Run File** (`Shift+F6`). Tato volba spouští vybraný soubor.
4. Text najdeš v okně **Output**. Když ho nevidíš, otevři **Window → Output**. Grafické hry později otevřou vlastní okno.

Další úkoly otevírej a spouštěj stejně. Pokud spouštění v editoru ještě nemáš nastavené, použij postup z terminálu výše. Pro kontrolu jediné hry použij terminálový příkaz v části [Jak později ověříš opravu hry](#checks).

<a id="errors"></a>
## Když se něco nepovede

| Co vidíš | Co to znamená a co zkusit |
| --- | --- |
| Příkaz `java` není nalezen. | Terminál nemá dostupné JDK. Ověř instalaci a nastavení podle stránky výše. |
| Java nemůže najít `WakeUp.java`. | Ověř složku terminálu a cestu `learn/examples/01/WakeUp.java`. |
| Hlášení obsahuje `error:` a jméno souboru s číslem řádku. | Compiler (překladač) nerozumí zapsanému programu. Začni prvním hlášením; zkontroluj uvozovky, závorky a středníky u označeného místa. |
| Po úpravě pořád vidíš starý výsledek. | Ulož soubor a ověř, že spouštíš stejnou kopii, kterou upravuješ. Staré herní okno zavři. |
| Po výpisu se nic dalšího neděje. | U prvních příkladů je to konec programu. Další příkaz zadáváš terminálu. |

Na konci výpisu může být více hlášení vyvolaných jednou chybou. Oprav nejprve tu první a spusť soubor znovu. Příkaz Undo v editoru umí vrátit poslední změnu.

<a id="checks"></a>
## Jak později ověříš opravu hry

Malé úkoly v učebnici mají přímo uvedený očekávaný výstup. Porovnáš s ním vlastní spuštění. Hry mají navíc připravené funkční kontroly ve svém `Check.java`.

Například pro první hru, ze stejné hlavní složky:

```text
java L01/Check.java M01_CafeWallLab.java
```

- **GREEN** znamená, že tato hra splnila ověřované případy zadání.
- **RED** znamená, že některý ověřovaný případ zatím nesouhlasí. Přečti zprávu za názvem hry.
- **Compiler error** znamená chybu zápisu, kvůli které se příslušný kód nepodařilo přeložit.

Na začátku může být hra RED, přestože se spustí. Úkol po tobě chce opravu nebo nové pravidlo. `Check.java` při řešení neupravuj. Až přijdeš k první hře, lekce uvede právě její kontrolu.

**Můžeš pokračovat, když dokážeš spustit WakeUp, změnit první zprávu a po uložení vidět novou.**

Pokračuj na [Tvůj první program](01-first-program.md).
