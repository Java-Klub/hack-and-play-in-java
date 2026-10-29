# Hack & Play in Java

**[English](https://github.com/Java-Klub/hack-and-play-in-java/tree/main) · [Česky](https://github.com/Java-Klub/hack-and-play-in-java/tree/ceska-verze)**

**Learn Java: explore a game, change its rules and make it your own.**

12 levels · 145 games, puzzles and creative experiments · 36 core Java skills

![Six programs from Hack & Play in Java: MiniInvaders, MiniPacman, MirrorLaser, SunflowerSpiral, ReactionDiffusionSkins and FractalTreeGarden.](cover.png)

Send aliens packing. Steer a laser through mirrors. Turn a handful of numbers into a rainbow spiral, a branching tree or a retro sound.

Each mission gives you a runnable program and a small piece to finish, fix or change. Predict what the code will do, make your move, check it and play again. Your first repair might fit two lines; later ones give a game a new rule or bring a simulation to life.

## Start learning

**[Open the self-study textbook →](learn/README.md)**

Begin with a tiny complete program. Learn what the new notation means, try it in a short task, then use it in a game. The first two chapters are ready: running and reading a program, statement order, values and calculations. The contents also show the plan for later chapters.

On GitHub, read the textbook using the link above. To read offline, download and extract the whole branch, then open `learn/index.html` in your browser. Run examples and games in your editor or terminal using the setup lesson.

## The first mystery

The drawing tools are ready. The wall is grey. Which line hides it?

```java
void paintWall(Graphics2D g) {
    paintTiles(g);
    paintBackground(g);
}
```

This is a taste of the first repair. The textbook first explains calls and statement order using a few lines, then brings you to this game.

## What you learn

| What you’ll do | Java skills you will use |
| --- | --- |
| Read and decide | Execution order, variables, strings, arithmetic, conditions, methods and loops |
| Build a world | Arrays, grids, records, enums, `List`, `Map`, `Set` and independent copies |
| Make it respond | Callbacks, coordinates, game ticks, seeded randomness and files |
| Explain and extend | Contracts, functional checks, interfaces, composition, pattern matching and refactoring |

The **[textbook](learn/README.md)** explains new skills and offers short practice tasks. Specific game missions, explanations of supplied parts and staged hints stay inside the code. Once downloaded, the textbook, programs and checks work offline; the code uses JDK libraries.

Find a hidden pass. Install a cheat code. Build a coach to help beat a bot, or change the value of corners in Reversi. Other missions ask you to rescue a broken game, expose a fake rule with an experiment or reconstruct a black box. Worked explanations follow concrete values; open the staged hints when you need them.

## Want to try a game straight away?

If you can already run and edit Java files, explore the games directly. For step-by-step learning, follow the textbook lessons.

1. Have **JDK 26 or newer** ready. Download your language branch above and open the Java files in your editor or IDE.
2. Open **[L01/M01_CafeWallLab.java](L01/M01_CafeWallLab.java)** and read the mission inside it. In a terminal, go to the downloaded course folder containing this README and run:

   ```text
   java L01/M01_CafeWallLab.java
   ```

3. Close the game, edit and save the file. Check this mission, then run the game again:

   ```text
   java L01/Check.java M01_CafeWallLab.java
   ```

Checks judge the mission goal: `GREEN` means the checked cases passed; `RED` means the goal is not yet complete. Some games start broken; others already work and wait for your new rule or tool. A working original game can therefore still show `RED`. To check a whole level, use `java L01/Check.java`; this includes missions you have not attempted yet.

Games live directly in `L01` through `L12`, in numbered order. Each level has its own `Check.java`. Already inside `L01`? Use `java Check.java` for the level or `java Check.java M01_CafeWallLab.java` for one mission. Keep check files unchanged while solving missions. No project build setup is needed.

Designed for curious beginners, coding clubs and guided workshops. Choose extra games whenever an idea needs another round.

**After completing a mission, make your own version.** Each program offers a specific follow-up experiment. Its check still expects the precise original mission, so a deliberate rule change may show RED even when your new version works correctly.

<details>
<summary>A selection from the game catalogue</summary>

Folders L01–L12 form the current game catalogue. For self-study, follow the textbook lessons and their practice links; the selection below is for free exploration. All 145 programs are not a compulsory checklist.

| Level | Starting selection |
| --- | --- |
| L01 | [CafeWallLab](L01/M01_CafeWallLab.java) → [TypeMelody](L01/M03_TypeMelody.java) → [HigherOrLower](L01/M06_HigherOrLower.java) |
| L02 | [CannonPuzzle](L02/M01_CannonPuzzle.java) → [MadLibsStoryMachine](L02/M05_MadLibsStoryMachine.java) → [Codebreaker](L02/M07_Codebreaker.java) → [PokerDice](L02/M08_PokerDice.java) |
| L03 | [Pig](L03/M01_Pig.java) → [TicTacToe](L03/M04_TicTacToe.java) → [SOS](L03/M09_SOS.java) → [ShutTheBox](L03/M10_ShutTheBox.java) |
| L04 | [Hangman](L04/M01_Hangman.java) → [VirtualScribbler](L04/M06_VirtualScribbler.java) → [YatzyScorecard](L04/M13_YatzyScorecard.java) |
| L05 | [MemoryPairs](L05/M01_MemoryPairs.java) → [Nonogram](L05/M08_Nonogram.java) → [MirrorLaser](L05/M10_MirrorLaser.java) |
| L06 | [SlidingTiles](L06/M03_SlidingTiles.java) → [Sokoban](L06/M05_Sokoban.java) → [LightsOut](L06/M09_LightsOut.java) |
| L07 | [CooperateOrDefect](L07/M02_CooperateOrDefect.java) → [TargetPop](L07/M05_TargetPop.java) → [MiniInvaders](L07/M08_MiniInvaders.java) |
| L08 | [Minesweeper](L08/M01_Minesweeper.java) → [Bridges](L08/M07_Bridges.java) → [ForestFireSandbox](L08/M13_ForestFireSandbox.java) |
| L09 | [FlipbookStudio](L09/M03_FlipbookStudio.java) → [MarkovMischief](L09/M07_MarkovMischief.java) → [FractalTreeGarden](L09/M09_FractalTreeGarden.java) |
| L10 | [AsteroidDrift](L10/M01_AsteroidDrift.java) → [Untangle](L10/M04_Untangle.java) → [Pong](L10/M09_Pong.java) |
| L11 | [TinyStepSequencer](L11/M01_TinyStepSequencer.java) → [MandelbrotPostcards](L11/M04_MandelbrotPostcards.java) → [SecretPNGPostcard](L11/M10_SecretPNGPostcard.java) |
| L12 | [ReactionDiffusionSkins](L12/M01_ReactionDiffusionSkins.java) → [KnightTour](L12/M05_KnightTour.java) → [MountainPostcards](L12/M12_MountainPostcards.java) |

</details>
