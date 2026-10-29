/// # 04.11 · PokerDice
///
/// Hoď pěti kostkami, ponech slibné hodnoty a před uložením bodů přehoď vybrané pozice. Pět kol umožní
/// porovnat malé jisté odměny se snahou získat silnější kombinace.
///
/// **Tvoje mise · Sestav férového rozhodčího:** Rozhodčí full house nemá funkční pravidlo. Doplň
/// rozhodnutí triple a pair a spoj
/// je tak, aby tuto kategorii vyhrála jen trojice společně s dvojicí. Samotná trojice si ponechá
/// menší běžnou odměnu.
///
/// **Dnes trénuješ:** Skládání boolean condition (logických podmínek): OR přijme kteroukoli část, zatímco
/// AND vyžaduje obě. U trojice bez dvojice nejdřív urči obě pravdivostní hodnoty zvlášť a teprve potom je
/// spoj.
///
/// Spusť program: kostky jsou očíslované a kliknutím je vybereš k přehození. V úvodní kombinaci
/// ponech tři dvojky, vyber kostky 4 a 5 a stiskni Reroll selected. Bank points uloží body a
/// připraví další kolo; Scoring help vysvětlí kombinace.
boolean fullHouse(int largestGroup, int pairs) {
    boolean triple = largestGroup == 3;
    boolean pair = pairs == 1;
    return triple && pair;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F06">
/// Boolean expression (logický výraz) dává `true` nebo `false`. `&&` vyžaduje obě condition
/// (podmínky), `||` alespoň jednu a `!` obrátí logický výsledek. Comparison (porovnání) jako `>=`
/// zahrnuje rovnost; `>` ji nezahrnuje.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// int age = 14;
/// boolean hasTicket = false;
/// boolean enter = age >= 12 && hasTicket;
/// boolean recognised = "open".equals("OPEN");
/// ```
///
/// Předpověz oba výsledky: každý je `false`. Věk stačí, ale podmínka vstupenky ne. `equals` porovnává
/// obsah String (řetězců) a rozlišuje velikost písmen; `equalsIgnoreCase` ji záměrně ignoruje. U
/// object (objektů) se `==` ptá, zda dva reference (odkazy) označují tentýž objekt, nikoli zda mají
/// stejný text. Porovnání obsahu a rozsahu odpovídají na různé otázky. Než condition uvěříš, ověř
/// hodnotu přesně na hranici a těsně za ní.
///
/// Nyní předpověz enter pro věk dvanáct s platnou vstupenkou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Dvě skutečnosti a jeden rozsudek
///
/// largestGroup a pairs jsou dodané počty, nikoli kostky k hodu. Doplň dva boolean expression
/// (logické výrazy), každý pro jednu skutečnost. Jméno boolean může být součástí dalšího boolean
/// expression. return jejich odpovědi spojí; points už přiděluje 35 za tuto kategorii a 20 za běžnou
/// trojici. Rozhodčí tedy mění skutečné skórování.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Průchod hodnotami · zkus předpovědět">
/// Odděl dvě skutečnosti před jejich spojením. Ruka 4,4,4,2,6 má trojici bez dvojice: true a false.
/// Ruka 4,4,4,2,2 má obojí: true a true. Boolean local variable (místní logická proměnná) uchovává
/// jednu takovou odpověď. Před výběrem spojení předpověz obě odpovědi pro 4,4,2,2,6.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Po zprovoznění standardního full house navrhni záměrně jiné domácí pravidlo a před hrou je
/// vysvětli kamarádovi. Například změň odměnu v points, místo abys omylem každou trojici nazýval full
/// house. Porovnej dopad na přehazování kostek.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Obě skutečnosti odvoď nezávisle z largestGroup a pairs.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Před volbou spojovacího operátoru ověř true/false, false/true a true/true.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// triple je largestGroup == 3; pair je pairs == 1; vrať triple && pair.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] dice;
int rolls;
int round;
int total;
String outcome;
Random random = new Random();

void deal() {
    dice = new int[5];
    for (int i = 0; i < 5; i++) {
        dice[i] = random.nextInt(1, 7);
    }
    rolls = 1;
}

void reset() {
    round = total = 0;
    outcome = "";
    deal();
    dice = new int[]{2, 2, 2, 3, 4};
}

int points() {
    int[] n = new int[7];
    for (int d : dice) {
        n[d]++;
    }
    int max = Arrays.stream(n).max().orElse(0);
    int pairs = 0;
    for (int v : n) {
        if (v == 2) {
            pairs++;
        }
    }
    boolean straight = true;
    for (int i = 2; i <= 5; i++) {
        straight &= n[i] == 1;
    }
    straight &= n[1] == 1 || n[6] == 1;
    return max == 5 ? 50 : max == 4 ? 40 : fullHouse(max, pairs) ? 35 : straight ? 30 : max == 3 ? 20 : pairs == 2 ? 10 : pairs == 1 ? 5 : 0;
}

void reroll(String[] p) {
    require(rolls < 3 && p.length > 1);
    var ids = new HashSet<Integer>();
    for (int i = 1; i < p.length; i++) {
        int n = Integer.parseInt(p[i]) - 1;
        require(n >= 0 && n < 5 && ids.add(n));
    }
    for (int n : ids) {
        dice[n] = random.nextInt(1, 7);
    }
    rolls++;
}

void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("bank")) {
        total += points();
        round++;
        if (round == 5) {
            outcome = "Five rounds finished. Total: " + total;
        } else {
            deal();
        }
    } else {
        String[] p = input.split("\\s+");
        require(p[0].equalsIgnoreCase("roll"));
        reroll(p);
    }
}

void main() {
    reset();
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Poker Dice needs a graphical display; the model remains headless-testable.");
        return;
    }
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("Poker Dice");
        var dicePanel = new javax.swing.JPanel(new java.awt.GridLayout(1, 5, 8, 8));
        var picks = new javax.swing.JToggleButton[5];
        for (int i = 0; i < picks.length; i++) {
            picks[i] = new javax.swing.JToggleButton();
            picks[i].setPreferredSize(new java.awt.Dimension(95, 100));
            dicePanel.add(picks[i]);
        }
        var status = new javax.swing.JLabel();
        var feedback = new javax.swing.JLabel("Opening: keep the three 2s; try selecting dice 4 and 5, then Reroll selected.");
        var reroll = new javax.swing.JButton("Reroll selected");
        var bank = new javax.swing.JButton("Bank points / next round");
        var restart = new javax.swing.JButton("New game");
        var rules = new javax.swing.JButton("Scoring help");
        Runnable refresh = () -> {
            boolean selected = false;
            for (int i = 0; i < picks.length; i++) {
                picks[i].setText("<html><center>Die " + (i + 1) + "<br><span style='font-size:30px'>" + dice[i] + "</span><br>" + (picks[i].isSelected() ? "REROLL" : "keep") + "</center></html>");
                picks[i].setEnabled(outcome.isEmpty() && rolls < 3);
                selected |= picks[i].isSelected();
            }
            status.setText(outcome.isEmpty() ? "Round " + (round + 1) + "/5. Rerolls left: " + (3 - rolls) + ". This hand: " + points() + " points. Banked total: " + total : outcome);
            reroll.setEnabled(outcome.isEmpty() && rolls < 3 && selected);
            bank.setEnabled(outcome.isEmpty());
        };
        for (var pick : picks) pick.addActionListener(event -> refresh.run());
        reroll.addActionListener(event -> {
            String request = "roll";
            for (int i = 0; i < picks.length; i++) if (picks[i].isSelected()) request += " " + (i + 1);
            command(request);
            for (var pick : picks) pick.setSelected(false);
            feedback.setText(rolls < 3 ? "Select dice to change, or bank the current points." : "No rerolls left. Bank this hand to continue.");
            refresh.run();
        });
        bank.addActionListener(event -> {
            int earned = points();
            command("bank");
            for (var pick : picks) pick.setSelected(false);
            feedback.setText(outcome.isEmpty() ? "Banked " + earned + " points. A new hand is ready." : "Finished. Use New game to play again.");
            refresh.run();
        });
        restart.addActionListener(event -> { reset(); for (var pick : picks) pick.setSelected(false); feedback.setText("Select the dice you want to change; unselected dice stay."); refresh.run(); });
        rules.addActionListener(event -> javax.swing.JOptionPane.showMessageDialog(frame,
            "Only the highest matching hand scores.\nFive equal dice: 50. Four equal: 40.\nFull house: a triple AND a pair, 35.\nStraight 1-2-3-4-5 or 2-3-4-5-6: 30.\nThree equal: 20. Two pairs: 10. One pair: 5. Otherwise: 0.\nBank keeps those points and advances one of five rounds.", "Scoring help", javax.swing.JOptionPane.INFORMATION_MESSAGE));
        var buttons = new javax.swing.JPanel();
        buttons.add(reroll); buttons.add(bank); buttons.add(restart); buttons.add(rules);
        var content = new javax.swing.JPanel(new java.awt.BorderLayout(8, 8));
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(new javax.swing.JLabel("Click a die to select it for reroll. You may reroll twice, or bank at any time."), java.awt.BorderLayout.NORTH);
        content.add(dicePanel);
        var bottom = new javax.swing.JPanel(new java.awt.GridLayout(3, 1));
        bottom.add(status); bottom.add(feedback); bottom.add(buttons);
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
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
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
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
// </editor-fold>
