package com.example.pic;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class PostFragment extends Fragment {

    //private static final int PICK_IMAGE_REQUEST =1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private EditText mEditTextQuote;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    private ActivityResultLauncher<Intent> someActivityResultLauncher;

    //private View v;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post,container,false);

        mButtonChooseImage = v.findViewById(R.id.button_choose_image);
        mButtonUpload = v.findViewById(R.id.button_upload);
        mEditTextQuote =v.findViewById(R.id.edit_quote);
        mImageView = v.findViewById(R.id.image_upload);
        mProgressBar = v.findViewById(R.id.progress_bar);

        mStorageRef = FirebaseStorage.getInstance().getReference("posts");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("posts");

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 100);}


            mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        openFileChooser();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "No permission to access storage", Toast.LENGTH_SHORT).show();

                    }
                }
            });

            mButtonUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUploadTask != null && mUploadTask.isInProgress()) {
                        Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadFile();
                    }

                }
            });


        //ActivityResultLauncher<Intent>
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            mImageUri = data.getData();
                            Picasso.get().load(mImageUri).into(mImageView);

                        }
                    }
                });
        return v;
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == -1
                && data != null && data.getData() != null){
                mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(mImageView);
            //mImageView.setImageURI(mImageUri); // if the line up doesn't work
        }
    }*/

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
    }

    private String getFileExtention(Uri uri){ //gets the extention of a file, exp: jpg
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile() {
        if(mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtention(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler(Looper.myLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String please = uri.toString();
                                    String usr = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                    Post post = new Post(mEditTextQuote.getText().toString().trim(),
                                            please,
                                            //"url:" + taskSnapshot.getMetadata().getReference().getDownloadUrl().getResult().toString(),
                                            usr);
                                    String postId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(postId).setValue(post);

                                }
                            });
                            Toast.makeText(getActivity(), "Posted", Toast.LENGTH_SHORT).show();
                            //Task<Uri> downUrl=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            //Log.i("url:",downUrl.getResult().toString());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    })
                    /*.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                String downloadUri = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                                // System.out.println("Upload " + downloadUri);
                                Toast.makeText(getActivity(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                if (downloadUri != null) {

                                    String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                    //System.out.println("Upload " + photoStringLink);
                                    Post post = new Post(mEditTextQuote.getText().toString().trim(),
                                            photoStringLink,
                                            //"url:" + taskSnapshot.getMetadata().getReference().getDownloadUrl().getResult().toString(),
                                            "username");
                                    String postId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(postId).setValue(post);

                                }
                            }
                        }})*/;
        }
        else{
            Toast.makeText(getActivity(), "No picture selected", Toast.LENGTH_SHORT).show();
        }
    }
}
