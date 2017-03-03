package com.tos.launcher.lockscreen.superspineboy;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.EventData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;

/**
 *
 * Created by xff on 2017/3/2.
 */

public class Enemy {
    public State state = State.run;
    public Type type=Type.strong;
    public float scale =2.5f;
    public  float heightSource = 398, width = 105 * scale, height = 200 * scale;
    public  Animation hitAnimation;
    public  Skeleton skeleton;
    public  AnimationState animationState;
    public  TextureAtlas enemyAtlas;
    public  SkeletonData  enemySkeletonData;
    public  Attachment burstHeadAttachment;
    public  Slot headSlot;
    public   AnimationStateData  enemyAnimationData;
    public   Color headColor;
    public   ObjectMap<State, StateView>  enemyStates = new ObjectMap();
    public Vector2 position = new Vector2(800, 100);
    public  float dir=-1;
    public  float size = 1;
    public Enemy(){
        loadEnemyAssets ();
        skeleton = new Skeleton(enemySkeletonData);
        burstHeadAttachment = skeleton.getAttachment("head", "burst01");
        headSlot = skeleton.findSlot("head");
        hitAnimation = skeleton.getData().findAnimation("hit");

        animationState = new AnimationState(enemyAnimationData);

        // Play squish sound when enemies die.
        final EventData squishEvent = enemySkeletonData.findEvent("squish");
        animationState.addListener(new AnimationState.AnimationStateAdapter() {
            public void event (int trackIndex, Event event) {
//                if (event.getData() == squishEvent) SoundEffect.squish.play();
            }
        });

        // Enemies have slight color variations.
        if (type == Type.strong) {
            headColor = new Color(1, 0.6f, 1, 1);
        }
        else {
            headColor = new Color(MathUtils.random(0.8f, 1), MathUtils.random(0.8f, 1), MathUtils.random(0.8f, 1), 1);
        }
        headSlot.getColor().set(headColor);

        animationState.setAnimation(0, enemyStates.get(state).animation, true);
    }

    void loadEnemyAssets () {
        enemyAtlas = new TextureAtlas(Gdx.files.internal("superspineboy/alien/alien.atlas"));

        SkeletonJson json = new SkeletonJson(enemyAtlas);
        json.setScale(height / heightSource);
        enemySkeletonData = json.readSkeletonData(Gdx.files.internal("superspineboy/alien/alien.json"));

        enemyAnimationData = new AnimationStateData(enemySkeletonData);
        enemyAnimationData.setDefaultMix(0.1f);

        setupState(enemyStates, State.idle, enemySkeletonData, "run", true);
        setupState(enemyStates, State.jump, enemySkeletonData, "jump", true);
        setupState(enemyStates, State.run, enemySkeletonData, "run", true);
        setupState(enemyStates, State.death, enemySkeletonData, "death", false);
        setupState(enemyStates, State.fall, enemySkeletonData, "run", false);

    }


    StateView setupState (ObjectMap map, State state, SkeletonData skeletonData, String name, boolean loop) {
        StateView stateView = new StateView();
        stateView.animation = skeletonData.findAnimation(name);
        stateView.loop = loop;
        map.put(state, stateView);
        return stateView;
    }

    public void dispose() {
        enemyAtlas.dispose();
    }

    public enum State {
        //禁止，跑，跳，死亡，下落
        idle, run, jump, death, fall
    }
    public enum Type {
        weak, normal, strong, becomesBig, big, small
    }
    //当前模型的动画状态
    public class StateView {
        Animation animation;
        boolean loop;
        // Controls the start frame when changing from another animation to this animation.
        ObjectFloatMap<Animation> startTimes = new ObjectFloatMap();
        float defaultStartTime;
    }

    void update (float delta) {
        // Change head attachment for enemies that are about to die.
//        if (enemy.hp == 1 && enemy.type != Type.weak) headSlot.setAttachment(burstHeadAttachment);

        // Change color for big enemies.
//        if (type == Type.big) headSlot.getColor().set(headColor).lerp(0, 1, 1, 1, 1 - bigTimer / Enemy.bigDuration);

        skeleton.setX(position.x);
        skeleton.setY(position.y);

        //if (!setAnimation(view.assets.enemyStates.get(enemy.state), enemy.stateChanged))
        animationState.update(delta);
        animationState.apply(skeleton);

        Bone root = skeleton.getRootBone();
        root.setScaleX(root.getScaleX() * size);
        root.setScaleY(root.getScaleY() * size);

        skeleton.setFlipX(dir == -1);
        skeleton.updateWorldTransform();
    }
}
