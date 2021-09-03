package com.example.kidsdrawingapp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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
                // run our code to get the image from the gallery
                    //this intent is for getting the picture from the gallery
                val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    //starting the activity for result
                startActivityForResult(pickPhotoIntent, GALLERY)
            }else {
                requestStoragePermission()
            }
        }
        //undo button
        ib_undo.setOnClickListener {
            drawing_view.onClickUndo()
        }

        //save button to access something
        ib_save.setOnClickListener {
            if(isReadStorageAllowed()){
                //it will run BitmapAsyncTask, pass in my container bellow, in the background its going to make it in a bitmap
                BitmapAsyncTask(getBitmapFromView(fl_drawing_view_container)).execute()
           //if the user doesn't have access, I want to request permission to get permission to have access to this storage.
            } else {
                requestStoragePermission()
            }
        }
    }
    // to get the gallery result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //for checking if the result was ok
        if(resultCode == Activity.RESULT_OK){
            //checking if the request code of that is going to be the same from our GALLERY
            if(requestCode == GALLERY){
                // try to get data
                try {
                    if(data!!.data != null){
                        iv_background.visibility = View.VISIBLE
                        //because I'm getting the URI from the data
                        iv_background.setImageURI(data.data)
                        //if the data was empty (the user didn't select anything)
                    }else{
                        Toast.makeText(this@MainActivity,"Error in parsing the image or its corrupted", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception) {
                    //this will give me in the log the detail of the error
                    e.printStackTrace()
                }
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

    // gets a bitmap from the view
    private fun getBitmapFromView(view: View) : Bitmap {
        // returns the bitmap(it will take the whole view, height and config or ARGB_8888)
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        // for binding the canvas that is on that view (the thing that user draw)
        val canvas = Canvas(returnedBitmap)
        // also I need to get the background (bitmap is the comb of the 3 - background of the view[image], canvas, colors)
        val bgDrawable = view.background
        // if there is a background
        if(bgDrawable != null){
            bgDrawable.draw(canvas)
            // if there is no background, it draws white onto the canvas
        }else{
            canvas.drawColor(Color.WHITE)
        }
        // draw the canvas on to the view
        view.draw(canvas)

        return returnedBitmap
    }

    //bitmap Async task
    //I want to get a bitmap whenever I create a bitmap Async task, and whenever I execute it, it should inherit from Async task
    //Any, Void, String needs to be passed to this Async task
    private inner class BitmapAsyncTask(val mBitmap: Bitmap): AsyncTask<Any, Void, String>(){
        //progress bar, lateinit it will initialise later on
        private lateinit var mProgressDialog : Dialog
        //at what moment to use showProgressDialog()
        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            // I need to return the String, like I wrote above
            var result = ""
            // I'm checking if the bitmap that was passed to my bitmap async task here at the top here, if that bitmap is not null
            if (mBitmap != null) {
                // when working with streams, I should do try and catch
                try {
                    val bytes = ByteArrayOutputStream()
                    //I'm going to compress my bitmap, that's why im passing ByteArrayOutputStream
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    //Creating a file out of it
                    //I get the external cache directory, then the absolute file returns the absolute form of this abstract path name
                    //the file separator is the system name separator character, then I give it the name, whenever the time has passed since the 1st january 1970
                    //so that each file gets a unique name
                    val f = File(
                        externalCacheDir!!.absoluteFile.toString()
                                + File.separator + "KidsDrawingApp_" + System.currentTimeMillis() / 1000 + ".png"
                    )
                    //creating a file output stream, and passing a fike to it(val f)
                    val fos = FileOutputStream(f)
                    fos.write(bytes.toByteArray())
                    //closing the outputstream because once its open, it will stay open until u close it
                    fos.close()
                    //storing the result
                    result = f.absolutePath
                } catch (e: Exception) {
                    // this result is going to be empty
                    result = ""
                    e.printStackTrace()
                }
            }
            return result
        }

            // I want to inform the user with a Toast that everything has been done
            override fun onPostExecute(result: String?) {
                // when I want to set cancel the prg dialog
                cancelProgressDialog()
                super.onPostExecute(result)
                if (!result!!.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "File saved successfully :$result",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Something went wrong while saving the file.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            // it will allow to show prg bar
            private fun showProgressDialog(){
                // I'm setting the progress dialogue
                mProgressDialog = Dialog(this@MainActivity)
                // I'm setting the content view of prg bar
                mProgressDialog.setContentView(R.layout.dialog_custom_progress)
                // I'm displaying the prg bar by calling show()
                mProgressDialog.show()
            }
                // This will cancel the prg dialog
        private fun cancelProgressDialog(){
            mProgressDialog.dismiss()
        }
        }


    companion object{
        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
    }

    }
