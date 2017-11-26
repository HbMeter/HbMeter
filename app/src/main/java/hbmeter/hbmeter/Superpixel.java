package hbmeter.hbmeter;

import java.util.ArrayList;

/**
 * Created by user on 08/11/2017.
 */

public class Superpixel {
    int label=-1;
    boolean selected=false;
    ArrayList<Integer> coordX =  new ArrayList<Integer>();
    ArrayList<Integer> coordY =  new ArrayList<Integer>();



    Superpixel(int l){
        label=l;
    }

    Superpixel(){
    }

    ArrayList<Integer> getCoordX(){
        return coordX;
    }


    ArrayList<Integer> getCoordY(){
        return coordY;
    }

    boolean isSelected(){
        return selected;
    }

    int getLabel(){
        return label;
    }

    void setLabel(int l){
       label=l;
    }

    void addcoordX(int x){
        coordX.add(x);
    }

    void addcoordY(int y){
        coordY.add(y);
    }
}
