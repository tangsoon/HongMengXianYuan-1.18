package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsServiceException extends Exception {
   public final int httpResultCode;
   public final String httpResponseContent;
   public final int errorCode;
   public final String errorMsg;

   public RealmsServiceException(int pHttpResultCode, String pHttpResponseContent, RealmsError pRealmsError) {
      super(pHttpResponseContent);
      this.httpResultCode = pHttpResultCode;
      this.httpResponseContent = pHttpResponseContent;
      this.errorCode = pRealmsError.getErrorCode();
      this.errorMsg = pRealmsError.getErrorMessage();
   }

   public RealmsServiceException(int pHttpResultCode, String pHttpResponseContent, int pErrorCode, String pErrorMsg) {
      super(pHttpResponseContent);
      this.httpResultCode = pHttpResultCode;
      this.httpResponseContent = pHttpResponseContent;
      this.errorCode = pErrorCode;
      this.errorMsg = pErrorMsg;
   }

   public String toString() {
      if (this.errorCode == -1) {
         return "Realms (" + this.httpResultCode + ") " + this.httpResponseContent;
      } else {
         String s = "mco.errorMessage." + this.errorCode;
         String s1 = I18n.get(s);
         return (s1.equals(s) ? this.errorMsg : s1) + " - " + this.errorCode;
      }
   }
}