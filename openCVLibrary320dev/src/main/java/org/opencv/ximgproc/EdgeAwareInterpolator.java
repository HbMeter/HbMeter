
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ximgproc;



// C++: class EdgeAwareInterpolator
//javadoc: EdgeAwareInterpolator
public class EdgeAwareInterpolator extends SparseMatchInterpolator {

    protected EdgeAwareInterpolator(long addr) { super(addr); }


    //
    // C++:  bool getUsePostProcessing()
    //

    //javadoc: EdgeAwareInterpolator::getUsePostProcessing()
    public  boolean getUsePostProcessing()
    {
        
        boolean retVal = getUsePostProcessing_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  float getFGSLambda()
    //

    //javadoc: EdgeAwareInterpolator::getFGSLambda()
    public  float getFGSLambda()
    {
        
        float retVal = getFGSLambda_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  float getFGSSigma()
    //

    //javadoc: EdgeAwareInterpolator::getFGSSigma()
    public  float getFGSSigma()
    {
        
        float retVal = getFGSSigma_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  float getLambda()
    //

    //javadoc: EdgeAwareInterpolator::getLambda()
    public  float getLambda()
    {
        
        float retVal = getLambda_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  float getSigma()
    //

    //javadoc: EdgeAwareInterpolator::getSigma()
    public  float getSigma()
    {
        
        float retVal = getSigma_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  int getK()
    //

    //javadoc: EdgeAwareInterpolator::getK()
    public  int getK()
    {
        
        int retVal = getK_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void setFGSLambda(float _lambda)
    //

    //javadoc: EdgeAwareInterpolator::setFGSLambda(_lambda)
    public  void setFGSLambda(float _lambda)
    {
        
        setFGSLambda_0(nativeObj, _lambda);
        
        return;
    }


    //
    // C++:  void setFGSSigma(float _sigma)
    //

    //javadoc: EdgeAwareInterpolator::setFGSSigma(_sigma)
    public  void setFGSSigma(float _sigma)
    {
        
        setFGSSigma_0(nativeObj, _sigma);
        
        return;
    }


    //
    // C++:  void setK(int _k)
    //

    //javadoc: EdgeAwareInterpolator::setK(_k)
    public  void setK(int _k)
    {
        
        setK_0(nativeObj, _k);
        
        return;
    }


    //
    // C++:  void setLambda(float _lambda)
    //

    //javadoc: EdgeAwareInterpolator::setLambda(_lambda)
    public  void setLambda(float _lambda)
    {
        
        setLambda_0(nativeObj, _lambda);
        
        return;
    }


    //
    // C++:  void setSigma(float _sigma)
    //

    //javadoc: EdgeAwareInterpolator::setSigma(_sigma)
    public  void setSigma(float _sigma)
    {
        
        setSigma_0(nativeObj, _sigma);
        
        return;
    }


    //
    // C++:  void setUsePostProcessing(bool _use_post_proc)
    //

    //javadoc: EdgeAwareInterpolator::setUsePostProcessing(_use_post_proc)
    public  void setUsePostProcessing(boolean _use_post_proc)
    {
        
        setUsePostProcessing_0(nativeObj, _use_post_proc);
        
        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  bool getUsePostProcessing()
    private static native boolean getUsePostProcessing_0(long nativeObj);

    // C++:  float getFGSLambda()
    private static native float getFGSLambda_0(long nativeObj);

    // C++:  float getFGSSigma()
    private static native float getFGSSigma_0(long nativeObj);

    // C++:  float getLambda()
    private static native float getLambda_0(long nativeObj);

    // C++:  float getSigma()
    private static native float getSigma_0(long nativeObj);

    // C++:  int getK()
    private static native int getK_0(long nativeObj);

    // C++:  void setFGSLambda(float _lambda)
    private static native void setFGSLambda_0(long nativeObj, float _lambda);

    // C++:  void setFGSSigma(float _sigma)
    private static native void setFGSSigma_0(long nativeObj, float _sigma);

    // C++:  void setK(int _k)
    private static native void setK_0(long nativeObj, int _k);

    // C++:  void setLambda(float _lambda)
    private static native void setLambda_0(long nativeObj, float _lambda);

    // C++:  void setSigma(float _sigma)
    private static native void setSigma_0(long nativeObj, float _sigma);

    // C++:  void setUsePostProcessing(bool _use_post_proc)
    private static native void setUsePostProcessing_0(long nativeObj, boolean _use_post_proc);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
