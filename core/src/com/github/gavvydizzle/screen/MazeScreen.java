package com.github.gavvydizzle.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.github.gavvydizzle.utils.BodyHelperService;
import com.github.gavvydizzle.utils.Constants;
import com.github.gavvydizzle.utils.TileMapHelper;

//TODO - Probably broken because of the random 1.5f thrown around everywhere
public class MazeScreen extends ScreenAdapter implements ContactListener, InputProcessor {

    private final OrthographicCamera camera;
    private SpriteBatch batch;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;

    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private TileMapHelper tileMapHelper;

    // Game Objects
    private Body cursor, goal;
    private boolean dragging, dead;

    // Bottles
    private Texture bottles;
    private final int bottlePixelSize = 32;

    public MazeScreen(OrthographicCamera camera) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.box2DDebugRenderer = new Box2DDebugRenderer();

        tileMapHelper = new TileMapHelper(this);

        setupLevel();
    }

    public void setupLevel() {
        this.world = new World(new Vector2(0, 0), false);
        world.setContactListener(this);
        Gdx.input.setInputProcessor(this);
        orthogonalTiledMapRenderer = tileMapHelper.setupMazeMap();

        bottles = new Texture(Gdx.files.internal("assets/bottles.png"));
    }

    @Override
    public void render(float delta) {
        this.update();

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        orthogonalTiledMapRenderer.render();

        batch.begin();
        if (dragging) {
            batch.draw(bottles, cursor.getPosition().x * Constants.PPM - bottlePixelSize/2f, cursor.getPosition().y * Constants.PPM - bottlePixelSize/2f);
        }
        else {
            batch.draw(bottles, 3.25f * 1.5f * Constants.PPM, 0.25f * 1.5f * Constants.PPM);
        }
        batch.end();

        //box2DDebugRenderer.render(world, camera.combined.scl(Constants.PPM));
    }

    private void update() {
        world.step(1/60f, 6, 2);
        cameraUpdate();

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);

        if (dead) {
            reset();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void cameraUpdate() {
        Vector3 position = camera.position;
        camera.position.set(position);
        camera.update();
    }

    public World getWorld() {
        return world;
    }

    public void setGoal(Body goal) {
        this.goal = goal;
    }

    private void reset() {
        if (cursor != null) {
            world.destroyBody(cursor);
            cursor = null;
        }
        dragging = false;
        dead = false;
    }


    @Override
    public void beginContact(Contact contact) {
        if (dead || cursor == null) return;

        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        if (fa.getBody() != cursor && fb.getBody() != cursor) return;

        Fixture csr, other;
        if (fa.getBody() == cursor) {
            csr = fa;
            other = fb;
        }
        else {
            csr = fb;
            other = fa;
        }

        if (other.getBody() == goal) {
            dead = true;
            //reset();
        }
        else {
            dead = true;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!dragging) {
            System.out.println(screenX + ", " + screenY);
            dragging = true;

            //create the cursor body
            cursor = BodyHelperService.createSensorBody(screenX, screenY, bottlePixelSize, world);

            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (dragging) {
            cursor.setTransform(screenX*1.5f/Constants.PPM, (Gdx.graphics.getHeight() - screenY)*1.5f/Constants.PPM, 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
