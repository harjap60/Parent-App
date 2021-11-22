package com.cmpt276.parentapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityChildBinding;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildDao;
import com.cmpt276.parentapp.model.ParentAppDatabase;

import java.util.Objects;

import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Add Child Activity - This activity lets you add a new child to the list.
 * Or edit an existing child in the list.
 * The user sets the name of the child but has a restriction to it
 * - the name of the new/edit child cannot be empty
 */
public class ChildActivity extends AppCompatActivity {

    private static final String EXTRA_FOR_INDEX =
            "com.cmpt276.parentapp.ui.AddChildActivity.childId";
    private static final int NEW_CHILD_INDEX = -1;
    private final String[] IMAGE_OPTIONS = {"Take Photo", "Choose from Gallery", "Cancel"};
    private final int REQUEST_READ_EXTERNAL_STORAGE = 111;
    private final int REQUEST_CODE_FOR_CAMERA = 222;
    private final int REQUEST_CODE_FOR_TAKE_PHOTO = 10;
    private final int REQUEST_CODE_FOR_CHOOSE_FROM_GALLERY = 20;

    private Child child;
    private ChildDao childDao;
    private ActivityChildBinding binding;
    private Bitmap image;

    public static Intent getIntentForNewChild(Context context) {
        return getIntentForExistingChild(context, NEW_CHILD_INDEX);
    }

    public static Intent getIntentForExistingChild(Context context, int index) {
        Intent intent = new Intent(context, ChildActivity.class);
        intent.putExtra(EXTRA_FOR_INDEX, index);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        int id = getIntent().getIntExtra(EXTRA_FOR_INDEX, NEW_CHILD_INDEX);

        setupDB();
        setupChild(id);
        setUpToolbar(id);
        setupSaveButton();
        setupImageCaptureButton();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (child != null) {
            getMenuInflater().inflate(R.menu.menu_edit_child, menu);
        }

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_child_button:
                showDeleteChildDialog();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (isClean()) {
            finish();
            return;
        }

        AlertDialog.Builder builder = getAlertDialogBox();
        builder.setMessage(
                getString(child == null ?
                        R.string.warning_change_happened_for_add_child :
                        R.string.warning_change_happened_for_edit_child)
        );

        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> finish());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_CODE_FOR_TAKE_PHOTO:
                    if (resultCode == RESULT_OK && data != null) {
                        image = (Bitmap) data.getExtras().get("data");
                    }
                    break;

                case REQUEST_CODE_FOR_CHOOSE_FROM_GALLERY:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver()
                                    .query(
                                            selectedImage,
                                            filePathColumn,
                                            null,
                                            null,
                                            null
                                    );

                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);

                                image = (Bitmap)BitmapFactory.decodeFile(picturePath);
                                cursor.close();
                            }
                        }
                    }
                    break;

                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
            updateUI();
        }
    }


    private void setupDB() {
        childDao = ParentAppDatabase.getInstance(this).childDao();
    }

    private void setupChild(int id) {

        if (id == NEW_CHILD_INDEX) {
            return;
        }

        childDao.get(id)
                .subscribeOn(Schedulers.newThread())
                .subscribe((Child child) -> {
                    this.child = child;
                    this.image = child.getImage();
                    updateUI();
                });
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(view -> saveChild());
    }

    private void setUpToolbar(int id) {
        binding.toolbar.setTitle(
                getString(id == NEW_CHILD_INDEX ?
                        R.string.add_child_title :
                        R.string.edit_child_title)
        );

        setSupportActionBar(binding.toolbar);

        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);
    }

    private void setupImageCaptureButton() {
        binding.fabEditChildImage.setOnClickListener(view -> selectImage());
    }

    private void updateUI() {
        int deviceWidth = getResources().getDisplayMetrics().widthPixels;
        binding.txtName.setTextSize((deviceWidth / 25f));

        if (image != null) {
            binding.imageViewChildImage.setImageBitmap(image);
        }

        if (child != null) {
            binding.txtName.setText(child.getName());
        }
    }

    private void handleUnsavedChanges() {
        if (isClean()) {
            return;
        }

        getAlertDialogBox()
                .setMessage(getString(
                        R.string.confirm_edit_child_dialog_box_message,
                        child.getName(),
                        binding.txtName.getText()
                ))
                .setPositiveButton(R.string.yes, (dialog, which) -> saveChild())
                .create()
                .show();
    }

    private void showDeleteChildDialog() {
        getAlertDialogBox()
                .setMessage(getString(
                        R.string.confirm_delete_child_dialog_box_message,
                        child.getName()
                ))
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> deleteChild())
                .create()
                .show();
    }

    private void saveChild() {
        new Thread(() -> {

            String name = this.binding.txtName.getText().toString();

            if (child == null) {
                int coinFlipOrder = childDao.getNextCoinFlipOrder().blockingGet();
                childDao.insert(new Child(name, coinFlipOrder,image)).blockingAwait();
            } else {
                child.setName(name);
                child.setImage(image);
                childDao.update(child).blockingAwait();
            }
            runOnUiThread(this::finish);
        }).start();
    }

    private void deleteChild() {

        if (child == null) {
            return;
        }

        childDao.delete(this.child)
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::finish);
    }

    private AlertDialog.Builder getAlertDialogBox() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.warning_message)
                .setNegativeButton(R.string.no, null);
    }

    private void selectImage() {
        // create a dialog box that will let the user choose from three options
        // - take a photo using camera
        // - choose an image from gallery
        // - cancel
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose picture");
        Log.e("TAG", "Reached the select image");
        builder.setItems(IMAGE_OPTIONS, (dialogInterface, item) -> {
            if (IMAGE_OPTIONS[item].equals(getString(R.string.take_photo))) {
                captureImage();
            } else if (IMAGE_OPTIONS[item].equals(getString(R.string.choose_from_gallery))) {
                chooseImageFromGallery();
            } else if (IMAGE_OPTIONS[item].equals(getString(R.string.cancel))) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void captureImage() {
        // if we do not have the permission for the camera
        if (ContextCompat
                .checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // then ask the permission from the user
            ActivityCompat.requestPermissions(
                    ChildActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    }, REQUEST_CODE_FOR_CAMERA
            );
        } else {
            // permission has been granted
            Intent takePicture = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE
            );

            startActivityForResult(takePicture, REQUEST_CODE_FOR_TAKE_PHOTO);
        }
    }

    private void chooseImageFromGallery() {
        // if we do not have permission to read external storage
        if (ContextCompat
                .checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // then ask the permission from the user
            ActivityCompat.requestPermissions(
                    ChildActivity.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    REQUEST_READ_EXTERNAL_STORAGE
            );
        } else {
            // permission has been granted
            Intent pickPhoto = new Intent(Intent.ACTION_PICK);
            pickPhoto.setType("image/*");
            startActivityForResult(pickPhoto, REQUEST_CODE_FOR_CHOOSE_FROM_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_FOR_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Toast.makeText(
                        this,
                        "You need to grant camera permission to take photo of the child",
                        Toast.LENGTH_LONG
                ).show();
            }
        }

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImageFromGallery();
            } else {
                Toast.makeText(
                        this,
                        "You need to grant read external storage permission to choose a photo of the child",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    private boolean isClean() {
        String name = binding.txtName.getText().toString();
        return (child == null && name.isEmpty()) ||
                (child != null && name.equals(child.getName()));
    }

}