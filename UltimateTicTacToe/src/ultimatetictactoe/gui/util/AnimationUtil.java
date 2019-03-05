/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.gui.util;

import java.util.List;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 *
 * @author Kiddo
 */
public class AnimationUtil {
    
    public static String inactiveElementColor = "0xb4b4b4";
    public static String crossColor = "0xff2989";
    public static String circleColor = "0x4deee9";

    public static TranslateTransition createHorizontalSlide(int startingPosition, int endingPosition, Node element)
    {
        int slideDuration = 150;     
        TranslateTransition translate = new TranslateTransition(Duration.millis(slideDuration), element);
        translate.setFromX(startingPosition);
        translate.setToX(endingPosition);       
        return translate;
    }
    
    public static ParallelTransition createShowCrossAnimation(List<Node> elements)
    {
        Color inactiveColor = Color.web(inactiveElementColor);
        Color activeColor = Color.web(crossColor);
        return createCrossAnimation(0.05, inactiveColor, activeColor, elements); 
    }
    
    public static ParallelTransition createHideCrossAnimation(List<Node> elements)
    {
        Color inactiveColor = Color.web(inactiveElementColor);
        Color activeColor = Color.web(crossColor);
        return createCrossAnimation(-0.05, activeColor, inactiveColor, elements); 
    }
    
    private static ParallelTransition createCrossAnimation(double setScale, Color startingColor, Color endingColor, List<Node> elements)
    {
        ParallelTransition transition = new ParallelTransition();
        for(Node e: elements)
        {
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), e);
        scale.setByX(setScale);
        scale.setByY(setScale);
        FillTransition fill = new FillTransition(Duration.millis(150), (Shape) e, startingColor, endingColor);
        transition.getChildren().addAll(scale,fill);
        }
        return transition;
    }
    
    public static ParallelTransition createShowCircleAnimation(Circle activeElement, Circle backgroundElement)
    {
        Color inactiveColor = Color.web(inactiveElementColor);
        Color activeColor = Color.web(circleColor);
        return createCircleAnimation(0.3, inactiveColor, activeColor, activeElement, backgroundElement);
    }
    
    public static ParallelTransition createHideCircleAnimation(Circle activeElement, Circle backgroundElement)
    {
        Color inactiveColor = Color.web(inactiveElementColor);
        Color activeColor = Color.web(circleColor);
        return createCircleAnimation(-0.3, activeColor, inactiveColor, activeElement, backgroundElement);
    }
    
    private static ParallelTransition createCircleAnimation(double setScale, Color startingColor, Color endingColor, Circle activeElement, Circle backgroundElement)
    {
        ParallelTransition transition = new ParallelTransition();
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), activeElement);
        scale.setByX(setScale);
        scale.setByY(setScale);
        ScaleTransition scale2 = new ScaleTransition(Duration.millis(150), backgroundElement);
        scale2.setByX(setScale);
        scale2.setByY(setScale);
        FillTransition fill = new FillTransition(Duration.millis(150), (Shape) activeElement, startingColor, endingColor);
        transition.getChildren().addAll(scale,fill,scale2);
        
        return transition;
    }
    
    public static ParallelTransition createFieldAnimation(ImageView field)
    {
        ParallelTransition transition = new ParallelTransition();
        ScaleTransition scale = new ScaleTransition(Duration.millis(130), field);
        scale.setFromX(0.3);
        scale.setFromY(0.3);
        scale.setByX(0.5);
        scale.setByY(0.5);
        scale.play();
        transition.getChildren().addAll(scale);
        return transition;
    }
}
