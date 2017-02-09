package com.mygdx.game;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 * Created by x002 on 2017/2/9.
 */

public class ParticleTest implements ApplicationListener
{
    SpriteBatch batch;
    ParticleEffect effect;
    int index;
    Array<ParticleEmitter> emitters;
    int count;
    @Override
    public void render () {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        effect.draw(batch, Gdx.graphics.getDeltaTime());
        batch.end();
    }

    @Override
    public void create ()
    {
        batch=new SpriteBatch();
        effect=new ParticleEffect();
        effect.load(Gdx.files.internal("data/missile_fire.p"), Gdx.files.internal("data/"));
        effect.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

        emitters=new Array<ParticleEmitter>(effect.getEmitters());
        effect.getEmitters().clear();
        effect.getEmitters().add(emitters.get(index));

        InputProcessor inputProcessor=new InputProcessor()
        {

            @Override
            public boolean touchUp(int arg0, int arg1, int arg2, int arg3)
            {
                return false;
            }


            @Override
            public boolean touchDragged(int arg0, int arg1, int arg2)
            {
                effect.setPosition(arg0, Gdx.graphics.getHeight()-arg1);
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean touchDown(int arg0, int arg1, int arg2, int arg3)
            {
                ParticleEmitter emitter=emitters.get(index);
                count+=100;
                if(count>emitter.getMaxParticleCount())
                    emitter.setMaxParticleCount(count*2);
                count=Math.max(0, count);
                emitter.getEmission().setHigh(count/emitter.getLife().getHighMax()*1000);
                effect.getEmitters().clear();
                effect.getEmitters().add(emitter);
                return false;
            }

            @Override
            public boolean scrolled(int arg0)
            {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean keyUp(int arg0)
            {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean keyTyped(char arg0)
            {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean keyDown(int arg0)
            {
                // TODO Auto-generated method stub
                return false;
            }
        };
        Gdx.input.setInputProcessor(inputProcessor);
    }

    @Override
    public void dispose()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void resize(int arg0, int arg1)
    {
    }

    @Override
    public void resume()
    {

    }

}