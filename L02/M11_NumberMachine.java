/// # L02.11 · NumberMachine
///
/// **Your mission · Reconstruct a black box:** A small machine and your copy receive the same number.
/// Infer a rule from the printed pairs, add at least one revealing compare call, and make your copy
/// agree for every integer from -20 to 20. Keep the reference machine unchanged. The source is
/// available: predict a new case before reading its reference formula.

// <editor-fold defaultstate="expanded" desc="Learn · F04">
/// A few examples can fit several rules. Choose a new input that separates your guesses. Negative and
/// zero inputs are useful evidence. Each compare call passes one argument to two methods and prints
/// both return values. The parameter number is a local name for that call. Multiplication uses *, not
/// a mathematical superscript; arithmetic is evaluated before return hands its result back. Neutral
/// example: `return amount * 3 - 1;` gives 5 for amount 2. Do not copy the example’s rule into this
/// machine.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · search area">
/// Compare the outputs for 2 and -2. Does merely doubling the input fit both?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a concrete route">
/// imitation must calculate from its parameter. A fixed list of printed answers will not handle other
/// calls.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Possible change · reveals the direction">
/// The reference squares the input, then adds one. Use that rule in imitation and verify another
/// input.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Small program · explore">
void main() {
    IO.println("NUMBER MACHINE: input -> machine / your copy");
    compare(0);
    compare(1);
    compare(2);
    compare(-2);
    compare(5);
}

void compare(int number) {
    IO.println(number + " -> " + machine(number) + " / " + imitation(number));
}

int imitation(int number) {
    return number * number + 1;
}


// <editor-fold defaultstate="collapsed" desc="Reference machine · after predicting">
int machine(int number) {
    return number * number + 1;
}
// </editor-fold>
// </editor-fold>
