package com.github.gavvydizzle.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class BodyHelperService {

    private static final float sensorSize = 0.25f;
    private static final float sensorStretch = 0.95f;

    public static Body createBody(float x, float y, float width, float height, boolean isStatic, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x/Constants.PPM, y/Constants.PPM);
        bodyDef.fixedRotation = true;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2/Constants.PPM, height/2/Constants.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef).setUserData("body");
        shape.dispose();
        return body;
    }

    public static Body createPlayerBody(float x, float y, float width, float height, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x/Constants.PPM, y/Constants.PPM);
        bodyDef.fixedRotation = true;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2/Constants.PPM, height/2/Constants.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef).setUserData("body");

        // Add sensors
        shape.setAsBox(width*sensorStretch/2/Constants.PPM, sensorSize/2/Constants.PPM, new Vector2(0, -(height+sensorSize)/2/Constants.PPM), 0);
        fixtureDef.density = 0;
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData("bottom");

        shape.setAsBox(sensorSize/2/Constants.PPM, height*sensorStretch/2/Constants.PPM, new Vector2(-(width+sensorSize)/2/Constants.PPM, 0), 0);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData("left");

        shape.setAsBox(sensorSize/2/Constants.PPM, height*sensorStretch/2/Constants.PPM, new Vector2((width+sensorSize)/2/Constants.PPM, 0), 0);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData("right");

        shape.dispose();

        return body;
    }

    public static Body createFlagBody(float x, float y, float width, float height, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x/Constants.PPM, y/Constants.PPM);
        bodyDef.fixedRotation = true;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2/Constants.PPM, height/2/Constants.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    public static Body createSensorBody(float x, float y, int size, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x*1.5f/Constants.PPM, (Gdx.graphics.getHeight() - y)*1.5f/Constants.PPM);
        bodyDef.fixedRotation = true;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size/2/Constants.PPM, size/2/Constants.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    public static Body createPolygonRigidBody(float x, float y, Vector2[] vertices, boolean isSensor, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x/Constants.PPM, y/Constants.PPM);
        bodyDef.fixedRotation = true;
        Body body = world.createBody(bodyDef);

        for (Vector2 vertex : vertices) {
            vertex.scl(1 / Constants.PPM);
        }

        PolygonShape shape = new PolygonShape();
        shape.set(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = isSensor;
        fixtureDef.friction = 0.5f;
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    public static Body createTrash(float x, float y, float radius, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x/Constants.PPM, y/Constants.PPM);
        bodyDef.fixedRotation = true;
        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.2f;
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }
}
