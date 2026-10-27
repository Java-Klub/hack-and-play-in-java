// Functional checks: game behaviour only.
public class M10_SlantTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
        + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
        + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }
    public static void main(String[] args) throws Exception {
        var g = new M10_Slant();
        g.reset();
        g.command("1 1 /");
        g.command("undo");
        equal('.', g.cells[0][0], "I085/F20: undo needs independent row arrays");
        g.command("1 1 /");
        g.command("2 2 /");
        g.command("undo");
        equal('/', g.cells[0][0], "I085/F20: older diagonal retained");
        equal('.', g.cells[1][1], "I085/F20: latest diagonal reverted");
        char[][] copy = g.snapshot();
        copy[0][0] = '.';
        equal('/', g.cells[0][0], "I085/F20: snapshot edits cannot change current board");
        g.reset();
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                g.command(r + " " + c + " /");
            }
        }
        expect(g.solved() && !g.outcome.isEmpty(), "I085/F20: authored all-slash board completes");
        g.command("undo");
        expect(!g.solved() && g.outcome.isEmpty(), "I085/F20: undo after completion restores an unfinished round");
        g.command("3 3 /");
        expect(g.solved() && !g.outcome.isEmpty(), "I085/F20: restored round can be completed again");
        StringBuilder input = new StringBuilder();
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                input.append(r + " " + c + " /\n");
            }
        }
        input.append("undo\n/q\n");
        var process = new ProcessBuilder(java.nio.file.Path.of(System.getProperty("java.home"), "bin", "java").toString(), "-cp", System.getProperty("java.class.path"), M10_Slant.class.getName()).redirectErrorStream(true).start();
        try {
            try(var stream = process.getOutputStream()) {
                stream.write(input.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
            expect(process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS), "I085/F20: console replay terminates");
            String output = new String(process.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            int last = output.lastIndexOf("Cell diagonals");
            expect(process.exitValue() == 0 && last >= 0 && output.substring(last).contains("3 [/, /, .]"), "I085/F20: real finished input loop permits undo");
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
        System.out.println("PASS " + checks + " checks");
    }
}
