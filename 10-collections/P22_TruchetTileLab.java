// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # 10.22 · TruchetTileLab
///
/// Řiď růst klikatého vzoru z dílků. Nenech jednu orientaci příliš utéct druhé: Map (mapa) se stane
/// pravidlem kreslení, nejen počítadlem v popisku.
///
/// **Tvoje mise:** Vytvoř chooseOrientation(preferred). Přijmi jen LEFT nebo RIGHT. Obvykle vrať
/// preference (preferovanou orientaci), ale pokud její aktuální count (počet) vede alespoň o dvě, vyber
/// druhou. Zvyš jen count vybrané orientace a tu vrať. Jiný text musí vyvolat exception (výjimku) beze
/// změn Map (mapy). Tvoje volba řídí skutečné oblouky při růstu vzoru.
///
/// **Dnes trénuješ:** Použij uložené vztahy key/value (klíč/hodnota) k rozhodnutí o skutečném obrázku
/// nebo jeho vzniku, ne jen popisku.
///
/// Spusť program a prohlédni obloukový vzor i počty LEFT/RIGHT. Po opravě oba počty odpovídají dílkům a
/// pravidlo výběru mění skutečné oblouky.
String chooseOrientation(String preferred) {
    if (!preferred.equals("LEFT") && !preferred.equals("RIGHT")) throw new IllegalArgumentException("Use LEFT or RIGHT.");
    String other = preferred.equals("LEFT") ? "RIGHT" : "LEFT";
    int count = orientationCounts.getOrDefault(preferred, 0);
    int otherCount = orientationCounts.getOrDefault(other, 0);
    String chosen = count - otherCount >= 2 ? other : preferred;
    orientationCounts.put(chosen, orientationCounts.getOrDefault(chosen, 0) + 1);
    return chosen;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F24">
/// ### Map (mapa) pamatuje hodnotu pro každý key (klíč)
///
/// List (seznam) drží pozice. Map drží vztah: každý key vede k jedné aktuální value (hodnotě). Zde
/// jméno barvy vede k počtu.
///
/// ```java
/// Map<String, Integer> marbles = new HashMap<>();
/// marbles.put("blue", 2);
/// int before = marbles.getOrDefault("blue", 0);
/// marbles.put("blue", before + 1);
/// IO.println(marbles.getOrDefault("gold", 0));
/// ```
///
/// Blue má nyní počet 3; pro gold se vypíše 0. `getOrDefault(key, fallback)` vrátí fallback (náhradní
/// hodnotu), když key chybí, ale nic nevloží. `put` nahradí dosavadní value daného key: samo nepřičítá.
/// Ostatní key zůstanou beze změny.
///
/// `containsKey` zjišťuje samotnou přítomnost. `entrySet()` umožní projít dvojice key/value: každý
/// entry (položka) poskytuje `getKey()` a `getValue()`. Map neslibuje, že první entry má nejvyšší
/// value; vítěze musíš vybrat porovnáváním. Předpověz blue po dalším stejném kroku. Potom předpověz
/// výsledek `getOrDefault("gold", 9)` a rozhodni, zda se tím gold uložilo.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst tuto část
///
/// Chybějící key (klíče) mají přes getOrDefault count (počet) 0. Náskok je preferredCount - otherCount
/// před přidáním dílku. Dvě volby LEFT z prázdné Map (mapy) jsou povolené; třetí požadavek LEFT se musí
/// změnit na RIGHT. Renderer (vykreslení) kreslí vrácenou volbu a pro nový obrázek Map vyprázdní.
/// Nezvyšuj orientaci, kterou jsi nevybral.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Přečti oba count (počty), než jeden změníš.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Pojmenuj druhou orientaci a rozhodni, zda preference (preferovaná) už vede o dvě.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Odmítni neznámé jméno, vyber podle starého rozdílu, zvyš zvolený key (klíč) pomocí getOrDefault a
/// vrať jej.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta · dobrovolně">
/// Změň povolený náskok na čtyři, nebo záměrně zvýhodni LEFT. Porovnej vzniklé smyčky se stejným random
/// seed (náhodným zárodkem), aby se měnilo jen tvoje pravidlo.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
BufferedImage render(long seed, double option) {
    int count = (int)option;
    if (!Double.isFinite(option) || count < 2 || count > 30) {
        throw new IllegalArgumentException("Tiles per side: 2..30.");
    }
    orientationCounts.clear();
    int size = 480 / count;
    var image = new BufferedImage(size * count, size * count, BufferedImage.TYPE_INT_RGB);
    var random = new Random(seed);
    var g = image.createGraphics();
    try {
        g.setColor(new Color(245, 230, 205));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setColor(new Color(20, 65, 90));
        g.setStroke(new BasicStroke(Math.max(2, size / 7)));
        for (int row = 0; row < count; row++) {
            for (int col = 0; col < count; col++) {
                int x = col * size;
                int y = row * size;
                String orientation = chooseOrientation(random.nextBoolean() ? "LEFT" : "RIGHT");
                if (orientation.equals("LEFT")) {
                    g.drawArc(x - size / 2, y - size / 2, size, size, 270, 90);
                    g.drawArc(x + size / 2, y + size / 2, size, size, 90, 90);
                } else {
                    g.drawArc(x + size / 2, y - size / 2, size, size, 180, 90);
                    g.drawArc(x - size / 2, y + size / 2, size, size, 0, 90);
                }
            }
        }
        g.drawString("LEFT " + orientationCounts.getOrDefault("LEFT", 0) + " / RIGHT " + orientationCounts.getOrDefault("RIGHT", 0), 8, 14);
    } finally {
        g.dispose();
    }
    return image;
}


Map<String, Integer> orientationCounts = new HashMap<>();

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
    try {
        showImages("Truchet Tile Lab", new String[]{"Result"}, new java.awt.image.BufferedImage[]{render(42, 12)});
    } catch (Exception failure) {
        IO.println("Cannot draw this result; check the editable rule: " + failure);
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
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Map` — Přiřazuje key (klíčům) hodnoty. Pro chybějící key je třeba rozhodnout výchozí chování.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
// </editor-fold>
