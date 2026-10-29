# 1.2 Statement order

You can already print your own messages. Now you will find out why two programs with the same steps can do different things.

## Read, predict, run

Open [MissionControl.java](examples/01/MissionControl.java). First, try writing its output on paper or in a note:

```java
void main() {
    IO.println("Check power");
    IO.println("Open gate");
    IO.println("Go");
}
```

<details>
<summary>Output to compare</summary>

```text
Check power
Open gate
Go
```

Inside `main`, these calls run from top to bottom. The first prints its message and finishes; then the second starts, followed by the third.

</details>

Move the last call before the first. What will be at the top of the output now? Save and run it. You changed the order of the steps; their three messages stayed the same.

## Statements and comments

A comment is a message for people reading the code. Java does not carry out an action because of it. `//` marks a comment that continues to the end of that line:

```java
void main() {
    // Say hello to the pilot.
    IO.println("Hello");
}
```

This program prints only Hello. The line beginning with `//` is skipped when it runs. Even an `IO.println` written **after `//` on the same line** stays a comment.

In games, you will also see lines beginning with `///`: these are explanatory comments for you. Arrows beside some sections in the editor let you collapse a longer explanation. If your editor does not offer folding, you can read the comments normally like any other text.

## Task: fix the launch procedure

Open [LaunchSequence.java](practice/01/LaunchSequence.java). It has all three messages it needs, but in the wrong order.

**Move whole lines inside `main` so the output is:**

```text
Doors closed
Engine on
Launch
```

Keep each message exactly once. Changing a comment alone will not change the output. After editing, predict the output and compare it with a run.

<details>
<summary>Hint</summary>

The doors must close first. Then the engine can start, and only then can the ship launch. Move whole lines, including their semicolons.

</details>

## Use the same idea in a game

Now open [CafeWallLab](../L01/M01_CafeWallLab.java) in folder `L01`. Running it shows a grey surface: the tiles are hidden beneath a later coat of paint. The task at the start of the file identifies two lines to move.

Here, you call supplied drawing actions instead of printing text. `paintTiles(g);` asks for tiles, and `paintBackground(g);` asks for grey paint. `g` is the supplied drawing tool. These actions work; you decide their order. `Graphics2D g` in the declaration simply describes the tool that the supplied code provides. You do not need to change it today.

Close the old window, save your change and run the file again. The result should show tiles on a grey background. Check your change from the course's main folder:

```text
java L01/Check.java M01_CafeWallLab.java
```

GREEN confirms the checked cases. If you get RED, read the message and return to the two drawing steps.

Want another picture? [MissingTriangle](../L01/M02_MissingTriangle.java) practises the same ordering idea. Its task asks you to build a drawing from two supplied calls; you do not need to know their internal drawing algorithm to choose between them.

**You have understood this lesson when you can predict the order of printed lines and explain why later drawing can cover earlier drawing.**

Continue to [Give a value a name](02-values.md).
