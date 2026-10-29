# 2.3 Let the program calculate

The same code can calculate a result for different numbers of players, energy levels or scores. Instead of writing an answer by hand, you build an expression that calculates it.

## Basic calculations

You can put these expressions inside `IO.println(...)` in the `main` you already know:

| Expression | Result | Meaning |
| --- | --- | --- |
| `7 + 2` | `9` | Add. |
| `7 - 2` | `5` | Subtract. |
| `7 * 2` | `14` | Multiply; use an asterisk for multiplication. |
| `7 / 2` | `3` | Divide two whole numbers; the fractional part is discarded towards zero. |
| `7 % 2` | `1` | Return the remainder after integer division. |

Multiplication and division take priority over addition and subtraction. `2 + 3 * 4` gives 14. Parentheses choose a different order: `(2 + 3) * 4` gives 20. `/`, `*` and `%` have the same priority; operations at that level go from left to right.

You can use names instead of numbers: `energy - cost` calculates using their currently stored values.

## Whole numbers and decimals

`int` stores whole numbers. `double` can also hold a fractional part. In code, we use a decimal **point**, as in `2.5`.

Open [Sharing.java](examples/02/Sharing.java):

```java
void main() {
    double share = 5 / 2.0;
    IO.println(share);
}
```

The result is `2.5`. The operation with the decimal number `2.0` uses decimal division.

Now predict what will happen if you change only `2.0` to `2`.

<details>
<summary>Why the result can be surprising</summary>

It prints `2.0`. The right-hand side, `5 / 2`, first divides two whole numbers and produces the whole number 2. Only then is it stored in `double` as 2.0. The `double` on the left cannot bring back the lost half.

</details>

Restore the original `2.0`. When dividing, also check the divisor: we do not divide by zero in these examples. `double` stores some decimal values approximately. We will return to money and exact decimal calculations later. For today's speeds and energy sharing, it will do the job.

## Task: split a beam's energy

Open [BeamSplit.java](practice/02/BeamSplit.java). You have `double energy = 9;` and `int beams = 2;`.

**Change the calculation of `perBeam` to divide the energy equally between the beams.** The output should be `4.5`. Use both names so the program can work with other settings too.

Then change only the energy to 10 and the number of beams to 4. Expect `2.5`. `energy` is already a `double`, so a calculation with its value keeps the fractional part.

<details>
<summary>Hint</summary>

Ask which operation finds an equal share. The total amount and the number of shares already have names. Do not copy their values directly into the calculation.

</details>

## Adding numbers and joining text

In Sharing, you can replace the original print call with these two lines inside `main`. Predict their output first:

```java
IO.println("Score: " + 3 + 2);
IO.println("Score: " + (3 + 2));
```

<details>
<summary>Answer</summary>

```text
Score: 32
Score: 5
```

In the first line, `"Score: " + 3` already creates text, and another 2 is joined onto it. The parentheses in the second line calculate the numeric sum, 5, first and then join it to the message.

</details>

## Task: build the pilot's score

Open [Scoreboard.java](practice/02/Scoreboard.java). The pilot starts with a score of 10.

**Add 5 points to `score` and replace the two print calls with a single line** that builds a message from `pilot`, a colon followed by a space, and the current `score`:

```text
Nova: 15
```

With a different initial value of 2 and the name Mika, the same procedure should print `Mika: 7`. Use an assignment to add the points; do not just build a message containing a fixed answer.

<details>
<summary>Hint</summary>

Change the stored score first. Then use text joining from lesson 2.1. Only the separator `": "` is literal text; the value names do not belong inside quotes.

</details>

## Use numbers in a picture

[SunflowerSpiral](../L01/M08_SunflowerSpiral.java) has a supplied picture calculation and one angle for you to find. Practise reading and changing a named decimal value. You do not need to write the internal trigonometric calculations for this mission; the supplied part of the file describes them.

**You have understood this chapter when you can write a short program with your own values, change them through calculations, and explain the difference between `5 / 2` and `5 / 2.0`.**

The next chapter will cover text and player input. For now, you can continue with your own experiment: in a new small program, calculate a reward for different numbers of players and print it with a label. Compare every prediction with an actual run.

[Back to the textbook contents](README.md)
