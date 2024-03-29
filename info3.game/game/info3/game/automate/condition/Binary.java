package info3.game.automate.condition;

import java.util.ArrayList;
import java.util.List;

import info3.game.GameSession;
import info3.game.entity.Entity;

public class Binary extends Condition{

    Condition left;
    Condition right;
    String operator;
    public Binary(){
        super(false);
    }

    public Binary(String test){
        super(false);
        operator=test;
    }
    public Binary( Condition left, Condition right, String operator){
        super(false);
        this.operator=operator;
        this.left=left;
        this.right=right;
    }
    public Binary(boolean notOp) {
        super(notOp);
        //TODO Auto-generated constructor stub
    }

    @Override
    public boolean eval(Entity e) {
        
        switch(this.operator){
            case "&":
                return right.eval(e) && left.eval(e);
            case "/":
                return right.eval(e) | left.eval(e);
            default:
                return false;
        }
    }

    public List<Key> loadKeys(){
        List<Key> keys=new ArrayList<>();
        if(this.left instanceof Key)
            if(GameSession.gameSession.findKEy(((Key)this.left).name)==-1)
                keys.add((Key)left);
       if(this.right instanceof Binary){
            keys.addAll(((Binary)this.right).loadKeys());
        }
        if(this.right instanceof Key)
            if(GameSession.gameSession.findKEy(((Key)this.right).name)==-1)
                keys.add((Key)right);
        return keys;
    }
    
}
