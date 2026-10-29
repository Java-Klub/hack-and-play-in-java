/// # L01.06 · HigherOrLower
///
/// Hádej, zda bude příští karta vyšší nebo nižší, a snaž se udržet sérii, než ji ukončí chybný odhad.
/// Stejné karty ti dají další šanci bez trestu.
///
/// **Tvoje mise:** Automat slaví už po jediném správném odhadu. Oprav sdílený target tak, aby displej
/// skóre i pravidlo výhry popisovaly kolo s deseti správnými odhady.
///
/// **Dnes trénuješ:** Named value (pojmenovaná hodnota): target uchovává jediný údaj, který může číst
/// několik částí. Změna jedné initialization (inicializace) má udržet soulad vypsaného a skutečného cíle.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L01/M06_HigherOrLower.java`
/// - Kontrola této mise: `java L01/Check.java M06_HigherOrLower.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše, prohlédni Current card a klikni Higher pro vyšší
/// nebo Lower pro nižší příští kartu. Výsledek i cíl zůstanou vidět; New round obnoví kolo.
///
/// Potřebuješ výklad? Nová dovednost · F03 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int target = 10;

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F03">
/// Variable (proměnná) pojmenuje uloženou hodnotu. Type (typ) popisuje přípustné hodnoty: `int`
/// uchovává celá čísla a `double` může uchovat desetinnou část. Initialization (počáteční přiřazení)
/// dodá první hodnotu; pozdější assignment (přiřazení) ji nahradí.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// int charge = 4;
/// charge = charge + 3;
/// IO.println(charge);
/// ```
///
/// Předpověz output (výstup). Nejprve `charge` uchovává čtyřku. Na druhém řádku Java přečte starou
/// hodnotu pro výpočet pravého expression (výrazu) a potom pod stejným jménem uloží sedmičku. `=` je
/// assignment, nikoli matematické tvrzení, že obě strany mají navždy zůstat stejné. Další statement
/// (příkazy) čtoucí toto jméno nyní dostanou sedmičku. Jméno se neaktualizuje samo: hodnotu musí
/// změnit provedené assignment. Variable označenou `final` nelze znovu přiřadit.
///
/// Nyní předpověz konečné charge, pokud počáteční přiřazení použije šest místo čtyř.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int` — Uložená hodnota je celé číslo vhodné pro počítání správných odhadů.
/// - `target` — Anglické jméno označuje sdílený cíl; liší se od correct, skóre měnícího se během hry.
/// - `=` — Ulož hodnotu vpravo pod jménem vlevo.
/// - `1` — Současný numeric literal (doslovný zápis čísla) je jedna, což vysvětluje příliš krátké kolo.
/// - `;` — Ukonči declaration (zavedení jména) a jeho initialization (výchozí přiřazení).
///
/// ### Co už obstarává připravená část
///
/// reset sestaví a zamíchá balíček. command provede odhad a čte target při rozhodnutí o výhře;
/// Připravené okno čte stejný target pro popisek skóre. main poskytuje tlačítka a restart.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Defaultní main poskytuje karty a tlačítka přímo v Javě. Vyšší karta má větší hodnotu; Jack je
/// 11, Queen 12, King 13 a Ace 14. Shodná karta nepočítá správný tip ani neprohrává. Cíl i počet
/// správných tipů čtou stejný target. Po změně target ulož soubor, zavři staré okno a znovu spusť
/// command pro hraní; New round samo upravenou Javu nenačte.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Najdi jedinou initialization (inicializaci) target nahoře.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Připravený displej i porovnání výhry už čtou target.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Změň int target = 1; na int target = 10;
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
ArrayDeque<Integer> deck;
int current;
int correct;
String outcome;
String message;

void reset() {
    var cards = new ArrayList<Integer>();
    for (int suit = 0; suit < 4; suit++) {
        for (int rank = 2; rank <= 14; rank++) {
            cards.add(rank);
        }
    }
    Collections.shuffle(cards);
    deck = new ArrayDeque<>(cards);
    current = deck.remove();
    correct = 0;
    outcome = message = "";
}

void command(String input) {
    require(outcome.isEmpty());
    require(input.matches("(?i)[hl]"));
    require(!deck.isEmpty());
    int next = deck.remove();
    message = "Next card: " + cardName(next);
    if (next == current) {
        message += "; tie, no penalty.";
    } else {
        if (input.equalsIgnoreCase("h") == (next > current)) {
            correct++;
            message += "; correct!";
        } else {
            outcome = "Wrong prediction. Final score: " + correct;
        }
    }
    current = next;
    if (correct == target) {
        outcome = target + " correct: you win!";
    }
    if (deck.isEmpty() && outcome.isEmpty()) {
        outcome = "Deck exhausted. Final score: " + correct;
    }
}

// Prepared display names; ranks remain numeric in the game rules.
String cardName(int rank) {
    return switch (rank) {
        case 11 -> "Jack";
        case 12 -> "Queen";
        case 13 -> "King";
        case 14 -> "Ace";
        default -> Integer.toString(rank);
    };
}

void main() {
    reset();
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Higher or Lower needs a graphical display; the model remains headless-testable.");
        return;
    }
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("Higher or Lower");
        var card = new javax.swing.JLabel("", javax.swing.SwingConstants.CENTER);
        card.setOpaque(true); card.setBackground(java.awt.Color.WHITE);
        card.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.DARK_GRAY, 2));
        card.setPreferredSize(new java.awt.Dimension(190, 170));
        var score = new javax.swing.JLabel();
        var feedback = new javax.swing.JLabel("Choose whether the next card will be higher or lower.");
        var higher = new javax.swing.JButton("Higher");
        var lower = new javax.swing.JButton("Lower");
        var restart = new javax.swing.JButton("New round");
        Runnable refresh = () -> {
            card.setText("<html><center>Current card<br><span style='font-size:32px'>" + cardName(current) + "</span></center></html>");
            score.setText("Correct guesses: " + correct + " / " + target + "    Cards left: " + deck.size());
            feedback.setText(outcome.isEmpty() ? (message.isEmpty() ? "Choose Higher or Lower. Equal cards give another chance." : message) : outcome + " Use New round to play again.");
            higher.setEnabled(outcome.isEmpty()); lower.setEnabled(outcome.isEmpty());
        };
        higher.addActionListener(event -> { command("h"); refresh.run(); });
        lower.addActionListener(event -> { command("l"); refresh.run(); });
        restart.addActionListener(event -> { reset(); refresh.run(); });
        var buttons = new javax.swing.JPanel();
        buttons.add(higher); buttons.add(lower); buttons.add(restart);
        var content = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        content.add(new javax.swing.JLabel("Guess the next rank. Ace is highest; an equal rank does not count or lose."), java.awt.BorderLayout.NORTH);
        content.add(card);
        var bottom = new javax.swing.JPanel(new java.awt.GridLayout(3, 1));
        bottom.add(score); bottom.add(feedback); bottom.add(buttons);
        content.add(bottom, java.awt.BorderLayout.SOUTH);
        frame.add(content); frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        refresh.run(); frame.pack(); frame.setLocationByPlatform(true); frame.setVisible(true);
    });
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// Připravené vstupní body: main, reset, command.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Malá mapa Javy · volitelné">
/// ## Neznámý zápis?
///
/// Tato malá mapa je dobrovolná. Najdi jen znak nebo slovo, kterému nerozumíš. Není třeba se ji učit
/// dopředu.
///
/// - `///` — Slova pro čtenáře. Java tyto comment (komentářové) řádky při spuštění přeskočí.
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
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `?` — V condition ? first : second vybere první hodnotu, když condition (podmínka) platí, jinak
///   druhou.
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
// </editor-fold>

