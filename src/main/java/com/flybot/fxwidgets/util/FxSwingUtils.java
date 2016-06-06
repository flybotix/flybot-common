package com.flybot.fxwidgets.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import javax.swing.JComponent;
import javax.swing.JWindow;

public class FxSwingUtils
{
   public static final java.awt.Color sSWING_PANEL_DEFAULT_BACKGROUND = new java.awt.Color(0f, 0f, 0f, 0f);
   private static final boolean sCAN_CREATE_TRANSPARENT_POPUPS;
   private static final boolean sCAN_CREATE_SHAPED_POPUPS;
   
   /*
    * Determine transparency.
    * Log all transparency allowances.
    */
   static
   {
      GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      sCAN_CREATE_TRANSPARENT_POPUPS = gd.isWindowTranslucencySupported(
            GraphicsDevice.WindowTranslucency.TRANSLUCENT);
      sCAN_CREATE_SHAPED_POPUPS = gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT);
      
      StringBuilder sb = new StringBuilder("Translucency Support:  ");
      for(GraphicsDevice.WindowTranslucency t : GraphicsDevice.WindowTranslucency.values())
      {
         sb.append(t.name()).append(":");
         sb.append(gd.isWindowTranslucencySupported(t));
         sb.append("  ");
      }
      System.out.println(sb);
   }
   
   /**
    * Creates a JComponent which contains the input JavaFX node.  The returned
    * JComponent will have a transparent background, allowing the JavaFX node
    * to control what is transparent and what is not.
    * @param pFxComponent
    * @return
    */
   public static JComponent createSwingPanelWithFxComponent(final Node pFxComponent)
   {
      final JFXPanel fxpanel = new JFXPanel();
      Platform.runLater(() -> {
         StackPane pane = new StackPane(pFxComponent);
         Scene scene = new Scene(pane);
         scene.setFill(Color.TRANSPARENT);
         fxpanel.setScene(scene);
         Dimension d = convertToDimension(pFxComponent.getLayoutBounds());
         fxpanel.setPreferredSize(d);
         fxpanel.setMinimumSize(d);
      });
      
      return fxpanel;
   }
   
   /**
    * Creates an undecorated popup JWindow which contains the input FX Node.
    * If the platform supports it, this will return a transparent background in the popup. If
    * the platform does not support window transparency, then this method will check to see
    * if the platform supports shaped windows.  If it does, then this method will apply
    * a shape to the popup window.<br><br>
    * This is only useful if you know the approximate shape that the FX component will take.
    * If the shape is a standard rectangle, use the other createPopupWithFxComponent() method.
    * For example, a RadialMenuFX component will need an Ellipse2D at the correct radius
    * to 'fake' transparency around its edges
    * 
    * @param pParentFrame
    * @param pFxComponent
    * @param pApproximateShape
    * @param pShapeBackground
    * @return
    */
   public static JWindow createPopupWithFxComponent(final Frame pParentFrame, final Node pFxComponent, 
         java.awt.Shape pApproximateShape, java.awt.Color pShapeBackground)
   {
      JWindow popup = createPopupWithFxComponent(pParentFrame, pFxComponent);
      if(!sCAN_CREATE_TRANSPARENT_POPUPS && sCAN_CREATE_SHAPED_POPUPS // i.e. raw translucent didn't work
            && pApproximateShape != null && pShapeBackground != null) 
      {
         popup.setShape(pApproximateShape);
         popup.setBackground(pShapeBackground);
      }
      return popup;
   }
   
   /**
    * Creates an undecorated popup JWindow which contains the input FX Node.
    * If the platform supports it, this will return a transparent background in the popup. If
    * the platform does not support window transparency, then the background will be unchanged
    * from the default Swing background.
    * 
    * @param pParentFrame
    * @param pFxComponent
    * @return
    */
   public static JWindow createPopupWithFxComponent(final Frame pParentFrame, final Node pFxComponent)
   {
      // bind visibility of the component and the popup
      @SuppressWarnings("serial")
      JWindow popup = new JWindow(pParentFrame)
      {
         @Override
         public void setVisible(boolean pVisible)
         {
            super.setVisible(pVisible);
            pFxComponent.visibleProperty().set(pVisible);
         }
      };
      pFxComponent.visibleProperty().addListener(event -> {
         popup.setVisible(pFxComponent.isVisible());
      });
      
      // create the swing component and add it to the popup
      JComponent component = createSwingPanelWithFxComponent(pFxComponent);
      popup.getContentPane().setLayout(new BorderLayout());
      popup.getContentPane().setBackground(sSWING_PANEL_DEFAULT_BACKGROUND);
      popup.getContentPane().add(component, BorderLayout.CENTER);
      popup.setSize(getDimensionOfComponent(pFxComponent));
      
      // Need to figure out how to get hardware acceleration to work.  Is it
      // simply a matter of installing the proper video driver?
      if(sCAN_CREATE_TRANSPARENT_POPUPS)
      {
         popup.setOpacity(0f);
      }
      
      return popup;
   }
   
   /**
    * Returns the dimensions of the component's layout bounds
    * @param pFxComponent
    * @return
    */
   public static Dimension getDimensionOfComponent(final Node pFxComponent)
   {
      return convertToDimension(pFxComponent.getLayoutBounds());
   }
   
   /**
    * Converts a Bound to a Dimension.  Note that some information will be
    * lost (depth, x, y, etc) yet the width and height will be preserved
    * @param pBound
    * @return
    */
   public static Dimension convertToDimension(Bounds pBound)
   {
      Dimension result = new Dimension();
      result.setSize(pBound.getWidth(), pBound.getHeight());
      return result;
   }
}
