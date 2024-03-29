package info3.game.entity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import info3.game.GameSession;
import info3.game.Camera;
import info3.game.automate.Automate;
import info3.game.automate.State;
import info3.game.hitbox.HitBox;
import info3.game.weapon.Bullet;
import info3.game.weapon.Weapon;

public abstract class Entity {
  // here are the coords where the entity is
  public int x;
  public int y;

  // here are the velocities at which the entity is moving
  public float velX;
  public float velY;
  public State state;

  public Direction facingDirection;
  public Direction movingDirection;

  // constant regulating the movement of entitites
  PhysicConstant model;
  // acceleration far computing velocity
  double accelerationX;
  // double accelerationY ;

  // elapsed time necessary for movements
  long moveElapsed;

  public Automate automate;
  public HitBox hitbox;
  public EntityView view;
  public int team;
  public int jumpCounter;
  public int jumpCooldown;
  public int jumpAmount;
  long deltatime;
  public boolean solid;
  public float timer = 0;
  public int addVelX = 0;

  public Entity(int x, int y, int team, String filename, int nrows, int ncols) throws IOException {
    this(x, y, team);
    this.view = new EntityView(filename, nrows, ncols, this);
    this.hitbox = new HitBox(this);
    solid = false;
    facingDirection = Direction.IDLE;
  }

  public Entity(int x, int y, int team) throws IOException {
    this.team = team;
    this.x = x;
    this.y = y;
    this.automate = loadAutomate();
    facingDirection = Direction.IDLE;
    if (this.automate == null)
      this.automate = GameSession.gameSession.defaultAutomate;
    state = this.automate.initalState;
    solid = false;
  }

  private Automate loadAutomate() {
    return GameSession.gameSession.findAutomate(this);
  }

  public void tick(long elapsed){
    try {
      this.automate.step(this);
    } catch (Exception e) {
      e.printStackTrace();
    };
  }

  public static BufferedImage[] loadSprite(String filename, int nrows, int ncols) throws IOException {
    File imageFile = new File(filename);
    if (imageFile.exists()) {
      BufferedImage image = ImageIO.read(imageFile);
      int width = image.getWidth(null) / ncols;
      int height = image.getHeight(null) / nrows;

      BufferedImage[] images = new BufferedImage[nrows * ncols];
      for (int i = 0; i < nrows; i++) {
        for (int j = 0; j < ncols; j++) {
          int x = j * width;
          int y = i * height;
          images[(i * ncols) + j] = image.getSubimage(x, y, width, height);
        }
      }
      return images;
    }
    return null;
  }

  public BufferedImage getImage() {
    return view.getImage();
  }

  public int getWidth() {
    return view.width;
  }

  public int getHeight() {
    return view.height;
  }

  // checking where the entity is looking
  public boolean stFaceLeft() {
    return facingDirection == Direction.LEFT;
  }

  public boolean stFaceRight() {
    return facingDirection == Direction.RIGHT;
  }

  public void FaceLeft() {
    facingDirection = Direction.LEFT;
  }

  public void FaceRight() {
    facingDirection = Direction.RIGHT;
  }

  public void Idle() {
    facingDirection = Direction.IDLE;
  }

  // jump management
  // public void StartJump(){
  // velY = -1;

  protected void affectTor() {
    if (Camera.centeredCoordinateX(this) < 0) {
      x = GameSession.gameSession.map.realWidth() - getWidth();
    }
    if (Camera.centeredCoordinateX(this) > GameSession.gameSession.map.realWidth()) {
      x = 0;
    }
    if (Camera.centeredCoordinateY(this) < 0) {
      y = GameSession.gameSession.map.realHeight() - getHeight();
    }
    if (Camera.centeredCoordinateY(this) > GameSession.gameSession.map.realHeight()) {
      y = 0;
    }
  }

  public int distanceTo(Entity e) {
    if ((e instanceof Player && ((Player) e).dead))
      return 101;
    return (int) Math.sqrt(Math.pow(Camera.centeredCoordinateX(this) - Camera.centeredCoordinateX(e), 2)
        + Math.pow(Camera.centeredCoordinateY(this) - Camera.centeredCoordinateY(e), 2));
  }

  public DynamicEntity nearestEnemyEntity() {
    DynamicEntity nearest = null;
    int minDist = Integer.MAX_VALUE;
    for (DynamicEntity e : GameSession.gameSession.entities) {
      if (e.team != this.team && !(e instanceof Weapon) &&!(e instanceof Bullet) && e.team!=TEAM.NONE) {
        int dist = distanceTo(e);
        if (dist < minDist) {
          minDist = dist;
          nearest = e;
        }
      }
    }
    return nearest;
  }

  void updateVelocityX() {
    this.velX = (float) ((PhysicConstant.maxVelX + addVelX) * (1 - Math.exp(-accelerationX)));
  }

  void updateVelocityY() {
    this.velY = (float) (Math.max(velY + PhysicConstant.gravity, -PhysicConstant.maxVelY));
  }

  void updateJumpVelocity() {
    if (jumpCooldown > 0) {
      this.velY = (float) (this.velY * (1 - Math.exp(-this.velY)));
    }
  }

  public boolean isSittingOn(Entity e) {
    return hitbox.isSittingOn(e);
  }

  // Actions

  public  void move(Direction direction){
    switch(direction){
      case UPPER:
        this.y-=10;
        break;
      case BOTTOM:
        this.y+=10;
        break;
      case RIGHT:
        this.x+=10;
        break;
      case LEFT:
        this.x-=10;
        break;
      default:
        break;
    }
  }

  public abstract void turn();

  public abstract void wizz();

  public abstract void pop();

  public abstract void egg(Entity type);

  public abstract void pick();

  // Conditions

  public abstract boolean gotPower();

    public abstract boolean gotStuff();

  public abstract boolean cell(Direction direction, String category);

  public abstract boolean MyDir(String direction);

  public abstract void wizz(String direction);

  public abstract void jump(String direction);

  public abstract void hit(String direction);

}
