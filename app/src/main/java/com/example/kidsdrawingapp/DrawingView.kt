package com.example.kidsdrawingapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

// I'm using this class as a view only, which will display in the main activity
// I inherit from class View
@SuppressLint("NewApi")
class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    // CustomPath is a type which is a nested class that I have created
    private var mDrawPath : CustomPath? = null
    // For adding bitmap
    private var mCanvasBitmap: Bitmap? = null
    // Paint, style, color
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    // How thick brush size is
    private var mBrushSize: Float = 0.toFloat()
    // Standard color to draw
    private var color = Color.BLACK
    // the blank canvas
    private var canvas: Canvas? = null
    // this is for drawing line to stay on the screen and not disappear
    private val mPaths = ArrayList<CustomPath>()

    // this will run this function
    init {
        setUpDrawing()
    }

    // setting the Null variables from the top to be not Null
    // this settings will be like the default values when u start the app
    private fun setUpDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint!!.color = color // if I won't paint it's still null so that's why !!
        mDrawPaint!!.style = Paint.Style.STROKE // STROKE a line style
        // how the beginning and the end of the STROKE should be
        // because I use STROKE I also set up the Cap
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        // setting up Canvas Paint
        mCanvasPaint = Paint(Paint.DITHER_FLAG) // Dither = Shake = дрожь
        // setting brush size
        // mBrushSize = 20.toFloat() // I've set it later in the MainActivity
    }

    //onSizeChanged exists in the View class, and my DrawingView class inherits from View class, so I can use it's members and override them here
    //to display my canvas
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888) // ARGB - the amount of colors I want to use
        // using this bitmap as the canvas
        // setting up the mCanvasBitmap
        canvas = Canvas(mCanvasBitmap!!)
    }
    //for drawing something on the canvas
    // Change Canvas to Canvas? if fails
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //setting up canvas for drawing, spot where to start drawing
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)

        // so the lines won't disappear after user draws
        for(path in mPaths){
            //setting up the stroke width that I want to draw with, I set the width before in mBrushSize, but I need to set how thick the paint should be
            mDrawPaint!!.strokeWidth = path.brushThickness
            //setting the color of the CustomPath class
            mDrawPaint!!.color = path.color // mDrawPath is a nullable
            canvas.drawPath(path, mDrawPaint!!)
        }

        if(!mDrawPath!!.isEmpty){ // if its not empty,then draw
            //setting up the stroke width that I want to draw with, I set the width before in mBrushSize, but I need to set how thick the paint should be
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            //setting the color of the CustomPath class
            mDrawPaint!!.color = mDrawPath!!.color // mDrawPath is a nullable
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }
    // what should happen when I draw, I want to draw when touching the screen
    // I need to fill mDrawPath with a path that should be draw, because otherwise onDraw function has nothing to do because mDrawPath is going to be empty
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x // storing the x value of where it was touched
        val touchY = event.y
        //multiple actions that can happen, drag down, up, let go of finger
        when(event.action){
            MotionEvent.ACTION_DOWN ->{
                mDrawPath!!.color = color // to be correct color
                mDrawPath!!.brushThickness = mBrushSize // how thick the path is

                mDrawPath!!.reset() // clear any lines from the path
                mDrawPath!!.moveTo(touchX, touchY)
            }
            //what should happen when user drags over the screen
            MotionEvent.ACTION_MOVE -> {
                mDrawPath!!.lineTo(touchX, touchY)
            }
            //what should happen when user releases the touch
            MotionEvent.ACTION_UP ->{
                mPaths.add(mDrawPath!!) // add the mDrawPath to mPath and draw it in onDraw() on top
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false // all the other motion events return false
        }
        //Invalidate the whole view, if the view is visible
        invalidate()
        return true // if none of the else cases is the case then return true
    }

    // sets the size for brush
    fun setSizeForBrush(newSize: Float){
        //applyDimension() the app will know which screen size it's drawing on and depending on that it will adjust the size of the paint brush
        //for instance 20px on one screen will look different then 20px on another screen(big and small screen example)
        //and TypedValue has the method for that applyDimension()
        //for applyDimension() I need 3 things
        //1.unit = COMPLEX_UNIT_DIP
        //2.value = newSize (to apply the unit to)
        //3.metrics = resources.displayMetrics
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize // to use this brush size
    }

    //this will set the a specific color for the paint brush
    //I'm passing the color as a string, because I can use them that way from colors.xml
    fun setColor(newColor: String){
        //parseColor will parse the color string, and return the corresponding color-int #RRGGBB, #AARRGGBB
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color // no color is of type color
    }

    //it will be usable only within my DrawingView class, and I want to have access to it's variables
    // type Path, I inherit from class Path (android.graphics)
    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path()

    }


