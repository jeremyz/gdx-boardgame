package ch.asynk.gdx.boardgame;

import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

public class Camera extends OrthographicCamera
{
    private static final float ZEROF = 0.01f;

    private int padding;
    private float boardWidth;
    private float boardHeight;
    private float boardAspectRatio;
    private float zoomMax;
    private float zoomMin;
    private int screenWidth;
    private int screenHeight;
    private float widthFactor;
    private float heightFactor;
    private Rectangle viewport;

    private Rectangle hud;
    private Matrix4 hudMatrix;
    private Matrix4 hudInvProjMatrix;
    private int hudLeft;
    private int hudBottom;
    private boolean hudInBoard;

    public Camera(int padding, float boardWidth, float boardHeight, float zoomMax, float zoomMin, boolean hudInBoard)
    {
        super(boardWidth, boardHeight);
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.padding = padding;
        this.boardAspectRatio = (boardWidth / boardHeight);
        this.zoomMax = zoomMax;
        this.zoomMin = zoomMin;
        this.viewport = new Rectangle();

        this.hudInBoard = hudInBoard;
        this.hud = new Rectangle();
        this.hudMatrix = new Matrix4();
        this.hudInvProjMatrix = new Matrix4();
    }

    public void setDimension(float boardWidth, float boardHeight)
    {
        setToOrtho(false, boardWidth, boardHeight);
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.boardAspectRatio = (boardWidth / boardHeight);
    }

    public void updateViewport(int screenWidth, int screenHeight)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        float screenAvailableWidth = screenWidth - (2 * padding);
        float screenAvailableHeight = screenHeight - (2 * padding);
        float screenAspectRatio = (screenWidth / (float) screenHeight);
        float diff = (boardAspectRatio - screenAspectRatio);

        if (diff <= -ZEROF) {
            // screen wider than board : use max height, width grows up until max available on zooming
            viewport.height = screenAvailableHeight;
            viewport.width = java.lang.Math.min((screenAvailableHeight * boardAspectRatio /  zoom), screenAvailableWidth);
            viewport.x = padding + ((screenAvailableWidth - viewport.width) / 2f);
            viewport.y = padding;
            // camera aspect ratio must follow viewport aspect ration
            viewportHeight = boardHeight;
            viewportWidth = (viewportHeight * (viewport.width / viewport.height));
            // hud
            hud.y = 0;
            hud.x = (hud.y * viewportWidth / viewportHeight);
        } else if (diff > ZEROF) {
            // word is wider than screen : use max width, height grows up until max available on zooming
            viewport.width = screenAvailableWidth;
            viewport.height = java.lang.Math.min((screenAvailableWidth / boardAspectRatio / zoom), screenAvailableHeight);
            viewport.y = padding + ((screenAvailableHeight - viewport.height) / 2f);
            viewport.x = padding;
            // camera aspect ratio must follow viewport aspect ration
            viewportWidth = boardWidth;
            viewportHeight = (viewportWidth * (viewport.height / viewport.width));
            // hud
            hud.x = 0;
            hud.y = (hud.x * viewportHeight / viewportWidth);
        }

        if (hudInBoard) {
            hud.width = (viewport.width - (2 * hud.x));
            hud.height = (viewport.height - (2 * hud.y));
        } else {
            hud.x = 0;
            hud.y = 0;
            hud.width = screenWidth;
            hud.height = screenHeight;
        }

        // ratio viewport -> camera
        widthFactor = (viewportWidth / viewport.width);
        heightFactor = (viewportHeight / viewport.height);

        clampPosition();
        update(true);

        hudMatrix.setToOrtho2D(hud.x, hud.y, hud.width, hud.height);
        hudInvProjMatrix.set(hudMatrix);
        Matrix4.inv(hudInvProjMatrix.val);
    }

    public void applyScreenViewport()
    {
        HdpiUtils.glViewport(0, 0, screenWidth, screenHeight);
    }

    public void applyBoardViewport()
    {
        HdpiUtils.glViewport((int)viewport.x, (int)viewport.y, (int)viewport.width, (int)viewport.height);
    }

    public void applyHudViewport()
    {
        if (hudInBoard)
            applyBoardViewport();
        else
            applyScreenViewport();
    }

    public void centerOnWorld()
    {
        position.set((boardWidth / 2f), (boardHeight / 2f), 0f);
    }

    public void zoom(float dz)
    {
        zoom += dz;
        clampZoom();
    }

    public void clampZoom()
    {
        zoom = MathUtils.clamp(zoom, zoomMin, zoomMax);
    }

    public void translate(float dx, float dy)
    {
        float deltaX = (dx * zoom * widthFactor);
        float deltaY = (dy * zoom * heightFactor);
        translate(deltaX, -deltaY, 0);
        clampPosition();
        update(true);
    }

    public void clampPosition()
    {
        float cameraWidth = (viewportWidth * zoom);
        float cameraHeight = (viewportHeight * zoom);

        // on each axis, clamp on [ cameraDim/2 ; boardDim - cameraDim/2 ]

        if ((boardWidth - cameraWidth) > ZEROF) {
            cameraWidth /= 2f;
            position.x = MathUtils.clamp(position.x, cameraWidth, (boardWidth - cameraWidth));
        } else {
            position.x = (boardWidth / 2f);
        }

        if ((boardHeight - cameraHeight) > ZEROF) {
            cameraHeight /= 2f;
            position.y = MathUtils.clamp(position.y, cameraHeight, (boardHeight - cameraHeight));
        } else {
            position.y = (boardHeight / 2f);
        }
    }

    public void unproject(int x, int y, Vector3 v)
    {
        unproject(v.set(x, y, 0), viewport.x, viewport.y, viewport.width, viewport.height);
    }

    public void unprojectHud(float x, float y, Vector3 v)
    {
        Rectangle r = (hudInBoard ? viewport : hud);
        x = x - r.x;
        y = screenHeight - y - 1;
        y = y - r.y;
        v.x = (2 * x) / r.width - 1;
        v.y = (2 * y) / r.height - 1;
        v.z = 2 * v.z - 1;
        v.prj(hudInvProjMatrix);
    }

    public int getScreenWidth()
    {
        return screenWidth;
    }

    public int getScreenHeight()
    {
        return screenHeight;
    }

    public Matrix4 getHudMatrix()
    {
        return hudMatrix;
    }

    public Rectangle getViewport()
    {
        return viewport;
    }

    public Rectangle getHud()
    {
        return hud;
    }
}
