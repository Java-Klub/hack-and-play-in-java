# 2.2 Change a stored value

The starting number of lives is not the whole story of a game. After a crash, it should be smaller. A variable can hold different values over time.

## Trace one change

Open [Energy.java](examples/02/Energy.java):

```java
void main() {
    int energy = 5;
    IO.println(energy);
    energy = energy - 2;
    IO.println(energy);
}
```

Predict the output, then run the program.

<details>
<summary>Output and trace</summary>

```text
5
3
```

| Step | Stored value of `energy` |
| --- | --- |
| `int energy = 5;` | 5 |
| First print | 5; printing does not change it |
| `energy = energy - 2;` | 3 |
| Second print | 3 |

</details>

On the line `energy = energy - 2;`, Java reads the right-hand side first. It subtracts 2 from the current value, 5. Only then does it store the result, 3, under the name `energy`. This is called assignment.

Here, `=` is not a mathematical statement that both sides must stay equal forever. It is an instruction to change a stored value. Do not write `int` again when changing it: the name was introduced above.

What will be printed if the initial value is 9? Check your prediction. Then restore the initial value to 5 and move the line that changes it until after both print calls. Predict and check this output too.

<details>
<summary>Compare your predictions</summary>

With an initial value of 9 and the change in its original place, you get 9 and 7. If you move the change in the original program until after both print calls, both print 5. Java does not go back and change a line already printed.

</details>

## Task: pay to open a door

Open [EnergyCounter.java](practice/02/EnergyCounter.java). It should print energy twice: before opening the door and afterwards. The door costs 3 units, but the supplied program leaves the value unchanged.

**Change the assignment between the print calls so it subtracts 3 from the energy currently stored.** With an initial value of 8, expect:

```text
8
5
```

Then change only the initial value to 14. The result must be 14 and 11. This checks that the program really calculates from the current value.

<details>
<summary>Hint</summary>

Energy already shows the same kind of change. The right-hand side reads the stored energy and subtracts the cost; the left-hand side stores the result back. Do not write a fixed final number: a different starting value must give a different ending value.

</details>

## When several places read a value

[SharedSettings.java](examples/02/SharedSettings.java) has a setting before `main`:

```java
int goal = 6;

void main() {
    IO.println("Target: " + goal);
    IO.println("Win at: " + goal);
}
```

Both messages read the same `goal`. Change its initial value to 4 and check both printed lines.

A value written outside `main` like this can also be available to other actions in the same program. Games use this to share settings such as a target. We will explore its lifetime in more detail when writing our own methods. For this change, you only need to find the named value and follow where it is read.

## Choose some game practice

- [HigherOrLower](../L01/M06_HigherOrLower.java): find the shared target setting and shorten the card challenge. You search the program; hints in its file will gradually guide you.
- [SteppingFeet](../L01/M07_SteppingFeet.java): change the second assignment to bring both rectangles back into the picture. Here, the place to edit is already marked.

Each task gives its own target value. After playing, you can run that game's check, for example:

```text
java L01/Check.java M06_HigherOrLower.java
```

**You have understood this lesson when you can explain how an assignment reads the old value before storing the new one, and correctly predict several assignments in a row.**

Continue to [Let the program calculate](02-calculations.md).
