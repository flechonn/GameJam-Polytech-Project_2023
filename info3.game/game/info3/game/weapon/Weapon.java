package info3.game.weapon;

import java.io.IOException;
import info3.game.Camera;
import info3.game.Game;
import info3.game.Sound;
import info3.game.entity.Direction;
import info3.game.entity.DynamicEntity;
import info3.game.entity.Player;

public abstract class Weapon extends DynamicEntity {

    public Player player;

    private final int cooldown; // in ms
    private int currentCooldown;
    public int clips;
    public final int clipSize;
    public int ammo;
    private Sound soundEffect;
    public int damage;

    public Weapon(Player player) throws IOException {
        super(0, 0, player.team);
        cooldown = 500;
        clipSize = 15;
        ammo = clipSize;
        clips = 3;
        damage = 25;
        currentCooldown = cooldown;
        this.player = player;
        this.view = new WeaponView(this);
        this.soundEffect = new Sound(Game.game.m_canvas, "bulletSound" + player.team, "resources/bullets/shot.ogg", 0,
                0.8F);
    }

    public Weapon(Player player, int clips, int ammo) throws IOException {
        super(0, 0, player.team);
        cooldown = 500;
        clipSize = 15;
        this.ammo = ammo;
        this.clips = clips;
        damage = 25;
        currentCooldown = cooldown;
        this.player = player;
        this.view = new WeaponView(this);
        this.soundEffect = new Sound(Game.game.m_canvas, "bulletSound" + player.team, "resources/bullets/shot.ogg", 0,
                0.8F);
    }

    public Weapon(int cooldown, int clips, int damage, int clipSize, Player player) throws IOException {
        super(0, 0, player.team);
        this.cooldown = cooldown;
        this.clips = clips;
        this.damage = damage;
        this.clipSize = clipSize;
        this.currentCooldown = cooldown;
        this.ammo = clipSize;
        this.player = player;
        this.view = new WeaponView(this);
        this.soundEffect = new Sound(Game.game.m_canvas, "bulletSound" + player.team, "resources/bullets/shot.ogg", 0,
                0.8F);
    }

    public void reload() {
        if (currentCooldown <= 0) {
            if (clips > 0) {
                ammo = clipSize;
                currentCooldown = cooldown;
                clips--;
            }
        }
    }

    public void reset() {
        ammo = clipSize;
        clips = 3;
    }

    public abstract void createBullet(int startx, int starty);

    public void shoot() {
        if (player.facingDirection != Direction.IDLE && !player.isDead()) {
            if (currentCooldown <= 0) {
                if (ammo > 0) {
                    soundEffect.playSound();
                    createBullet(Camera.centeredCoordinateX(player), Camera.centeredCoordinateY(player));
                    ammo--;
                }
                currentCooldown = cooldown;
            }
        }
    }

    public void tick(long elapsed) {
        try {
            automate.step(this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (currentCooldown > 0)
            currentCooldown -= elapsed;

    }

    @Override
    public void wizz(String Direction) {
        reload();
    }

    @Override
    public void pop() {
        shoot();
    }

}
