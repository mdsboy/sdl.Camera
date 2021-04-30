package jp.ac.titech.itpro.sdl.camera

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var currentPhotoUri: Uri? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val photoButton = findViewById<Button>(R.id.photo_button)
        photoButton.setOnClickListener { v: View? ->
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val manager = packageManager
            @SuppressLint("QueryPermissionsNeeded")

            val activities = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (activities.isNotEmpty()) {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    currentPhotoUri = FileProvider.getUriForFile(
                            this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            it
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
                    startActivityForResult(intent, REQ_PHOTO)
                }
            } else {
                Toast.makeText(this@MainActivity, R.string.toast_no_activities, Toast.LENGTH_LONG).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        )
    }

    private fun showPhoto() {
        if (currentPhotoUri == null) {
            return
        }
        val photoView = findViewById<ImageView>(R.id.photo_view)
        // photoView.setImageBitmap(photoImage)
        photoView.setImageURI(currentPhotoUri)
    }

    override fun onActivityResult(reqCode: Int, resCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resCode, data)
        if (reqCode == REQ_PHOTO) {
            if (resCode == RESULT_OK) {
                // photoImage = data?.extras?.get("data") as Bitmap
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showPhoto()
    }

    companion object {
        private const val REQ_PHOTO = 1234
    }
}
