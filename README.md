# Hack & Play in Java

**[English](https://github.com/Java-Klub/hack-and-play-in-java/tree/main) · [Česky](https://github.com/Java-Klub/hack-and-play-in-java/tree/ceska-verze)**

**Learn Java by fixing little games — then make them your own.**

12 levels · 143 games, puzzles and creative experiments · 36 core Java skills

![Six programs from Hack & Play in Java: MiniInvaders, MiniPacman, MirrorLaser, SunflowerSpiral, ReactionDiffusionSkins and FractalTreeGarden.](assets/readme/cover.png)

Send aliens packing. Steer a laser through mirrors. Turn a handful of numbers into a rainbow spiral, a branching tree or a retro sound.

Each mission gives you a runnable program and a small piece to finish, fix or change. Predict what the code will do, make your move, check it and play again. Your first repair might fit two lines; later ones give a game a new rule or bring a simulation to life.

## The first mystery

The drawing tools are ready. The wall is grey. Which line hides it?

```java
void paintWall(Graphics2D g) {
    paintTiles(g);
    paintBackground(g);
}
```

Start here, then work towards mazes, arcade games, generative pictures and sound. New ideas arrive in small steps and return in different games.

## What you learn

| What you’ll do | Java skills you will use |
| --- | --- |
| Read and decide | Execution order, variables, strings, arithmetic, conditions, methods and loops |
| Build a world | Arrays, grids, records, enums, `List`, `Map`, `Set` and independent copies |
| Make it respond | Callbacks, coordinates, game ticks, seeded randomness and files |
| Explain and extend | Contracts, functional checks, interfaces, composition, pattern matching and refactoring |

The guide lives **inside the code**: short explanations, worked examples and hints that open one at a time. Explore the supplied game parts when curious. Once downloaded, the programs and checks work offline using JDK libraries.

## Start playing

1. Have **NetBeans and JDK 26 or newer** ready.
2. Download your language branch above. Open **[L01](L01/)** with **File → Open Project**, then open `M01_CafeWallLab.java` under **Source Packages**.
3. Choose **Run → Run File**. Read the mission, edit, **Save → Test File**, then play again. The starter's first failed check is an intentional clue; `PASS` means the checked cases agree with your repair.

Designed for curious beginners, coding clubs and guided workshops. Choose extra games whenever an idea needs another round.
