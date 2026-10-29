# Set up your workshop

In this lesson, you will open the course in NetBeans, run the first file and change its message. We will take the program apart in the next lesson.

## What you need

- **JDK 26 or later** and NetBeans with Java source file launcher support. The JDK compiles and runs code. NetBeans must use this JDK to run these files. If your tools are not installed yet, prepare them before working offline; the official [Getting Started with Java](https://dev.java/learn/getting-started/) page covers JDK setup.
- **The whole course folder, downloaded and extracted.** Its main folder contains `README.md`, `learn` and the source folders. Previewing an individual file on GitHub is not enough to run it.

## Add the whole folder to Favorites

1. In NetBeans, choose **Window → Favorites**.
2. Right-click an empty area in the Favorites window and choose **Add to Favorites…**.
3. Select the main extracted course folder, the one containing `README.md`. Add it and expand its tree.

Favorites shows real files on disk. Editing a file in NetBeans changes this downloaded copy. The whole textbook and all its programs are now in one tree. You do not need to create a project: the Java source file launcher runs `.java` files directly.

<a id="run"></a>
## Run your first program

1. In Favorites, expand `learn`, then `examples` and `01`.
2. Double-click **WakeUp.java** to open it.
3. Choose **Run → Run File** (`Shift+F6`). This runs the open file.
4. Find this in the **Output** window:

   ```text
   ARCADE
   Power on
   ```

If you cannot see Output, choose **Window → Output**. The program printed two messages and finished. It is fine that no game window opened. Later graphical programs will open their own window.

## Change, save, run

In the open WakeUp file, change the text inside the quotes from `ARCADE` to `HELLO`. Choose **File → Save**, then **Run → Run File** again. The first message in Output should now say HELLO. Afterwards, you can put the original text back.

Save stores your edited text in the file. The next run reads that saved version. An already open game window does not update itself: close it before running the program again.

Open later examples and tasks in the same way from Favorites. Each lesson names the path, for example `learn/practice/01/SecretSignal.java`: expand `learn`, `practice` and `01`, open the file and use Run File.

When a lesson asks you to create your own file, keep the `.java` ending, not `.java.txt`. Select that file in the editor just as you would a supplied file.

<a id="checks"></a>
## Check a task with Run File

Short textbook tasks give their expected output directly. Run them and compare the result. Larger programs have a prepared **Check.java** file in their folder.

When a lesson brings you to the first game, check it like this:

1. In Favorites, select **L01/Check.java**. Right-click it and choose **Properties**.
2. Under **Run Single File**, set **Program Arguments** to `M01_CafeWallLab.java`. This selects one task to check. Use exactly the filename given in the lesson, including `.java`.
3. Open Check.java and choose **Run → Run File**. Find the result in Output.

For another task in the same folder, change only Program Arguments. NetBeans remembers this property; leave the Check.java source code unchanged. **Empty Program Arguments run checks for all programs in that folder**, including tasks you have not attempted yet.

- **GREEN**: the checked cases passed.
- **RED**: at least one case does not match yet. Read the message after the program name.
- **Compiler error**: a mistake in the code prevented compilation.

A task may start at RED even though its program runs. Its goal may be a repair or a new rule. To play again, open the program's own file and use Run File.

<a id="errors"></a>
## When something goes wrong

| What you see | What to try |
| --- | --- |
| A different program appeared in Output. | Open the correct `.java` file and use Run File. Also check that you are editing your downloaded copy. |
| Run File is unavailable, or Java does not understand `void main()` or `IO`. | Check that NetBeans has JDK 26 or later available for running files. The file's Properties → Run Single File → JDK selects it. |
| The output contains `error:` and a line number. | The compiler cannot understand the code. Start with the first message; check quotes, brackets and semicolons near the indicated place. |
| You still see the old result after a change. | Save the file and run it again. Close the old game window first. |
| A check reported many unfinished tasks. | In Check.java's Properties, set Program Arguments to the one file named in the current lesson. |

Fix the first error in the output, then run the program again. **Edit → Undo** can reverse the last change.

<details>
<summary>Running from a terminal, if you want to use one</summary>

The JDK can also run the same files directly outside NetBeans. From a terminal in the main course folder:

```text
java learn/examples/01/WakeUp.java
java L01/Check.java M01_CafeWallLab.java
```

After `java`, write the path to the chosen file, including `.java`. After the path to Check.java, you may add the filename of one program to check. You do not need this option to work through the textbook.

</details>

**You are ready to move on when you can find WakeUp in Favorites, run it, and see a changed message after saving.**

Continue to [Your first program](01-first-program.md).
