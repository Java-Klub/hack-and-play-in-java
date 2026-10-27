# Hack & Play in Java

**[English](https://github.com/Java-Klub/hack-and-play-in-java/tree/main) · [Česky](https://github.com/Java-Klub/hack-and-play-in-java/tree/ceska-verze)**

**Uč se Javu opravováním malých her — a pak si je předělej po svém.**

12 úrovní · 143 her, hádanek a tvořivých experimentů · 36 základních Java skills (dovedností)

![Šest programů z Hack & Play in Java: MiniInvaders, MiniPacman, MirrorLaser, SunflowerSpiral, ReactionDiffusionSkins a FractalTreeGarden.](assets/readme/cover.png)

Zažeň vetřelce. Proveď laser mezi zrcadly. Z několika čísel vytvoř duhovou spirálu, rozvětvený strom nebo retro zvuk.

V každé misi dostaneš spustitelný program a malou část k dokončení, opravě či změně. Předpověz, co kód udělá, proveď svůj tah, ověř jej a hraj znovu. První oprava se může vejít do dvou řádků; pozdější přidá hře nové pravidlo nebo oživí simulaci.

## První záhada

Kreslení je připravené. Stěna je šedá. Který řádek ji schoval?

```java
void paintWall(Graphics2D g) {
    paintTiles(g);
    paintBackground(g);
}
```

Začni tady a postupně se propracuj k bludištím, arkádám, generovaným obrázkům i zvukům. Nové nápady přicházejí po malých krocích a vracejí se v různých hrách.

## Co se naučíš

| Tvoje rostoucí výbava | Java skills (dovednosti), které využiješ |
| --- | --- |
| Čti a rozhoduj se | Execution order (pořadí provádění), variables (proměnné), strings (řetězce), arithmetic (výpočty), conditions (podmínky), methods (metody) a loops (smyčky) |
| Postav svět | Arrays (pole), grids (mřížky), records (datové záznamy), enums (výčtové typy), `List`, `Map`, `Set` a independent copies (nezávislé kopie) |
| Rozhýbej ho | Callbacks (zpětná volání), coordinates (souřadnice), game ticks (časové kroky hry), seeded randomness (náhoda s počáteční hodnotou) a files (soubory) |
| Vysvětli a rozšiř | Contracts (dohody o chování), functional checks (funkční kontroly), interfaces (rozhraní), composition (skládání), pattern matching (rozpoznávání vzorů) a refactoring (úpravy struktury) |

Průvodce najdeš **přímo v kódu**: krátká vysvětlení, vyložené příklady a nápovědy otevírané po jedné. Ze zvědavosti prozkoumej i připravené části her. Po stažení fungují programy i kontroly offline a využívají JDK libraries (knihovny JDK).

## Začni hrát

1. Připrav si **NetBeans a JDK 26 nebo novější**. Projekty cílí na Javu 26 bez preview features (předběžných prvků).
2. Stáhni si svou jazykovou větev z odkazů nahoře. Přes **File → Open Project** otevři **[L01](L01/)** a v **Source Packages** najdi `M01_CafeWallLab.java`.
3. Zvol **Run → Run File**. Přečti misi, uprav kód, použij **Save → Test File** a hraj znovu. První neúspěšná check (kontrola) je záměrná stopa; `PASS` znamená shodu s ověřenými případy.

Pro zvídavé začátečníky, programátorské kluby i společné lekce s učitelem. Když nějaký nápad potřebuje další kolo, vyber si další hru.

<details>
<summary>Pro učitele · hotová řešení</summary>

[English solutions](https://github.com/Java-Klub/hack-and-play-in-java/tree/solution) · [Česká řešení](https://github.com/Java-Klub/hack-and-play-in-java/tree/reseni)

</details>
