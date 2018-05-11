import java.io.File;
import java.io.IOException;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import static com.jogamp.opengl.GL.*;

public class Planet {
    private GL2 gl2;
    private GLU glu;

    private float yearSpeed;
    private float yearAngle;
    private float daySpeed;
    private float dayAngle;
    private float dist;
    private float radius;
    private Texture texture;

    private double red;
    private double green;
    private double blue;

    private final int slices = 50;
    private final int stacks = 50;

    public Planet(float ys, float ds, float d, float r, String text, GLU glu, GL2 gl2) {
        yearSpeed = ys;
        yearAngle = 0;
        daySpeed = ds;
        dayAngle = 0;
        dist = d;
        radius = r;
        try {
            texture = TextureIO.newTexture(new File(text), true);
        } catch (IOException exc) {
            exc.printStackTrace();
            System.exit(1);
        }
        this.glu = glu;
        this.gl2 = gl2;

        red = Math.random();
        green = Math.random();
        blue = Math.random();
    }

    public void drawPlanet() {
        // Apply texture.
        texture.enable(gl2);
        texture.bind(gl2);

        gl2.glPushMatrix();
        {
            gl2.glRotatef(yearAngle, 0.0f, -1.0f, 0.0f); // Rotate around the sun
            gl2.glTranslatef(dist, 0.0f, 0.0f); // Move away from sun
            gl2.glRotatef(dayAngle, 0.0f, -1.0f, 0.0f); // Rotate planet
            gl2.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f); // Correctly orient planet
            // Draw planet
            GLUquadric quad = glu.gluNewQuadric();
            glu.gluQuadricTexture(quad, true);
            glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
            glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
            glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);

            glu.gluSphere(quad, radius, slices, stacks);
            glu.gluDeleteQuadric(quad);
            texture.disable(gl2);
        }
        gl2.glPopMatrix();

        texture.disable(gl2);
    }

    public void drawPath() {
        double inc = Math.PI / 24;
        double max = 2 * Math.PI;
        gl2.glEnable(GL_LINE_SMOOTH);
        gl2.glLineWidth(2f);

        gl2.glBegin(GL_LINE_LOOP);
        gl2.glColor3d(red, green, blue);
        for (double d = 0; d < max; d += inc) {
            gl2.glVertex3d(Math.sin(d) * dist, 0, Math.cos(d) * dist);
        }
        gl2.glColor3f(1.0f, 1.0f, 1.0f);
        gl2.glEnd();
    }

    public void update(float animation_speed) {
            yearAngle += yearSpeed*animation_speed;
            if (yearAngle >360f)
                yearAngle = yearAngle-360;
            else if( yearAngle <0)
                yearAngle = yearAngle+360;

            dayAngle += daySpeed;
            if (dayAngle >360f)
                dayAngle = dayAngle-360;
            else if( dayAngle <0)
                dayAngle = dayAngle+360;
            System.out.println("yearAngle: " + yearAngle + " dayAngle: " + dayAngle);
    }

}
