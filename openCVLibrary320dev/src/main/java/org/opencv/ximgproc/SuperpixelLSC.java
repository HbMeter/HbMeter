
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ximgproc;

import org.opencv.core.Algorithm;
import org.opencv.core.Mat;

// C++: class SuperpixelLSC
//javadoc: SuperpixelLSC
public class SuperpixelLSC extends Algorithm {

    protected SuperpixelLSC(long addr) { super(addr); }


    //
    // C++:  int getNumberOfSuperpixels()
    //

    //javadoc: SuperpixelLSC::getNumberOfSuperpixels()
    public  int getNumberOfSuperpixels()
    {
        
        int retVal = getNumberOfSuperpixels_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void enforceLabelConnectivity(int min_element_size = 20)
    //

    //javadoc: SuperpixelLSC::enforceLabelConnectivity(min_element_size)
    public  void enforceLabelConnectivity(int min_element_size)
    {
        
        enforceLabelConnectivity_0(nativeObj, min_element_size);
        
        return;
    }

    //javadoc: SuperpixelLSC::enforceLabelConnectivity()
    public  void enforceLabelConnectivity()
    {
        
        enforceLabelConnectivity_1(nativeObj);
        
        return;
    }


    //
    // C++:  void getLabelContourMask(Mat& image, bool thick_line = true)
    //

    //javadoc: SuperpixelLSC::getLabelContourMask(image, thick_line)
    public  void getLabelContourMask(Mat image, boolean thick_line)
    {
        
        getLabelContourMask_0(nativeObj, image.nativeObj, thick_line);
        
        return;
    }

    //javadoc: SuperpixelLSC::getLabelContourMask(image)
    public  void getLabelContourMask(Mat image)
    {
        
        getLabelContourMask_1(nativeObj, image.nativeObj);
        
        return;
    }


    //
    // C++:  void getLabels(Mat& labels_out)
    //

    //javadoc: SuperpixelLSC::getLabels(labels_out)
    public  void getLabels(Mat labels_out)
    {
        
        getLabels_0(nativeObj, labels_out.nativeObj);
        
        return;
    }


    //
    // C++:  void iterate(int num_iterations = 10)
    //

    //javadoc: SuperpixelLSC::iterate(num_iterations)
    public  void iterate(int num_iterations)
    {
        
        iterate_0(nativeObj, num_iterations);
        
        return;
    }

    //javadoc: SuperpixelLSC::iterate()
    public  void iterate()
    {
        
        iterate_1(nativeObj);
        
        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  int getNumberOfSuperpixels()
    private static native int getNumberOfSuperpixels_0(long nativeObj);

    // C++:  void enforceLabelConnectivity(int min_element_size = 20)
    private static native void enforceLabelConnectivity_0(long nativeObj, int min_element_size);
    private static native void enforceLabelConnectivity_1(long nativeObj);

    // C++:  void getLabelContourMask(Mat& image, bool thick_line = true)
    private static native void getLabelContourMask_0(long nativeObj, long image_nativeObj, boolean thick_line);
    private static native void getLabelContourMask_1(long nativeObj, long image_nativeObj);

    // C++:  void getLabels(Mat& labels_out)
    private static native void getLabels_0(long nativeObj, long labels_out_nativeObj);

    // C++:  void iterate(int num_iterations = 10)
    private static native void iterate_0(long nativeObj, int num_iterations);
    private static native void iterate_1(long nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
