// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import module java.base;
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

class M05_ChaosGame {
    /// # L11.05 · ChaosGame
    ///
    /// Opakovaně poskoč o polovinu vzdálenosti k náhodně vybranému rohu. Z velice malého náhodného pravidla
    /// může vyrůst překvapivě uspořádaný fraktál.
    ///
    /// **Tvoje mise:** Poslední platný vrchol se nikdy nevybere. Oprav rozmezí volby včetně jediného
    /// dostupného vrcholu a chyby při nulovém počtu.
    ///
    /// **Dnes trénuješ:** Meze Random mají exclusive upper end (nezahrnutý horní konec). Převeď počet
    /// platných kandidátů na správné rozmezí volby a nevyřaď omylem posledního kandidáta.
    ///
    /// Z hlavní složky obsahující README.md:
    /// - Hraní: `java L11/M05_ChaosGame.java`
    /// - Kontrola této mise: `java L11/Check.java M05_ChaosGame.java`
    ///
    /// Po spuštění programu napiš seed (počáteční číslo) nebo stiskni Enter pro 42; prohlédni Java
    /// obrázek, potom jej zavři a zopakuj stejný seed nebo porovnej 43. Po opravě: ulož soubor, spusť
    /// uvedenou kontrolu a zahraj si znovu.
    ///
    /// Potřebuješ výklad? Připomenutí · F27 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
    /// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
    int chooseVertex(Random random, int count) {
        if (count < 1) throw new IllegalArgumentException("No legal vertices.");
        return random.nextInt(Math.max(1, count - 1));
    }

    // <editor-fold defaultstate="collapsed" desc="Připomenutí · F27">
    /// ### Seeded randomness (náhoda se seedem): opakuj pokus
    ///
    /// Pseudorandom generator (generátor pseudonáhodných hodnot) vytváří posloupnost. Seed (počáteční
    /// hodnota) určuje výchozí stav; stejné seedy a stejná volání zopakují stejné výsledky.
    ///
    /// ```java
    /// var first = new Random(9);
    /// var second = new Random(9);
    /// int a = first.nextInt(4);
    /// int b = second.nextInt(4);
    /// IO.println(a == b);
    /// ```
    ///
    /// Výstup je `true`. Každé nextInt posune příslušný generátor. `nextInt(4)` vybere celé číslo od 0 do
    /// 3; horní mez nepatří do výběru. V celém generovaném obrázku používej jeden generátor, aby další
    /// volby četly další místa posloupnosti. Opakované vytváření se stejným seedem uvnitř loop (cyklu)
    /// stále vrací první volbu. Různé seedy mohou náhodou dát stejný jednotlivý výsledek.
    ///
    /// Předpověz: která čísla smí vrátit `nextInt(3)` a může vrátit 3?
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
    /// ## Jak číst právě tyto řádky
    ///
    /// - `count < 1` — Při nulovém či záporném počtu kandidátů neexistuje platná volba.
    /// - `random.nextInt` — Vrátí integer (celé číslo) od nuly přísně pod předanou mezí.
    /// - `Math.max(1, count - 1)` — Udržuje kladnou mez, ale nyní zmenšuje platné rozmezí.
    ///
    /// ### Co už obstarává připravená část
    ///
    /// render opakovaně žádá chooseVertex o další roh pomocí jednoho generator (generátoru), posouvá
    /// bod a kreslí návštěvy. main zobrazí výsledek zvoleného seed (počátečního čísla) v Javě.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
    /// Random.nextInt(bound) hranici bound nezahrnuje.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
    /// Odečtení jedné od count odstraní poslední platný vrchol.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
    /// Prázdný rozsah ověř před omezeným náhodným voláním.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
    double[] jump(double x, double y, double targetX, double targetY) {
        return new double[]{(x + targetX) / 2, (y + targetY) / 2};
    }

    /// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
    BufferedImage render(long seed, double option) {
        int count = (int)option;
        if (!Double.isFinite(option) || count < 100 || count > 300000) {
            throw new IllegalArgumentException("Points: 100..300000.");
        }
        var image = new BufferedImage(500, 420, BufferedImage.TYPE_INT_RGB);
        var random = new Random(seed);
        double[][] vertices = {{250, 15}, {15, 405}, {485, 405}};
        double[] point = {250, 210};
        for (int i = 0; i < count + 20; i++) {
            int corner = chooseVertex(random, 3);
            point = jump(point[0], point[1], vertices[corner][0], vertices[corner][1]);
            if (i >= 20) {
                image.setRGB((int)point[0], (int)point[1], Color.HSBtoRGB(corner / 3.0F, 0.6F, 1));
            }
        }
        return image;
    }


    /// Program spusť uvedeným command (příkazem) pro hraní. Kontrola hry ověří pravidlo bez otevření
    /// okna. Připravená část zajišťuje pouze uvedenou hlavní hru nebo pokus.
    void showImages(String title, String[] captions, java.awt.image.BufferedImage[] images) {
        if (java.awt.GraphicsEnvironment.isHeadless()) return;
        javax.swing.SwingUtilities.invokeLater(() -> {
            var frame = new javax.swing.JFrame(title);
            frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
            var pictures = new javax.swing.JPanel(new java.awt.GridLayout(1, images.length, 8, 8));
            for (int i = 0; i < images.length; i++) {
                var picture = new javax.swing.JPanel(new java.awt.BorderLayout());
                picture.add(new javax.swing.JLabel(captions[i], javax.swing.SwingConstants.CENTER), java.awt.BorderLayout.NORTH);
                double scale = Math.min(1, Math.min(900.0 / images.length / images[i].getWidth(), 550.0 / images[i].getHeight()));
                var icon = new javax.swing.ImageIcon(images[i].getScaledInstance(
                        Math.max(1, (int)(images[i].getWidth() * scale)),
                        Math.max(1, (int)(images[i].getHeight() * scale)), java.awt.Image.SCALE_FAST));
                picture.add(new javax.swing.JLabel(icon), java.awt.BorderLayout.CENTER);
                pictures.add(picture);
            }
            frame.add(pictures);
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        });
    }


    void main() {
        String input = IO.readln("Seed (blank 42, q quits): ");
        if (input == null || input.strip().equalsIgnoreCase("q")) return;
        try {
            long seed = input.isBlank() ? 42 : Long.parseLong(input.strip());
            showImages("Chaos Game", new String[]{"Seed " + seed}, new java.awt.image.BufferedImage[]{render(seed, 50000)});
        } catch (IllegalArgumentException failure) {
            IO.println("Use a whole-number seed: " + failure.getMessage());
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
    /// - `double` — Numeric type (číselný typ) pro přibližná desetinná čísla. Java používá desetinnou tečku.
    /// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
    /// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
    ///   textu nepatří.
    /// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
    ///   literal (přímo zapsanou hodnotu).
    /// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
    /// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
    /// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
    /// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
    /// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
    /// - `++` — Zvětší číselnou hodnotu o jedna.
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
    /// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
    /// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
    /// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
    /// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
    /// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
    /// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
    ///   zopakuje stejnou posloupnost.
    /// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
    ///   řádek vstupu.
    // </editor-fold>

}
