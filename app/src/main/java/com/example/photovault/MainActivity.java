package com.example.photovault;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    private String androidId;
    private IvParameterSpec ivspec;

    public String filesDir;

    /* Elements */
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize encryption variables like iv
        initializeEncryption();

        filesDir = getApplicationContext().getFilesDir() + "/";

        /* Elements */
        gridView = findViewById(R.id.main_grid_images);

        // FAB
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.create(MainActivity.this)
                        .folderMode(true)
                        .toolbarFolderTitle("Select a folder")
                        .toolbarImageTitle("Tap to select photos")
                        .includeVideo(true)
                        .showCamera(false)
                        .start();
            }
        });

        // Update the photos grid
        updateGrid();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);

            for (Image im: images) {
                try {
                    // Encrypt the image
                    byte[] encrypted = encryptImage(im.getPath());

                    // Save encrypted image to destination
                    FileOutputStream fos = new FileOutputStream(filesDir + im.getName());
                    fos.write(encrypted);
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Unable to encrypt photo", Toast.LENGTH_LONG).show();
                }
            }

            updateGrid();
        }
    }

    private void initializeEncryption() {
        // Generate ivSpec according to unique Android ID
        if (androidId.length() != 16) {
            Toast.makeText(getApplicationContext(), "Can't generate encryption, please contact us", Toast.LENGTH_LONG).show();
            return;
        }

        byte[] iv = new byte[16];
        for (int i = 0 ; i < 16; i++) {
            iv[i] = (byte) androidId.charAt(i);
        }
        ivspec = new IvParameterSpec(iv);
    }

    private byte[] encryptImage(String path) throws Exception {
        byte[] encrypted = null;

        File src = new File(path);
        Cipher cipher;
        SecretKeySpec secretKey = new SecretKeySpec(androidId.getBytes(), "AES");
        byte[] srcBytes = new byte[(int) src.length()];

        // Read source file bytes
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(src));
        buf.read(srcBytes, 0, srcBytes.length);
        buf.close();

        // Encrypt source file
        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        encrypted = cipher.doFinal(srcBytes);

        return encrypted;
    }

    private byte[] decryptImage(String path) throws Exception {
        byte[] decrypted = null;

        File src = new File(path);
        Cipher cipher;
        SecretKeySpec secretKey = new SecretKeySpec(androidId.getBytes(), "AES");
        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

        byte[] srcBytes = new byte[(int) src.length()];
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(src));
        buf.read(srcBytes, 0, srcBytes.length);
        buf.close();

        decrypted = cipher.doFinal(srcBytes);

        return decrypted;
    }

    private void updateGrid() {
        // Get all files in /files folder
        File[] listOfFiles = new File(filesDir).listFiles();

        assert listOfFiles != null;
        byte[][] imagesBytes = new byte[listOfFiles.length][];
        for (int i = 0; i < listOfFiles.length; i++) {
            if (!listOfFiles[i].isFile()) continue;
            try {
                imagesBytes[i] = decryptImage(listOfFiles[i].getPath());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to decrypt photo", Toast.LENGTH_LONG).show();
            }
        }

        // Grid view
        gridView.setAdapter(new ImageAdapter(this, imagesBytes));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FullScreenActivity.class);
                intent.putExtra("image", imagesBytes[position]);
                startActivity(intent);
            }
        });
    }

    /*
    public static void copyFile(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }*/
}