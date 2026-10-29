// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import module java.base;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
// </editor-fold>

class M01_CafeWallLab {
    /// # L01.01 · CafeWallLab
    ///
    /// Sestav stěnu z černých a bílých dlaždic, na které mohou dokonale rovné spáry vypadat nakloněně. Měň
    /// posunutí a porovnej svůj dojem s červeným pravítkem.
    ///
    /// **Tvoje mise:** Stěnu zakrývá šedá vrstva. Oprav pořadí dvou akcí v paintWall, aby dlaždice zůstaly
    /// viditelné.
    ///
    /// **Dnes trénuješ:** Sequence (pořadí provádění): uvnitř paintWall běží statement (příkazy) shora dolů,
    /// když je akce zavolána. Pozdější kreslicí akce může zakrýt dřívější. Samotná definice akce nahoře v
    /// souboru ji nespustí.
    ///
    /// Z hlavní složky obsahující README.md:
    /// - Hraní: `java L01/M01_CafeWallLab.java`
    /// - Kontrola této mise: `java L01/Check.java M01_CafeWallLab.java`
    ///
    /// Spusť command (příkaz) pro hraní uvedený výše, šipkami změň stěnu a potom pomocí Space porovnej
    /// její spáry s rovným červeným pravítkem. Po opravě: ulož soubor, spusť uvedenou kontrolu a zahraj
    /// si znovu.
    ///
    /// Řádky s `///` jsou průvodce pro tebe; při spuštění se neprovádějí.
    ///
    /// Potřebuješ výklad? Nová dovednost · F01 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
    /// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
    ///
    /// **První spuštění:** z hlavní složky obsahující README.md spusť výše uvedený command (příkaz) pro
    /// hraní. Šedé okno je připravená chyba. Zavři je a uprav dva řádky níže. Začni tady · spuštění a
    /// kontrola vysvětluje command i výsledky.
    // <editor-fold defaultstate="collapsed" desc="Začni tady · spuštění a kontrola">
    /// - Otevři hlavní staženou složku obsahující README.md. V editoru otevři L01/M01_CafeWallLab.java;
    ///   později postupuj v každé úrovni podle souborů M01, M02…
    /// - Vnější `class M01_CafeWallLab { ... }` pojmenovává program a ohraničuje jeho kód; zadanou
    ///   úpravu prováděj uvnitř.
    /// - V této hlavní složce otevři terminál. Zkopíruj výše uvedený command (příkaz) pro hraní a
    ///   potvrď Enter. Když hra žádá text, napiš odpověď do běžícího terminálu a potvrď Enter; jde o
    ///   input (vstup) do hry.
    /// - Před opakováním hru zavři. Po úpravě soubor ulož, spusť uvedenou kontrolu a potom znovu
    ///   command pro hraní. Běžící okno upravenou Javu nenačte.
    /// - `java L01/Check.java` zkontroluje celou úroveň včetně dosud neopravených her. Kontroly jsou v
    ///   L01/Check.java; při řešení misí kontrolní soubory neměň.
    /// - Výsledek čti v terminálu. `PASS` znamená úspěch ověřených případů. RED ve výchozí verzi je
    ///   očekávaná stopa: ukazuje záměrnou chybu. Čti zprávu: popisuje chování, které se liší.
    /// - Compiler error (chyba překladu) znamená, že se Java nemohla spustit. Čti první hlášení a jeho
    ///   řádek, ověř znaménka kolem úpravy nebo se pomocí Undo vrať k poslední přeložitelné verzi. Ulož
    ///   a zkus znovu.
    /// - Některé editory umějí sekce s názvem sbalit. Pokud to tvůj umí, podle potřeby sekci rozbal;
    ///   jinak stejné comment (komentáře) čti běžně. Fold marker (značky sbalení) a výklad s `///` se
    ///   neprovádějí.
    // </editor-fold>

    void paintWall(Graphics2D g) {
        paintTiles(g);
        paintBackground(g);
    }

    // <editor-fold defaultstate="expanded" desc="Nová dovednost · F01">
    /// Method (metoda) je pojmenovaná akce. Její definice akci zpřístupní; call (volání) ji požádá o
    /// provedení. Java spustí tento program v `main`, i kdyby jiná method byla zapsaná první.
    ///
    /// ```java
    /// void announce() {
    ///     IO.println("Before");
    /// }
    ///
    /// void main() {
    ///     announce();
    ///     IO.println("After");
    /// }
    /// ```
    ///
    /// Před dalším čtením předpověz dva řádky output (výstupu). Call `announce();` vstoupí do této
    /// method, vypíše `Before` a potom se vrátí k dalšímu statement (příkazu) v `main`, který vypíše
    /// `After`. Uvnitř zavolané method běží tyto statement v pořadí. `void` znamená, že se nevrací
    /// výsledná hodnota; `()` zde nepředává žádné argument (argumenty). Složené závorky ohraničují tělo a
    /// středník ukončuje každé call. Comment (komentáře) program vysvětlují, ale neprovádějí se.
    ///
    /// Nyní předpověz, co nastane, pokud main před posledním výpisem zavolá announce dvakrát.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
    /// ## Jak číst právě tyto řádky
    ///
    /// - `void` — Tato akce provede práci a nevrací volající části žádnou hodnotu.
    /// - `paintWall` — Jméno akce, kterou definuješ; připravená kreslicí část ji volá.
    /// - `(Graphics2D g)` — Závorky popisují jeden připravený input (vstup): kreslicí pero pojmenované g.
    ///   Pero používej beze změny jeho typu.
    /// - `{` — Otevírací složená závorka začíná tělo akce; odpovídající zavírací závorka je ukončuje.
    /// - `paintTiles(g);` — Call (volání) připravené akce pro dlaždice s perem g. Závorky předávají input
    ///   (vstup); středník ukončuje statement (příkaz).
    /// - `paintBackground(g);` — Call (volání) akce, která celé plátno vybarví šedě. Záleží na jejím místě v
    ///   pořadí.
    ///
    /// ### Co už obstarává připravená část
    ///
    /// Řádky začínající /// jsou slova pro tebe; Java je neprovádí. Připravená main níže spustí program a
    /// otevře okno. Když okno potřebuje obrázek, draw zavolá tvoji akci paintWall. paintBackground a
    /// paintTiles obsahují připravené kreslení; key zpracuje ovládání.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
    /// Čti dvě akce shora dolů.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
    /// Poslední nátěr zakryje dřívější nátěr na stejném místě.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
    /// Přesuň paintBackground(g) nad paintTiles(g).
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
    void paintBackground(Graphics2D g) {
        g.setColor(new Color(145, 145, 145));
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    void paintTiles(Graphics2D g) {
        for (int row = 0; row < 8; row++) {
            int shift = row % 2 == 0 ? 0 : offset;
            for (int tile = -1; tile < 12; tile++) {
                g.setColor(Math.floorMod(tile, 2) == 0 ? Color.WHITE : Color.BLACK);
                g.fillRect(tile * 60 + shift, rowY(row), 60, 40);
            }
        }
    }

    final int WIDTH = 640, HEIGHT = 450;

    int offset, mortar;
    boolean guide;
    void restart() {
        offset = 25;
        mortar = 4;
        guide = false;
    }

    void adjust(int shift, int width) {
        offset = Math.max(0, Math.min(60, offset + shift));
        mortar = Math.max(1, Math.min(14, mortar + width));
    }

    int rowY(int row) {
        return 30 + row * (40 + mortar);
    }

    void draw(Graphics2D g) {
        paintWall(g);
        if (guide) {
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(2));
            g.drawLine(0, rowY(4) - mortar / 2, WIDTH, rowY(4) - mortar / 2);
        }
        g.setColor(new Color(255, 220, 90));
        g.fillRect(0, 0, WIDTH, 25);
        g.setColor(Color.BLACK);
        g.drawString("Offset: " + offset + "  Mortar: " + mortar + "   The horizontal edges remain straight.", 10, 18);
    }

    void key(int code, boolean pressed) {
        if (pressed && code == KeyEvent.VK_R) {
            restart();
        }
        if (pressed) {
            switch (code) {
                case KeyEvent.VK_LEFT -> adjust(-2, 0);
                case KeyEvent.VK_RIGHT -> adjust(2, 0);
                case KeyEvent.VK_UP -> adjust(0, 1);
                case KeyEvent.VK_DOWN -> adjust(0, -1);
                case KeyEvent.VK_SPACE -> guide = !guide;
            }
        }
    }

    void showWindow() {
        var frame = new JFrame("Caf\u00e9 Wall Lab");
        var panel = new JPanel() {
            @Override protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                var g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                draw(g);
                g.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.setBackground(Color.WHITE);
        panel.setFocusable(true);
        panel.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                key(e.getKeyCode(), true);
                panel.repaint();
            }
        });
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        var help = new JLabel("<html><div style=\"text-align:center\">Left / Right: offset   Up / Down: mortar width   Space: red guide   R: reset</div></html>", SwingConstants.CENTER);
        help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        frame.add(help, BorderLayout.SOUTH);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }

    void main() {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            IO.println("This game needs a graphical display; model checks still work headlessly.");
            return;
        }
        restart();
        SwingUtilities.invokeLater(this::showWindow);
    }

    /// Připravené vstupní body: main, restart, draw, showWindow.
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
    /// - `boolean` — Hodnota true nebo false: ano nebo ne.
    /// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
    ///   textu nepatří.
    /// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
    /// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
    ///   se ale dál měnit může.
    /// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
    /// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
    /// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
    /// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
    /// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
    /// - `++` — Zvětší číselnou hodnotu o jedna.
    /// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
    /// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
    /// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
    /// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
    ///   (balíčku).
    /// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
    /// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
    ///   List<String> lomené závorky popisují element type (typ prvku).
    /// - `?` — V condition ? first : second vybere první hodnotu, když condition (podmínka) platí, jinak
    ///   druhou.
    /// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
    ///   možnosti ? : nebo následuje za značkou switch.
    /// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
    /// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
    /// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
    /// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
    /// - `switch` — Vybere case (větev) z několika možností. Může také vrátit výsledek.
    /// - `case` — Jedna větev switch; šipka ukazuje, co větev provede nebo vrátí.
    /// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
    ///   až při pozdějším volání.
    /// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
    /// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
    /// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
    /// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
    ///   (knihovním zpětným voláním), například paintComponent.
    /// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
    ///   implementaci), aby nejprve vykreslila běžné pozadí.
    /// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
    /// - `JFrame` — Okno dodané JDK knihovnou Swing.
    /// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
    /// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
    /// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
    ///   se provede později.
    // </editor-fold>

}
