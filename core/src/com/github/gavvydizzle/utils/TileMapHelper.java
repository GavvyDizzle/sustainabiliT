package com.github.gavvydizzle.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.github.gavvydizzle.screen.MazeScreen;

public class TileMapHelper {

    private TiledMap tiledMap;
    private MazeScreen gameScreen;

    public TileMapHelper(MazeScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public OrthogonalTiledMapRenderer setupMazeMap() {
        tiledMap = new TmxMapLoader().load("maps/maze.tmx");
        parseMapObjects(tiledMap.getLayers().get("objects").getObjects());
        return new OrthogonalTiledMapRenderer(tiledMap);
    }

    private void parseMapObjects(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof PolygonMapObject) {
                createStaticBody((PolygonMapObject) mapObject, null);
            }
            else if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                String rectangleName = mapObject.getName();
                if (rectangleName == null) continue;

                if (rectangleName.equals("goal")) {
                    Body body = BodyHelperService.createFlagBody(
                            rectangle.getX() + rectangle.getWidth() / 2,
                            rectangle.getY() + rectangle.getHeight() / 2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            gameScreen.getWorld()
                    );
                    gameScreen.setGoal(body);
                }
            }
        }
    }

    private void createStaticBody(PolygonMapObject polygonMapObject, String group) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodyDef);
        Shape shape = createPolygonShape(polygonMapObject);
        if (group != null) {
            body.createFixture(shape, 0).setUserData(group);
        }
        else {
            body.createFixture(shape, 0);
        }
        shape.dispose();
    }

    private Shape createPolygonShape(PolygonMapObject polygonMapObject) {
        float[] vertices = polygonMapObject.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length/2];

        for (int i = 0; i < worldVertices.length; i++) {
            Vector2 current = new Vector2(vertices[i*2] / Constants.PPM, vertices[i*2+1] / Constants.PPM);
            worldVertices[i] = current;
        }

        PolygonShape shape = new PolygonShape();
        shape.set(worldVertices);
        return shape;
    }
}
