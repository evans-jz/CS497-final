import java.awt.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.jogamp.opengl.GL.*; // GL constants
import static com.jogamp.opengl.GL2.*; // GL2 constants

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.FPSAnimator;

import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SolarSystem extends GLCanvas implements GLEventListener , KeyListener{
    // Define constants for the top-level container
    private static int CANVAS_WIDTH;
    private static int CANVAS_HEIGHT;
    private GLU glu;
    private FPSAnimator animator;
    private Camera cam;
    private Texture skyTexture;
    private ArrayList<Planet> planets;
//    private float cameraAzimuth = 0.0f, cameraSpeed = 0.0f, cameraElevation = 0.0f;
//    private float cameraUpx = 0.0f, cameraUpy = 1.0f, cameraUpz = 0.0f;
//    private float cameraCoordsPosx = 0.0f, cameraCoordsPosy = 0.0f, cameraCoordsPosz = -20.0f;
//    private Texture skyTexture;

    public SolarSystem(int width, int height, GLCapabilities capabilities) {
        super(capabilities);
        CANVAS_WIDTH = width;
        CANVAS_HEIGHT = height;
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        this.addGLEventListener(this);
        this.addKeyListener(this);
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glShadeModel(GL_SMOOTH);

        animator = new FPSAnimator(this, 60, true);
        animator.start();

        // adding planets
        String texturePath = "C:\\Users\\evans\\IdeaProjects\\CS497-final\\res\\";
        skyTexture = getObjectTexture(gl, texturePath+"starfield.png");

        Planet sun =        new Planet(0.0f,0.1f,0.0f, 0.7f, texturePath+"preview_sun.jpg", glu, gl);
        Planet mercury =    new Planet(2.0f,1.0f,1.0f, 0.1f, texturePath+"mercurymap.jpg", glu, gl);
        Planet venus =      new Planet(1.3f,0.5f,2.0f, 0.22f,texturePath+"venusmap.jpg", glu, gl);
        Planet earth =      new Planet(1.0f,0.6f,3.0f, 0.26f,texturePath+"earthmap1k.jpg", glu, gl);
        Planet mars =       new Planet(0.5f,0.9f,4.0f, 0.12f,texturePath+"mars_1k_color.jpg", glu, gl);
        Planet jupiter =    new Planet(0.2f,0.2f,5.0f, 0.5f,texturePath+"jupiter.jpg", glu, gl);
        Planet saturn =     new Planet(0.15f,0.3f,6.0f, 0.35f,texturePath+"saturn.jpg", glu, gl);
        Planet uranus =     new Planet(0.08f,0.45f,7.0f, 0.26f,texturePath+"uranuscyl1.jpg", glu, gl);
        Planet neptune =    new Planet(0.05f,0.4f,8.0f, 0.3f,texturePath+"neptune_current.jpg", glu, gl);

        planets = new ArrayList<>();
        planets.add(sun);
        planets.add(mercury);
        planets.add(venus);
        planets.add(earth);
        planets.add(mars);
        planets.add(jupiter);
        planets.add(saturn);
        planets.add(uranus);
        planets.add(neptune);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
//        setCamera(gl, 200);
//        aimCamera(gl, glu);
//        moveCamera();
        setLights(gl);

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -20.0f);
        gl.glRotatef(30f, 1.0f, 0.0f, 0.0f);

        skyTexture.enable(gl);
        skyTexture.bind(gl);
//        skyTexture.disable(gl);
        for (Planet p: planets) {
            p.drawPlanet();
            p.drawPath();
        }
        if (!animator.isAnimating()) {
            return;
        }
        update(drawable);
    }

    private void update(GLAutoDrawable drawable) {
        for (Planet p: planets) {
            p.update();
        }
    }

//    private float[] polarToCartesian(float azimuth, float length, float altitude) {
//        float[] result = new float[3];
//        float x, y, z;
//
//        float theta = (float) Math.toRadians(90 - azimuth);
//        float tantheta = (float) Math.tan(theta);
//        float radian_alt = (float) Math.toRadians(altitude);
//        float cospsi = (float) Math.cos(radian_alt);
//
//        x = (float) Math.sqrt((length * length) / (tantheta * tantheta + 1));
//        z = tantheta * x;
//        x = -x;
//
//        if ((azimuth >= 180.0 && azimuth <= 360.0) || azimuth == 0.0f) {
//            x = -x;
//            z = -z;
//        }
//        // Calculate y, and adjust x and z
//        y = (float) (Math.sqrt(z * z + x * x) * Math.sin(radian_alt));
//        if (length < 0) {
//            x = -x;
//            z = -z;
//            y = -y;
//        }
//        x = x * cospsi;
//        z = z * cospsi;
//        result[0] = x;
//        result[1] = y;
//        result[2] = z;
//
//        return result;
//    }
//    private void setCamera(GL2 gl, float distance) {
//        float widthHeightRatio = (float) getWidth() / (float) getHeight();
//
//        gl.glMatrixMode(GL2.GL_PROJECTION);
//        gl.glLoadIdentity();
//        glu.gluPerspective(50, widthHeightRatio, 1f, 1000f);
//        gl.glMatrixMode(GL2.GL_MODELVIEW);
//        gl.glLoadIdentity();
//        glu.gluLookAt(0, 10, distance, 0, 0, 0, 0, 1, 0);
//    }
//
//    public void moveCamera() {
//        float[] tmp = polarToCartesian(cameraAzimuth, cameraSpeed, cameraElevation);
//
//        cameraCoordsPosx += tmp[0];
//        cameraCoordsPosy += tmp[1];
//        cameraCoordsPosz += tmp[2];
//    }
//    public void aimCamera(GL2 gl, GLU glu) {
//        gl.glLoadIdentity();
//
//        float[] tmp = polarToCartesian(cameraAzimuth, 100.0f, cameraElevation);
//
//        float[] camUp = polarToCartesian(cameraAzimuth, 100.0f, cameraElevation + 90);
//
//        cameraUpx = camUp[0];
//        cameraUpy = camUp[1];
//        cameraUpz = camUp[2];
//
//        glu.gluLookAt(cameraCoordsPosx, cameraCoordsPosy, cameraCoordsPosz, cameraCoordsPosx + tmp[0],
//                cameraCoordsPosy + tmp[1], cameraCoordsPosz + tmp[2], cameraUpx, cameraUpy, cameraUpz);
//    }

    private void setLights(GL2 gl) {
        float SHINE_ALL_DIRECTIONS = 1;
        float[] lightPos = { 0, 0, 0, SHINE_ALL_DIRECTIONS };
        float[] lightColorAmbient = { 0.5f, 0.5f, 0.5f, 1f };
        float[] lightColorSpecular = { 0.8f, 0.8f, 0.8f, 1f };

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);

        float lmodel_ambient[] = {1.0f, 1.0f, 1.0f, 1.0f};
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);

        gl.glDisable(GL2.GL_COLOR_MATERIAL);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LESS);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glCullFace(GL2.GL_BACK);
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glShadeModel(GL2.GL_SMOOTH);
    }
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_W) {
            cam.moveForward();
        }
        if (keyCode == KeyEvent.VK_A) {
            cam.moveLeft();
        }
        if (keyCode == KeyEvent.VK_S) {
            cam.moveBack();
        }
        if (keyCode == KeyEvent.VK_D) {
            cam.moveRight();
        }
        if (keyCode == KeyEvent.VK_R) {
            cam.moveUp();
        }
        if (keyCode == KeyEvent.VK_F) {
            cam.moveDown();
        }
        if (keyCode == KeyEvent.VK_Q) {
            cam.rotateLeft();
        }
        if (keyCode == KeyEvent.VK_E) {
            cam.rotateRight();
        }
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
                        int height) {
        GL2 gl = drawable.getGL().getGL2();

        if (height == 0)
            height = 1;
        float aspect = (float) width / height;
        cam = new Camera(glu, drawable, 70, aspect, 0.3f, 1000);
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, aspect, 0.1, 100.0);

        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    private Texture getObjectTexture(GL2 gl, String fileName) {
        FileInputStream stream;
        Texture tex = null;
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        try {
            stream = new FileInputStream(new File(fileName));
            TextureData data = TextureIO.newTextureData(gl.getGLProfile(), stream, false, extension);
            tex = TextureIO.newTexture(data);
        } catch (FileNotFoundException e) {
            System.err.println("Error loading the file!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO Exception!");
            e.printStackTrace();
        }
        return tex;
    }
    public void keyReleased(KeyEvent e) { }
    public void keyTyped(KeyEvent e) { }
    public void dispose(GLAutoDrawable drawable) { }

}
