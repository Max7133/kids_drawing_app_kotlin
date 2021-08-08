package com.example.kidsdrawingapp

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
        ib_brush.setOnClickListener {
            showBrushSizeChooserDialog()
        }

        //gallery button
        ib_gallery.setOnClickListener{
            //User asks for permission, if he already has the permission the he doesn't need to ask for permission
            //so it checks first if user has the permission
            //if he has permission
            if(isReadStorageAllowed()){
                // run out code to get the image from the gallery
            }else {
                requestStoragePermission()
            }
        }
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

    private fun requestStoragePermission(){
        //It will check if I should request the permission or not
        //array often needs to be of type string,that's why I casted to be a string with toString()
        //rationale == reason
        //this could be used when user wants to deny the permission for example
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())){
            Toast.makeText(this,"Need permission to add a Background", Toast.LENGTH_SHORT).show()
        }
        //For requesting the permission
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    //Function which will take care of what should happen once the user gives the permission or doesn't
    //This is a built in function
    //this fun is just for to user to see if he has permission or not
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            //my code here
        //it checks if the req code is the same as my storage permission code
        //did user pressed allow or deny, and then it checks if the user pressed allow
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this@MainActivity, "Permission granted now you can read the storage files.",
            Toast.LENGTH_LONG).show()
            //if the user doesn't give the permission
        }else{
            Toast.makeText(this@MainActivity, "Oops you just denied the permission.",
                Toast.LENGTH_LONG).show()
        }
    }
    // fun that will find out if we have access to storage or not
    private fun isReadStorageAllowed():Boolean{
        //whatever the result will be, it will be stored in this result variable
        val result = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
    //now I return if the result was true or not
        return result == PackageManager.PERMISSION_GRANTED
    }

    companion object{
        private const val STORAGE_PERMISSION_CODE = 1
    }

    }
