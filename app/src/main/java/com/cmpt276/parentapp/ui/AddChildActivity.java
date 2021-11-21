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
            }
        });
    }

    private void selectImage(){
        // create a dialog box that will let the user choose from three options
        // - take a photo using camera
        // - choose an image from gallery
        // - cancel
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
        // if we do not have the permission for the camera
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // then ask the permission from the user
            ActivityCompat.requestPermissions(AddChildActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
        else {
            // permission has been granted
            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, REQUEST_CODE_FOR_TAKE_PHOTO);
        }
    }

    private void chooseImageFromGallery() {
        // if we do not have permission to read external storage
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // then ask the permission from the user
            ActivityCompat.requestPermissions(AddChildActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }
        else {
            // permission has been granted
            Intent pickPhoto = new Intent(Intent.ACTION_PICK);
            pickPhoto.setType("image/*");
            startActivityForResult(pickPhoto, REQUEST_CODE_FOR_CHOOSE_FROM_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
            else {
                Toast.makeText(AddChildActivity.this, "You need to grant camera permission to take photo of the child", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImageFromGallery();
            }
            else {
                Toast.makeText(AddChildActivity.this, "You need to grant read external storage permission to choose a photo of the child", Toast.LENGTH_LONG).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_CODE_FOR_TAKE_PHOTO:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        child.setChildImageBitmap(selectedImage);
                        childImage.setImageBitmap(selectedImage);
                    }
                    break;

                case REQUEST_CODE_FOR_CHOOSE_FROM_GALLERY:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);

                                Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);
                                child.setChildImageBitmap(imageBitmap);
                                childImage.setImageBitmap(imageBitmap);
                                cursor.close();
                            }
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