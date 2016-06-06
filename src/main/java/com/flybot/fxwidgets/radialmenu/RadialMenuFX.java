package com.flybot.fxwidgets.radialmenu;

import static com.flybot.fxwidgets.properties.EPropertyType.FILL;
import static com.flybot.fxwidgets.properties.EPropertyType.STROKE;
import static com.flybot.fxwidgets.properties.EState.MOUSE_OVER;
import static com.flybot.fxwidgets.properties.EState.NORMAL;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import com.flybot.fxwidgets.animation.FadeDefault;
import com.flybot.fxwidgets.properties.FXGraphicStateProperties;

public class RadialMenuFX extends Group
{
   protected ObservableList<RadialMenuItemFX> mMenuItems = FXCollections.observableArrayList();
   protected final DoubleProperty mMenuItemStartAngleRadians;
   private final double mInnerRadius;
   private final double mMenuGap;
   
   /** Cancel circle, in the middle of the menu */
   protected Circle mCancelButton = new Circle();
   
   /** Represents the cancel button and its text */
   protected Group mCenterGroup = new Group();
   
   /** Represents the menu items immediately surrounding the center button */
   protected Group mMenuItemGroup = new Group();
   
   /** The radial length of the menu items, such that <br>
    * <code>RadialMenuItemFX.mInnerRadius = mCancelButton.radius <br>
    * RadialMenuItemFX.mOuterRadius = RadialMenuItemFX.mInnerRadius + this.mMenuRadiusLength</code> 
    */
   protected final double mMenuRadiusLength;
   
   private final FadeDefault mFade;
   
   /**
    * Holds the colors which should be applies during mouse-over/etc. 
    */
   private FXGraphicStateProperties mProperties;
      
   /**
    * Constructs a Radial Menu based upon JavaFX. As of 9Sept2014, this is a simple single-tier menu.
    * 
    * Uses default parameters as follows:<br>
    * Initial Angle Radians = 0<br>
    * Inner radius = 32px (good size for touch)<br>
    * Menu Radius Length = 64px (good width for touch)<br>
    * 
    * @param pProperties - a set of properties which represents colors and
    * fonts to set when the object is in a specific state, etc
    */
   public RadialMenuFX(FXGraphicStateProperties pProperties)
   {
      this(-Math.PI/2, 32, 64, 4, pProperties);
   }

   /**
    * Constructs a radial menu based upon JavaFX. As of 9Sept2014, this is a simple single-tier menu.
    * 
    * @param pInitialAngleRadians - where to start building radial menu items
    * @param pInnerRadius - the 'cancel' button's inner radius
    * @param pMenuRadiusLength - the width of each radial menu item
    * @param pOffset - the gap between the menu items
    * @param pProperties - a set of properties which represents colors and
    * fonts to set when the object is in a specific state, etc
    * @param pMenuHideHandler - this listener will get a notification when the menu
    * is requested to be shown and hidden.  This is particularly useful when
    * the menu is embedded in Swing panels/popups
    */
   public RadialMenuFX(final double pInitialAngleRadians, final double pInnerRadius,
         final double pMenuRadiusLength, final double pOffset, 
         FXGraphicStateProperties pProperties)
   {
      mMenuRadiusLength = pMenuRadiusLength;
      mProperties = pProperties;
      mInnerRadius = pInnerRadius;
      mMenuGap = pOffset;
      mFade = new FadeDefault(this);
      
      getChildren().add(mMenuItemGroup);
      getChildren().add(mCenterGroup);
      
      mMenuItemStartAngleRadians = new SimpleDoubleProperty(pInitialAngleRadians);
      mMenuItemStartAngleRadians.addListener((v,p1,p2) -> rebuildMenu());

      mCancelButton.setRadius(mInnerRadius);
      mCancelButton.strokeProperty().bind(mProperties.getColors().getProperty(STROKE));
      mCancelButton.fillProperty().bind(mProperties.getColors().getProperty(FILL));
      
      mCenterGroup.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> mProperties.getColors().setActiveState(MOUSE_OVER));
      mCenterGroup.addEventHandler(MouseEvent.MOUSE_EXITED, e ->  mProperties.getColors().setActiveState(NORMAL));
      mCenterGroup.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> setVisible(false));
      
      visibleProperty().addListener(e->{
         for(RadialMenuItemFX item : mMenuItems)
         {
            item.setSubMenuItemsVisible(false);
            mFade.fadeToVisibility(isVisible());
         }
      });

      mCenterGroup.getChildren().add(mCancelButton);
      mCenterGroup.getChildren().add(createCenterCloseText());
      mCenterGroup.visibleProperty().bind(super.visibleProperty());
      
      mMenuItems.addListener(new ListChangeListener<RadialMenuItemFX>()
      {
         @Override
         public void onChanged(
               javafx.collections.ListChangeListener.Change<? extends RadialMenuItemFX> pC)
         {
            rebuildMenu();
         }
      });
   }
   
   /**
    * Constructs and stylizes the 'X' in the middle of the radial menu
    * @return the 'X' in the middle of the menu
    */
   private Text createCenterCloseText()
   {
      Text text = new Text("X");
      text.setFont(Font.font(null, FontWeight.BOLD, (mInnerRadius)));
      text.setBoundsType(TextBoundsType.VISUAL);
      
      // Set fill and stroke to the 'stroke' so the bold letter stays the proper color
      text.strokeProperty().bind(mProperties.getColors().getProperty(STROKE));
      text.fillProperty().bind(mProperties.getColors().getProperty(STROKE));
      
      // Center the text visually on the circle
      text.relocate(
            -text.getBoundsInLocal().getWidth()/2, 
            -text.getBoundsInLocal().getHeight()/2);
      return text;
   }

   /**
    * Sets the initial angle of the menu.<br>
    * Value of 0 = starting at the right<br>
    * Value of -PI/2 = starting at the top
    * @param pAngleRadians
    */
   public void setInitialAngle(final double pAngleRadians)
   {
      mMenuItemStartAngleRadians.set(pAngleRadians);
   }
   
   /**
    * Adds a menu item object
    * @param pMenuItem
    */
   public void addMenuItem(RadialMenuItemFX pMenuItem)
   {
      // When the cancel button is hit, all menu items will be set invisible
      pMenuItem.getMenuGraphicNode().visibleProperty().bind(visibleProperty());
      
      // Will auto-rebuild the menu just by adding to this list
      mMenuItems.add(pMenuItem);
   }

   /**
    * Adds a menu item to the first ring of this menu
    * @param pGraphic
    * @param pActionHandler - will be called on a MOUSE_RELEASE or TOUCH_RELEASE
    */
   public void addMenuItem(int pTier, Node pGraphic, EventHandler<ActionEvent> pActionHandler)
   {
      RadialMenuItemFX item = new RadialMenuItemFX(pTier, pGraphic, pActionHandler, 
            new FXGraphicStateProperties(mProperties));
      addMenuItem(item);
   }
   
   /**
    * Utility method which calls the addMenuItem(Node...) method.  This is only
    * recommended for rapid prototyping.  In order to control the font, size,
    * and stroke it is recommended to pass a Text object in directly to the other
    * method.
    * 
    * @param pText
    * @param pActionHandler - will be called on a MOUSE_RELEASE or TOUCH_RELEASE
    */
   public void addMenuItem(int pTier, String pText, EventHandler<ActionEvent> pActionHandler)
   {
      Text text = new Text(pText);
      addMenuItem(pTier, text, pActionHandler);
   }

   /**
    * Rebuilds the menu.  Resizes the menu items based upon how many
    * menu items there are
    */
   private void rebuildMenu()
   {
      mMenuItemGroup.getChildren().clear();
      double angleOffset = mMenuItemStartAngleRadians.get();
      double menuArcRadians = Math.PI * 2 / Math.max(mMenuItems.size(), 1d);
      for (final RadialMenuItemFX item : mMenuItems)
      {
         mMenuItemGroup.getChildren().add(item.getMenuGraphicNode());
         item.setMenuSizeProperties(
               mInnerRadius, 
               mInnerRadius + mMenuRadiusLength, 
               mMenuGap, angleOffset, menuArcRadians);
         angleOffset = angleOffset + menuArcRadians;
      }
   }
}
