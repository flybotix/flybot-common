package com.flybot.fxwidgets;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class BorderedTitlePane extends StackPane
{
   public BorderedTitlePane(String pTitle, Node pContent)
   {
      Label title = new Label(" " + pTitle + " ");
      title.getStyleClass().add("bordered-titled-title");
      StackPane.setMargin(title, new Insets(0, 25, 0, 0));
      StackPane.setAlignment(title, Pos.TOP_LEFT);
      
      StackPane contentPane = new StackPane();
      pContent.getStyleClass().add("bordered-titled-content");
      contentPane.getChildren().add(pContent);
      
      getStyleClass().add("bordered-titled-border");
      getChildren().addAll(title, contentPane);
   }
}
