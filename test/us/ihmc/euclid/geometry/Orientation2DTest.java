package us.ihmc.euclid.geometry;

import org.junit.Test;
import us.ihmc.euclid.geometry.tools.EuclidGeometryRandomTools;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Orientation2DTest
{
   private int ITERATIONS = 1000;
   
   @Test
   public void testGeometricallyEquals() {
      Random random = new Random(19732L);
      Orientation2D firstOrientation, secondOrientation;
      double epsilon = 1e-7;
      
      firstOrientation = EuclidGeometryRandomTools.generateRandomOrientation2D(random);
      secondOrientation = new Orientation2D(firstOrientation);

      assertTrue(firstOrientation.geometricallyEquals(secondOrientation, epsilon));
      assertTrue(secondOrientation.geometricallyEquals(firstOrientation, epsilon));
      assertTrue(firstOrientation.geometricallyEquals(firstOrientation, epsilon));
      assertTrue(secondOrientation.geometricallyEquals(secondOrientation, epsilon));
      
      for (int i = 0; i < ITERATIONS; ++i) {
         firstOrientation = EuclidGeometryRandomTools.generateRandomOrientation2D(random);
         secondOrientation = new Orientation2D(firstOrientation);
         
         secondOrientation.setYaw(firstOrientation.getYaw() + 0.99 * epsilon);
         
         assertTrue(firstOrientation.geometricallyEquals(secondOrientation, epsilon));
         
         secondOrientation.setYaw(firstOrientation.getYaw() - 0.99 * epsilon);
         
         assertTrue(firstOrientation.geometricallyEquals(secondOrientation, epsilon));
         
         secondOrientation.setYaw(firstOrientation.getYaw() + 1.01 * epsilon);
         
         assertFalse(firstOrientation.geometricallyEquals(secondOrientation, epsilon));
         
         secondOrientation.setYaw(firstOrientation.getYaw() - 1.01 * epsilon);

         assertFalse(firstOrientation.geometricallyEquals(secondOrientation, epsilon));
      }
   }
}
