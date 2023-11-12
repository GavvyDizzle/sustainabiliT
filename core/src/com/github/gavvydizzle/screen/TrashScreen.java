package com.github.gavvydizzle.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ArrowShapeBuilder;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.github.gavvydizzle.utils.Constants;
import com.github.gavvydizzle.utils.TileMapHelper2;

public class TrashScreen extends ScreenAdapter implements ContactListener, InputProcessor {

    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final World world;
    private final Box2DDebugRenderer box2DDebugRenderer;

    private final OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private TileMapHelper2 tileMapHelper;

    // Images
    private Texture trashCanImage, trashImage;

    // Trash
    private final Array<Body> trash;
    private Body containedZone;
    private final int numTrash = 6;
    private int trashContained;
    private final float shotPower = 1250f;

    // Selection
    private Vector2 clickLoc;
    private Vector2 dir;
    private Body selectedTrash;

    private boolean win;

    public TrashScreen(OrthographicCamera camera) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, -25), false);
        this.box2DDebugRenderer = new Box2DDebugRenderer();

        world.setContactListener(this);
        Gdx.input.setInputProcessor(this);

        trash = new Array<>(numTrash);
        trashContained = 0;

        tileMapHelper = new TileMapHelper2(this);
        orthogonalTiledMapRenderer = tileMapHelper.setupTrashMap();

        loadImages();
    }

    private void loadImages() {

    }

    @Override
    public void render(float delta) {
        if (win) {
            System.out.println("Win");
            return;
        }

        this.update();

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.end();

        box2DDebugRenderer.render(world, camera.combined.scl(Constants.PPM));

        if (clickLoc != null && dir != null) {
            MeshBuilder meshbuilder = new MeshBuilder();
            meshbuilder.begin(VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, GL20.GL_TRIANGLES);
            ArrowShapeBuilder.build(
                    meshbuilder,
                    clickLoc.x, clickLoc.y, 0, /* Starting xyz */
                    dir.x * 2 + clickLoc.x, dir.y * 2 + clickLoc.y, 0, /* Ending xyz */
                    0.1f, /* percentage of arrow head */
                    0.4f, /* percentage of stem thickness */
                    10 /* divisions, basically level of detail */
            );
            Mesh debug_arrow = meshbuilder.end();
            debug_arrow.render(batch.getShader(), GL20.GL_TRIANGLES);
        }
    }

    private void update() {
        world.step(1/60f, 6, 2);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }


    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        trashCanImage.dispose();
        trashImage.dispose();
        batch.dispose();
    }

    public World getWorld() {
        return world;
    }

    public void setZone(Body body) {
        containedZone = body;
    }

    public void addTrash(Body body) {
        trash.add(body);
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
        if (pointer != Input.Buttons.LEFT) return false;

        float x = screenX * 1.0f / Constants.PPM;
        float y = (Gdx.graphics.getHeight() - screenY) / Constants.PPM;

        for (Body body : trash) {
            if (Math.abs(body.getLinearVelocity().y) < 0.01 && body.getPosition().dst2(x, y) < Math.pow(0.75, 2)) {
                selectedTrash = body;
                clickLoc = body.getPosition();
                break;
            }
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer != Input.Buttons.LEFT) return false;

        if (selectedTrash != null && dir != null) {
            selectedTrash.applyForceToCenter(dir.scl(shotPower), true);
        }

        selectedTrash = null;
        clickLoc = null;
        dir = null;
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (selectedTrash == null) return false;

        float x = screenX * 1.0f / Constants.PPM;
        float y = (Gdx.graphics.getHeight() - screenY) / Constants.PPM;
        dir = new Vector2(x, y).sub(clickLoc).scl(-1);

        if (dir.dst2(Vector2.Zero) > 1) {
            dir.setLength(1);
        }

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        if (fa.getBody() == containedZone || fb.getBody() == containedZone) {
            trashContained++;
            if (trashContained >= numTrash) win = true;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        if (fa.getBody() == containedZone || fb.getBody() == containedZone) {
            trashContained--;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
