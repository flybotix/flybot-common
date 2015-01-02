package com.flybot.fxwidgets.graph;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import com.flybot.util.lang.IUpdate;

public class FXControlledGraphPanel 
<KEY extends com.flybot.util.lang.IDisplayable> // Each series in this graph needs a representation
extends VBox
{
  private final FXControllableLineGraph<Number, Number> mGraph;

  private final Map<KEY, Series<Number, Number>> mSeries = new HashMap<>();
  private Map<KEY, ToggleButton> mToggles = new HashMap<>();
  private Map<KEY, DoubleProperty> mCursorValues = new HashMap<>();
  private Map<KEY, HBox> mLabelPanes = new HashMap<>();
  private static final DecimalFormat sFORMAT = new DecimalFormat("0.000");

  private Slider mCursorSlider = new Slider(0d, 2.5d, 0d);
  private Series<Number, Number> mCursorSeries = new Series<>();
  private double mCursorX = 0d;
  private double mCursorY = 0d;
  
  private final DoubleProperty mMaxYAxisProperty;
  
  public FXControlledGraphPanel(
      FXControllableLineGraph<Number, Number> pGraph
      ,KEY[] pEnums // Could do a generic Set, yet order would be different every time
      ,DoubleProperty pMaxXAxisProperty
      ,DoubleProperty pMaxYAxisProperty)
  {
    mGraph = pGraph;
    mMaxYAxisProperty = pMaxYAxisProperty;
    HBox upper = new HBox(20d);
    HBox lower = new HBox(20d);
    upper.setAlignment(Pos.CENTER);
    lower.setAlignment(Pos.CENTER);

    mCursorSeries.setName("Cursor");
    mGraph.getData().add(mCursorSeries);
    
    for(int i = 0; i < pEnums.length; i++)
    {
      KEY k = pEnums[i];
      String label = k.getLabel();
      Series<Number, Number> series = new Series<>();
      series.setName(label);
      mSeries.put(k, series);
      
      ToggleButton b = new ToggleButton(label);
      mToggles.put(k, b);

      b.selectedProperty().addListener(e->{
        if(b.isSelected()){
          mGraph.getData().add(series);
        } else {
          mGraph.getData().remove(series);
        }
      });
      b.selectedProperty().set(true); // Also adds the series to the graph display
      
      HBox labelbox = new HBox(2d);
      Label valuelabel = new Label("0.000");
      labelbox.getChildren().add(new Label(label + ":"));
      labelbox.getChildren().add(valuelabel);
      mLabelPanes.put(k, labelbox);
      mCursorValues.put(k, new SimpleDoubleProperty());
      valuelabel.textProperty().bindBidirectional(mCursorValues.get(k), sFORMAT);
      
      upper.getChildren().add(mToggles.get(k));
      lower.getChildren().add(mLabelPanes.get(k));
    }
    
    mGraph.createSymbolsProperty().set(false);
    mCursorSlider.maxProperty().bind(pMaxXAxisProperty);
    mCursorSlider.valueProperty().addListener((observable, oldV, newV)->{
      mCursorY = 0d;
      mSeries.keySet()./*parallelStream().*/forEach(k->{
        List<Data<Number, Number>> data = mSeries.get(k).getData();
        Iterator<Data<Number, Number>> it = data.iterator();
        while(it.hasNext())
        {
          Data<Number, Number> d = it.next();
          if(d.getXValue().doubleValue() > newV.doubleValue())
          {
            mCursorValues.get(k).set(d.getYValue().doubleValue());
            mCursorX = d.getXValue().doubleValue();
            if(mGraph.getData().contains(mSeries.get(k)))
            {
              mCursorY = Math.max(mCursorY, d.getYValue().doubleValue());              
            }
            break;
          }
        }
      });

      updateCursor();
    });
    mCursorSlider.valueProperty().set(0d);
    mMaxYAxisProperty.addListener(c->updateCursor());
    
    getChildren().add(upper);
    getChildren().add(mGraph);
    getChildren().add(mCursorSlider);
    getChildren().add(lower);
    VBox.setVgrow(mGraph, Priority.ALWAYS);
  }
  
  /**
   * Does any preparation to the chart prior to an update, then restores
   * the visibility parameters to the chart after the update.  The caller
   * implement the INotify interface rather than updating the series directly.
   * @param pUpdater
   */
  public void updateDataSafely(IUpdate<Map<KEY, Series<Number, Number>>> pUpdater)
  {
    Map<KEY, Boolean> visibleStates =new HashMap<>();
    
    // PRE UPDATES - Store metadata, set series visibility to safe values so the
    // underlying chart reference is not null
    for(KEY k : mToggles.keySet())
    {
      visibleStates.put(k, mToggles.get(k).isSelected());
      mToggles.get(k).setSelected(true);
    }
    pUpdater.update(mSeries);
    
    // POST UPDATES - Restore metadata
    for(KEY k : visibleStates.keySet())
    {
      mToggles.get(k).setSelected(visibleStates.get(k));
    }
  }
  
  private void updateCursor()
  {
    mCursorSeries.getData().clear();
    mCursorSeries.getData().add(new Data<>(mCursorX, 0d));
    mCursorSeries.getData().add(new Data<>(mCursorX, mCursorY));
  }
}
