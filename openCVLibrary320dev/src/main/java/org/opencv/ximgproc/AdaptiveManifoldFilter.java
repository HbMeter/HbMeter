
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ximgproc;

import org.opencv.core.Algorithm;
import org.opencv.core.Mat;

// C++: class AdaptiveManifoldFilter
//javadoc: AdaptiveManifoldFilter
public class AdaptiveManifoldFilter extends Algorithm {

    protected AdaptiveManifoldFilter(long addr) { super(addr); }


    //
    // C++: static Ptr_AdaptiveManifoldFilter create()
    //

    //javadoc: AdaptiveManifoldFilter::create()
    public static AdaptiveManifoldFilter create()
    {
        
        AdaptiveManifoldFilter retVal = new AdaptiveManifoldFilter(create_0());
        
        return retVal;
    }


    //
    // C++:  void collectGarbage()
    //

    //javadoc: AdaptiveManifoldFilter::collectGarbage()
    public  void collectGarbage()
    {
        
        collectGarbage_0(nativeObj);
        
        return;
    }


    //
    // C++:  void filter(Mat src, Mat& dst, Mat joint = Mat())
    //

    //javadoc: AdaptiveManifoldFilter::filter(src, dst, joint)
    public  void filter(Mat src, Mat dst, Mat joint)
    {
        
        filter_0(nativeObj, src.nativeObj, dst.nativeObj, joint.nativeObj);
        
        return;
    }

    //javadoc: AdaptiveManifoldFilter::filter(src, dst)
    public  void filter(Mat src, Mat dst)
    {
        
        filter_1(nativeObj, src.nativeObj, dst.nativeObj);
        
        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++: static Ptr_AdaptiveManifoldFilter create()
    private static native long create_0();

    // C++:  void collectGarbage()
    private static native void collectGarbage_0(long nativeObj);

    // C++:  void filter(Mat src, Mat& dst, Mat joint = Mat())
    private static native void filter_0(long nativeObj, long src_nativeObj, long dst_nativeObj, long joint_nativeObj);
    private static native void filter_1(long nativeObj, long src_nativeObj, long dst_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
