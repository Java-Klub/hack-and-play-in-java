# Set up your workshop

In this lesson, you will run a supplied file for the first time and find out where its result appears. We will take the program apart in the next lesson.

## What you need

1. **JDK 26 or later.** It includes the `java` program, which compiles and runs your code. An editor alone does not replace Java. If you do not have a JDK yet, install it before working offline. The official [Getting Started with Java](https://dev.java/learn/getting-started/) page explains the basic setup.
2. **The whole course folder, downloaded to your computer.** Its main folder contains `README.md`, `learn`, `L01` and the other games. Extract the ZIP first. Previewing an individual file on GitHub is not enough to run the whole course.
3. **An editor for Java files.** You can use NetBeans, IntelliJ IDEA, VS Code or a text editor. The course uses ordinary `.java` files that the JDK can run directly.

The first programs only print text. They finish after printing it. It is fine if no game window opens.

<a id="run"></a>
## Your first run from a terminal

A terminal is a window where you type commands for your computer. It may be a separate application or part of your editor. The following steps also work if your editor is not set up to run Java yet.

1. Open a terminal **in the main folder of the extracted course**. On Windows, you can click the address bar of that folder in File Explorer, type `cmd` and press Enter. On macOS, you can use the **New Terminal at Folder** service on a folder in Finder; on Linux, there is usually an **Open in Terminal** option.
2. Check that Java is available:

   ```text
   java --version
   ```

   The output must show version 26 or later. If the command is not found or shows an older version, sort out your JDK installation or selection first. Your editor and terminal may be using different JDKs.

3. Run the supplied file:

   ```text
   java learn/examples/01/WakeUp.java
   ```

   The correct result is:

   ```text
   ARCADE
   Power on
   ```

   The terminal then lets you type another command. The program has finished its work.

If Java says it cannot find the file, check the terminal's folder. It should contain the `learn` and `L01` folders directly. The folder containing the whole course can have any name.

### How to run other files

After `java`, write the path to the chosen file from the main course folder, including the `.java` ending. For the first short task, use:

```text
java learn/practice/01/SecretSignal.java
```

For another example or task, change the whole path to the file named in the lesson. Keep the terminal in the main course folder. In an editor that can run Java, select the corresponding file instead.

## Open and change a file

Open `learn/examples/01/WakeUp.java` in your editor. **The filename must end in `.java`, not `.java.txt`.** Opening a link in a browser may show the file's text or download it. To edit it, open it in your editor.

Change `ARCADE` inside the quotes to `HELLO`, save, and run the same file again. The first line of output should now say HELLO. Afterwards, you can put the original text back.

`Save` stores your edited text in the file. The next run reads that saved version. An already open game window does not update itself: close it before running the program again.

## The same work in NetBeans

If you use NetBeans with JDK 26 or later, you can run a program directly from the editor:

1. Choose **File → Open File…** and open `learn/examples/01/WakeUp.java` from the downloaded course folder. You do not need to create a project.
2. After editing, choose **File → Save**.
3. Choose **Run → Run File** (`Shift+F6`). This runs the selected file.
4. Text appears in the **Output** window. If you cannot see it, choose **Window → Output**. Graphical games will open their own window later.

Open and run later tasks in the same way. If running Java in your editor is not set up yet, use the terminal steps above. To check one game, use the terminal command under [How you will check a game change later](#checks).

<a id="errors"></a>
## When something goes wrong

| What you see | What it means and what to try |
| --- | --- |
| The `java` command is not found. | The terminal cannot access a JDK. Check its installation and setup using the page above. |
| Java cannot find `WakeUp.java`. | Check the terminal's folder and the path `learn/examples/01/WakeUp.java`. |
| A message contains `error:` and a filename with a line number. | The compiler cannot understand the program as written. Start with the first message; check quotes, brackets and semicolons around the indicated place. |
| You still see the old result after an edit. | Save the file and check that you are running the same copy you edited. Close the old game window. |
| Nothing else happens after the output. | In the first examples, this is the end of the program. Your next command goes to the terminal. |

One mistake can produce several messages. Fix the first one, then run the file again. Your editor's Undo command can reverse the last change.

<a id="checks"></a>
## How you will check a game change later

The short textbook tasks give their expected output directly. Run your program and compare it with that output. Games also have prepared functional checks in their own `Check.java`.

For example, to check the first game from the same main folder:

```text
java L01/Check.java M01_CafeWallLab.java
```

- **GREEN** means the game passed the cases checked for its task.
- **RED** means at least one checked case does not match yet. Read the message after the game's name.
- **Compiler error** means a mistake in the code prevented it from being compiled.

A game may start at RED even though it runs. Its task asks you to fix something or introduce a new rule. Do not edit `Check.java` while solving the task. When you reach your first game, the lesson will give you that game's check command.

**You are ready to move on when you can run WakeUp, change its first message and see the new message after saving.**

Continue to [Your first program](01-first-program.md).
