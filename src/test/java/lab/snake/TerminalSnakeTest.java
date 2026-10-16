package lab.snake;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class TerminalSnakeTest {
    private String run(Scenario scenario, String keys) {
        String out=capture(()->TerminalSnake.replay(scenario,keys));
        return out;
    }
    private String runMain(String[] args,String stdin) {
        return capture(()->TerminalSnake.main(args),stdin);
    }
    private String capture(Runnable r) {
        return capture(r,null);
    }
    private String capture(Runnable r,String stdin) {
        PrintStream oldOut=System.out;
        InputStream oldIn=System.in;
        ByteArrayOutputStream buf=new ByteArrayOutputStream();
        try {
            if (stdin!=null) System.setIn(new ByteArrayInputStream(stdin.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(buf,true,StandardCharsets.UTF_8));
            r.run();
        } finally {
            System.setOut(oldOut);
            if (stdin!=null) System.setIn(oldIn);
        }
        return buf.toString(StandardCharsets.UTF_8);
    }
    @Test void replayRunsKeys() {
        String out=run(Scenario.NORMAL,"D");
        assertTrue(out.contains("scenario=NORMAL replay=D"));
        assertTrue(out.contains("key=D"));
    }
    @Test void replayHandlesResetKey() {
        String out=run(Scenario.LOOP,"SR");
        assertTrue(out.contains("key=S"));
        assertTrue(out.contains("key=R"));
    }
    @Test void mainReplayOnlyPath() {
        String out=runMain(new String[]{"--replay","D"},"");
        assertTrue(out.contains("scenario=NORMAL replay=D"));
    }
    @Test void mainScenarioAndReplay() {
        String out=runMain(new String[]{"--scenario","loop","--replay","S"},"");
        assertTrue(out.contains("scenario=LOOP replay=S"));
    }
    @Test void mainUnknownArgThrows() {
        assertThrows(IllegalArgumentException.class,()->runMain(new String[]{"--bad"},""));
    }
    @Test void mainDefaultChoiceThenQuit() {
        String out=runMain(new String[]{},"1\nQ\n");
        assertTrue(out.contains("TERMINAL SNAKE"));
    }
    @Test void mainChoiceWallAndMove() {
        String out=runMain(new String[]{},"2\nD\nQ\n");
        assertTrue(out.contains("Choose a starting position"));
    }
    @Test void mainChoiceLoopAndReset() {
        String out=runMain(new String[]{},"3\nR\nQ\n");
        assertTrue(out.contains("Choose a starting position"));
    }
    @Test void mainChoicePicnicAndContinue() {
        String out=runMain(new String[]{},"4\n\nQ\n");
        assertTrue(out.contains("Choose a starting position"));
    }
    @Test void mainInvalidKeyShowsHint() {
        String out=runMain(new String[]{},"1\nX\nQ\n");
        assertTrue(out.contains("Use one direction, R or Q."));
    }
    @Test void mainRoundEndedMessage() {
        String out=runMain(new String[]{},"1\nW\nW\nW\nQ\n");
        assertTrue(out.contains("Round ended. R restarts; Q quits."));
    }
    @Test void mainLoopNotEnteredOnEof() {
        String out=runMain(new String[]{},"1\n");
        assertTrue(out.contains("Choose a starting position"));
    }
    @Test void mainMultiCharKeyShowsHint() {
        String out=runMain(new String[]{},"1\nWW\nQ\n");
        assertTrue(out.contains("Use one direction, R or Q."));
    }
    @Test void instantiateUtility() {
        assertNotNull(new TerminalSnake());
    }
}