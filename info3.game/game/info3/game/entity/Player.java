/*
 * Copyright (C) 2020  Pr. Olivier Gruber
 * Educational software for a basic game development
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Created on: March, 2020
 *      Author: Pr. Olivier Gruber
 */
package info3.game.entity;

import java.io.IOException;
import java.util.Random;

import info3.game.Camera;
import info3.game.Game;
import info3.game.GameSession;
import info3.game.entity.blocks.SpawnerPoint;
import info3.game.entity.life.LifeBar;
import info3.game.hitbox.HitBox;
import info3.game.weapon.Rifle;
import info3.game.weapon.Weapon;

/**
 * A simple class that holds the images of a sprite for an animated cowbow.
 *
 */
public class Player extends DynamicEntity {
  long m_imageElapsed;

  public LifeBar lifeBar;
  public Weapon weapon;
  public boolean dead = false;
  private boolean respawned = true;
  private int respawnTimer = 3000;
  public int kills;
  public Player() throws IOException {
    this(1);
  }

  public Player(int team) throws IOException {
    super(40, 40, team, Getchar(team) + "PlayerSprite.png", 2, 2);
    view = new PlayerView(Getchar(team) + "PlayerSprite.png", 2, 2, this);
    this.lifeBar = new LifeBar(team);
    hitbox = new HitBox(12, 8, 15, 21, this); // 32 - 15 - 12
    weapon = new Rifle(this);
    this.facingDirection = Direction.RIGHT;
    jumpAmount = 2;
    jumpCounter = jumpAmount;
  }

  public Player(int team, String filename) throws IOException {
    super(40, 40, team, filename, 4, 6);
    view = new PlayerView(filename, 4, 6, this);
    this.lifeBar = new LifeBar(team);
    hitbox = new HitBox(12, 8, 20, 35, this);
    weapon = new Rifle(this);
    this.facingDirection = Direction.RIGHT;
    jumpAmount = 2;
    jumpCounter = jumpAmount;
  }

  static private String Getchar(int team) {
    if (team == 1) {
      return "resources/Mexicain/";
    } else {
      return "resources/Ingenieur/";
    }
  }

  public void takeDamage(int amount) {
    lifeBar.life.removeHealth(amount);
  }

  private boolean isDead() {
    return this.lifeBar.life.health <= 0;
  }

  private void respawn() {
    if (respawnTimer <= 0) {
      Random random = new Random();
      int size = GameSession.gameSession.spawnerPoints.size();
      if (size > 0) {
        int randomIndex = random.nextInt(size);
        SpawnerPoint spawner = GameSession.gameSession.spawnerPoints.get(randomIndex);
        this.x = spawner.x;
        this.y = spawner.y - 50;
      } else {
        this.x = 50;
        this.y = 50;
      }
      this.lifeBar.life.addHealth(this.lifeBar.life.maxHealth);
      this.weapon.reset();
      respawnTimer = 3000;
      respawned = true;
      this.dead = false;

      //Find ennemy and add him a kill
      Player enemy;
      if(this.team==TEAM.TEAM_1)
        enemy=GameSession.gameSession.player2;
      else
        enemy=GameSession.gameSession.player1;
      enemy.kills++;
    }
  }

  /*
   * Simple animation here, the cowbow
   */
  public void tick(long elapsed) {
    if (isDead()) {
      this.dead = true;
      if (!respawned)
        respawnTimer -= elapsed;
      respawn();
    }
    respawned = false;
    jumpCooldown -= elapsed;
    deltatime = elapsed;
    try {
      movingDirection = Direction.IDLE;
      this.automate.step(this);
      if (movingDirection.x != 0)
        facingDirection = movingDirection;
      if (facingDirection != movingDirection)
        accelerationX = 0.1;
    } catch (Exception e) {
      System.out.println("Normally we should not reach here");
      e.printStackTrace();
    }
    view.tick(deltatime);
    Movement.Walk(this);
    Movement.affectGravity(this);
  }

  @Override
  public void move(Direction direction) {
    accelerationX += 0.04;
    movingDirection = direction;
    if (direction.y == Direction.UPPER.y)
      Movement.Jump(this);
  }

  @Override
  public void wizz() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'wizz'");
  }
}
