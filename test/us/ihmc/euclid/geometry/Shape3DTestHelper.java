package us.ihmc.euclid.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import us.ihmc.euclid.tools.EuclidCoreRandomTools;
import us.ihmc.euclid.tools.EuclidCoreTestTools;
import us.ihmc.euclid.transform.RigidBodyTransform;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.euclid.tuple3D.Vector3D;

public class Shape3DTestHelper
{
   public Shape3DTestHelper()
   {

   }

   public void runSimpleTests(Shape3D<?> shape3d, Random random, int numberOfPoints)
   {
      for (int i = 0; i < numberOfPoints; i++)
      {
         Point3D point = EuclidCoreRandomTools.nextPoint3D(random, 1.0, 1.0, 1.0);

         boolean pointIsInside = shape3d.isInsideEpsilon(point, -1e-5);
         boolean pointIsOutside = !shape3d.isInsideEpsilon(point, 1e-5);

         if (pointIsInside && pointIsOutside)
            fail();

         if (pointIsInside)
            runSomeTestsWithPointInside(shape3d, point);
         else if (pointIsOutside)
            runSomeTestsWithPointOutside(shape3d, point);

         else
         {
            shape3d.orthogonalProjection(point);
            Point3D pointOnSurface = new Point3D();
            Vector3D surfaceNormal = new Vector3D();
            shape3d.checkIfInside(point, pointOnSurface, surfaceNormal);
            runSomeTestsWithPointOnSurface(shape3d, pointOnSurface, surfaceNormal);
         }
      }

      Point3D pointLikelyOutside = new Point3D(100.0, 1500.0, 2000.0);
      Point3D pointToProject = new Point3D(pointLikelyOutside);
      shape3d.orthogonalProjection(pointToProject);

      RigidBodyTransform transform = EuclidCoreRandomTools.nextRigidBodyTransform(random);
      RigidBodyTransform transformInverse = new RigidBodyTransform(transform);
      transformInverse.invert();

      shape3d.applyTransform(transform);

      Point3D pointToProjectAfterTransform = new Point3D(pointLikelyOutside);
      transform.transform(pointToProjectAfterTransform);
      shape3d.orthogonalProjection(pointToProjectAfterTransform);
      transformInverse.transform(pointToProjectAfterTransform);

      Point3D pointToProjectAfterTransformAndInverse = new Point3D(pointLikelyOutside);
      shape3d.applyTransform(transformInverse);
      shape3d.orthogonalProjection(pointToProjectAfterTransformAndInverse);

      // This assertion appears to be incorrect. pointLikelyOutside stays the same but the shape is transformed
      // We would expect it to project to a different point
      // EuclidCoreTestTools.assertTuple3DEquals(pointToProject, pointToProjectAfterTransform, 1e-10);
      EuclidCoreTestTools.assertTuple3DEquals(pointToProject, pointToProjectAfterTransformAndInverse, 1e-10);

   }

   private void runSomeTestsWithPointOutside(Shape3D<?> shape3d, Point3D pointOutside)
   {
      // Check to make sure the point is actually outside:
      Point3D pointOnSurface = new Point3D();
      Vector3D surfaceNormal = new Vector3D();
      boolean isInside = shape3d.checkIfInside(pointOutside, pointOnSurface, surfaceNormal);
      assertFalse(isInside);

      if (pointOnSurface.containsNaN())
      {
         assertTrue(surfaceNormal.containsNaN());

         pointOnSurface.set(pointOutside);
         shape3d.orthogonalProjection(pointOnSurface);
      }

      runSomeTestsWithPointOnSurface(shape3d, pointOnSurface, surfaceNormal);

   }

   private void runSomeTestsWithPointInside(Shape3D<?> shape3d, Point3D pointInside)
   {
      // Check to make sure the point is actually inside:
      Point3D pointOnSurface = new Point3D();
      Vector3D surfaceNormal = new Vector3D();
      boolean isInside = shape3d.checkIfInside(pointInside, pointOnSurface, surfaceNormal);

      assertTrue(isInside);
      assertFalse(surfaceNormal.containsNaN());
      assertFalse(pointOnSurface.containsNaN());

      runSomeTestsWithPointOnSurface(shape3d, pointOnSurface, surfaceNormal);
   }

   private void runSomeTestsWithPointOnSurface(Shape3D<?> shape3d, Point3D pointOnSurface, Vector3D surfaceNormal)
   {
      // If the surface normal is NaN, then that means it was projected or something. If it does not contain NaN, then it should be checked for validity.
      if (surfaceNormal.containsNaN())
      {
         assertEquals(1.0, surfaceNormal.length(), 1e-7);
      }

      // Check that a further projection onto the surface doesn't change anything:
      Point3D newProjection = new Point3D();
      Vector3D newNormal = new Vector3D();
      boolean isInside = shape3d.checkIfInside(pointOnSurface, newProjection, newNormal);

      if (newProjection.containsNaN())
      {
         assertFalse(isInside); // Check this!!!
         assertTrue(newNormal.containsNaN());
         newProjection.set(pointOnSurface);
         shape3d.orthogonalProjection(newProjection);

         EuclidCoreTestTools.assertTuple3DEquals(pointOnSurface, newProjection, 1e-3);
      }
      else
      {
         assertTrue(shape3d.isInsideOrOnSurface(newProjection));
         assertEquals(1.0, newNormal.length(), 1e-7);
      }
      EuclidCoreTestTools.assertTuple3DEquals(pointOnSurface, newProjection, 1e-7);

      // Check that if you move a little in the direction of the normal you are outside and a little
      // opposite the normal, you are inside:
      Point3D pointALittleOutside = new Point3D(pointOnSurface);
      Point3D pointALittleInside = new Point3D(pointOnSurface);
      pointALittleOutside.scaleAdd(1e-4, surfaceNormal, pointALittleOutside);
      pointALittleInside.scaleAdd(-1e-4, surfaceNormal, pointALittleInside);

      boolean isInsideCheck = shape3d.isInsideOrOnSurface(pointALittleOutside);
      if (isInsideCheck)
      {
         System.out.println("shape3d = " + shape3d);
         System.out.println("pointOnSurface = " + pointOnSurface);
         System.out.println("surfaceNormal = " + surfaceNormal);

         isInside = shape3d.checkIfInside(pointOnSurface, newProjection, newNormal);

      }
      assertFalse(isInsideCheck);

      boolean insideOrOnSurface = shape3d.isInsideOrOnSurface(pointALittleInside);
      boolean isNormalWayDifferent = false;

      if (!insideOrOnSurface)
      { // When dealing with sharp edges, the point can jump on the other side of the shape even when shifting only by a little.
        // If that's the case, computing the normal for this point should reveal that by having the normal having a much different orientation.
         Vector3D problematicNormal = new Vector3D();
         shape3d.checkIfInside(pointALittleInside, null, problematicNormal);
         isNormalWayDifferent = problematicNormal.angle(surfaceNormal) > Math.toRadians(90.0);
      }

      if (!insideOrOnSurface && !isNormalWayDifferent)
      {
         System.out.println(shape3d);
         System.out.println("pointOnSurface = " + pointOnSurface);
         System.out.println("surfaceNormal = " + surfaceNormal);
         System.out.println("newNormal = " + newNormal);
         System.out.println("pointALittleInside = " + pointALittleInside);
         insideOrOnSurface = shape3d.isInsideOrOnSurface(pointALittleInside);
         shape3d.checkIfInside(pointOnSurface, newProjection, newNormal);
         System.out.println("newNormal = " + newNormal);
      }

      assertTrue(insideOrOnSurface || isNormalWayDifferent);
   }

}
