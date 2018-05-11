import java.awt.*;

import java.io.File;
import java.io.IOException;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2.*;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.FPSAnimator;

import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jogamp.opengl.glu.GLUquadric;

public class SolarSystem extends GLCanvas implements GLEventListener , MouseListener, MouseMotionListener, KeyListener, ActionListener{
    private static int CANVAS_WIDTH;
    private static int CANVAS_HEIGHT;
    private GLU glu;
    private GL2 gl;

    private FPSAnimator animator;
    private ArrayList<Planet> planets;

    private float xpos = 0, ypos = 0, zpos = 0;
    private float centerx, centery, centerz;
    private float roth = 0, rotv = 0;
    private int mouseX, mouseY, mouseButton;
    private float motionSpeed, rotateSpeed;
    private float xmin = -10f, ymin = -10f, zmin = -10f;
    private float xmax = 10f, ymax = 10f, zmax = 10f;
    private float animation_speed = 1.0f;

    private final String texturePath = "C:\\Users\\evans\\IdeaProjects\\CS497-final\\res\\";
    private Texture skyTexture;

    public SolarSystem(int width, int height, GLCapabilities capabilities) {
        super(capabilities);
        CANVAS_WIDTH = width;
        CANVAS_HEIGHT = height;
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        this.addGLEventListener(this);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    void initViewParameters() {
        roth = rotv = 0;
        float sun_r = (float) Math.sqrt((xmax - xmin) * (xmax - xmin) + (ymax - ymin) * (ymax - ymin) + (zmax - zmin) * (zmax - zmin)) * 0.707f;

        centerx = (xmax + xmin) / 2.f;
        centery = (ymax + ymin) / 2.f;
        centerz = (zmax + zmin) / 2.f;
        xpos = centerx;
        ypos = centery;
        zpos = sun_r / (float) Math.sin(45.f * Math.PI / 180.f) + centerz;

        motionSpeed = 0.002f * sun_r;
        rotateSpeed = 0.1f;
    }

    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL2();

        initViewParameters();
        setLights(gl);

        glu = new GLU();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glShadeModel(GL_SMOOTH);

        initPlanets(gl);
        animator = new FPSAnimator(drawable, 60,true);
        animator.start();

        try {
            skyTexture = TextureIO.newTexture(new File(texturePath+"starfield.png"), true);
            skyTexture.bind(gl);
            skyTexture.enable(gl);
        } catch (IOException exc) {
            exc.printStackTrace();
            System.exit(1);
        }
        this.requestFocus();
    }

    public void initPlanets(GL2 gl){
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
        gl = drawable.getGL().getGL2();

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glTranslatef(-xpos, -ypos, -zpos);
        gl.glTranslatef(centerx, centery, centerz);
        gl.glRotatef(360.f - roth, 0, 1.0f, 0);
        gl.glRotatef(30f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotv, 1.0f, 0, 0);
        gl.glTranslatef(-centerx, -centery, -centerz);

        skyTexture.enable(gl);
        skyTexture.bind(gl);
        drawCube(gl);
        skyTexture.disable(gl);

        for (Planet p: planets) {
            p.drawPlanet();
            p.drawPath();
        }
        if (animator.isAnimating()) {
            for (Planet p: planets) {
                p.update(animation_speed);
            }
        }
    }

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
    private void drawCube(GL2 gl) {
        skyTexture.enable(gl);
        skyTexture.bind(gl);

        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        final float radius = 70f;
        final int slices = 50;
        final int stacks = 50;
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_COLOR, GL2.GL_DST_ALPHA);
        GLUquadric sky = glu.gluNewQuadric();
        glu.gluQuadricTexture(sky, true);
        glu.gluQuadricDrawStyle(sky, GLU.GLU_FILL);
        glu.gluQuadricNormals(sky, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(sky, GLU.GLU_INSIDE);
        glu.gluSphere(sky, radius, slices, stacks);

        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_COLOR, GL2.GL_DST_ALPHA);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
                        int height) {
        GL2 gl = drawable.getGL().getGL2();

        if (height == 0)
            height = 1;
        float aspect = (float) width / height;
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, aspect, 0.1, 100.0);

        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
            case 'r':
            case 'R':
                initViewParameters();
                break;
            case 'a':
            case 'A':
                if (animator.isAnimating()) {
                    animator.stop();
                } else {
                    animator.start();
                }
                break;
            case '+':
            case '=':
                animation_speed += 0.1f;
                if(animation_speed > 3)
                    animation_speed = 3;
                break;
            case '-':
            case '_':
                animation_speed -= 0.1f;
                if(animation_speed < 0)
                    animation_speed = 0.1f;
                break;
            default:
                break;
        }
        this.display();
    }
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (mouseButton == MouseEvent.BUTTON3) {
            zpos -= (y - mouseY) * motionSpeed;
            mouseX = x;
            mouseY = y;
            this.display();
        } else if (mouseButton == MouseEvent.BUTTON2) {
            xpos -= (x - mouseX) * motionSpeed;
            ypos += (y - mouseY) * motionSpeed;
            mouseX = x;
            mouseY = y;
            this.display();
        } else if (mouseButton == MouseEvent.BUTTON1) {
            roth -= (x - mouseX) * rotateSpeed;
            rotv += (y - mouseY) * rotateSpeed;
            mouseX = x;
            mouseY = y;
            this.display();
        }
    }
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mouseButton = e.getButton();
        this.display();
    }

    public void mouseReleased(MouseEvent e) {
        mouseButton = MouseEvent.NOBUTTON;
        this.display();
    }
    public void dispose(GLAutoDrawable glautodrawable) {
    }
    public void keyTyped(KeyEvent e) {
    }
    public void keyReleased(KeyEvent e) {
    }
    public void mouseMoved(MouseEvent e) {
    }
    public void actionPerformed(ActionEvent e) {
    }
    public void mouseClicked(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }

}
