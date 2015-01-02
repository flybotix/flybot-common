package com.flybot.fxwidgets.properties;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;


public class StateBasedProperties<T, E extends Enum<E>>
{
   private Map<E, Property<T>> mActiveProperties = new HashMap<>();
   private Map<EState, Map<E, T>> mFullPropertyMap = new HashMap<>();
   private final EnumSet<E> mEnumeration;
   private EState mActiveState = EState.NORMAL;
   
   public StateBasedProperties(EnumSet<E> pEnumerations, T pDefault)
   {
      if(pEnumerations == null || pEnumerations.isEmpty())
      {
         throw new IllegalArgumentException(
               "Must be passed a valid enumeration set.  "
               + "If a set is not needed, use EnumSet.of(EProprtyType.ANY)");         
      }
      
      mEnumeration = pEnumerations;
      for(EState EState : EState.values())
      {
         Map<E, T> map = new HashMap<>();
         for(E type : mEnumeration)
         {
            map.put(type, pDefault);
         }
         mFullPropertyMap.put(EState,  map);
      }

      for(E type : mEnumeration)
      {
         mActiveProperties.put(type, new SimpleObjectProperty<T>());
      }
      
      setActiveState(EState.NORMAL);
   }
   
   
   public StateBasedProperties(StateBasedProperties<T, E> pCopy)
   {
      mEnumeration = pCopy.mEnumeration;
      for(EState state : EState.values())
      {
         Map<E, T> map = new HashMap<>();
         for(E type : mEnumeration)
         {
            map.put(type, pCopy.get(state, type));
         }
         mFullPropertyMap.put(state,  map);
      }

      for(E type : mEnumeration)
      {
         mActiveProperties.put(type, new SimpleObjectProperty<T>());
      }
      
      setActiveState(EState.NORMAL);
   }
   
   /**
    * Returns the existing value that will be set for <code>pType</code>
    * when <code>pState</code> becomes active
    * @param pState
    * @param pType
    * @return
    */
   public T get(EState pState, E pType)
   {
      return mFullPropertyMap.get(pState).get(pType);
   }

   /**
    * Sets a property of this FX object for the given EState
    * @param pEStateF
    * @param pType
    * @param pColor
    */
   public void set(EState pState, E pType, T pObject)
   {
      mFullPropertyMap.get(pState).put(pType, pObject);
      setActiveState(mActiveState); // Update active colors
   }
   
   /**
    * Sets all state values for the given type. Useful for
    * updating all default states after construction
    * @param pType
    * @param pObject
    */
   public void set(E pType, T pObject)
   {
      for(EState state : EState.values())
      {
         set(state, pType, pObject);
      }
   }

   /**
    * Sets the paint property based upon the input EState.
    * Both background and foreground paints will be updated
    * @param pState
    */
   public final void setActiveState(EState pState)
   {
      mActiveState = pState;
      for(E aType : mEnumeration)
      {
         T obj = mFullPropertyMap.get(mActiveState).get(aType);
         mActiveProperties.get(aType).setValue(obj);
      }
   }

   /**
    * Adds a listener to all of the EState-based properties -
    * i.e. NOT the entire paint map, but instead the active
    * paints.  The listener will be informed when the active
    * paint changes, as well a visibility or enabled flags.
    * @param pListener
    */
   public void addChangeListener(ChangeListener<T> pListener)
   {
      for(Property<T> aProperty : mActiveProperties.values())
      {
         aProperty.addListener(pListener);
      }
   }
   
   /**
    * @param pType
    * @return the active value for the given type
    */
   public T getActiveValue(E pType)
   {
      return getProperty(pType).getValue();
   }
   
   /**
    * @param pType
    * @return the active property of the given type
    */
   public Property<T> getProperty(E pType)
   {
      return mActiveProperties.get(pType);
   }
}
