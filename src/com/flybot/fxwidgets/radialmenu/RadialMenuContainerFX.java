package com.flybot.fxwidgets.radialmenu;

import static com.flybot.fxwidgets.properties.EPropertyType.STROKE;

import java.util.LinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Polyline;

import com.flybot.fxwidgets.animation.FadeDefault;
import com.flybot.fxwidgets.properties.FXGraphicStateProperties;

public class RadialMenuContainerFX extends RadialMenuItemFX
{
   private boolean mIsSubMenuVisible = false;
   private ObservableList<RadialMenuItemFX> mSubMenuItems = 
         FXCollections.observableList(new LinkedList<>());
   private final  Polyline mArrow;
   private Group mSubMenuGroup = new Group();
   private final FadeDefault mFade = new FadeDefault(mSubMenuGroup, 1000d, 0d, 1d);
   private final double mMaxArcLengthRadians;
   
   /**
    * 
    * @param pTier - which tier of radial menus this is in.  Should be
    * 1-indexed since index-0 is the cancel button
    * @param pGraphic - the label of this menu item.  Could be text,
    * an image, an icon, a Path, etc
    * @param pProperties - The color & font properties which define
    * mouse over, disabled, and other such graphic properties depending
    * on the state.
    */
   public RadialMenuContainerFX(int pTier, Node pGraphic,
         FXGraphicStateProperties pProperties, double pMaxArcLengthRadians)
   {
      super(pTier, pGraphic, null, pProperties);
      mMaxArcLengthRadians = pMaxArcLengthRadians;
      setActionListener(e -> setSubMenuItemsVisible(!mIsSubMenuVisible));
      mSubMenuItems.addListener(new ListChangeListener<RadialMenuItemFX>(){
         @Override
         public void onChanged(ListChangeListener.Change<? extends RadialMenuItemFX> pChange)
         {
            resizeSubItems();
            redraw();
         }});
      
      mArrow  = new Polyline(-5.0, -5.0, 5.0, 0.0, -5.0, 5.0, -5.0, -5.0);
      mArrow.fillProperty().bind(mProperties.getColors().getProperty(STROKE));
      mArrow.strokeProperty().bind(mProperties.getColors().getProperty(STROKE));
      mMenuGroup.getChildren().add(mSubMenuGroup);
      mMenuGroup.getChildren().add(mArrow);
   }
   
   /**
    * Adds a default sub menu item based upon the input graphic and the event handler.
    * The menu item will adopt the color/font properties of this container
    * @param pGraphic
    * @param pActionHandler
    */
   public void addSubMenuItem(Node pGraphic, EventHandler<ActionEvent> pActionHandler)
   {
      addSubMenuItem(
            new RadialMenuItemFX(mTier+1, pGraphic, pActionHandler, mProperties));
   }

   /**
    * Add a pre-constructed menu item as a sub menu under this container.
    * This menu item could also be its own container, if chosen to be so
    * @param item
    */
   public void addSubMenuItem(final RadialMenuItemFX item)
   {
      mFade.bindVisibility(item.getMenuGraphicNode().visibleProperty());
      mSubMenuGroup.getChildren().add(item.getMenuGraphicNode());
      mSubMenuItems.add(item); // Triggers a redraw
   }

   /**
    * Removes a menu 
    * @param item
    */
   public void removeMenuItem(final RadialMenuItemFX item)
   {
      mSubMenuGroup.getChildren().remove(item);
      mSubMenuItems.remove(item);
   }
   
   /* (non-Javadoc)
    * @see com.lmco.fxwidgets.radialmenu.RadialMenuItemFX#redraw()
    */
   @Override
   protected void redraw()
   {
      super.redraw();
      for(RadialMenuItemFX item : mSubMenuItems)
      {
         item.redraw();
      }
   }

   /**
    * Self Explanatory
    * @param pVisible
    */
   @Override
   protected void setSubMenuItemsVisible(boolean pVisible)
   {
      mIsSubMenuVisible = pVisible;
      mSubMenuGroup.setVisible(pVisible);
      if(pVisible == false) // i.e. if the menu is closing
      {
         for(RadialMenuItemFX item : mSubMenuItems)
         {
            item.setSubMenuItemsVisible(pVisible);
         }
      }
   }
   
   /**
    * Resize each sub menu item based upon max arc length
    */
   private void resizeSubItems()
   {
      double arc = mMaxArcLengthRadians / (mSubMenuItems.size() );
      double start = mStartAngleRadians - mMaxArcLengthRadians/2 + mMenuArcRadians/2;
      for(RadialMenuItemFX i : mSubMenuItems)
      {
         i.setMenuSizeProperties(mInnerRadius, mWidth, mMenuGap, start, arc);
         start += arc;
      }
      setSubMenuItemsVisible(mIsSubMenuVisible);
   }
}
