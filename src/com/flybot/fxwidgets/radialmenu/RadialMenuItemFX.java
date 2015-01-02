package com.flybot.fxwidgets.radialmenu;

import static com.flybot.fxwidgets.properties.EPropertyType.ANY;
import static com.flybot.fxwidgets.properties.EPropertyType.FILL;
import static com.flybot.fxwidgets.properties.EPropertyType.STROKE;
import static com.flybot.fxwidgets.properties.EState.MOUSE_OVER;
import static com.flybot.fxwidgets.properties.EState.NORMAL;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import com.flybot.fxwidgets.properties.FXGraphicStateProperties;

/**
 * @author knightj3
 * Is a RadialMenuContainerFX with no sub menus
 */
public class RadialMenuItemFX
{

   protected final FXGraphicStateProperties mProperties;
   
   protected double mStartAngleRadians = 0;

   protected double mMenuArcRadians = Math.PI/4; // 45 degrees by default
   protected double mInnerRadius;
   protected double mWidth;
   protected final int mTier;
   protected double mMenuGap;
   protected final Group mMenuGroup = new Group();

   /**
    * The graphic which represents the Menu Item's label
    * Could be text, an icon, or another shape
    */
   protected Node mGraphic;

   /**
    * The shape path and its components
    */
   private final Path mShapePath = new Path();
   private final MoveTo mShapeTranslate = new MoveTo();
   private final ArcTo mInnerArc = new ArcTo();
   private final ArcTo mOuterArc = new ArcTo();
   private final LineTo mLine1 = new LineTo();
   private final LineTo mLine2 = new LineTo();

   /**
    * @param pTier - which tier of radial menus this is in.  Should be
    * 1-indexed since index-0 is the cancel button
    * @param pGraphic - the label of this menu item.  Could be text,
    * an image, an icon, a Path, etc
    * @param pActionHandler - the menu item callback.  This callback
    * will be triggered during a mouse release and a touch release
    * @param pProperties - The color & font properties which define
    * mouse over, disabled, and other such graphic properties depending
    * on the state.
    */
   public RadialMenuItemFX(int pTier, Node pGraphic,
         final EventHandler<ActionEvent> pActionHandler,
         FXGraphicStateProperties pProperties)
   {
      mTier = pTier;
      mProperties = pProperties;
      mProperties.getColors().addChangeListener((obj, old, pNew)-> redraw());
      

      mShapePath.getElements().add(mShapeTranslate);
      mShapePath.getElements().add(mInnerArc);
      mShapePath.getElements().add(mLine1);
      mShapePath.getElements().add(mOuterArc);
      mShapePath.getElements().add(mLine2);
      mShapePath.setSmooth(true);
      mShapePath.setFillRule(FillRule.EVEN_ODD);

      mShapePath.fillProperty().bind(mProperties.getColors().getProperty(FILL));
      mShapePath.strokeProperty().bind(mProperties.getColors().getProperty(STROKE));
      mShapePath.setOnMouseEntered(e-> mProperties.setActiveState(MOUSE_OVER));      
      mShapePath.setOnMouseExited(e-> mProperties.setActiveState(NORMAL));
      
      mMenuGroup.getChildren().add(mShapePath);

      mGraphic = pGraphic;
      if (mGraphic != null)
      {
         mMenuGroup.getChildren().add(mGraphic);
         mGraphic.setOnMouseEntered(e-> mProperties.setActiveState(MOUSE_OVER));      
         mGraphic.setOnMouseExited(e-> mProperties.setActiveState(NORMAL));
         if(mGraphic instanceof Text)
         {
            Text text = (Text)mGraphic;
            text.setBoundsType(TextBoundsType.VISUAL);
            text.strokeProperty().bind(mProperties.getColors().getProperty(STROKE));
            text.fillProperty().bind(mProperties.getColors().getProperty(STROKE));
            text.fontProperty().bind(mProperties.getFonts().getProperty(ANY));
         }
      }
   }
   
   /**
    * @param pActionHandler
    */
   protected final void setActionListener(final EventHandler<ActionEvent> pActionHandler)
   {
      if(pActionHandler != null)
      {
         mMenuGroup.addEventHandler(MouseEvent.MOUSE_RELEASED, (event) -> {
            pActionHandler.handle(
                  new ActionEvent(event.getSource(), event.getTarget()));

         });
         mMenuGroup.addEventHandler(TouchEvent.TOUCH_RELEASED, (event) -> {
            pActionHandler.handle(
                  new ActionEvent(event.getSource(), event.getTarget()));

         });
      }
   }
   
   /**
    * Package-level method to update all menu size properties.  Intended to
    * be called from RadialMenuFX and not outside code.
    * 
    * @param pInnerRadius - the menu item arc's inner radius, in pixels
    * @param pWidth - the menu item's specific width/radius, in pixels
    * @param pMenuGap - the gap, margin or offset in between the menu items , in pixels
    * @param pMenuStartAngleRadians - where this menu starts, in radians
    * @param pMenuArcRadians - the arc length of this menu item, in radians
    */
   void setMenuSizeProperties(double pInnerRadius, double pWidth, 
         double pMenuGap, double pMenuStartAngleRadians, double pMenuArcRadians)
   {
      mInnerRadius = pInnerRadius;
      mWidth = pWidth;
      mMenuGap = pMenuGap;
      mMenuArcRadians = pMenuArcRadians;
      mStartAngleRadians = pMenuStartAngleRadians;
      redraw();
   }
   
   /**
    * If this menu item class has been extended, it will do something
    * @param pVisible
    */
   protected void setSubMenuItemsVisible(boolean pVisible)
   {
      // No-op for menu item, this is stubbed so a subclass can implement it
      // without the external menu needing to know about the sub class
   }

   /**
    * Re-calculates and redraws this menu item's shape path and graphic
    * Heavily based off of JavaFX radial menu items, with some corrections
    */
   protected void redraw()
   {
      final double graphicAngle = mStartAngleRadians + (mMenuArcRadians / 2.0);
      final double innerRadius = mInnerRadius + (mTier-1)*(mMenuGap+mWidth);
      final double graphicRadius = innerRadius + mWidth / 2.0;
      final double outerRadius = innerRadius + mWidth;
      double innerStartX = innerRadius * Math.cos(mStartAngleRadians);
      double innerStartY = innerRadius * Math.sin(mStartAngleRadians);
      double innerEndX = innerRadius * Math.cos(mStartAngleRadians + mMenuArcRadians);
      double innerEndY = innerRadius * Math.sin(mStartAngleRadians + mMenuArcRadians);

      double startX = outerRadius * Math.cos(mStartAngleRadians + mMenuArcRadians);
      double startY = outerRadius * Math.sin(mStartAngleRadians + mMenuArcRadians);
      double endX = outerRadius * Math.cos(mStartAngleRadians);
      double endY = outerRadius * Math.sin(mStartAngleRadians);
      double translateX = mMenuGap * Math.cos(mStartAngleRadians + (mMenuArcRadians / 2.0));
      double translateY = mMenuGap * Math.sin(mStartAngleRadians + (mMenuArcRadians / 2.0));

      if (mGraphic != null)
      {
         double graphicX = graphicRadius * Math.cos(graphicAngle)
               - mGraphic.getBoundsInParent().getWidth() / 2.0;
         double graphicY = graphicRadius * Math.sin(graphicAngle)
               + mGraphic.getBoundsInParent().getHeight() / 2.0;
         mGraphic.setTranslateX(graphicX + translateX);
         mGraphic.setTranslateY(graphicY + translateY);

      }
      
      mShapeTranslate.setX(innerStartX + translateX);
      mShapeTranslate.setY(innerStartY + translateY);

      mInnerArc.setX(innerEndX + translateX);
      mInnerArc.setY(innerEndY + translateY);
      mInnerArc.setSweepFlag(true);
      mInnerArc.setRadiusX(innerRadius);
      mInnerArc.setRadiusY(innerRadius);

      mLine1.setX(startX + translateX);
      mLine1.setY(startY + translateY);

      mOuterArc.setX(endX + translateX);
      mOuterArc.setY(endY + translateY);
      mOuterArc.setSweepFlag(false);

      mOuterArc.setRadiusX(outerRadius);
      mOuterArc.setRadiusY(outerRadius);

      mLine2.setX(innerStartX + translateX);
      mLine2.setY(innerStartY + translateY);
   }
   
   public Node getMenuGraphicNode()
   {
      return mMenuGroup;
   }
}