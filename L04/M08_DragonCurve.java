// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
// </editor-fold>

/// # L04.08 · DragonCurve
///
/// Nech z posloupnosti levých a pravých odbočení vyrůst papírového draka. Opakování malého
/// skládacího receptu vytváří překvapivě složitou křivku, kterou program zobrazí jako barevný
/// obrázek.
///
/// **Tvoje mise:** Křivka dostane o jeden přehyb méně, než bylo požadováno. Oprav pouze vnější smyčku ve
/// foldTurns tak, aby nula přehybů neudělala nic a kladný požadavek provedl právě tolik průchodů
/// skládání.
///
/// **Dnes trénuješ:** For jako počet opakování: vnější counter (počítadlo) počítá celé přehyby, nikoli
/// již uložená odbočení. Než začneš uvažovat o podrobném obrázku s jedenácti přehyby, ověř drobné
/// požadavky nula a jedna.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L04/M08_DragonCurve.java`
/// - Kontrola této mise: `java L04/Check.java M08_DragonCurve.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše: obrázek se ihned objeví v Java okně. Po úpravě
/// source (zdrojového textu) okno zavři a znovu spusť command pro hraní; Kontrola hry ověřuje
/// výpočet odděleně.
///
/// Potřebuješ výklad? Připomenutí · F11 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
List<Integer> foldTurns(int folds) {
    var turns = new ArrayList<Integer>();
    for (int fold = 1; fold < folds; fold++) {
        int old = turns.size();
        turns.add(1);
        for (int i = old - 1; i >= 0; i--) {
            turns.add(-turns.get(i));
        }
    }
    return turns;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F11">
/// Counted for (počítaná smyčka for) sdruží initialization (počáteční nastavení), podmínku
/// pokračování a postup do jediného záhlaví. Nastavení proběhne jednou. Potom Java ověří condition
/// (podmínku), při pravdě provede tělo, udělá postup a znovu ověří podmínku.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// for (int floor = 2; floor <= 4; floor++) {
///     IO.println(floor);
/// }
/// ```
///
/// Předpověz navštívené hodnoty: `2`, `3`, `4`. Jsou to tři průchody, i když poslední vypsané číslo
/// je čtyřka. Dva středníky oddělují tři části záhlaví; `floor++` přičte jedničku po průchodu.
/// Složené závorky sdružují opakované statement (příkazy). Pokud počáteční hodnota podmínku už
/// nesplňuje, neproběhne žádný průchod. Rozlišuj počet pozic a mezer: tři pozice mají dvě mezery. Než
/// změníš hranici, napiš drobnou trace (stopu průběhu).
///
/// Nyní předpověz output (výstup), pokud floor začíná na pětce.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `for (int fold = 1; fold < folds; fold++)` — Toto je rozsah vnějšího opakování, který upravuješ;
///   folds je požadovaný počet, zatímco fold sleduje aktuální průchod.
/// - `int old = turns.size();` — Zapamatuj počet odbočení před tímto přehybem; vnitřní kopírování musí
///   použít právě tuto dřívější část.
/// - `turns.add(1);` — Připravený recept připojí doprostřed každého přehybu jedno pravé odbočení.
/// - `for (int i = old - 1; i >= 0; i--)` — Tato připravená vnitřní smyčka čte stará odbočení pozpátku;
///   i-- postupuje k prvnímu uloženému odbočení.
/// - `turns.add(-turns.get(i));` — Zkopíruj odbočení s obráceným směrem. Toto připravené skládací
///   pravidlo zachovej.
/// - `return turns;` — Předej hotovou posloupnost odbočení připravené části pro kreslení cesty.
///
/// ### Co už obstarává připravená část
///
/// Připravená vnitřní smyčka dodává jeden celý přehyb obrácením dřívějších odbočení a jejich směrů.
/// Pozdější method (metody) převedou posloupnost na pozice, přizpůsobí ji plátnu, obarví ji a
/// zobrazí obrázek v připraveném okně. Připravené Java okno ukazuje křivku přímo; skládací recept
/// zůstává stejný.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Vyzkoušej folds = 1 se současným počátečním fold.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Vnější counter (čítač) má navštívit 0 až folds - 1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Dej fold výchozí hodnotu 0; zachovej fold < folds.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
List<Point> path(int folds) {
    if (folds < 0 || folds > 15) {
        throw new IllegalArgumentException("Folds: 0..15.");
    }
    var turns = foldTurns(folds);
    var points = new ArrayList<Point>();
    int x = 0;
    int y = 0;
    int direction = 0;
    int[] dx = {1, 0, -1, 0};
    int[] dy = {0, 1, 0, -1};
    points.add(new Point(x, y));
    x++;
    points.add(new Point(x, y));
    for (int turn : turns) {
        direction = Math.floorMod(direction + turn, 4);
        x += dx[direction];
        y += dy[direction];
        points.add(new Point(x, y));
    }
    return points;
}

BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite fold count required.");
    }
    var points = path((int)option);
    int minX = points.stream().mapToInt((p)->p.x).min().orElseThrow();
    int minY = points.stream().mapToInt((p)->p.y).min().orElseThrow();
    int maxX = points.stream().mapToInt((p)->p.x).max().orElseThrow();
    int maxY = points.stream().mapToInt((p)->p.y).max().orElseThrow();
    double scale = 450.0 / Math.max(1, Math.max(maxX - minX, maxY - minY));
    var image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        for (int i = 1; i < points.size(); i++) {
            g.setColor(Color.getHSBColor((seed % 100) / 100.0F + i / (float)points.size(), 0.7F, 1));
            Point a = points.get(i - 1);
            Point b = points.get(i);
            g.drawLine(25 + (int)((a.x - minX) * scale), 25 + (int)((a.y - minY) * scale),
                25 + (int)((b.x - minX) * scale), 25 + (int)((b.y - minY) * scale));
        }
    } finally {
        g.dispose();
    }
    return image;
}

// Prepared display of the picture calculated above.
void showPicture() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Image preview needs a graphical display.");
        return;
    }
    var image = render(42, 11);
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("DragonCurve");
        var picture = new javax.swing.JLabel(new javax.swing.ImageIcon(image));
        picture.setOpaque(true);
        picture.setBackground(new java.awt.Color(35, 39, 48));
        frame.add(picture);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}

void main() {
    showPicture();
}

/// Připravené vstupní body: main, render.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Malá mapa Javy · volitelné">
/// ## Neznámý zápis?
///
/// Tato malá mapa je dobrovolná. Najdi jen znak nebo slovo, kterému nerozumíš. Není třeba se ji učit
/// dopředu.
///
/// - `///` — Slova pro čtenáře. Java tyto comment (komentářové) řádky při spuštění přeskočí.
/// - `import` — Zpřístupní v souboru jména z knihovny; knihovnu tím nespustí.
/// - `;` — Ukončuje jeden statement (příkaz), podobně jako tečka větu. V záhlaví for odděluje jeho tři
///   části.
/// - `()` — Za jménem: volání akce nebo seznam parameter (parametrů) při jejím popisu. Prázdné závorky
///   znamenají bez předávaných argument (argumentů).
/// - `{}` — Složené závorky ohraničují block (blok kódu). Otevírací a uzavírací závorka patří k sobě.
/// - `.` — Zpřístupňuje něco patřícího hodnotě nebo type (typu) vlevo, například IO.println.
/// - `void` — Tato method (metoda) provede akci a nevrací výslednou hodnotu.
/// - `main` — Tady Java program zahajuje. Ostatní method declaration (popisy metod) se neprovedou jen
///   proto, že jsou v souboru výše.
/// - `int` — Whole-number type (typ pro celá čísla) s omezeným rozsahem.
/// - `double` — Numeric type (číselný typ) pro přibližná desetinná čísla. Java používá desetinnou tečku.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
/// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
// </editor-fold>

