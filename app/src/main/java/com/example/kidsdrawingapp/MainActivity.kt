package com.example.kidsdrawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //sets the brush size to 20
        drawing_view.setSizeForBrush(20.toFloat())

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
    }
