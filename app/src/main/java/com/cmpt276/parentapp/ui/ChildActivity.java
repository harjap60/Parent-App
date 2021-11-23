package com.cmpt276.parentapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityChildBinding;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildDao;
import com.cmpt276.parentapp.model.ChildTaskCrossRef;
import com.cmpt276.parentapp.model.ParentAppDatabase;
import com.cmpt276.parentapp.model.Task;
import com.cmpt276.parentapp.model.TaskDao;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    private final int REQUEST_CODE_FOR_TAKE_PHOTO = 10;
    private final int REQUEST_CODE_FOR_CHOOSE_FROM_GALLERY = 20;
    private int childId;
    private Child child;
    private ChildDao childDao;
    private ActivityChildBinding binding;
    private String imagePath;

    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestStoragePermissionLauncher;

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

        childId = getIntent().getIntExtra(EXTRA_FOR_INDEX, NEW_CHILD_INDEX);
        childDao = ParentAppDatabase.getInstance(this).childDao();

        setupImageCaptureButton();
        setupPermissionLaunchers();
        setupChild(childId);
        setUpToolbar(childId);
        setupAddEditButton();
        updateUI();
    }

    private void setupAddEditButton() {
        binding.btnSave.setOnClickListener((v)-> saveChild());
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(
                childId == NEW_CHILD_INDEX ?
                        R.menu.menu_child :
                        R.menu.menu_child_edit,
                menu
        );

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_child_save:
                saveChild();
                return true;

            case R.id.btn_child_delete:
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

        showUpConfirmationDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_FOR_CHOOSE_FROM_GALLERY
                && resultCode == RESULT_OK
                && data != null) {
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
                    imagePath = cursor.getString(columnIndex);
                    cursor.close();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        updateUI();
    }

    private void setupChild(int id) {

        if (id == NEW_CHILD_INDEX) {
            return;
        }

        childDao.get(id)
                .subscribeOn(Schedulers.newThread())
                .subscribe((Child child) -> {
                    this.child = child;
                    this.imagePath = child.getImagePath();
                    runOnUiThread(this::updateUI);
                });
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

    private void setupPermissionLaunchers() {
        requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                captureImage();
            } else {
                Toast.makeText(
                        this,
                        R.string.camera_permission_message,
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        requestStoragePermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        chooseImageFromGallery();
                    } else {
                        Toast.makeText(
                                this,
                                R.string.storage_permission_message,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setupImageCaptureButton() {
        binding.fabEditChildImage.setOnClickListener(view -> selectImage());
    }

    private void updateUI() {
        int deviceWidth = getResources().getDisplayMetrics().widthPixels;
        binding.txtName.setTextSize((deviceWidth / 25f));

        if (imagePath != null) {
            Glide.with(this)
                    .load(imagePath)
                    .centerCrop()
                    .placeholder(R.drawable.child_image_icon)
                    .into(binding.imageViewChildImage);
        }

        if (child != null) {
            binding.txtName.setText(child.getName());
            binding.btnSave.setText(R.string.edit_child_button_text);
        }
    }

    private void showUpConfirmationDialog() {
        new AlertDialog.Builder(ChildActivity.this)
                .setTitle(R.string.up_alert_title)
                .setNegativeButton(R.string.no, null)
                .setMessage(
                        getString(child == null ?
                                R.string.warning_change_happened_for_add_child :
                                R.string.warning_change_happened_for_edit_child)
                ).setPositiveButton(R.string.yes, (dialogInterface, i) -> finish())
                .create()
                .show();
    }

    private void showDeleteChildDialog() {
        new AlertDialog.Builder(ChildActivity.this)
                .setTitle(R.string.delete_alert_title)
                .setNegativeButton(R.string.no, null)
                .setMessage(getString(
                        R.string.confirm_delete_child_dialog_box_message,
                        child.getName()
                ))
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> deleteChild())
                .create()
                .show();
    }

    private void saveChild() {

        String name = this.binding.txtName.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(
                    this,
                    R.string.empty_child_name_message,
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        new Thread(() -> {
            if (child == null) {
                int coinFlipOrder = childDao.getNextCoinFlipOrder().blockingGet();
                Long id = childDao.insert(new Child(name, coinFlipOrder, imagePath)).blockingGet();

                TaskDao taskDao = ParentAppDatabase.getInstance(ChildActivity.this).taskDao();

                List<Task> tasks = taskDao.getAll().blockingGet();
                for (Task task : tasks) {
                    int order = taskDao.getNextOrder(task.getTaskId()).blockingGet();
                    taskDao.insertRef(new ChildTaskCrossRef(
                            task.getTaskId(),
                            id.intValue(),
                            order
                    )).blockingAwait();
                }
            } else {
                child.setName(name);
                child.setImagePath(imagePath);
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
        new AlertDialog.Builder(this)
                .setTitle(R.string.choose_picture)
        .setItems(IMAGE_OPTIONS, (dialogInterface, item) -> {
            if (IMAGE_OPTIONS[item].equals(getString(R.string.take_photo))) {
                captureImage();
            } else if (IMAGE_OPTIONS[item].equals(getString(R.string.choose_from_gallery))) {
                chooseImageFromGallery();
            } else if (IMAGE_OPTIONS[item].equals(getString(R.string.cancel))) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void captureImage() {
        if (isGranted(Manifest.permission.CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(
                        this,
                        getString(R.string.could_not_create_file),
                        Toast.LENGTH_SHORT
                ).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.cmpt276.parentapp",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CODE_FOR_TAKE_PHOTO);
            }
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void chooseImageFromGallery() {
        // if we do not have permission to read external storage
        if (isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            // permission has been granted
            Intent pickPhoto = new Intent(Intent.ACTION_PICK);
            pickPhoto.setType("image/*");
            startActivityForResult(pickPhoto, REQUEST_CODE_FOR_CHOOSE_FROM_GALLERY);
        } else {
            requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        imagePath = image.getAbsolutePath();
        return image;
    }

    private boolean isGranted(String permission) {
        return ContextCompat
                .checkSelfPermission(
                        this,
                        permission
                ) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isClean() {
        String name = binding.txtName.getText().toString();
        return (child == null && name.isEmpty()) ||
                (child != null && name.equals(child.getName()));
    }

}