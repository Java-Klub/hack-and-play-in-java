// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # 10.24 · DungeonBlueprints
///
/// Navrhni skladbu místností v podzemí. Přijmi omezený počet malých úkrytů a velkých síní a nech chodby
/// propojit místnosti, které tvoje pravidlo povolilo.
///
/// **Tvoje mise:** Vytvoř admitRoom(room): area (plocha) nejvýše 42 včetně je SMALL s quota (limitem)
/// tři; větší room (místnosti) jsou LARGE s limitem dvě. Přijmi a započti room jen při volné kapacitě
/// její kategorie. Odmítnutí vrací false beze změny počtů. Generator (vytvářecí postup) tě volá až po
/// geometrické kontrole, takže Map quota (limity mapy) rozhodují o skutečném obrázku.
///
/// **Dnes trénuješ:** Použij uložené vztahy key/value (klíč/hodnota) k rozhodnutí o skutečném obrázku
/// nebo jeho vzniku, ne jen popisku.
///
/// Spusť program: nedokončené přijímání nechává plán prázdný. Po dokončení admitRoom prohlédni přijaté
/// místnosti, spojovací chodby a limity kategorií.
boolean admitRoom(Room room) {
    String kind = room.width() * room.height() <= 42 ? "SMALL" : "LARGE";
    int limit = kind.equals("SMALL") ? 3 : 2;
    int used = roomCounts.getOrDefault(kind, 0);
    if (used >= limit) return false;
    roomCounts.put(kind, used + 1);
    return true;
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
/// Room je record (datový záznam): width() a height() čtou components (složky). Jejich součin dává area
/// (plochu). Map key (klíče mapy) jsou společná jména kategorií, nikoli jednotlivé room (místnosti).
/// getOrDefault dá před prvním přijetím nulu. Odmítnutí nesmí spotřebovat quota (limit). Připravené
/// generate() řeší překryvy a omezený počet pokusů a přijaté room spojí chodbami.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Nejdřív podle area (plochy) vyber kategorii a pak přečti její count (počet).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Porovnej used s odpovídajícím limit (limitem) před změnou Map (mapy).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Při plném limit vrať false; jinak ulož used+1 pod kategorii a vrať true.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta · dobrovolně">
/// Navrhni dungeon (podzemí) s jedinou velkou boss chamber (místností pro hlavního nepřítele) a
/// několika malými úkryty. Změň práh area (plochy) i quota (limity) a sleduj jiný příběh stejných
/// pokusů o rozmístění.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
record Room(int x, int y, int width, int height) {
    boolean overlaps(Room other) {
        return x - 1 < other.x + other.width && x + width + 1 > other.x && y - 1 < other.y + other.height && y + height + 1 > other.y;
    }
}

boolean[][] generate(long seed, int requested) {
    if (requested < 2 || requested > 18) {
        throw new IllegalArgumentException("Rooms: 2..18.");
    }
    roomCounts.clear();
    var random = new Random(seed);
    var rooms = new ArrayList<Room>();
    for (int attempt = 0; attempt < 500 && rooms.size() < requested; attempt++) {
        var room = new Room(1 + random.nextInt(37), 1 + random.nextInt(22), 4 + random.nextInt(7), 4 + random.nextInt(7));
        boolean fits = true;
        for (var other : rooms) {
            fits &= !room.overlaps(other);
        }
        if (fits && admitRoom(room)) {
            rooms.add(room);
        }
    }
    boolean[][] floor = new boolean[34][50];
    Room previous = null;
    for (var room : rooms) {
        for (int y = room.y; y < room.y + room.height; y++) {
            for (int x = room.x; x < room.x + room.width; x++) {
                floor[y][x] = true;
            }
        }
        if (previous != null) {
            int x = previous.x + previous.width / 2;
            int y = previous.y + previous.height / 2;
            int targetX = room.x + room.width / 2;
            int targetY = room.y + room.height / 2;
            while (x != targetX) {
                floor[y][x] = true;
                x += Integer.signum(targetX - x);
            }
            while (y != targetY) {
                floor[y][x] = true;
                y += Integer.signum(targetY - y);
            }
            floor[y][x] = true;
        }
        previous = room;
    }
    return floor;
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite room count required.");
    }
    var floor = generate(seed, (int)option);
    var image = new BufferedImage(600, 408, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        for (int y = 0; y < 34; y++) {
            for (int x = 0; x < 50; x++) {
                g.setColor(floor[y][x] ? new Color(235, 220, 180) : new Color(30, 35, 40));
                g.fillRect(x * 12, y * 12, 11, 11);
            }
        }
        g.setColor(Color.WHITE);
        g.drawString("SMALL " + roomCounts.getOrDefault("SMALL", 0) + "/3 | LARGE " + roomCounts.getOrDefault("LARGE", 0) + "/2", 8, 14);
    } finally {
        g.dispose();
    }
    return image;
}


Map<String, Integer> roomCounts = new HashMap<>();

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
        showImages("Dungeon Blueprints", new String[]{"Result"}, new java.awt.image.BufferedImage[]{render(42, 8)});
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
/// - `boolean` — Hodnota true nebo false: ano nebo ne.
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
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
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `null` — Žádný object (objekt) tu není.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Map` — Přiřazuje key (klíčům) hodnoty. Pro chybějící key je třeba rozhodnout výchozí chování.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
// </editor-fold>
