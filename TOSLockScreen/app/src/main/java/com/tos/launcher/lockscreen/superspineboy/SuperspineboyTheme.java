package com.tos.launcher.lockscreen.superspineboy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;

/**
 * 超级英雄主题
 */
public class SuperspineboyTheme extends ApplicationAdapter {
    OrthographicCamera camera;
    SpriteBatch batch;
    SkeletonRenderer renderer;
    SkeletonRendererDebug debugRenderer;
    Character mPlay=null;
    Enemy mEnemy=null;

    Texture img;


    @Override
    public void create () {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true); // PMA results in correct blending without outlines.
        debugRenderer = new SkeletonRendererDebug();
        debugRenderer.setBoundingBoxes(false);
        debugRenderer.setRegionAttachments(false);
        mPlay=new Character();
        mEnemy=new Enemy();
        img = new Texture("bg.jpg");
        Gdx.input.setInputProcessor(new InputMultiplexer(new TouchListem()));
    }

    //触摸监听
    public class TouchListem extends InputAdapter{


    }
    @Override
    public void render () {

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.getProjectionMatrix().set(camera.combined);
        debugRenderer.getShapeRenderer().setProjectionMatrix(camera.combined);

        mPlay.update(Gdx.graphics.getDeltaTime());
        mEnemy.update(Gdx.graphics.getDeltaTime());
        batch.begin();
        batch.draw(img,0,0,Gdx.graphics.getDisplayMode().width,Gdx.graphics.getDisplayMode().height);
        renderer.draw(batch, mPlay.skeleton); // Draw the skeleton images.
        renderer.draw(batch, mEnemy.skeleton); // Draw the skeleton images.

        batch.end();

       // debugRenderer.draw(mPlay.skeleton); // Draw debug lines.
    }
    public void resize (int width, int height) {
        camera.setToOrtho(false); // Update camera with new size.
    }
    @Override
    public void dispose () {
        batch.dispose();
        mPlay.dispose();
        mEnemy.dispose();
    }
}
