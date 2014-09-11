package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Given a background area and a safe zone area within it, this viewport will see the
 * safe zone as the main world and scale it up till one of its width or height fit the 
 * screen. Unlike FitViewport, this viewport won't leave black bars at the remaining 
 * space, instead it will show the background area outside of the safe zone.
 * @author roger.chuang
 */
public class SafeZoneViewport extends Viewport {
    static final String TAG = SafeZoneViewport.class.getSimpleName();

    float mBackgroundWidth, mBackgroundHeight;
    float mWorldRatio;

    public SafeZoneViewport(Vector2 backgroundSize, Vector2 safeZoneSize, OrthographicCamera camera) {
        mBackgroundWidth = backgroundSize.x;
        mBackgroundHeight = backgroundSize.y;

        super.worldWidth = safeZoneSize.x;
        super.worldHeight = safeZoneSize.y;
        mWorldRatio = safeZoneSize.x / safeZoneSize.y;

        super.camera = camera;
    }

    @Override
    public void update (int screenWidth, int screenHeight, boolean centerCamera) {
        float screenRatio = screenWidth / (float)screenHeight;

        Vector2 scaled = new Vector2();
        int offsetX = (int)((mBackgroundWidth - super.worldWidth) / 2);
        int offsetY = (int)((mBackgroundHeight - super.worldHeight) / 2);

        /* Wide screen */
        if (screenRatio > mWorldRatio) {
            Gdx.app.log(TAG, "Wide screen");
            float scale = screenHeight / super.worldHeight;
            scaled.set(mBackgroundWidth * scale, super.worldHeight * scale);
            offsetX = 0;

        /* Tall screen */
        } else if (screenRatio < mWorldRatio) {
            Gdx.app.log(TAG, "Tall screen");
            float scale = screenWidth / super.worldWidth;
            scaled.set(super.worldWidth * scale, mBackgroundHeight * scale);
            offsetY = 0;

        /* 3:2 screen */
        } else {
            Gdx.app.log(TAG, "3:2 screen");
            float scale = screenWidth / this.worldWidth;
            scaled.set(worldWidth * scale, worldHeight * scale);
        }

        super.viewportWidth = Math.round(scaled.x);
        super.viewportHeight = Math.round(scaled.y);

        super.viewportX = (screenWidth - super.viewportWidth) / 2;
        super.viewportY = (screenHeight - super.viewportHeight) / 2;

        Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        camera.viewportWidth = (screenRatio > mWorldRatio) ? mBackgroundWidth : super.worldWidth;
        camera.viewportHeight = (screenRatio < mWorldRatio) ? mBackgroundHeight : super.worldHeight;

        if (centerCamera) {
            camera.position.set(camera.viewportWidth / 2 + offsetX, camera.viewportHeight / 2 + offsetY, 0);
        }

        camera.update();
    }
}
