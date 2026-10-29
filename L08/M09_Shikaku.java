// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import module java.base;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
// </editor-fold>

class M09_Shikaku {
    /// # L08.09 · Shikaku
    ///
    /// Kresli obdélníky kolem nápověd tak, aby plocha každého odpovídala jeho jedinému číslu. Špatný odhad
    /// smaž nebo vrať a zkus jiné rozdělení.
    ///
    /// **Tvoje mise:** Vrácení tahu sdílí živý seznam obdélníků, takže přidání či odebrání přepíše i uložený
    /// stav. Oprav snapshot(), aby uchovalo členy seznamu.
    ///
    /// **Dnes trénuješ:** Copies and aliasing (kopie a sdílené odkazy): kopíruj měnitelný kontejner, pokud
    /// lze bezpečně sdílet jeho immutable elements (neměnitelné prvky). Záznamy Box není třeba zdvojovat
    /// kvůli zachování členů seznamu.
    ///
    /// Z hlavní složky obsahující README.md:
    /// - Hraní: `java L08/M09_Shikaku.java`
    /// - Kontrola této mise: `java L08/Check.java M09_Shikaku.java`
    ///
    /// Spusť command (příkaz) pro hraní uvedený výše a tažením vyznač obdélník kolem jedné nápovědy,
    /// potom jej zkus vrátit klávesou U nebo smazat pravým kliknutím. Po opravě: ulož soubor, spusť
    /// uvedenou kontrolu a zahraj si znovu.
    ///
    /// Potřebuješ výklad? Připomenutí · F20 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
    /// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
    ArrayList<Box> snapshot() {
        return boxes;
    }

    // <editor-fold defaultstate="collapsed" desc="Připomenutí · F20">
    /// ### References: jiné jméno, nebo jiný objekt?
    ///
    /// Array variable (proměnná pole) uchovává reference (odkaz). Assignment (přiřazení) může dát dvěma
    /// jménům přístup k jedinému array (poli); zápis přes kterékoli změní sdílený objekt. `clone()` na
    /// jednorozměrném int array vytvoří další array se zkopírovanými hodnotami. Původní array i kopie se
    /// potom mohou měnit nezávisle.
    ///
    /// ```java
    /// int[] wave = {4, 1};
    /// int[] shared = wave;
    /// int[] saved = wave.clone();
    /// wave[1] = 7;
    /// IO.println(shared[1]);
    /// IO.println(saved[1]);
    /// ```
    ///
    /// Výstup je sedm a potom jedna: shared vidí zápis, saved uchová kopii. Po `saved[0] = 8` předpověz
    /// `wave[0]` a `saved[0]`. `clone()` kopíruje jednu vrstvu: element (prvky) mřížky jsou reference na
    /// řádky, takže kopie vnějšího array řádky dál sdílí. Ptej se, které objekty zůstaly společné.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
    /// ## Jak číst právě tyto řádky
    ///
    /// - `ArrayList<Box>` — Mutable list (měnitelný seznam), jehož prvky jsou hodnoty record (záznamu) Box.
    /// - `snapshot()` — Požaduje členy seznamu obdélníků před další skutečnou úpravou.
    /// - `return boxes` — Vrací stejný objekt seznamu, takže změny jeho členů zůstávají sdílené.
    ///
    /// ### Co už obstarává připravená část
    ///
    /// add() ověřuje plochu, počet nápověd a překryv; remove() hledá vybraný obdélník. Obě části před změnou
    /// ukládají snapshot (snímek) a připravené undo() tento seznam jednou obnoví.
    // </editor-fold>

    // <editor-fold defaultstate="expanded" desc="Pro tuto hru">
    /// ### Mutable List (měnitelný seznam) potřebuje novou nádobu
    ///
    /// ArrayList ukládá uspořádanou měnitelnou skupinu element (prvků). `<String>` určuje element type
    /// (typ prvku); `<>` nechá constructor (konstruktor) typ odvodit. Constructor přijímající jinou
    /// collection (kolekci) zkopíruje členství do nového měnitelného List.
    ///
    /// ```java
    /// var draft = new ArrayList<String>();
    /// draft.add("swirl");
    /// var kept = new ArrayList<>(draft);
    /// draft.clear();
    /// IO.println(kept.size());
    /// ```
    ///
    /// Výstup je jedna: `add` připojilo hodnotu, `clear` vyprázdnilo jen draft a `size` počítá prvky
    /// kept. Předpověz přidání dalšího prvku do draft. Box této hry obsahuje jen pevné int component
    /// (složky), proto lze bezpečně sdílet hodnoty Box, ale ne měnitelný List. Undo potřebuje mutable
    /// container (měnitelnou nádobu), protože další kreslení přidává či odebírá obdélníky.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
    /// Seznam se mění, ale každý Box record si navždy ponechá své složky.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
    /// Vrácení boxes sdílí tentýž seznam; klonování každého Box není nutné.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
    /// Vrať new ArrayList<>(boxes), aby změny členství byly nezávislé.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
    final int WIDTH = 470, HEIGHT = 400;

    record Box(int x, int y, int width, int height) {
        boolean contains(int xx, int yy) {
            return xx >= x && yy >= y && xx < x + width && yy < y + height;
        }
    }

    int[][] clues = {{0, 0, 0, 6, 0}, {6, 0, 0, 0, 0}, {0, 0, 0, 0, 4}, {0, 0, 3, 0, 0}, {0, 4, 0, 2, 0}};
    ArrayList<Box> boxes = new ArrayList<>();
    int startX = -1, startY, endX, endY;
    String message;
    /// restart(): Obnovuje počáteční model Shikaku pro další pokus.
    void restart() {
        boxes.clear();
        previousBoxes = null;
        startX = -1;
        message = "Drag a rectangle around exactly one clue";
    }

    /// add(): Před uložením a přidáním ověřuje číslo, plochu a nepřekrývání obdélníku.
    boolean add(int x1, int y1, int x2, int y2) {
        int x = Math.min(x1, x2), y = Math.min(y1, y2), w = Math.abs(x1 - x2) + 1, h = Math.abs(y1 - y2) + 1;
        if (x < 0 || y < 0 || x + w > 5 || y + h > 5) {
            return false;
        }
        int count = 0, area = 0;
        for (int yy = y; yy < y + h; yy++) {
            for (int xx = x; xx < x + w; xx++) {
                if (clues[yy][xx] != 0) {
                    count++;
                    area = clues[yy][xx];
                }
                for (var b : boxes) {
                    if (b.contains(xx, yy)) {
                        return false;
                    }
                }
            }
        }
        if (count != 1 || area != w * h) {
            return false;
        }
        previousBoxes = snapshot();
        boxes.add(new Box(x, y, w, h));
        return true;
    }

    void remove(int x, int y) {
        boolean found = false;
        for (var box : boxes) {
            if (box.contains(x, y)) {
                found = true;
            }
        }
        if (found) {
            previousBoxes = snapshot();
            boxes.removeIf(box -> box.contains(x, y));
            message = "Rectangle removed";
        }
    }

    boolean won() {
        int area = 0;
        for (var b : boxes) {
            area += b.width * b.height;
        }
        return area == 25;
    }

    int cellX(int x) {
        return (x - 80) / 55;
    }

    int cellY(int y) {
        return (y - 40) / 55;
    }

    boolean inside(int x, int y) {
        return x >= 80 && y >= 40 && x < 355 && y < 315;
    }

    /// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
    void draw(Graphics2D g) {
        Color[] palette = {new Color(210, 230, 255), new Color(255, 225, 195), new Color(215, 245, 210), new Color(240, 215, 250), new Color(255, 245, 185), new Color(200, 245, 240)};
        for (int i = 0; i < boxes.size(); i++) {
            var b = boxes.get(i);
            g.setColor(palette[i % palette.length]);
            g.fillRect(80 + b.x * 55, 40 + b.y * 55, b.width * 55, b.height * 55);
        }
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= 5; i++) {
            g.drawLine(80 + i * 55, 40, 80 + i * 55, 315);
            g.drawLine(80, 40 + i * 55, 355, 40 + i * 55);
        }
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.DARK_GRAY);
        for (var b : boxes) {
            g.drawRect(80 + b.x * 55 + 2, 40 + b.y * 55 + 2, b.width * 55 - 4, b.height * 55 - 4);
        }
        if (startX >= 0) {
            g.setColor(Color.BLUE);
            g.drawRect(80 + Math.min(startX, endX) * 55, 40 + Math.min(startY, endY) * 55, (Math.abs(startX - endX) + 1) * 55, (Math.abs(startY - endY) + 1) * 55);
        }
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g.setColor(Color.BLACK);
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (clues[y][x] != 0) {
                    g.drawString("" + clues[y][x], 102 + x * 55, 74 + y * 55);
                }
            }
        }
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        g.drawString(won() ? "Every square covered!" : message, 20, 370);
    }

    void key(int code, boolean pressed) {
        if (pressed && code == KeyEvent.VK_U) {
            undo();
        }
        if (pressed && code == KeyEvent.VK_R) {
            restart();
        }
    }

    void press(int x, int y, int button) {
        if (inside(x, y)) {
            if (button == MouseEvent.BUTTON3) {
                remove(cellX(x), cellY(y));
            }
            else {
                startX = endX = cellX(x);
                startY = endY = cellY(y);
            }
        }
    }

    void drag(int x, int y) {
        if (startX >= 0 && inside(x, y)) {
            endX = cellX(x);
            endY = cellY(y);
        }
    }

    void release(int x, int y) {
        if (startX >= 0) {
            if (inside(x, y)) {
                endX = cellX(x);
                endY = cellY(y);
                message = add(startX, startY, endX, endY) ? "Rectangle added" : "Rejected: check clue, area and overlap";
            }
            startX = -1;
        }
    }

    /// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; zrušení okna
    /// uzavírá toto zobrazení.
    void showWindow() {
        var frame = new JFrame("Shikaku");
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
        var mouse = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                panel.requestFocusInWindow();
                press(e.getX(), e.getY(), e.getButton());
                panel.repaint();
            }
            @Override public void mouseDragged(MouseEvent e) {
                drag(e.getX(), e.getY());
                panel.repaint();
            }
            @Override public void mouseReleased(MouseEvent e) {
                release(e.getX(), e.getY());
                panel.repaint();
            }
        };
        panel.addMouseListener(mouse);
        panel.addMouseMotionListener(mouse);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        var help = new JLabel("<html><div style=\"text-align:center\">Drag: add rectangle   Right click: remove rectangle<br>U: undo one change   R: reset</div></html>", SwingConstants.CENTER);
        help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        frame.add(help, BorderLayout.SOUTH);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }

    /// main(): Spouští model a potom plánuje dodané okno na Swing event thread.
    /// Připravené Undo (vrácení změny) ovládá U: obnoví jeden předchozí tah. R začne hádanku znovu.
    void main() {
        restart();
        SwingUtilities.invokeLater(this::showWindow);
    }

    ArrayList<Box> previousBoxes;

    /// undo(): Obnovuje právě jeden uložený snapshot modelu a spotřebuje možnost vrácení.
    void undo() {
        if (previousBoxes != null) {
            boxes = previousBoxes;
            previousBoxes = null;
            startX = -1;
            message = "Previous layout restored";
        }
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
    /// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
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
    /// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
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
    /// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
    ///   až při pozdějším volání.
    /// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
    /// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
    /// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
    /// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
    ///   (knihovním zpětným voláním), například paintComponent.
    /// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
    ///   implementaci), aby nejprve vykreslila běžné pozadí.
    /// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
    ///   složky a závorky ().
    /// - `null` — Žádný object (objekt) tu není.
    /// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
    /// - `JFrame` — Okno dodané JDK knihovnou Swing.
    /// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
    /// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
    /// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
    ///   se provede později.
    // </editor-fold>

}
