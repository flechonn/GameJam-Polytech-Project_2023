package info3.game.entity;

import java.io.IOException;

import info3.game.hitbox.HitBox;
import info3.game.weapon.Bazooka;


public class Engineer extends Player {
    int turretCooldown;
    int bazookaCooldown;

    public int oldClips;
    public int oldAmmo;

    public Engineer() throws IOException {
        super();
    }

    public Engineer(int team) throws IOException {
        super(team);
        hitbox = new HitBox(10, 5, 18, 21, this);
        this.view=new EngineerView(this);
    }

    public Engineer(int team, String filename) throws IOException {
        super(team, filename);
    }


    @Override
    public void tick(long elapsed) {
        turretCooldown -= elapsed;
        bazookaCooldown -= elapsed;
        view.tick(elapsed);
        super.tick(elapsed);
    }

    @Override
    public void wizz(String Direction) {
        turretCooldown = 15000;
        try {
            new Turret(this.x, this.y, this.team);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pop() {
        bazookaCooldown = 7000;
        try {
            oldClips = weapon.clips;
            oldAmmo = weapon.ammo;
            weapon.kill();
            weapon = new Bazooka(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean gotPower() {
        return turretCooldown <= 0;
    }

    @Override
    public boolean gotStuff() {
        return bazookaCooldown <= 0;
    }

    @Override
    public boolean MyDir(String direction) {
        return facingDirection.equals(Direction.fromString(direction));
    }
}
