package com.flybot.fxwidgets.animation;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Default behavior for fading animation
 * @author knightj3
 *
 */
public class FadeDefault
{
   private final Node mNode;
   private final double mTime;
   private final double mFadeFrom;
   private final double mFadeTo;
   
   private final BooleanProperty mVisible = new SimpleBooleanProperty();
   
   /**
    * @param pNode
    */
   public FadeDefault(Node pNode)
   {
      this(pNode, 1000d);
   }
   
   /**
    * @param pNode
    * @param pTimeMs
    */
   public FadeDefault(Node pNode, double pTimeMs)
   {
      this(pNode, pTimeMs, 0d, 1d);
   }
   
   /**
    * @param pNode
    * @param pTimeMs
    * @param pLowOpacity
    * @param pHighOpacity
    */
   public FadeDefault(Node pNode, double pTimeMs, double pLowOpacity, double pHighOpacity)
   {
      mNode = pNode;
      mTime = pTimeMs;
      mFadeFrom = pLowOpacity;
      mFadeTo = pHighOpacity;
      
      mVisible.addListener(e -> fadeToVisibility(mVisible.get()));
   }
   
   /**
    * Binds this animation's fade visibility to another boolean property
    * @param pOtherProperty
    */
   public void bindVisibility(BooleanProperty pOtherProperty)
   {
      mVisible.bind(pOtherProperty);
   }
   
   /**
    * Fades to visible or not visible.
    * 
    * Technically it fades to "pToOpacity" if visible == true
    * and "pFromOpacity" if visible == false.
    * @param pVisible
    */
   public void fadeToVisibility(boolean pVisible)
   {
      double from = pVisible ? mFadeFrom : mFadeTo;
      double to = pVisible ? mFadeTo : mFadeFrom;
      final List<Animation> anim = new ArrayList<Animation>();

      final FadeTransition fadeItemGroup = new FadeTransition(
            Duration.millis(mTime), mNode);
      fadeItemGroup.setFromValue(from);
      fadeItemGroup.setToValue(to);
      anim.add(fadeItemGroup);
      final ParallelTransition transition = new ParallelTransition();
      transition.getChildren().addAll(anim);
      transition.play();
   }
   
}
