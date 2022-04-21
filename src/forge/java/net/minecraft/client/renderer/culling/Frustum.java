package net.minecraft.client.renderer.culling;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Frustum {
   private final Vector4f[] frustumData = new Vector4f[6];
   private double camX;
   private double camY;
   private double camZ;

   public Frustum(Matrix4f p_113000_, Matrix4f p_113001_) {
      this.calculateFrustum(p_113000_, p_113001_);
   }

   public void prepare(double pCamX, double pCamY, double pCamZ) {
      this.camX = pCamX;
      this.camY = pCamY;
      this.camZ = pCamZ;
   }

   private void calculateFrustum(Matrix4f pProjection, Matrix4f pFrustrumMatrix) {
      Matrix4f matrix4f = pFrustrumMatrix.copy();
      matrix4f.multiply(pProjection);
      matrix4f.transpose();
      this.getPlane(matrix4f, -1, 0, 0, 0);
      this.getPlane(matrix4f, 1, 0, 0, 1);
      this.getPlane(matrix4f, 0, -1, 0, 2);
      this.getPlane(matrix4f, 0, 1, 0, 3);
      this.getPlane(matrix4f, 0, 0, -1, 4);
      this.getPlane(matrix4f, 0, 0, 1, 5);
   }

   private void getPlane(Matrix4f pFrustrumMatrix, int pX, int pY, int pZ, int pId) {
      Vector4f vector4f = new Vector4f((float)pX, (float)pY, (float)pZ, 1.0F);
      vector4f.transform(pFrustrumMatrix);
      vector4f.normalize();
      this.frustumData[pId] = vector4f;
   }

   public boolean isVisible(AABB pAabb) {
      return this.cubeInFrustum(pAabb.minX, pAabb.minY, pAabb.minZ, pAabb.maxX, pAabb.maxY, pAabb.maxZ);
   }

   private boolean cubeInFrustum(double pMinX, double pMinY, double pMinZ, double pMaxX, double pMaxY, double pMaxZ) {
      float f = (float)(pMinX - this.camX);
      float f1 = (float)(pMinY - this.camY);
      float f2 = (float)(pMinZ - this.camZ);
      float f3 = (float)(pMaxX - this.camX);
      float f4 = (float)(pMaxY - this.camY);
      float f5 = (float)(pMaxZ - this.camZ);
      return this.cubeInFrustum(f, f1, f2, f3, f4, f5);
   }

   private boolean cubeInFrustum(float pMinX, float pMinY, float pMinZ, float pMaxX, float pMaxY, float pMaxZ) {
      for(int i = 0; i < 6; ++i) {
         Vector4f vector4f = this.frustumData[i];
         if (!(vector4f.dot(new Vector4f(pMinX, pMinY, pMinZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(pMaxX, pMinY, pMinZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(pMinX, pMaxY, pMinZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(pMaxX, pMaxY, pMinZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(pMinX, pMinY, pMaxZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(pMaxX, pMinY, pMaxZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(pMinX, pMaxY, pMaxZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(pMaxX, pMaxY, pMaxZ, 1.0F)) > 0.0F)) {
            return false;
         }
      }

      return true;
   }
}