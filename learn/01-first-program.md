# 1.1 Your first program

Your goal is to understand a few lines that print text and write a similar program yourself. If you cannot run a file yet, return to [setup](00-start.md#run).

## Print two messages

Open [WakeUp.java](examples/01/WakeUp.java) in your editor:

```java
void main() {
    IO.println("ARCADE");
    IO.println("Power on");
}
```

Running it prints two lines:

```text
ARCADE
Power on
```

The program finishes after printing them. No window needs to open.

## What each piece means

| Notation | What it means here |
| --- | --- |
| `void main()` | Describes an action named `main`, which Java starts when you run the program. For now, copy this first line without changing it. `void` says the action does not return a result value; the empty `()` lists no inputs here. |
| `{` and `}` | Mark the beginning and end of this action's body. The steps between them are the ones that run. Every opening brace needs its closing partner. |
| `IO.println` | A built-in Java action for printing one line. The dot selects `println` from `IO`; uppercase and lowercase letters matter in the name. |
| `("ARCADE")` | The parentheses pass the text you want to print to the action. |
| `"ARCADE"` | The text sits between double quotes. The quotes mark its beginning and end; they are not printed themselves. |
| `;` | Ends this statement. Both calls to `IO.println` need one. |

The whole line `IO.println("ARCADE");` is a call to a supplied action. Just as a play button asks for playback, this call asks for printing.

Java keeps spaces inside quotes: `"Power on"` contains a space between its words. Spaces before `IO.println` only help readers see which lines belong inside `main`. We use four spaces to keep the code easy to read.

## Predict, then run

What will be printed if you change the second message to `"Ready!"`? Change it in the file, save, and check your prediction.

<details>
<summary>Compare your result</summary>

```text
ARCADE
Ready!
```

Only the text in the second call changed. The first stayed the same. The result still has two lines.

</details>

## Task: wake the robot

Open [SecretSignal.java](practice/01/SecretSignal.java). For now, it only prints Beep once.

**Add two more calls so running it prints exactly:**

```text
Beep
Beep
Ready
```

Print each message in a separate call. Keep the first line, `void main() {`, and the final `}`.

<details>
<summary>Hint: where to start</summary>

There is already one complete call inside `main`. You can copy its whole line and change the text inside the quotes. Put your new lines before the closing `}` so they stay inside the action.

</details>

<details>
<summary>If the compiler reports an error</summary>

Compare your new lines with the first one: `IO.println`, parentheses, a pair of double quotes and a semicolon. The word `println` contains the letter l followed by n, not the digit 1. Use the plain straight quotes `"` used in the source file.

</details>

## Now write a small program of your own

Create `MySignal.java` in the same folder as SecretSignal. Write the opening `void main() {`, three messages of your own and the closing `}`. Your messages could be an access signal, a short joke or a countdown written in words. We write program messages in English.

Run this new file just like the previous one. From a terminal in the course's main folder, for example:

```text
java learn/practice/01/MySignal.java
```

**You have understood this lesson when you can add your own message, explain quotes and semicolons, and write a small file that runs.**

Continue to [Statement order](01-order.md).
