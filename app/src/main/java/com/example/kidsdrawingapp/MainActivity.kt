package com.example.kidsdrawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*

class MainActivity : AppCompatActivity() {

    private var mImageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //sets the brush size to 20
        drawing_view.setSizeForBrush(20.toFloat())

        //which color should be drawn at first, [1] because first position was black in activity_main.xml
        //im accessing ll_paint_colors which is a array list which is a linear layout that has all those image buttons, and I'm accessing the as a index position
        mImageButtonCurrentPaint = ll_paint_colors[1] as ImageButton
        //I need to tell my Image Button how it will look after I pressed it
        mImageButtonCurrentPaint!!.setImageDrawable( //!! because it is a nullable
            // getting the xml file
            // once I define the color that is going to be selected, I also want it to be visible on the screen
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        //im calling the showBrushSizeChooserDialog() function
        ib_brush.setOnClickListener { showBrushSizeChooserDialog() }
    }

    // to show brush size selection for changing the brush sizes when drawing
    private fun showBrushSizeChooserDialog(){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size: ")
        val smallBtn = brushDialog.ib_small_brush
        // if user clicks on this button
        // once the user clicked on the small button, the dialog should disappear,
        // but at the same time size for the brush should be changed to 10
        smallBtn.setOnClickListener { drawing_view.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
            }
        val mediumBtn = brushDialog.ib_medium_brush
        // if user clicks on this button
        // once the user clicked on the small button, the dialog should disappear,
        // but at the same time size for the brush should be changed to 20
        mediumBtn.setOnClickListener { drawing_view.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        val largeBtn = brushDialog.ib_large_brush
        // if user clicks on this button
        // once the user clicked on the small button, the dialog should disappear,
        // but at the same time size for the brush should be changed to 30
        largeBtn.setOnClickListener { drawing_view.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }
        //to show the dialog
        brushDialog.show()
        }

        // the View that is going to be passed is a button that was clicked on
        fun paintClicked(view: View){
            //checking if the button that was selected is the current button or not
            if(view !== mImageButtonCurrentPaint){
                //then I want to make the button active(pressed)and of the color that I selected
                //also I need to check if the view that was passed is a type of ImageButton, "as" to convert it to ImageButton
                //after all this I added the paintClicked onClick listener in the activity_main, now I can use this function
                val imageButton = view as ImageButton
                //it will read the tag property of my ImageButton that I clicked on
                val colorTag = imageButton.tag.toString()
                drawing_view.setColor(colorTag)
                //for changing the appearance of the button when it's pressed, and the other one should be unpressed
                //when pressed
                imageButton.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
                )
                //for other buttons
                mImageButtonCurrentPaint!!.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.pallet_normal)
                )
                //I need to make sure that my mImageButtonCurrentPaint is set to view
                mImageButtonCurrentPaint = view
            }
        }

    }
