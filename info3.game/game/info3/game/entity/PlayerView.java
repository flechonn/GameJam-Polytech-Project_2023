package info3.game.entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import info3.game.Camera;
import info3.game.Game;
import info3.game.GameSession;
import info3.game.weapon.Bazooka;
import info3.game.weapon.Weapon;

public class PlayerView extends EntityView {

    int deltatime = 0;
    long m_imageElapsed = 0;
    BufferedImage[] killImage;

    PlayerView(BufferedImage[] images, Player player) {
        super(images, player);
        try {
            killImage = Player.loadSprite("resources/Kill.png", 1, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public PlayerView(String string, int nrows, int ncols, Player player) {
        super(string, nrows, ncols, player);
        try {
            killImage = Player.loadSprite("resources/Kill.png", 1, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void tick(long elapsed) {
        m_imageElapsed += elapsed;
        if (m_imageElapsed > 200) {
            m_imageElapsed = 0;
            if (((Player) entity).accelerationX == 0.1) {
                imageIndex = (imageIndex + 1) % 2;
            } else {
                imageIndex = (imageIndex + 1) % 4;
            }
        }
    }

    public void paintKills(Graphics g) {
        int height = ((Player) this.entity).lifeBar.getHeight();
        int windowHeight = Game.game.m_canvas.getHeight();
        int windowWidth = Game.game.m_canvas.getWidth();

        int y = windowHeight - height;

        String kills = Integer.toString(((Player) entity).kills);

        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(Color.BLACK);
        FontMetrics fontMetrics = g.getFontMetrics();
        if (entity.team == 1) {
            g.drawString(kills, 37, y);
            g.drawImage(killImage[0], 0, y - 32, 32, 32, null);
        } else {
            g.drawString(kills, windowWidth - 37 - fontMetrics.stringWidth(kills), y);
            g.drawImage(killImage[0], windowWidth - 32, y - 32, 32, 32, null);
        }
    }

    @Override
    public void paint(Graphics g) {
        // Camera.drawImage(g, getImage(), entity.x, entity.y, entity.getWidth(),
        // entity.getHeight());
        //entity.hitbox.showHitBox(g);
        ((Player) entity).lifeBar.showLifeBar(g);
        // didnt know a better way to implement it ATM
        Camera.drawEntity(entity, g);
        paintKills(g);
        ((Player) entity).weapon.view.paint(g);
    }
}
