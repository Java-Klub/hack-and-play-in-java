// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import module java.base;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

class M08_FallingBlocks {
    /// # L06.08 · FallingBlocks
    ///
    /// Posouvej a otáčej padající dílky do skládačky. Zkušební otočení u stěny se má buď vejít, nebo čistě
    /// odmítnout.
    ///
    /// **Tvoje mise:** Kontrola navrhované polohy mění skutečnou polohu dílku a způsobuje skoky či dvojí
    /// pohyb. Oprav fits(), aby zůstalo jen dotazem.
    ///
    /// **Dnes trénuješ:** Pure contracts (smlouvy čistých dotazů): kontrola čte desku a kandidátní polohu a
    /// vrací boolean (ano/ne). O zápisu kandidáta rozhoduje až volající kód.
    ///
    /// Z hlavní složky obsahující README.md:
    /// - Hraní: `java L06/M08_FallingBlocks.java`
    /// - Kontrola této mise: `java L06/Check.java M08_FallingBlocks.java`
    ///
    /// Spusť command (příkaz) pro hraní uvedený výše, šipkami vlevo a vpravo posouvej padající dílek a
    /// šipkou nahoru vyzkoušej otočení u skládačky nebo stěny. Po opravě: ulož soubor, spusť uvedenou
    /// kontrolu a zahraj si znovu.
    ///
    /// Potřebuješ výklad? Připomenutí · F15 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
    /// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
    boolean fits(int[][] cells, int x, int y) {
        for (var c : cells) {
            int xx = x + c[0], yy = y + c[1];
            if (xx < 0 || xx >= 10 || yy < 0 || yy >= 16 || board[yy][xx] != 0) {
                return false;
            }
        }
        return true;
    }

    // <editor-fold defaultstate="collapsed" desc="Připomenutí · F15">
    /// ### Contracts: ptej se bez změny světa
    ///
    /// Contract (smlouva) popisuje přijímaný input (vstup) a význam výsledku. Pure calculation (čistý
    /// výpočet) používá své vstupy bez změny dalšího state (stavu). Query (dotaz) může číst aktuální
    /// model (model pravidel), ale má jej zachovat. Získání odpovědi a potvrzení akce jsou různé
    /// odpovědnosti.
    ///
    /// ```java
    /// int contrast(int light, int dark) {
    ///     return Math.abs(light - dark);
    /// }
    /// ```
    ///
    /// `Math.abs` dává nezápornou velikost, odstraní tedy případné záporné znaménko. Tuto method (metodu)
    /// můžeš volat opakovaně, aniž by něco spotřebovala. Argument (argumenty) `(9, 4)` dávají pět: rozdíl
    /// je pět. Předpověz `(4, 9)`. Které uložené hodnoty porovnáš před dotazem a po něm, abys doložil
    /// nepřítomnost side effect (vedlejšího účinku)? Stejnou ochranu potřebuje odmítnutý kandidát.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
    /// ## Jak číst právě tyto řádky
    ///
    /// - `px = x` — Zapisuje kandidátní souřadnici do field (proměnné programu) skutečného dílku.
    /// - `for (var c : cells)` — Prochází všechna relativní políčka navrženého dílku; var nechá typ určit z
    ///   dat.
    /// - `x + c[0]` — Převede relativní posun jednoho políčka na sloupec desky.
    /// - `board[yy][xx] != 0` — Nenulová uložená hodnota políčka znamená, že je místo obsazené.
    ///
    /// ### Co už obstarává připravená část
    ///
    /// Pohyb i rotate() navrhnou souřadnice a před přijetím volají fits(). Timer (časovač) posouvá padající
    /// dílky a clearRows() stlačuje dokončené řádky; skutečné změny už mají na starost tyto části.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
    /// Přečti volající methods: každá nejprve ověřuje fits a pak mění pozici či tvar.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
    /// Dotaz se při hledání okamžitého pádu může volat mnohokrát.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
    /// Uvnitř fits odeber přiřazení px/py; ponech hranice i obsazenost.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
    final int WIDTH = 540, HEIGHT = 430;

    int[][] board = new int[16][10];
    int[][] piece;
    int px, py, kind, lines, ticks, nextKind;
    boolean started, over;
    int[][] shape(int type) {
        return switch (type) {
            case 0 -> new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}};
            case 1 -> new int[][]{{-1, 0}, {0, 0}, {1, 0}};
            default -> new int[][]{{0, -1}, {0, 0}, {1, 0}};
        };
    }

    /// restart(): Obnovuje počáteční model Falling Blocks pro další pokus.
    void restart() {
        board = new int[16][10];
        lines = ticks = nextKind = 0;
        started = over = false;
        spawn();
    }

    void spawn() {
        kind = nextKind++ % 3;
        piece = shape(kind);
        px = 4;
        py = 1;
        if (!fits(piece, px, py)) {
            over = true;
        }
    }

    void move(int dx) {
        if (!over && fits(piece, px + dx, py)) {
            px += dx;
        }
    }

    /// rotate(): Dodané otočení souřadnic navrhuje (-y,x) a provede jej jen po úspěchu fits.
    void rotate() {
        if (over || kind == 0) {
            return;
        }
        int[][] rotated = new int[piece.length][2];
        for (int i = 0; i < piece.length; i++) {
            rotated[i][0] = -piece[i][1];
            rotated[i][1] = piece[i][0];
        }
        if (fits(rotated, px, py)) {
            piece = rotated;
        }
    }

    /// clearRows(): Posouvá neúplné řádky dolů a nahoře vytváří prázdné.
    int clearRows() {
        int destination = 15, removed = 0;
        for (int y = 15; y >= 0; y--) {
            boolean full = true;
            for (int cell : board[y]) {
                if (cell == 0) {
                    full = false;
                }
            }
            if (full) {
                removed++;
            }
            else {
                board[destination--] = board[y].clone();
            }
        }
        while (destination >= 0) {
            board[destination--] = new int[10];
        }
        lines += removed;
        return removed;
    }

    void down() {
        if (over) {
            return;
        }
        if (fits(piece, px, py + 1)) {
            py++;
        }
        else {
            for (var c : piece) {
                board[py + c[1]][px + c[0]] = kind + 1;
            }
            clearRows();
            spawn();
        }
    }

    void drop() {
        if (over) {
            return;
        }
        while (fits(piece, px, py + 1)) {
            py++;
        }
        down();
    }

    void step() {
        if (started && !over && ++ticks % 25 == 0) {
            down();
        }
    }

    /// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
    void draw(Graphics2D g) {
        Color[] colours = {Color.WHITE, new Color(255, 180, 60), new Color(60, 180, 240), new Color(170, 110, 230)};
        g.setColor(new Color(22, 28, 40));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 10; x++) {
                g.setColor(board[y][x] == 0 ? new Color(45, 50, 65) : colours[board[y][x]]);
                g.fillRect(30 + x * 24, 25 + y * 24, 23, 23);
            }
        }
        g.setColor(colours[kind + 1]);
        for (var c : piece) {
            g.fillRect(30 + (px + c[0]) * 24, 25 + (py + c[1]) * 24, 23, 23);
        }
        g.setColor(Color.WHITE);
        g.drawString("Lines: " + lines, 295, 65);
        g.drawString(over ? "TOP OUT - R: retry" : !started ? "Press an arrow" : "Three shapes; no wall kicks", 295, 95);
    }

    void key(int code, boolean pressed) {
        if (pressed && code == KeyEvent.VK_R) {
            restart();
        }
        if (pressed && !over && code != KeyEvent.VK_R) {
            if (code == KeyEvent.VK_LEFT) {
                started = true;
                move(-1);
            }
            if (code == KeyEvent.VK_RIGHT) {
                started = true;
                move(1);
            }
            if (code == KeyEvent.VK_UP) {
                started = true;
                rotate();
            }
            if (code == KeyEvent.VK_DOWN) {
                started = true;
                down();
            }
            if (code == KeyEvent.VK_SPACE) {
                started = true;
                drop();
            }
        }
    }

    /// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; časovače končí při
    /// zavření okna.
    void showWindow() {
        var frame = new JFrame("Falling Blocks");
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
        var timer = new Timer(20, e -> {
            step();
            panel.repaint();
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                timer.stop();
            }
        });
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        var help = new JLabel("<html><div style=\"text-align:center\">Left / Right: move   Up: rotate   Down: lower   Space: drop   R: restart</div></html>", SwingConstants.CENTER);
        help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        frame.add(help, BorderLayout.SOUTH);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        panel.requestFocusInWindow();
        timer.start();
    }

    /// main(): Spouští model a potom plánuje dodané okno na Swing event thread.
    void main() {
        restart();
        SwingUtilities.invokeLater(this::showWindow);
    }
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
    /// - `!=` — Ověří, že se dvě hodnoty liší.
    /// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
    /// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
    /// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
    /// - `++` — Zvětší číselnou hodnotu o jedna.
    /// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
    /// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
    /// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
    /// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
    /// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
    ///   (balíčku).
    /// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
    ///   List<String> lomené závorky popisují element type (typ prvku).
    /// - `?` — V condition ? first : second vybere první hodnotu, když condition (podmínka) platí, jinak
    ///   druhou.
    /// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
    ///   možnosti ? : nebo následuje za značkou switch.
    /// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
    ///   (prvek); length je počet prvků.
    /// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
    /// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
    /// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
    /// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
    /// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
    /// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
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
