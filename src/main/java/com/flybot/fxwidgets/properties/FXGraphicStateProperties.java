package com.flybot.fxwidgets.properties;

import java.util.EnumSet;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * @author knightj3
 *
 * This class holds properties for mouse over, selected, normal and disabled EStates
 * in lieu of CSS3 for JavaFX.  It's sole purpose is to provide the mechanism to
 * automatically change colors in the case where CSS isn't available to stylize a
 * Node - for example in complex Shape/Path objects.<br>
 * 
 * Note that enabled and visible are part of the base Node class, so there
 * is no need to include them here.<br>
 */
public class FXGraphicStateProperties
{   
   private final StateBasedProperties<Paint, EPropertyType> mColors;
   private final StateBasedProperties<Font, EPropertyType> mFonts;
   
   public FXGraphicStateProperties()
   {
      mColors = new StateBasedProperties<>(
            EnumSet.of(EPropertyType.FILL, EPropertyType.STROKE), Color.BEIGE);
      mFonts = new StateBasedProperties<>(EnumSet.of(EPropertyType.ANY), new Font(12));
      mColors.set(EPropertyType.STROKE, Color.BLACK);
   }

   public FXGraphicStateProperties(FXGraphicStateProperties pCopy)
   {
      mColors = new StateBasedProperties<>(pCopy.mColors);
      mFonts = new StateBasedProperties<>(pCopy.mFonts);
   }

   /**
    * Flows the active state down to all aggregate properties classes
    * @param pState
    */
   public final void setActiveState(EState pState)
   {
      mColors.setActiveState(pState);
      mFonts.setActiveState(pState);
   }
   
   public StateBasedProperties<Paint, EPropertyType> getColors()
   {
      return mColors;
   }
   
   public StateBasedProperties<Font, EPropertyType> getFonts()
   {
      return mFonts;
   }
}
