package us.ihmc.euclid.referenceFrame;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import org.junit.Test;

import us.ihmc.euclid.geometry.Pose3D;
import us.ihmc.euclid.geometry.interfaces.Pose3DReadOnly;
import us.ihmc.euclid.geometry.tools.EuclidGeometryRandomTools;
import us.ihmc.euclid.referenceFrame.interfaces.FramePose3DReadOnly;
import us.ihmc.euclid.referenceFrame.tools.EuclidFrameAPITestTools;
import us.ihmc.euclid.tools.EuclidCoreRandomTools;

public abstract class FramePose3DReadOnlyTest<T extends FramePose3DReadOnly>
{
   public abstract T createFramePose(ReferenceFrame referenceFrame, Pose3DReadOnly pose);

   public final T createEmptyFramePose(ReferenceFrame referenceFrame)
   {
      return createFramePose(referenceFrame, new Pose3D());
   }

   public final T createRandomPose(Random random)
   {
      return createRandomFramePose(random, ReferenceFrame.getWorldFrame());
   }

   public final T createRandomFramePose(Random random, ReferenceFrame referenceFrame)
   {
      return createFramePose(referenceFrame, EuclidGeometryRandomTools.nextPose3D(random));
   }

   public final T createRandom2DFramePose(Random random, ReferenceFrame referenceFrame)
   {
      Pose3D pose = new Pose3D();
      pose.setOrientationYawPitchRoll(EuclidCoreRandomTools.nextDouble(random, Math.PI), 0, 0);
      pose.setPosition(EuclidCoreRandomTools.nextPoint2D(random));
      return createFramePose(referenceFrame, pose);
   }

   @Test
   public void testReferenceFrameChecks() throws Throwable
   {
      Random random = new Random(234);
      Predicate<Method> methodFilter = m -> !m.getName().contains("IncludingFrame") && !m.getName().equals("equals") && !m.getName().equals("epsilonEquals");
      EuclidFrameAPITestTools.assertMethodsOfReferenceFrameHolderCheckReferenceFrame(frame -> createRandomFramePose(random, frame), methodFilter);
   }

   @Test
   public void testOverloading() throws Exception
   {
      Map<String, Class<?>[]> framelessMethodsToIgnore = new HashMap<>();
      EuclidFrameAPITestTools.assertOverloadingWithFrameObjects(FramePose3DReadOnly.class, Pose3DReadOnly.class, true, 1, framelessMethodsToIgnore);
   }
}
