package info3.game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import info3.game.automata.ast.AST;
import info3.game.automata.parser.AutomataParser;
import info3.game.automate.Automate;
import info3.game.automate.ParserToAutomate;
import info3.game.automate.State;
import info3.game.automate.Transitions;
import info3.game.automate.condition.Key;
import info3.game.automate.condition.Binary;
import info3.game.entity.Block;
import info3.game.entity.DynamicEntity;
import info3.game.entity.Engineer;
import info3.game.automate.condition.True;
import info3.game.entity.Entity;
import info3.game.entity.Malus;
import info3.game.entity.Mexican;
import info3.game.entity.Player;
import info3.game.entity.PowerUp;
import info3.game.entity.TEAM;
import info3.game.entity.blocks.MalusBlock;
import info3.game.entity.blocks.MovingHorizontalPlatform;
import info3.game.entity.blocks.MovingVerticalPlatform;
import info3.game.entity.blocks.PortalBlock;
import info3.game.entity.blocks.PowerUpBlock;
import info3.game.entity.blocks.SpawnerPoint;
import info3.game.gametimer.GameTimer;
import info3.game.weapon.Weapon;

public class GameSession {
    public Game game;
    public static GameSession gameSession;

    private long updateTime;

    public GameTimer gametime;

    public Player player1;
    public Player player2;

    public Camera camera;

    long totalElapsed;

    public List<DynamicEntity> entities;
    List<DynamicEntity> toAddEntities;
    List<DynamicEntity> toRemoveEntities;

    public List<PowerUpBlock> powerUpBlocks = new ArrayList<PowerUpBlock>();
    public List<MalusBlock> malusBlocks = new ArrayList<MalusBlock>();

    public List<Key> keys;
    public Map map;
    public List<Automate> allAutomates;
    public Automate defaultAutomate;
    public List<SpawnerPoint> spawnerPoints;
    public BufferedImage image;
    public boolean restart = false;

    public List<Integer> keysName = new ArrayList<Integer>();

    public GameSession(Game game, String mapPath, String GalFile, String p1, String p2) throws Exception {
        this.game = game;
        gameSession = this;
        loadAutomates(GalFile);

        keys = new ArrayList<>();
        loadKeys();

        entities = new ArrayList<DynamicEntity>();
        toAddEntities = new ArrayList<DynamicEntity>();
        toRemoveEntities = new ArrayList<DynamicEntity>();
        spawnerPoints = new ArrayList<SpawnerPoint>();

        map = new Map(mapPath);

        camera = new Camera();
        loadEntities(mapPath);
        gametime = new GameTimer();
        if (p1.equals("Mexican"))
            player1 = new Mexican(TEAM.BLUE);
        else
            player1 = new Engineer(TEAM.BLUE);
        if (p2.equals("Mexican"))
            player2 = new Mexican(TEAM.RED);
        else
            player2 = new Engineer(TEAM.RED);
    }

    private void loadKeys() {
        for (Automate current : this.allAutomates) {
            for (Transitions transition : current.trans) {
                if (transition.cond instanceof Key) {
                    if (findKEy(((Key) transition.cond).name) == -1)
                        keys.add((Key) transition.cond);
                } else if (transition.cond instanceof Binary) {
                    keys.addAll(((Binary) transition.cond).loadKeys());
                }
            }
        }
    }

    static public List<PowerUp> getPowerUps() {
        List<PowerUp> arr = new ArrayList<>();
        for (DynamicEntity entity : gameSession.entities) {
            if (entity instanceof PowerUp) {
                arr.add((PowerUp) entity);
            }
        }
        return arr;
    }

    static public List<Malus> getMalus() {
        List<Malus> arr = new ArrayList<>();
        for (DynamicEntity entity : gameSession.entities) {
            if (entity instanceof Malus) {
                arr.add((Malus) entity);
            }
        }
        return arr;
    }

    private void loadEntities(String filename) throws IOException {
        String content = Map.readFile(filename);
        JSONObject json = new JSONObject(content);
        JSONArray jsonEntities = json.getJSONArray("entities");
        for (int i = 0; i < jsonEntities.length(); i++) {
            JSONObject jsonEntity = jsonEntities.getJSONObject(i);
            String id = jsonEntity.getString("id");
            int x = jsonEntity.getInt("x");
            int y = jsonEntity.getInt("y");
            JSONObject tags = jsonEntity.getJSONObject("tags");
            // If it need somes tags...
            IdToEntity(id, x * Block.BLOCK_SIZE, y * Block.BLOCK_SIZE, tags);
        }
    }

    private DynamicEntity IdToEntity(String name, int x, int y, JSONObject tags) throws IOException {
        int speed;
        switch (name) {
            case "MovingHorizontalPlatform":
                int moveX = tags.getInt("blockMove");
                speed = tags.getInt("speed");
                return new MovingHorizontalPlatform(x, y, moveX * Block.BLOCK_SIZE, speed);
            case "MovingVerticalPlatform":
                int moveY = tags.getInt("blockMove");
                speed = tags.getInt("speed");
                return new MovingVerticalPlatform(x, y, moveY * Block.BLOCK_SIZE, speed);
            case "PowerUpBlock":
                PowerUpBlock powerUpBlock = new PowerUpBlock(x, y);
                powerUpBlocks.add(powerUpBlock);
                return powerUpBlock;
            case "MalusBlock":
                MalusBlock malusBlock = new MalusBlock(x, y);
                malusBlocks.add(malusBlock);
                return malusBlock;
            case "PortalBlock":
                int id = tags.getInt("id");
                return new PortalBlock(x, y, id);
            default:
                return null;
        }
    }

    public void addEntity(DynamicEntity entity) {
        this.toAddEntities.add(0, entity);
    }

    public void removeEntity(DynamicEntity entity) {
        this.toRemoveEntities.add(0, entity);
    }

    public void tick(long elapsed) {
        totalElapsed += elapsed;
        clearDeadEntities();
        addBornEntities();
        if (totalElapsed >= 24) {
            for (DynamicEntity entity : entities) {
                entity.tick(totalElapsed);
            }
            map.tick(totalElapsed);
            camera.tick(totalElapsed);
            totalElapsed = 0;
        }
        gametime.tick(elapsed);
    }

    public void paint(Graphics g) {
        map.paint(g, camera);
        for (Entity entity : entities) {
            if (entity instanceof Player)
                continue;
            entity.view.paint(g);
        }
        if (!player1.dead && !this.gametime.end)
            player1.view.paint(g);
        if (!player2.dead && !this.gametime.end)
            player2.view.paint(g);
        camera.paint(g);
        gametime.showGameTimer(g);
    }

    int getLevelWidth() {
        return map.realWidth();
    }

    private void clearDeadEntities() {
        Iterator<DynamicEntity> removeIterator = toRemoveEntities.iterator();
        while (removeIterator.hasNext()) {
            DynamicEntity entity = removeIterator.next();
            entities.remove(entity);
            removeIterator.remove();
        }
    }

    private void addBornEntities() {
        Iterator<DynamicEntity> addIterator = toAddEntities.iterator();
        while (addIterator.hasNext()) {
            DynamicEntity entity = addIterator.next();
            entities.add(entity);
            addIterator.remove();
        }
    }

    int getLevelHeight() {
        return map.realHeight();
    }

    public int findKEy(String letter) {
        for (int i = 0; i < this.keys.size(); i++) {
            if (this.keys.get(i).name.equals(letter)) {
                return i;
            }
        }
        return -1;
    }

    public Automate findAutomate(Entity entity) {
        String className = entity.getClass().getSimpleName();
        for (Automate automate : this.allAutomates) {
            if (automate.className.equals("Default"))
                defaultAutomate = automate;
            if (automate.className.equals(className)) {
                return automate;
            } else if ((entity instanceof Player && automate.className.startsWith("Player"))
                    || (entity instanceof Weapon && automate.className.startsWith("Weapon"))) {
                if (automate.className.endsWith("1") && entity.team == TEAM.BLUE) {
                    return automate;
                } else if (automate.className.endsWith("2") && entity.team == TEAM.RED) {
                    return automate;
                }
            }

        }
        return defaultAutomate;
    }

    public void loadAutomates(String GalFile) throws Exception {
        List<Transitions> trans = new ArrayList<Transitions>();
        List<State> states = new ArrayList<State>();
        State state = new State("default");
        states.add(state);
        trans.add(new Transitions(state, state, null, new True()));
        defaultAutomate = new Automate(trans, states, state);

        ParserToAutomate parser = new ParserToAutomate();
        AST ast;
        ast = AutomataParser.from_file(GalFile);
        ast.accept(parser);
        allAutomates = parser.autos;
    }
}
