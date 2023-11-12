package com.github.gavvydizzle.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class RainwaterScreen extends ScreenAdapter {

    private final OrthographicCamera camera;
    private SpriteBatch batch;

    // Images
    private Texture backgroundImage;
    private TextureRegion dropImage;
    private Array<Animation<TextureRegion>> animations;

    private Rectangle bucket;
    private long lastDropTime;

    private final float bucketSpeed = 275f;
    private final float dropSpeed = 200f;

    // Drops
    private boolean dead;
    private final int NUM_DROPS = 10;
    private int dropsRemaining;
    private int dropsCollected;
    private Array<Rectangle> drops;

    // A variable for tracking elapsed time for the animation
    float stateTime;

    public RainwaterScreen(OrthographicCamera camera) {
        this.camera = camera;
        this.batch = new SpriteBatch();

        dropsRemaining = NUM_DROPS;
        stateTime = 0f;

        loadImages();

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle();
        bucket.x = Gdx.graphics.getWidth()/2f;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        drops = new Array<>();
    }

    private void loadImages() {
        backgroundImage = new Texture(Gdx.files.internal("assets/landscape.png"));
        Texture assetSheet = new Texture(Gdx.files.internal("assets/landscape_assets.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(assetSheet,
                assetSheet.getWidth() / 10,
                assetSheet.getHeight() / 10);

        TextureRegion[] a1 = new TextureRegion[2];
        a1[0] = tmp[0][0];
        a1[1] = tmp[0][1];
        TextureRegion[] a2 = new TextureRegion[2];
        a2[0] = tmp[0][2];
        a2[1] = tmp[0][3];
        TextureRegion[] a3 = new TextureRegion[2];
        a3[0] = tmp[0][4];
        a3[1] = tmp[0][5];
        TextureRegion[] a4 = new TextureRegion[2];
        a4[0] = tmp[0][6];
        a4[1] = tmp[0][7];
        TextureRegion[] a5 = new TextureRegion[2];
        a5[0] = tmp[0][8];
        a5[1] = tmp[0][9];

        float frameTime = 20/60f;
        animations = new Array<>(5);
        animations.add(new Animation<>(frameTime, a1));
        animations.add(new Animation<>(frameTime, a2));
        animations.add(new Animation<>(frameTime, a3));
        animations.add(new Animation<>(frameTime, a4));
        animations.add(new Animation<>(frameTime, a5));

        dropImage = tmp[1][0];
    }

    private void reset() {
        dead = false;
        lastDropTime = 0;
        dropsRemaining = NUM_DROPS;
        dropsCollected = 0;

        bucket.x = Gdx.graphics.getWidth()/2f;
        drops.clear();
    }

    private void spawnRaindrop() {
        dropsRemaining--;

        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(96, Gdx.graphics.getWidth() - 96);
        raindrop.y = Gdx.graphics.getHeight() - 64;
        raindrop.width = 64;
        raindrop.height = 64;
        drops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        this.update();

        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundImage, 0, 0);
        batch.draw(animations.get(dropsCollected/2).getKeyFrame(stateTime, true) , bucket.x, bucket.y);
        for(Rectangle drop : drops) {
            batch.draw(dropImage, drop.x, drop.y);
        }
        batch.end();

        // check if we need to create a new raindrop
        if(dropsRemaining > 0 && TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the latter case we play back
        // a sound effect as well.
        for (Iterator<Rectangle> iter = drops.iterator(); iter.hasNext(); ) {
            Rectangle raindrop = iter.next();
            raindrop.y -= dropSpeed * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0) {
                iter.remove();
                dead = true;
            }
            else if(raindrop.overlaps(bucket)) {
                iter.remove();
                dropsCollected++;
            }
        }
    }

    private void update() {
        if (dead) {
            reset();
            return;
        }
        else if (dropsCollected >= NUM_DROPS) {
            return;
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= bucketSpeed * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += bucketSpeed * Gdx.graphics.getDeltaTime();

        // make sure the bucket stays within the screen bounds
        if(bucket.x < 0) bucket.x = 0;
        if(bucket.x > Gdx.graphics.getWidth() - 64) bucket.x = Gdx.graphics.getWidth() - 64;
    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        backgroundImage.dispose();
        batch.dispose();
    }
}
