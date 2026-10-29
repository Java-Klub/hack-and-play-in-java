# 2.1 Give a value a name

A program can store a number of lives, a player's name or a chosen target. Giving a value a name lets you use it again later.

## Two named values

Open [Lives.java](examples/02/Lives.java):

```java
void main() {
    int lives = 3;
    String pilot = "Nova";
    IO.println(pilot);
    IO.println(lives);
}
```

The output is:

```text
Nova
3
```

The line `int lives = 3;` introduces a variable named `lives` with an initial value of 3.

| Part | What it says |
| --- | --- |
| `int` | The type: this name stores a whole number. |
| `lives` | The name the author chose for the value. |
| `=` | Store the following value under this name. |
| `3` | The initial value. |
| `;` | The end of the statement. |

`String` is the type for text, so `pilot` gets the value `"Nova"`. For a new variable, write both its type and its name. When using it later, write only the name, as in `IO.println(lives);`.

Write the name exactly: `lives` and `Lives` are two different names. In these examples, use a name only after the line that introduces it. Clear English names such as `lives`, `score` and `pilot` help show what a value means.

## A name and literal text are not the same

Predict these two printed lines when `lives` holds 3:

```java
IO.println("lives");
IO.println(lives);
```

These two lines belong inside the `main` you already know, after the line introducing `lives`.

<details>
<summary>Answer</summary>

```text
lives
3
```

Quotes say “use these characters as text”. A name without quotes asks for the value stored under it. Java does not look for variables inside a quoted message.

</details>

In Lives, change the pilot's name to `Mika` and the number of lives to 5. Before running it, say which two output lines will change and why.

## Join a message and a value

The `+` symbol can join text. In Lives, you can replace the line that prints the number of lives with:

```java
IO.println("Lives: " + lives);
```

For `lives` equal to 3, the result is `Lives: 3`. There is a space after the colon inside the quotes. The name `lives` is outside the quotes, so its value is used. The joined text is passed to `println` as one complete result.

## A small task of your own

Create `PilotCard.java` in `learn/practice/02`. Inside `main`, introduce `String pilot` and `int lives` with your own values. Then print a two-line card, for example:

```text
Pilot: Nova
Lives: 3
```

Build the messages using the names `pilot` and `lives`. Then change only their initial values and check that both messages reflect the new details.

<details>
<summary>Hint</summary>

Introduce both values before printing. Put text labels inside quotes, and use the names after `+` without quotes. Lives shows how to begin and end the file.

</details>

**You have understood this lesson when you can distinguish a name from quoted text and use a name to print its stored value.**

Continue to [Change a stored value](02-change.md).
