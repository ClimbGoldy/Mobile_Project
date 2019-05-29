package com.example.myapplication;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class StorageActivity extends AppCompatActivity {
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private View imageContainer;
    private TextView overLayText;
    private ProgressBar progressBar;
    private Button uploadButton;
    private TextView downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        imageContainer = findViewById(R.id.image_container);
        overLayText = findViewById(R.id.overLay_text);
        overLayText.setText("");
        overLayText.setVisibility(View.INVISIBLE);
        EditText textInput = findViewById(R.id.text_input);
        textInput.addTextChangedListener(new InputTextWatcher());
        uploadButton = findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new UploadOnClickListener());
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        downloadUrl = findViewById(R.id.download_url);
    }

    private class InputTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            overLayText.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            overLayText.setText(s.toString());
        }
    }

    private class UploadOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //이미지 업로드 받아옴
            imageContainer.setDrawingCacheEnabled(true);
            imageContainer.buildDrawingCache();
            Bitmap bitmap = imageContainer.getDrawingCache();
            //이미지를 PNG로 변환
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            imageContainer.setDrawingCacheEnabled(false);
            //바이트 어레이로 담음
            byte[] data = baos.toByteArray();

            String path = "memes/" + UUID.randomUUID() + ".png";
            StorageReference memeRef = storage.getReference(path);

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setCustomMetadata("like", overLayText.getText().toString())
                    .build();

            progressBar.setVisibility(View.VISIBLE);
            uploadButton.setEnabled(false);

            UploadTask uploadTask = memeRef.putBytes(data, metadata);
            uploadTask.addOnSuccessListener(StorageActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    uploadButton.setEnabled(true);

                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uri.isComplete());
                    Uri url = uri.getResult();

                    downloadUrl.setText(url.toString());
                    downloadUrl.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}