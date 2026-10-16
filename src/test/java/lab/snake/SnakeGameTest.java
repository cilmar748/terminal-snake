package lab.snake;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SnakeGameTest {
    @Test void normalMovePreservesLength() {
        SnakeGame g=new SnakeGame(Scenario.NORMAL);g.tick(Direction.RIGHT);
        assertEquals(new Cell(4,2),g.head());assertEquals(3,g.body().size());assertEquals(SnakeGame.Status.RUNNING,g.status());
    }
    @Test void foodGrowsSnakeAndAddsTen() {
        SnakeGame g=new SnakeGame(Scenario.PICNIC);g.tick(Direction.RIGHT);
        assertEquals(4,g.body().size());assertEquals(10,g.score());assertFalse(g.body().contains(g.food()));
    }
    @Test void reverseDirectionIsIgnored() {
        SnakeGame g=new SnakeGame(Scenario.NORMAL);g.tick(Direction.LEFT);
        assertEquals(new Cell(4,2),g.head());assertEquals(Direction.RIGHT,g.direction());
    }
    @Test void topWallEndsRound() {
        SnakeGame g=new SnakeGame(Scenario.NORMAL);
        g.tick(Direction.UP);g.tick(Direction.UP);g.tick(Direction.UP);
        assertEquals(SnakeGame.Status.WALL,g.status());assertEquals(new Cell(3,0),g.head());
    }
    @Test void endedRoundDoesNotMove() {
        SnakeGame g=new SnakeGame(Scenario.NORMAL);
        g.tick(Direction.UP);g.tick(Direction.UP);g.tick(Direction.UP);Cell head=g.head();
        g.tick(Direction.RIGHT);assertEquals(head,g.head());assertEquals(SnakeGame.Status.WALL,g.status());
    }
    @Test void restartRestoresStartingBody() {
        SnakeGame g=new SnakeGame(Scenario.NORMAL);g.tick(Direction.RIGHT);g.reset();
        assertEquals(Scenario.NORMAL.snake,g.body());assertEquals(Scenario.NORMAL.food,g.food());assertEquals(SnakeGame.Status.RUNNING,g.status());
    }
    @Test void loopMoveIntoOldTailCellIsLegal() {
        SnakeGame g=new SnakeGame(Scenario.LOOP);g.tick(Direction.LEFT);
        assertEquals(SnakeGame.Status.RUNNING,g.status());assertEquals(new Cell(1,2),g.head());assertEquals(4,g.body().size());
    }
    @Test void rightWallEndsRound() {
        SnakeGame g=new SnakeGame(Scenario.WALL);
        g.tick(Direction.RIGHT);g.tick(Direction.RIGHT);
        assertEquals(SnakeGame.Status.WALL,g.status());assertEquals(new Cell(7,2),g.head());
    }
    @Test void restartResetsScoreToZero() {
        SnakeGame g=new SnakeGame(Scenario.PICNIC);g.tick(Direction.RIGHT);assertEquals(10,g.score());g.reset();
        assertEquals(0,g.score());
    }
    @Test void constructorRejectsTooSmallBoard() {
        assertThrows(IllegalArgumentException.class,()->new SnakeGame(6,6,Scenario.NORMAL));
        assertThrows(IllegalArgumentException.class,()->new SnakeGame(8,4,Scenario.NORMAL));
    }
    @Test void leftWallEndsRound() {
        SnakeGame g=new SnakeGame(Scenario.NORMAL);
        g.tick(Direction.DOWN);g.tick(Direction.LEFT);g.tick(Direction.LEFT);g.tick(Direction.LEFT);g.tick(Direction.LEFT);
        assertEquals(SnakeGame.Status.WALL,g.status());assertEquals(new Cell(0,3),g.head());
    }
    @Test void bottomWallEndsRound() {
        SnakeGame g=new SnakeGame(Scenario.NORMAL);
        g.tick(Direction.DOWN);g.tick(Direction.DOWN);g.tick(Direction.DOWN);g.tick(Direction.DOWN);
        assertEquals(SnakeGame.Status.WALL,g.status());assertEquals(new Cell(3,5),g.head());
    }
    @Test void selfCollisionEndsRound() {
        SnakeGame g=new SnakeGame(Scenario.PICNIC);
        String seq="DSAASSAWDD";
        for (char k:seq.toCharArray()) g.tick(Direction.fromKey(k));
        assertEquals(SnakeGame.Status.SELF,g.status());assertEquals(new Cell(1,4),g.head());
    }
    @Test void renderContainsHeadFoodAndBorders() {
        SnakeGame g=new SnakeGame(Scenario.NORMAL);
        String r=g.render();
        assertTrue(r.startsWith("+"+"-".repeat(8)+"+\n"));
        assertTrue(r.contains("H"));
        assertTrue(r.contains("*"));
        assertTrue(r.contains("status=RUNNING"));
        assertEquals(8,g.width());assertEquals(6,g.height());
    }
    @Test void renderAfterWonDoesNotThrow() {
        SnakeGame g=wonGame();
        assertDoesNotThrow(g::render);assertDoesNotThrow(g::snapshot);
        assertEquals(null,g.food());assertEquals(10,g.score());
    }
    @Test void eatingFoodOnTailCellIsSelfCollision() {
        SnakeGame g=new SnakeGame(7,5,Scenario.NORMAL);
        setField(g,"snake",new ArrayDeque<>(List.of(new Cell(2,2),new Cell(2,3),new Cell(3,3),new Cell(3,2))));
        setField(g,"direction",Direction.RIGHT);
        setField(g,"food",new Cell(3,2));
        setField(g,"status",SnakeGame.Status.RUNNING);
        g.tick(Direction.RIGHT);
        assertEquals(SnakeGame.Status.SELF,g.status());
    }
    @Test void fillingBoardWins() {
        SnakeGame g=wonGame();
        assertEquals(SnakeGame.Status.WON,g.status());assertEquals(null,g.food());
    }
    private SnakeGame wonGame() {
        SnakeGame g=new SnakeGame(7,5,Scenario.NORMAL);
        Deque<Cell> snake=new ArrayDeque<>();
        snake.addFirst(new Cell(5,4));
        for (int y=0;y<5;y++) for (int x=0;x<7;x++) {
            Cell c=new Cell(x,y);
            if (!c.equals(new Cell(5,4)) && !c.equals(new Cell(6,4))) snake.addLast(c);
        }
        setField(g,"snake",snake);
        setField(g,"direction",Direction.RIGHT);
        setField(g,"food",new Cell(6,4));
        setField(g,"status",SnakeGame.Status.RUNNING);
        g.tick(Direction.RIGHT);
        return g;
    }
    private void setField(Object target,String name,Object value) {
        try {
            Field f=target.getClass().getDeclaredField(name);f.setAccessible(true);f.set(target,value);
        } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
    }
}
