package info3.game.entity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.ImageIO;

import info3.game.GameSession;
import info3.game.Camera;
import info3.game.GameSession;
import info3.game.automate.Automate;
import info3.game.automate.State;
import info3.game.hitbox.HitBox;

public abstract class Entity {
    //here are the coords where the entity is
    public int x;
    public int y;

    //here are the velocities at which the entity is moving
    public float velX;
    public float velY;
    public State state ;

    int facing = 0;// where the entity is facing -1 for left and 1 for right
    public boolean IsJumping = false; // just checking if the player is currently jumping to prevent any illegal moves
    int jumptime =0;//init at 0 for implementation but represent the numbers of frames in which the player will be jumping
    boolean jumpcd = false; // checking if the jump is on cd same purpose as Isjumping

    //constant regulating the movement of entitites
    PhysicConstant model;

    long moveElapsed;

    public Automate automate;
    HitBox hitbox;
    public EntityView view;


    public Entity(int x, int y, String filename, int nrows, int ncols) throws IOException {
        this.x = x;
        this.y = y;
        this.view = new EntityView(filename, nrows, ncols, this);
        this.automate = loadAutomate();
        
        if(this.automate==null)
          this.automate=GameSession.gameSession.defaultAutomate;

        state = this.automate.initalState ;
    }

    private Automate loadAutomate() {
      System.out.println("Loading automate for " + this.getClass().getSimpleName());
      String className = this.getClass().getSimpleName();
      return GameSession.gameSession.findAutomate(className);
    }

    public abstract void tick(long elapsed);

    public abstract void move(Direction direction);

    public abstract void wizz();

    public abstract void pop();

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

  //checking where the entity is looking
  public boolean stFaceLeft(){
    return -1 == facing;
  }
  public boolean stFaceRight(){
    return 1 == facing;
  }
  public void FaceLeft(){
    facing =-1;
  }
  public void FaceRight(){
    facing =1;
  }
  public void Idle(){
    facing =0;
  }

  
public void SetVelX(int VelX){//Set the velocity at which the entity will move
   if(velX == 0){
    velX = VelX;
   } else {
    velX = velX * 1.01f;
   }
   
  }
  public void reSetVelX(){//Reset velocity to be called on button release
    velX = 0;
  }

  //jump management
    public boolean statusJump(){
    return IsJumping;
  }
  // public void StartJump(){
  //   velY = -1;

  // }
}
