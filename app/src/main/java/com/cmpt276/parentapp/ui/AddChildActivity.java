package com.cmpt276.parentapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildManager;
import com.cmpt276.parentapp.model.FlipHistoryManager;
import com.cmpt276.parentapp.model.PrefConfig;
//import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Add Child Activity - This activity lets you add a new child to the list.
 * Or edit an existing child in the list.
 * The user sets the name of the child but has a restriction to it
 * - the name of the new/edit child cannot be empty
 *
 * The link to add a picture from the phone
 * https://medium.com/@hasangi/capture-image-or-choose-from-gallery-photos-implementation-for-android-a5ca59bc6883
 */
public class AddChildActivity extends AppCompatActivity {

    private static final String EXTRA_FOR_INDEX =
            "com.cmpt276.parentapp.ui.AddChildActivity - the index";
    private static final int DEFAULT_VALUE_FOR_ADD_CHILD_FOR_INTENT = -1;

    private EditText childNameInput;
    private Button addChildButton;
    private ImageView childImage;
    private FloatingActionButton fabEditChildImage;

    private ChildManager manager;
    private boolean addChild;
    private int positionForEditChild;

    private String initialString = "";

    private final String[] IMAGE_OPTIONS = {"Take Photo", "Choose from Gallery", "Cancel"};

    private final int REQUEST_READ_EXTERNAL_STORAGE = 111;
    private final int REQUEST_CAMERA = 222;
    private final int REQUEST_WRITE_EXTERNAL_STORAGE = 333;
    private Uri imageUri, photoUri;
    private String stringPath;
    private Intent intentData;
    private File myFilesDir;

    private File photoFile = null;
    private String mCurrentPhotoPath = "";

    Child child;

    private final int REQUEST_CODE_FOR_TAKE_PHOTO = 10;
    private final int REQUEST_CODE_FOR_CHOOSE_FROM_GALLERY = 20;

    public static Intent makeIntentForAddChild(Context context) {
        return makeIntentForEditChild(context, DEFAULT_VALUE_FOR_ADD_CHILD_FOR_INTENT);
    }

    public static Intent makeIntentForEditChild(Context context, int index) {
        Intent intent = new Intent(context, AddChildActivity.class);
        intent.putExtra(EXTRA_FOR_INDEX, index);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        // instantiating the manager
        manager = ChildManager.getInstance(AddChildActivity.this);

        //--------------------------------
        /*myFilesDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.project/files");
        myFilesDir.mkdirs();*/
        //--------------------------------

        extractDataFromIntent();
        setUpChild();
        setUpInitialString();
        setupAddChildButton();
        setUpEditTextChildName();
        setUpChildImage();
        setUpToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu
        if (addChild) {
            getMenuInflater().inflate(R.menu.menu_add_child, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_edit_child, menu);
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_child_button:
                addOrDiscardChildName();
                return true;

            case R.id.action_edit_child_button:
                editChildName();
                return true;

            case R.id.action_delete_child_button:
                deleteChild();
                return true;

            case android.R.id.home: // up button
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (changeHappened()) {
            AlertDialog.Builder builder = getAlertDialogBox();
            builder.setMessage(
                    addChild ?
                            getString(R.string.warning_change_happened_for_add_child) :
                            getString(R.string.warning_change_happened_for_edit_child)
            );

            builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> finish());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } else {
            finish();
        }
    }

    private void setUpChild() {
        if (addChild) {
            child = new Child();
        }
        else {
            child = manager.getChild(positionForEditChild);
        }
    }

    private Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) {

        // Detect Rotation
        int rotation = getRotation(context, selectedImage);
        if (rotation != 0){
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        }
        else {
            return img;
        }
    }

    private int getRotation(Context context, Uri selectedImage) {
        int rotation = 0;
        ContentResolver content = context.getContentResolver();

        Cursor mediaCursor = content.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { "orientation", "date_added" },
                null, null, "date_added desc");

        if (mediaCursor != null && mediaCursor.getCount() != 0) {
            while (mediaCursor.moveToNext()) {
                rotation = mediaCursor.getInt(0);
                break;
            }
        }
        mediaCursor.close();
        return rotation;
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        positionForEditChild = intent.getIntExtra(EXTRA_FOR_INDEX, 0);
        addChild = positionForEditChild < 0;
    }

    private void setUpEditTextChildName() {
        childNameInput = findViewById(R.id.child_name_edit_text);
        childNameInput.setText(initialString);
        int deviceWidth = getResources().getDisplayMetrics().widthPixels;
        childNameInput.setTextSize((deviceWidth / 25f));

    }

    private void setUpChildImage() {
        childImage = findViewById(R.id.image_view_child_image);
        // if the user is editing an existing child and has specified an image for the child,
        // then show that image on the screen
        if (!addChild && child.getChildImageBitmap() != null) {
            childImage.setImageBitmap(child.getChildImageBitmap());
        }

        fabEditChildImage = findViewById(R.id.fab_edit_child_image);

        fabEditChildImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();

                //selectImage();
                //ImagePicker.Companion.with(AddChildActivity.this).start();
                        /*.crop()                 //Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();*/

            }
        });
    }

    private void selectImage(){

        AlertDialog.Builder builder = new AlertDialog.Builder(AddChildActivity.this);
        builder.setTitle("Choose picture");Log.e("TAG", "Reached the select image");
        builder.setItems(IMAGE_OPTIONS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (IMAGE_OPTIONS[item].equals(getString(R.string.take_photo))){
                    captureImage();
                }
                else if (IMAGE_OPTIONS[item].equals(getString(R.string.choose_from_gallery))) {
                    chooseImageFromGallery();
                }
                else if (IMAGE_OPTIONS[item].equals(getString(R.string.cancel))) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(AddChildActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // then ask the permission from the user
            // todo: make separate columns for the following lines as it asks two permission in one single thing
            ActivityCompat.requestPermissions(AddChildActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
        }
        else {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePicture.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                try {
                    photoFile = createImageFile();
                    Toast.makeText(AddChildActivity.this, photoFile.getAbsolutePath() + "", Toast.LENGTH_SHORT).show();

                    // Continue only if the file was successfully created
                    if (photoFile != null) {
                        photoUri = FileProvider.getUriForFile(AddChildActivity.this, "com.cmpt276.parentapp", photoFile);
                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takePicture, REQUEST_CODE_FOR_TAKE_PHOTO);
                    }
                }
                catch (IOException e) {
                    // Error occurred
                    Toast.makeText(AddChildActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",   // suffix
                storageDir     // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /*private void captureImage() {
        // TODO: check for the permission for the camera
        // if we do not have permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // then ask the permission from the user
            ActivityCompat.requestPermissions(AddChildActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }

                    */
    /*int PERMISSION_REQUEST_CODE = 1;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED) {

                            Log.d("permission", "permission denied to SEND_SMS - requesting it");
                            Toast.makeText(AddChildActivity.this, "Requesting permission", Toast.LENGTH_SHORT).show();
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

                            requestPermissions(permissions, PERMISSION_REQUEST_CODE);

                        }
                    }*/
    /*

        //---------------------------------------------
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE_FOR_TAKE_PHOTO);
        //---------------------------------------------
    }*/

    private void chooseImageFromGallery() {
        // if we do not have permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // then ask the permission from the user
            ActivityCompat.requestPermissions(AddChildActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }

        // todo: might need to put the code below in the else block
        // permission has been granted
        Intent pickPhoto = new Intent(Intent.ACTION_PICK);
        pickPhoto.setType("image/*");
        startActivityForResult(pickPhoto, REQUEST_CODE_FOR_CHOOSE_FROM_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 11) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
            else {
                Toast.makeText(AddChildActivity.this, "You need to grant camera permission to take photo of the child", Toast.LENGTH_LONG).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private int getOrientation(Context context, Uri selectedImage) {
        int orientation = 0;
        final String[] projection = new String[]{MediaStore.Images.Media.ORIENTATION};
        final Cursor cursor = context.getContentResolver().query(selectedImage, projection, null, null, null);
        if(cursor != null) {
            final int orientationColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
            if(cursor.moveToFirst()) {
                orientation = cursor.isNull(orientationColumnIndex) ? 0 : cursor.getInt(orientationColumnIndex);
            }
            cursor.close();
        }
        return orientation;
    }

    private Bitmap rotateImage(Context context, Bitmap bitmapImg, Uri uriImg){

        int orientation = getOrientation(context, uriImg);
        if (orientation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            Bitmap rotatedImg = Bitmap.createBitmap(bitmapImg, 0, 0, bitmapImg.getWidth(), bitmapImg.getHeight(), matrix, true);
            bitmapImg.recycle();
            return rotatedImg;
        }

        return bitmapImg;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_CODE_FOR_TAKE_PHOTO:
                    /*if (resultCode == RESULT_OK) {

                        try {
                            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                            thumbnail = rotateImage(AddChildActivity.this, thumbnail, imageUri);

                            child.setChildImageBitmap(thumbnail);

                            childImage.setImageBitmap(thumbnail);
                            //String imageUrl = getRealPathFromURI(imageUri);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Log.e("TAG", "Reached here");
                    }*/

                    /*Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    childImage.setImageBitmap(imageBitmap);*/

                    if (data != null) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                        //Uri imageUriFromCamera = data.getData();
                        //myBitmap = rotateImage(AddChildActivity.this, myBitmap, imageUriFromCamera);
                        //myBitmap = rotateImageIfRequired(AddChildActivity.this, myBitmap, photoUri);

                        childImage.setImageBitmap(myBitmap);
                    }
                    break;

                case REQUEST_CODE_FOR_CHOOSE_FROM_GALLERY:

                    if (resultCode == RESULT_OK && data != null) {

                        Uri selectedImageUri = data.getData();
                        try {
                            InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                            Bitmap selectedImageBitmap = BitmapFactory.decodeStream(imageStream);

                            //selectedImageBitmap = rotateImageIfRequired(AddChildActivity.this, selectedImageBitmap, selectedImageUri);
                            selectedImageBitmap = rotateImage(AddChildActivity.this, selectedImageBitmap, selectedImageUri);

                            child.setChildImageBitmap(selectedImageBitmap);
                            childImage.setImageBitmap(selectedImageBitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(AddChildActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }


                    }
                    break;

                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(
                addChild ?
                        getString(R.string.add_child_activity_toolbar_label) :
                        getString(R.string.edit_child_activity_toolbar_label)
        );

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupAddChildButton() {
        addChildButton = findViewById(R.id.add_child_button);
        addChildButton.setText(
                addChild ?
                        getString(R.string.add_child_button_text) :
                        getString(R.string.edit_child_button_text)
        );

        if (addChild) {
            addChildButton.setOnClickListener(view -> addOrDiscardChildName());
        } else {
            addChildButton.setOnClickListener(view -> editChildName());
        }
    }

    private void editChildName() {
        if (!childNameIsEmpty()) {
            if (changeHappened()) {
                // ----------change happened--------------
                // set alert dialog box that confirms if the user really wants to change the name
                AlertDialog.Builder builder = getAlertDialogBox();
                builder.setMessage(getString(
                        R.string.confirm_edit_child_dialog_box_message,
                        initialString,
                        childNameInput.getText()
                ));
                builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {

                    FlipHistoryManager historyManager = FlipHistoryManager.getInstance(AddChildActivity.this);
                    historyManager.updateHistoryName(initialString, String.valueOf(childNameInput.getText()));
                    PrefConfig.writeFlipHistoryInPref(getApplicationContext(), historyManager.getFullHistory());

                    changeChildName();
                    saveChildListToSharedPrefs();

                    finish();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                // ---------- change didn't happen -------------
                Toast.makeText(AddChildActivity.this,
                        getString(R.string.did_not_change_name_text_for_dialog_box),
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        } else {
            // show a toast that says did not add name because child name was empty
            Toast.makeText(
                    AddChildActivity.this,
                    R.string.empty_name,
                    Toast.LENGTH_SHORT
            ).show();
            finish();
        }
    }

    private void deleteChild() {
        AlertDialog.Builder builder = getAlertDialogBox();
        builder.setMessage(getString(
                R.string.confirm_delete_child_dialog_box_message,
                childNameInput.getText()
        ));
        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {

            FlipHistoryManager historyManager = FlipHistoryManager.getInstance(AddChildActivity.this);
            historyManager.deleteFlipHistoryOfChild(manager.getChild(positionForEditChild));
            PrefConfig.writeFlipHistoryInPref(getApplicationContext(), historyManager.getFullHistory());

            manager.removeChild(positionForEditChild);
            saveChildListToSharedPrefs();
            finish();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void addOrDiscardChildName() {
        if (!childNameIsEmpty()) {
            addChildInfo();
            saveChildListToSharedPrefs();
        } else {
            // show a toast that says did not add name because it was empty
            Toast.makeText(
                    AddChildActivity.this,
                    R.string.empty_name,
                    Toast.LENGTH_SHORT
            ).show();
        }
        finish();
    }

    private boolean childNameIsEmpty() {
        return String.valueOf(childNameInput.getText()).equals("");
    }

    private void addChildInfo() {
        String childName = childNameInput.getText().toString();
        child.setChildName(childName);
        manager.addChild(child);
        Toast.makeText(
                AddChildActivity.this,
                getString(R.string.toast_has_been_added_to_list, childName),
                Toast.LENGTH_SHORT
        ).show();
    }

    private AlertDialog.Builder getAlertDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddChildActivity.this);
        builder.setTitle(R.string.warning_message);
        builder.setNegativeButton(R.string.no, null);

        return builder;
    }

    private void changeChildName() {
        String childNameAfterChange = String.valueOf(childNameInput.getText());
//        Child child = manager.getChild(positionForEditChild);
        child.setChildName(childNameAfterChange);
    }

    private void setUpInitialString() {
        if (!addChild) {
            initialString = manager.getChild(positionForEditChild).getChildName();
        }
    }

    private boolean changeHappened() {
        return (!initialString.equals(String.valueOf(childNameInput.getText())));
    }

    private void saveChildListToSharedPrefs() {
        PrefConfig.writeChildListInPref(getApplicationContext(), manager.getAllChildren());
    }
}