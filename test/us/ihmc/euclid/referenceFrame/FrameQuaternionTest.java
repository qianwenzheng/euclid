package us.ihmc.euclid.referenceFrame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import org.ejml.data.DenseMatrix64F;
import org.junit.Test;

import us.ihmc.euclid.axisAngle.interfaces.AxisAngleReadOnly;
import us.ihmc.euclid.matrix.interfaces.RotationMatrixReadOnly;
import us.ihmc.euclid.referenceFrame.interfaces.FrameVector3DReadOnly;
import us.ihmc.euclid.referenceFrame.interfaces.ReferenceFrameHolder;
import us.ihmc.euclid.referenceFrame.tools.EuclidFrameAPITestTools;
import us.ihmc.euclid.referenceFrame.tools.EuclidFrameAPITestTools.FrameTypeBuilder;
import us.ihmc.euclid.referenceFrame.tools.EuclidFrameAPITestTools.GenericTypeBuilder;
import us.ihmc.euclid.referenceFrame.tools.EuclidFrameRandomTools;
import us.ihmc.euclid.referenceFrame.tools.EuclidFrameTestTools;
import us.ihmc.euclid.rotationConversion.YawPitchRollConversion;
import us.ihmc.euclid.tools.EuclidCoreRandomTools;
import us.ihmc.euclid.tools.EuclidCoreTestTools;
import us.ihmc.euclid.tools.EuclidCoreTools;
import us.ihmc.euclid.tuple3D.Vector3D;
import us.ihmc.euclid.tuple3D.interfaces.Vector3DReadOnly;
import us.ihmc.euclid.tuple4D.Quaternion;
import us.ihmc.euclid.tuple4D.QuaternionBasicsTest;
import us.ihmc.euclid.tuple4D.interfaces.QuaternionReadOnly;
import us.ihmc.euclid.tuple4D.interfaces.Tuple4DReadOnly;

public final class FrameQuaternionTest extends FrameQuaternionReadOnlyTest<FrameQuaternion>
{
   public static final ReferenceFrame worldFrame = ReferenceFrame.getWorldFrame();
   public static final double EPSILON = 1e-10;

   @Override
   public FrameQuaternion createFrameQuaternion(ReferenceFrame referenceFrame, QuaternionReadOnly quaternion)
   {
      return new FrameQuaternion(referenceFrame, quaternion);
   }

   @Test
   public void testConstructors() throws Exception
   {
      Random random = new Random(435345);

      { // Test FrameQuaternion()
         FrameQuaternion frameQuaternion = new FrameQuaternion();
         assertTrue(frameQuaternion.referenceFrame == worldFrame);
         EuclidCoreTestTools.assertQuaternionIsSetToZero(frameQuaternion);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test FrameQuaternion(ReferenceFrame referenceFrame)
         ReferenceFrame randomFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         FrameQuaternion frameQuaternion = new FrameQuaternion(randomFrame);
         assertTrue(frameQuaternion.referenceFrame == randomFrame);
         EuclidCoreTestTools.assertQuaternionIsSetToZero(frameQuaternion);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test FrameQuaternion(ReferenceFrame referenceFrame, double x, double y, double z, double s)
         ReferenceFrame randomFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         Quaternion randomQuaternion = EuclidCoreRandomTools.nextQuaternion(random);
         FrameQuaternion frameQuaternion = new FrameQuaternion(randomFrame, randomQuaternion.getX(), randomQuaternion.getY(), randomQuaternion.getZ(),
                                                               randomQuaternion.getS());
         assertTrue(frameQuaternion.referenceFrame == randomFrame);
         EuclidCoreTestTools.assertTuple4DEquals(randomQuaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test FrameQuaternion(ReferenceFrame referenceFrame, double[] quaternionArray)
         ReferenceFrame randomFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         Quaternion randomQuaternion = EuclidCoreRandomTools.nextQuaternion(random);
         double[] array = new double[4];
         randomQuaternion.get(array);
         FrameQuaternion frameQuaternion = new FrameQuaternion(randomFrame, array);
         assertTrue(frameQuaternion.referenceFrame == randomFrame);
         EuclidCoreTestTools.assertTuple4DEquals(randomQuaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test FrameQuaternion(ReferenceFrame referenceFrame, DenseMatrix64F matrix)
         ReferenceFrame randomFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         Quaternion randomQuaternion = EuclidCoreRandomTools.nextQuaternion(random);
         DenseMatrix64F denseMatrix = new DenseMatrix64F(4, 1);
         randomQuaternion.get(denseMatrix);
         FrameQuaternion frameQuaternion = new FrameQuaternion(randomFrame, denseMatrix);
         assertTrue(frameQuaternion.referenceFrame == randomFrame);
         EuclidCoreTestTools.assertTuple4DEquals(randomQuaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test FrameQuaternion(ReferenceFrame referenceFrame, QuaternionReadOnly quaternionReadOnly)
         ReferenceFrame randomFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         QuaternionReadOnly randomQuaternion = EuclidCoreRandomTools.nextQuaternion(random);
         FrameQuaternion frameQuaternion = new FrameQuaternion(randomFrame, randomQuaternion);
         assertTrue(frameQuaternion.referenceFrame == randomFrame);
         EuclidCoreTestTools.assertTuple4DEquals(randomQuaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test FrameQuaternion(ReferenceFrame referenceFrame, Tuple4DReadOnly tuple4DReadOnly)
         ReferenceFrame randomFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         Tuple4DReadOnly randomTuple = EuclidCoreRandomTools.nextVector4D(random);
         Quaternion expectedQuaternion = new Quaternion(randomTuple);
         FrameQuaternion frameQuaternion = new FrameQuaternion(randomFrame, randomTuple);
         EuclidCoreTestTools.assertTuple4DEquals(expectedQuaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test FrameQuaternion(ReferenceFrame referenceFrame, RotationMatrixReadOnly rotationMatrix)
         ReferenceFrame randomFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         RotationMatrixReadOnly randomRotationMatrix = EuclidCoreRandomTools.nextRotationMatrix(random);
         Quaternion expectedQuaternion = new Quaternion(randomRotationMatrix);
         FrameQuaternion frameQuaternion = new FrameQuaternion(randomFrame, randomRotationMatrix);
         EuclidCoreTestTools.assertTuple4DEquals(expectedQuaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test FrameQuaternion(ReferenceFrame referenceFrame, AxisAngleReadOnly axisAngle)
         ReferenceFrame randomFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         AxisAngleReadOnly randomAxisAngle = EuclidCoreRandomTools.nextAxisAngle(random);
         Quaternion expectedQuaternion = new Quaternion(randomAxisAngle);
         FrameQuaternion frameQuaternion = new FrameQuaternion(randomFrame, randomAxisAngle);
         EuclidCoreTestTools.assertTuple4DEquals(expectedQuaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test FrameQuaternion(ReferenceFrame referenceFrame, Vector3DReadOnly rotationVector)
         ReferenceFrame randomFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         Vector3DReadOnly randomRotationVector = EuclidCoreRandomTools.nextVector3D(random);
         Quaternion expectedQuaternion = new Quaternion(randomRotationVector);
         FrameQuaternion frameQuaternion = new FrameQuaternion(randomFrame, randomRotationVector);
         EuclidCoreTestTools.assertTuple4DEquals(expectedQuaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test FrameQuaternion(ReferenceFrame referenceFrame, double yaw, double pitch, double roll)
         ReferenceFrame randomFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         double yaw = EuclidCoreRandomTools.nextDouble(random, Math.PI);
         double pitch = EuclidCoreRandomTools.nextDouble(random, YawPitchRollConversion.MAX_SAFE_PITCH_ANGLE);
         double roll = EuclidCoreRandomTools.nextDouble(random, Math.PI);
         Quaternion expectedQuaternion = new Quaternion(yaw, pitch, roll);
         FrameQuaternion frameQuaternion = new FrameQuaternion(randomFrame, yaw, pitch, roll);
         EuclidCoreTestTools.assertTuple4DEquals(expectedQuaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Test FrameQuaternion(FrameQuaternionReadOnly other)
         ReferenceFrame randomFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         FrameQuaternion randomFrameQuaternion = EuclidFrameRandomTools.nextFrameQuaternion(random, randomFrame);
         FrameQuaternion frameQuaternion = new FrameQuaternion(randomFrameQuaternion);
         assertTrue(frameQuaternion.referenceFrame == randomFrame);
         EuclidCoreTestTools.assertTuple4DEquals(randomFrameQuaternion, frameQuaternion, EPSILON);
         EuclidFrameTestTools.assertFrameTuple4DEquals(randomFrameQuaternion, frameQuaternion, EPSILON);
      }
   }

   @Test
   public void testSetIncludingFrame() throws Exception
   {
      Random random = new Random(2342);

      ReferenceFrame initialFrame = ReferenceFrame.getWorldFrame();

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Tests setIncludingFrame(ReferenceFrame referenceFrame, AxisAngleReadOnly axisAngle)
         AxisAngleReadOnly axisAngle = EuclidCoreRandomTools.nextAxisAngle(random);
         ReferenceFrame newFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         FrameQuaternion frameQuaternion = createRandomFrameQuaternion(random, initialFrame);
         Quaternion quaternion = new Quaternion();
         assertEquals(initialFrame, frameQuaternion.getReferenceFrame());
         frameQuaternion.setIncludingFrame(newFrame, axisAngle);
         quaternion.set(axisAngle);
         assertEquals(newFrame, frameQuaternion.getReferenceFrame());
         EuclidCoreTestTools.assertTuple4DEquals(quaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Tests setIncludingFrame(ReferenceFrame referenceFrame, RotationMatrixReadOnly rotationMatrix)
         RotationMatrixReadOnly rotationMatrix = EuclidCoreRandomTools.nextRotationMatrix(random);
         ReferenceFrame newFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         FrameQuaternion frameQuaternion = createRandomFrameQuaternion(random, initialFrame);
         Quaternion quaternion = new Quaternion();
         assertEquals(initialFrame, frameQuaternion.getReferenceFrame());
         frameQuaternion.setIncludingFrame(newFrame, rotationMatrix);
         quaternion.set(rotationMatrix);
         assertEquals(newFrame, frameQuaternion.getReferenceFrame());
         EuclidCoreTestTools.assertTuple4DEquals(quaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Tests setIncludingFrame(ReferenceFrame referenceFrame, Vector3DReadOnly rotationVector)
         Vector3DReadOnly rotationVector = EuclidCoreRandomTools.nextRotationVector(random);
         ReferenceFrame newFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         FrameQuaternion frameQuaternion = createRandomFrameQuaternion(random, initialFrame);
         Quaternion quaternion = new Quaternion();
         assertEquals(initialFrame, frameQuaternion.getReferenceFrame());
         frameQuaternion.setIncludingFrame(newFrame, rotationVector);
         quaternion.set(rotationVector);
         assertEquals(newFrame, frameQuaternion.getReferenceFrame());
         EuclidCoreTestTools.assertTuple4DEquals(quaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Tests setIncludingFrame(FrameVector3DReadOnly rotationVector)
         ReferenceFrame newFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         FrameVector3DReadOnly rotationVector = new FrameVector3D(newFrame, EuclidCoreRandomTools.nextRotationVector(random));
         FrameQuaternion frameQuaternion = createRandomFrameQuaternion(random, initialFrame);
         Quaternion quaternion = new Quaternion();
         assertEquals(initialFrame, frameQuaternion.getReferenceFrame());
         frameQuaternion.setIncludingFrame(rotationVector);
         quaternion.set(rotationVector);
         assertEquals(newFrame, frameQuaternion.getReferenceFrame());
         EuclidCoreTestTools.assertTuple4DEquals(quaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Tests setYawPitchRollIncludingFrame(ReferenceFrame referenceFrame, double[] yawPitchRoll)
         double[] yawPitchRoll = EuclidCoreRandomTools.nextYawPitchRoll(random);
         ReferenceFrame newFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         FrameQuaternion frameQuaternion = createRandomFrameQuaternion(random, initialFrame);
         Quaternion quaternion = new Quaternion();
         assertEquals(initialFrame, frameQuaternion.getReferenceFrame());
         frameQuaternion.setYawPitchRollIncludingFrame(newFrame, yawPitchRoll);
         quaternion.setYawPitchRoll(yawPitchRoll);
         assertEquals(newFrame, frameQuaternion.getReferenceFrame());
         EuclidCoreTestTools.assertTuple4DEquals(quaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Tests setYawPitchRollIncludingFrame(ReferenceFrame referenceFrame, double yaw, double pitch, double roll)
         double yaw = EuclidCoreRandomTools.nextDouble(random, Math.PI);
         double pitch = EuclidCoreRandomTools.nextDouble(random, YawPitchRollConversion.MAX_SAFE_PITCH_ANGLE);
         double roll = EuclidCoreRandomTools.nextDouble(random, Math.PI);
         ReferenceFrame newFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         FrameQuaternion frameQuaternion = createRandomFrameQuaternion(random, initialFrame);
         Quaternion quaternion = new Quaternion();
         assertEquals(initialFrame, frameQuaternion.getReferenceFrame());
         frameQuaternion.setYawPitchRollIncludingFrame(newFrame, yaw, pitch, roll);
         quaternion.setYawPitchRoll(yaw, pitch, roll);
         assertEquals(newFrame, frameQuaternion.getReferenceFrame());
         EuclidCoreTestTools.assertTuple4DEquals(quaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Tests setEulerIncludingFrame(ReferenceFrame referenceFrame, Vector3DReadOnly eulerAngles)
         Vector3D eulerAngles = EuclidCoreRandomTools.nextRotationVector(random);
         eulerAngles.setY(EuclidCoreTools.clamp(eulerAngles.getY(), YawPitchRollConversion.MAX_SAFE_PITCH_ANGLE));
         ReferenceFrame newFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         FrameQuaternion frameQuaternion = createRandomFrameQuaternion(random, initialFrame);
         Quaternion quaternion = new Quaternion();
         assertEquals(initialFrame, frameQuaternion.getReferenceFrame());
         frameQuaternion.setEulerIncludingFrame(newFrame, eulerAngles);
         quaternion.setEuler(eulerAngles);
         assertEquals(newFrame, frameQuaternion.getReferenceFrame());
         EuclidCoreTestTools.assertTuple4DEquals(quaternion, frameQuaternion, EPSILON);
      }

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      { // Tests setEulerIncludingFrame(ReferenceFrame referenceFrame, double rotX, double rotY, double rotZ)
         double rotX = EuclidCoreRandomTools.nextDouble(random, Math.PI);
         double rotY = EuclidCoreRandomTools.nextDouble(random, YawPitchRollConversion.MAX_SAFE_PITCH_ANGLE);
         double rotZ = EuclidCoreRandomTools.nextDouble(random, Math.PI);
         ReferenceFrame newFrame = EuclidFrameRandomTools.nextReferenceFrame(random);
         FrameQuaternion frameQuaternion = createRandomFrameQuaternion(random, initialFrame);
         Quaternion quaternion = new Quaternion();
         assertEquals(initialFrame, frameQuaternion.getReferenceFrame());
         frameQuaternion.setEulerIncludingFrame(newFrame, rotX, rotY, rotZ);
         quaternion.setEuler(rotX, rotY, rotZ);
         assertEquals(newFrame, frameQuaternion.getReferenceFrame());
         EuclidCoreTestTools.assertTuple4DEquals(quaternion, frameQuaternion, EPSILON);
      }
   }

   @Override
   public FrameQuaternion createFrameQuaternion(ReferenceFrame referenceFrame, double x, double y, double z, double s)
   {
      FrameQuaternion frameQuaternion = new FrameQuaternion(referenceFrame);
      frameQuaternion.setUnsafe(x, y, z, s);
      return frameQuaternion;
   }

   @Test
   public void testConsistencyWithQuaternion()
   {
      Random random = new Random(234235L);

      FrameTypeBuilder<? extends ReferenceFrameHolder> frameTypeBuilder = (frame, quaternion) -> createFrameQuaternion(frame, (QuaternionReadOnly) quaternion);
      GenericTypeBuilder framelessTypeBuilder = () -> createRandomTuple(random).getGeometryObject();
      Predicate<Method> methodFilter = m -> !m.getName().equals("hashCode");
      EuclidFrameAPITestTools.assertFrameMethodsOfFrameHolderPreserveFunctionality(frameTypeBuilder, framelessTypeBuilder, methodFilter);

      GenericTypeBuilder frameless2DTypeBuilder = () -> createRandom2DFrameQuaternion(random, ReferenceFrame.getWorldFrame()).getGeometryObject();
      EuclidFrameAPITestTools.assertFrameMethodsOfFrameHolderPreserveFunctionality(frameTypeBuilder, frameless2DTypeBuilder, methodFilter);
   }

   @Override
   @Test
   public void testOverloading() throws Exception
   {
      super.testOverloading();
      Map<String, Class<?>[]> framelessMethodsToIgnore = new HashMap<>();
      framelessMethodsToIgnore.put("set", new Class<?>[] {Quaternion.class});
      framelessMethodsToIgnore.put("epsilonEquals", new Class<?>[] {Quaternion.class, Double.TYPE});
      framelessMethodsToIgnore.put("geometricallyEquals", new Class<?>[] {Quaternion.class, Double.TYPE});
      EuclidFrameAPITestTools.assertOverloadingWithFrameObjects(FrameQuaternion.class, Quaternion.class, true, 1, framelessMethodsToIgnore);
   }

   @Test
   public void testGetQuaternion()
   {
      Random random = new Random(43535);

      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++)
      {
         Quaternion expected = EuclidCoreRandomTools.nextQuaternion(random);
         FrameQuaternion frameQuaternion = new FrameQuaternion(worldFrame, expected);
         QuaternionReadOnly actual = frameQuaternion.getQuaternion();
         EuclidCoreTestTools.assertTuple4DEquals(expected, actual, EPSILON);
         EuclidCoreTestTools.assertTuple4DEquals(frameQuaternion, actual, EPSILON);
      }
   }

   @Test
   public void testFrameGeometryObjectFeatures() throws Throwable
   {
      FrameGeometryObjectTest<FrameQuaternion, Quaternion> frameGeometryObjectTest = new FrameGeometryObjectTest<FrameQuaternion, Quaternion>()
      {
         @Override
         public Quaternion createEmptyGeometryObject()
         {
            return createEmptyTuple().getGeometryObject();
         }

         @Override
         public Quaternion createRandomGeometryObject(Random random)
         {
            return createRandomTuple(random).getGeometryObject();
         }

         @Override
         public FrameQuaternion createEmptyFrameGeometryObject(ReferenceFrame referenceFrame)
         {
            return createEmptyFrameQuaternion(referenceFrame);
         }

         @Override
         public FrameQuaternion createFrameGeometryObject(ReferenceFrame referenceFrame, Quaternion geometryObject)
         {
            return createFrameQuaternion(referenceFrame, geometryObject);
         }

         @Override
         public FrameQuaternion createRandomFrameGeometryObject(Random random, ReferenceFrame referenceFrame)
         {
            return EuclidFrameRandomTools.nextFrameQuaternion(random, referenceFrame);
         }
      };

      for (Method testMethod : frameGeometryObjectTest.getClass().getMethods())
      {
         if (!testMethod.getName().startsWith("test"))
            continue;
         if (!Modifier.isPublic(testMethod.getModifiers()))
            continue;
         if (Modifier.isStatic(testMethod.getModifiers()))
            continue;
         // The following are due to normalization altering values during the createTuple() calls
         if (testMethod.getName().equals("testSetFromReferenceFrame"))
            continue;
         if (testMethod.getName().equals("testChangeFrame"))
            continue;
         if (testMethod.getName().equals("testGetGeometryObject"))
            continue;

         try
         {
            testMethod.invoke(frameGeometryObjectTest);
         }
         catch (InvocationTargetException e)
         {
            throw e.getCause();
         }
      }
   }

   @Test
   public void testQuaternionBasicsFeatures() throws Exception
   {
      QuaternionBasicsTest<FrameQuaternion> quaternionBasicsTest = new QuaternionBasicsTest<FrameQuaternion>()
      {
         @Override
         public FrameQuaternion createEmptyTuple()
         {
            return new FrameQuaternion();
         }

         @Override
         public FrameQuaternion createTuple(double v, double v1, double v2, double v3)
         {
            FrameQuaternion ret = new FrameQuaternion(ReferenceFrame.getWorldFrame());
            ret.setUnsafe(v, v1, v2, v3);
            return ret;
         }

         @Override
         public FrameQuaternion createRandomTuple(Random random)
         {
            return EuclidFrameRandomTools.nextFrameQuaternion(random, ReferenceFrame.getWorldFrame());
         }

         @Override
         public double getEpsilon()
         {
            return 1e-10;
         }
      };

      for (Method testMethod : quaternionBasicsTest.getClass().getMethods())
      {
         if (!testMethod.getName().startsWith("test"))
            continue;
         if (!Modifier.isPublic(testMethod.getModifiers()))
            continue;
         if (Modifier.isStatic(testMethod.getModifiers()))
            continue;

         testMethod.invoke(quaternionBasicsTest);
      }
   }
}