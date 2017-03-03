package com.tos.launcher.lockscreen.superspineboy;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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

public class Character {
    public float fps = 1 / 30f;
    public float scale = 1.5f;
    public float heightSource = 625, width = 67 * scale, height = 285 * scale;

    public Vector2 position = new Vector2(100, 100);
    public State state = State.idle;
    public float stateTime;
    public float dir;
    public Rectangle rect = new Rectangle();
    public Skeleton skeleton;
    public ObjectMap<State, StateView> playerStates = new ObjectMap();
    public Bone rearUpperArmBone, rearBracerBone, gunBone, headBone, torsoBone, frontUpperArmBone;
    public Animation shootAnimation, hitAnimation;
    public AnimationStateData playerAnimationData;
    public AnimationState animationState;
    TextureAtlas playerAtlas;
    public boolean stateChanged = false;

    public Character() {
        //初始化碰撞矩阵
        rect.width = width;
        rect.height = height;
        initModel();
    }

    //初始化模型
    private void initModel() {
        //初始化角色动画
        skeleton = new Skeleton(loadPlayerAssets());

        rearUpperArmBone = skeleton.findBone("rear_upper_arm");
        rearBracerBone = skeleton.findBone("rear_bracer");
        gunBone = skeleton.findBone("gun");
        headBone = skeleton.findBone("head");
        torsoBone = skeleton.findBone("torso");
        frontUpperArmBone = skeleton.findBone("front_upper_arm");

        shootAnimation = skeleton.getData().findAnimation("shoot");
        hitAnimation = skeleton.getData().findAnimation("hit");
        animationState = new AnimationState(playerAnimationData);

        // Play footstep sounds.
        final EventData footstepEvent = skeleton.getData().findEvent("footstep");
        animationState.addListener(new AnimationState.AnimationStateAdapter() {
            public void event(int trackIndex, Event event) {
                if (event.getData() == footstepEvent) {
//                    if (event.getInt() == 1)
//                        SoundEffect.footstep1.play();
//                    else
//                        SoundEffect.footstep2.play();
                }
            }
        });

        animationState.setAnimation(0, playerStates.get(state).animation, true);
        if (shootAnimation != null) animationState.setAnimation(1, shootAnimation, true);

    }


    SkeletonData loadPlayerAssets() {
        playerAtlas = new TextureAtlas(Gdx.files.internal("superspineboy/spineboy/spineboy.atlas"));

        SkeletonJson json = new SkeletonJson(playerAtlas);
        json.setScale(height / heightSource);
        SkeletonData playerSkeletonData = json.readSkeletonData(Gdx.files.internal("superspineboy/spineboy/spineboy.json"));

        playerAnimationData = new AnimationStateData(playerSkeletonData);
        playerAnimationData.setDefaultMix(0.2f);
        setMix(playerAnimationData, "idle", "run", 0.3f);
        setMix(playerAnimationData, "run", "idle", 0.1f);
        setMix(playerAnimationData, "shoot", "shoot", 0);

        setupState(playerStates, State.death, playerSkeletonData, "death", false);
        StateView idle = setupState(playerStates, State.idle, playerSkeletonData, "idle", true);
        StateView jump = setupState(playerStates, State.jump, playerSkeletonData, "jump", false);
        StateView run = setupState(playerStates, State.run, playerSkeletonData, "run", true);
        if (idle.animation != null) run.startTimes.put(idle.animation, 8 * fps);
        if (jump.animation != null) run.startTimes.put(jump.animation, 22 * fps);
        StateView fall = setupState(playerStates, State.fall, playerSkeletonData, "jump", false);
        fall.defaultStartTime = 22 * fps;
        return playerSkeletonData;
    }

    //设置混合
    void setMix(AnimationStateData data, String from, String to, float mix) {
        Animation fromAnimation = data.getSkeletonData().findAnimation(from);
        Animation toAnimation = data.getSkeletonData().findAnimation(to);
        if (fromAnimation == null || toAnimation == null) return;
        data.setMix(fromAnimation, toAnimation, mix);
    }

    StateView setupState(ObjectMap map, State state, SkeletonData skeletonData, String name, boolean loop) {
        StateView stateView = new StateView();
        stateView.animation = skeletonData.findAnimation(name);
        stateView.loop = loop;
        map.put(state, stateView);
        return stateView;
    }

    public void dispose() {
        playerAtlas.dispose();
    }

    public enum State {
        //禁止，跑，跳，死亡，下落
        idle, run, jump, death, fall
    }

    //当前模型的动画状态
    public class StateView {
        Animation animation;
        boolean loop;
        // Controls the start frame when changing from another animation to this animation.
        ObjectFloatMap<Animation> startTimes = new ObjectFloatMap();
        float defaultStartTime;
    }

    boolean canShoot = true;
    Vector2 temp1 = new Vector2(), temp2 = new Vector2();
    //更新状态
    void update(float delta) {

        skeleton.setX(position.x + width / 2);
        skeleton.setY(position.y);
        animationState.update(delta);
        animationState.apply(skeleton);
        gunBone.setRotation(50);

//        // Determine if the player can shoot at the mouse position.
//        canShoot = false;
//        if (rearUpperArmBone == null || rearBracerBone == null || gunBone == null) {
//            canShoot = true;
//        }
//        else if (player.hp > 0 && !view.ui.hasSplash
//                && (Math.abs(skeleton.getY() - mouse.y) > 2.7f || Math.abs(skeleton.getX() - mouse.x) > 0.75f)) {
//            // Store bone rotations from the animation that was applied.
//            float rearUpperArmRotation = rearUpperArmBone.getRotation();
//            float rearBracerRotation = rearBracerBone.getRotation();
//            float gunRotation = gunBone.getRotation();
//            // Straighten the arm and don't flipX, so the arm can more easily point at the mouse.
//            rearUpperArmBone.setRotation(0);
//            float shootRotation = 11;
//            if (animationState.getCurrent(1) == null) {
//                rearBracerBone.setRotation(0);
//                gunBone.setRotation(0);
//            } else
//                shootRotation += 25; // Use different rotation when shoot animation was applied.
//            skeleton.setFlipX(false);
//            skeleton.updateWorldTransform();
//
//            // Compute the arm's angle to the mouse, flipping it based on the direction the player faces.
//            Vector2 bonePosition = temp2.set(rearUpperArmBone.getWorldX() + skeleton.getX(),
//                    rearUpperArmBone.getWorldY() + skeleton.getY());
//            float angle = bonePosition.sub(mouse).angle();
//            float behind = (angle < 90 || angle > 270) ? -1 : 1;
//            if (behind == -1) angle = -angle;
//            if (state == State.idle || (view.touched && (player.state == State.jump || player.state == State.fall)))
//                player.dir = behind;
//            if (behind != player.dir) angle = -angle;
//            if (player.state != State.idle && behind != player.dir) {
//                // Don't allow the player to shoot behind themselves unless idle. Use the rotations stored earlier from the animation.
//                rearBracerBone.setRotation(rearBracerRotation);
//                rearUpperArmBone.setRotation(rearUpperArmRotation);
//                gunBone.setRotation(gunRotation);
//            } else {
//                if (behind == 1) angle += 180;
//                // Adjust the angle upward based on the number of shots in the current burst.
//                angle += kickbackAngle * Math.min(1, burstShots / kickbackShots) * (burstTimer / burstDuration);
//                float gunArmAngle = angle - shootRotation;
//                // Compute the head, torso and front arm angles so the player looks up or down.
//                float headAngle;
//                if (player.dir == -1) {
//                    angle += 360;
//                    if (angle < 180)
//                        headAngle = 25 * Interpolation.pow2In.apply(Math.min(1, angle / 50f));
//                    else
//                        headAngle = -15 * Interpolation.pow2In.apply(1 - Math.max(0, angle - 310) / 50f);
//                } else {
//                    if (angle < 360)
//                        headAngle = -15 * Interpolation.pow2In.apply(1 - Math.max(0, (angle - 310) / 50f));
//                    else
//                        headAngle = 25 * Interpolation.pow2In.apply(1 - Math.max(0, (410 - angle) / 50f));
//                }
//                float torsoAngle = headAngle * 0.75f;
//                if (headBone != null) headBone.setRotation(headBone.getRotation() + headAngle);
//                if (torsoBone != null) torsoBone.setRotation(torsoBone.getRotation() + torsoAngle);
//                if (frontUpperArmBone != null) frontUpperArmBone.setRotation(frontUpperArmBone.getRotation() - headAngle * 1.4f);
//                rearUpperArmBone.setRotation(gunArmAngle - torsoAngle - rearUpperArmBone.getWorldRotation());
//                canShoot = true;
//            }
//        }

        skeleton.setFlipX(dir == -1);
        skeleton.updateWorldTransform();
    }

//    boolean setAnimation (StateView state, boolean force) {
//        // Changes the current animation on track 0 of the AnimationState, if needed.
//        Animation animation = state.animation;
//        AnimationState.TrackEntry current = animationState.getCurrent(0);
//        Animation oldAnimation = current == null ? null : current.getAnimation();
//        //上个动画和现在动画不一致
//        if (force || oldAnimation != animation) {
//            if (state.animation == null) return true;
//            AnimationState.TrackEntry entry = animationState.setAnimation(0, state.animation, state.loop);
//            if (oldAnimation != null) entry.setTime(state.startTimes.get(oldAnimation, state.defaultStartTime));
//            if (!state.loop) entry.setEndTime(9999);
//            return true;
//        }
//        return false;
//    }

    static float shootDelay = 0.1f, shootOffsetX = 160, shootOffsetY = 11;
    //射击
    void shoot () {
//        if (!canShoot || shootTimer >= 0) return;
//        player.shootTimer = shootDelay;
//        burstTimer = burstDuration;

        // Compute the position and velocity to spawn a new bullet.
        float x = skeleton.getX(), y = skeleton.getY();
        if (rearUpperArmBone != null && rearBracerBone != null && gunBone != null) {
            x += rearUpperArmBone.getWorldX();
            y += rearUpperArmBone.getWorldY();
        } else {
            x += width / 2;
            y += height / 2;
        }
//        float mouseX = Gdx.input.getX(), mouseY = Gdx.input.getY();

//        float angle = view.viewport.unproject(temp1.set(mouseX, mouseY)).sub(x, y).angle();
//        angle += kickbackAngle * Math.min(1, burstShots / kickbackShots) * player.dir;
//        float variance = kickbackVariance * Math.min(1, burstShots / kickbackVarianceShots);
//        angle += MathUtils.random(-variance, variance);
//
        int angle=0;
        float cos = MathUtils.cosDeg(angle), sin = MathUtils.sinDeg(angle);
//        float vx = cos * bulletSpeed + player.velocity.x * bulletInheritVelocity;
//        float vy = sin * bulletSpeed + player.velocity.y * bulletInheritVelocity;
        if (rearUpperArmBone != null && rearBracerBone != null && gunBone != null) {
            x = skeleton.getX() + gunBone.getWorldX();
            y = skeleton.getY() + gunBone.getWorldY() + shootOffsetY * scale;
            x += cos * shootOffsetX * scale;
            y += sin * shootOffsetX * scale;
        }
       // model.addBullet(x, y, vx, vy, temp1.set(vx, vy).angle());
        if (shootAnimation != null) animationState.setAnimation(1, shootAnimation, false);

//        view.camera.position.sub(view.shakeX, view.shakeY, 0);
//        view.shakeX += View.cameraShake * (MathUtils.randomBoolean() ? 1 : -1);
//        view.shakeY += View.cameraShake * (MathUtils.randomBoolean() ? 1 : -1);
//        view.camera.position.add(view.shakeX, view.shakeY, 0);
//
//        player.velocity.x -= kickback * player.dir;
//        SoundEffect.shoot.play();
//
//        burstShots = Math.min(kickbackShots, burstShots + 1);
    }
}
