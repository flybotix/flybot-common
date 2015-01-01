package com.flybot.util.omi;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class CardPane <KEY> extends StackPane
{
   private Map<KEY, Node> mMiddlePanes = new HashMap<>();
   
   public void addPane(KEY pKey, Node pNode)
   {
      mMiddlePanes.put(pKey, pNode);
      getChildren().add(pNode);
      pNode.setVisible(false);
   }
   
   public Node removePane(KEY pKey)
   {
      Node node = mMiddlePanes.remove(pKey);
      if(node != null)
      {
         getChildren().remove(node);
      }
      return node;
   }
   
   public void activate(KEY pKey)
   {
      for(Node n : mMiddlePanes.values())
      {
         n.setVisible(false);
      }
      Node n = mMiddlePanes.get(pKey);
      if(n != null)
      {
         n.setVisible(true);
      }
   }
}
