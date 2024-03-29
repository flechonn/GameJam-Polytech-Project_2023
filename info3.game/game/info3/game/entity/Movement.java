package info3.game.entity;

import info3.game.Game;
import info3.game.GameSession;

public class Movement {

    static public void Walk(Entity E) {
        E.updateVelocityX();
        E.x += E.velX * E.movingDirection.x;
        Direction first ;
        Direction second ;
        if (E.facingDirection.x > 1) {
            first = Direction.RIGHT;
            second = Direction.LEFT;
        } else {
            first = Direction.LEFT;
            second = Direction.RIGHT;
        }
        while (E.hitbox.inCollision(first))
            E.x -= first.x ;
        while (E.hitbox.inCollision(second))
            E.x -= second.x ;
        E.affectTor();
    }

    public static void Dash(Entity E) {
        if (((Player) E).DashTime > 0) {
            switch (E.facingDirection) {
                case LEFT:
                    E.x -= (PhysicConstant.maxVelX + 5);
                    while (E.hitbox.inCollision(Direction.LEFT))
                        E.x += 1;
                    E.affectTor();
                    break;
                case RIGHT:
                    E.x += (PhysicConstant.maxVelX + 5);
                    while (E.hitbox.inCollision(Direction.RIGHT))
                        E.x -= 1;
                    E.affectTor();
                    break;
                case UPPER:
                    E.y -= (PhysicConstant.maxVelY + 5);
                    while (E.hitbox.inCollision(Direction.RIGHT))
                        E.y += 1;
                    E.affectTor();
                    break;
                case BOTTOM:
                    E.y += (PhysicConstant.maxVelY + 5);
                    while (E.hitbox.inCollision(Direction.RIGHT))
                        E.y -= 1;
                    E.affectTor();
                    break;
            }
            ((Player) E).DashTime--;
        }
    }

    public static void manageAirJump(Entity E) {
        E.y += 5;
        if (!E.hitbox.inCollision(Direction.BOTTOM) && E.jumpCounter == E.jumpAmount) {
            E.jumpCounter -= 1;
            if (E.jumpCounter == 0) {
                E.y -= 5;
                return;
            }
        }
        E.y -= 5;
    }

    public static void Jump(Entity E) {
        Jump(E,PhysicConstant.jumpForce);
    }
    public static void Jump(Entity E, float force) {
        if (E.jumpCounter > 0 && E.jumpCooldown < 0) {
            manageAirJump(E);
            E.velY = force;
            E.jumpCounter--;
            E.jumpCooldown = 250;
        }
    }

    public static void affectGravity(Entity E) {
        E.updateJumpVelocity();
        E.updateVelocityY();
        E.y -= E.velY;
        while (E.hitbox.inCollision(Direction.BOTTOM)) {
            E.y -= 1;
            E.velY = PhysicConstant.gravity;
            E.jumpCounter = E.jumpAmount;
        }
        while (E.hitbox.inCollision(Direction.UPPER)) {
            E.y += 1;
            E.velY = Math.min(0, E.velY);
        }
    }

}
