package com.flybot.fxwidgets.graph;

import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;

public class FXControllableLineGraph 
<XTYPE,                  // Any type for the x-axis
YTYPE extends Number>   // Most likely doubles 
extends LineChart<XTYPE,YTYPE>
{
  
  public FXControllableLineGraph(Axis<XTYPE> pXAxis, Axis<YTYPE> pYAxis)
  {
    super(pXAxis, pYAxis);
  }
}
