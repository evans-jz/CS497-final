import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.*;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.GLCapabilities;
import org.omg.CORBA.portable.InputStream;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;
import static com.jogamp.opengl.GL.*; // GL constants
import static com.jogamp.opengl.GL2.*; // GL2 constants
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * NeHe Lesson #6 (JOGL 2 Port): Texture
 *
 * @author Hock-Chuan Chua
 * @version May 2012
 */
@SuppressWarnings("serial")
public class SolarSystem extends GLCanvas implements GLEventListener , KeyListener{
    // Define constants for the top-level container
    private static String TITLE = "NeHe Lesson #6: Texture";
    private static int CANVAS_WIDTH; // width of the drawable
    private static int CANVAS_HEIGHT; // height of the drawable
    private static final int FPS = 60; // animator's target frames per second
    private FPSAnimator animator;

    /** Constructor to setup the GUI for this Component */
    public SolarSystem(int width, int height, GLCapabilities capabilities) {
        super(capabilities);
        CANVAS_WIDTH = width;
        CANVAS_HEIGHT = height;
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        this.addGLEventListener(this);
        this.addKeyListener(this);
    }

    // Setup OpenGL Graphics Renderer

    private GLU glu; // for the GL Utility

    private static float floorLevel = -.75f;
    private ArrayList<Planet> planets;

    private Texture skyTexture;

    private Camera cam;
    private float cameraAzimuth = 0.0f, cameraSpeed = 0.0f, cameraElevation = 0.0f;
    private float cameraUpx = 0.0f, cameraUpy = 1.0f, cameraUpz = 0.0f;
    private float cameraCoordsPosx = 0.0f, cameraCoordsPosy = 0.0f, cameraCoordsPosz = -20.0f;

    private Texture earthTexture;
    private Texture cloudTexture;
    private Texture moonTexture;
//    private Texture skyTexture;

    private static final float SUN_RADIUS = 12f;

    // Texture image flips vertically. Shall use TextureCoords class to retrieve
    // the
    // top, bottom, left and right coordinates.
    private float fTextureTop, fTextureBottom, fTextureLeft, fTextureRight;


    // ------ Implement methods declared in GLEventListener ------

    /**
     * Called back immediately after the OpenGL context is initialized. Can be
     * used to perform one-time initialization. Run only once.
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2(); // get the OpenGL graphics context
        glu = new GLU(); // get GL Utilities
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
        gl.glClearDepth(1.0f); // set clear depth value to farthest
        gl.glEnable(GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out

        animator = new FPSAnimator(this, 60, true);
        animator.start();
        // lighting

        // adding planets
        String texturePath = "C:\\Users\\evans\\IdeaProjects\\CS497-final\\res\\";
                
        earthTexture = getObjectTexture(gl, texturePath+"earthmap1k.jpg");
        cloudTexture = getObjectTexture(gl, texturePath+"tx_15_1.png");
        skyTexture = getObjectTexture(gl, texturePath+"starfield.png");
        moonTexture = getObjectTexture(gl, texturePath+"tx_0_0.png");

        Planet sun = new Planet(gl, glu, getObjectTexture(gl, texturePath+"preview_sun.jpg"), 0.1f, 0f , SUN_RADIUS);
        Planet mercury = new Planet(gl, glu, getObjectTexture(gl, texturePath+"mercurymap.jpg"), 1.2f, SUN_RADIUS + 2f, 2.56f);
        Planet venus = new Planet(gl, glu, getObjectTexture(gl, texturePath+"venusmap.jpg"), 0.7f, SUN_RADIUS + 12f, 3.56f);
        Planet Jupiter = new Planet(gl, glu, getObjectTexture(gl, texturePath+"jupiter.jpg"), 0.25f, SUN_RADIUS + 65f, 8.56f);
        Planet mars = new Planet(gl, glu, getObjectTexture(gl, texturePath+"mars_1k_color.jpg"), 0.3f, SUN_RADIUS + 50f, 3.56f);
        Planet Saturn = new Planet(gl, glu, getObjectTexture(gl, texturePath+"saturn.jpg"), 0.3f, SUN_RADIUS + 90f, 7.56f);
        Planet Uranus = new Planet(gl, glu, getObjectTexture(gl, texturePath+"uranuscyl1.jpg"), 0.25f, SUN_RADIUS + 105f, 6.56f);
        Planet Neptune = new Planet(gl, glu, getObjectTexture(gl, texturePath+"neptune_current.jpg"), 0.275f, SUN_RADIUS + 120f, 5.56f);

        planets = new ArrayList<Planet>();
        planets.add(sun);
        planets.add(mercury);
        planets.add(venus);
        planets.add(mars);
        planets.add(Jupiter);
        planets.add(Saturn);
        planets.add(Uranus);
        planets.add(Neptune);

    }


    /**
     * Called back by the animator to perform rendering.
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        if (!animator.isAnimating()) {
            return;
        }
        GL2 gl = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context
        setCamera(gl,200);

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//        cam.useView();
        for (Planet p: planets) {
            p.drawPlanet();
//            p.drawPath();
        }
        skyTexture.enable(gl);
        skyTexture.bind(gl);

        update(drawable);
    }

    private void setCamera(GL2 gl, float distance) {
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
        glu.gluLookAt(0, -5, distance, 0, 0, 0, 0, 1, 0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    public void moveCamera() {
        float[] tmp = polarToCartesian(cameraAzimuth, cameraSpeed, cameraElevation);

        cameraCoordsPosx += tmp[0];
        cameraCoordsPosy += tmp[1];
        cameraCoordsPosz += tmp[2];
    }

    private float[] polarToCartesian(float azimuth, float length, float altitude) {
        float[] result = new float[3];
        float x, y, z;

        // Do x-z calculation
        float theta = (float) Math.toRadians(90 - azimuth);
        float tantheta = (float) Math.tan(theta);
        float radian_alt = (float) Math.toRadians(altitude);
        float cospsi = (float) Math.cos(radian_alt);

        x = (float) Math.sqrt((length * length) / (tantheta * tantheta + 1));
        z = tantheta * x;

        x = -x;

        if ((azimuth >= 180.0 && azimuth <= 360.0) || azimuth == 0.0f) {
            x = -x;
            z = -z;
        }

        // Calculate y, and adjust x and z
        y = (float) (Math.sqrt(z * z + x * x) * Math.sin(radian_alt));

        if (length < 0) {
            x = -x;
            z = -z;
            y = -y;
        }

        x = x * cospsi;
        z = z * cospsi;

        result[0] = x;
        result[1] = y;
        result[2] = z;

        return result;
    }

    public void aimCamera(GL2 gl, GLU glu) {
        gl.glLoadIdentity();

        float[] tmp = polarToCartesian(cameraAzimuth, 100.0f, cameraElevation);

        float[] camUp = polarToCartesian(cameraAzimuth, 100.0f, cameraElevation + 90);

        cameraUpx = camUp[0];
        cameraUpy = camUp[1];
        cameraUpz = camUp[2];

        glu.gluLookAt(cameraCoordsPosx, cameraCoordsPosy, cameraCoordsPosz, cameraCoordsPosx + tmp[0],
                cameraCoordsPosy + tmp[1], cameraCoordsPosz + tmp[2], cameraUpx, cameraUpy, cameraUpz);
    }
    /**
     * Called back before the OpenGL context is destroyed. Release resource such
     * as buffers.
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    private void update(GLAutoDrawable drawable) {
        for(int i = 0; i < planets.size(); i++) {
            planets.get(i).update();
        }
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
    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
    /**
     * Call-back handler for window re-size event. Also called when the drawable
     * is first set to visible.
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
                        int height) {
        GL2 gl = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context

        if (height == 0)
            height = 1; // prevent divide by zero
        float aspect = (float) width / height;
        cam = new Camera(glu, drawable, 70, aspect, 0.3f, 1000);

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION); // choose projection matrix
        gl.glLoadIdentity(); // reset projection matrix
        glu.gluPerspective(45.0, aspect, 0.1, 100.0); // fovy, aspect, zNear,
        // zFar

        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity(); // reset

    }

    private Texture getObjectTexture(GL2 gl, String fileName) {
        FileInputStream stream = null;
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
}
