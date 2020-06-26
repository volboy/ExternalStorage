package com.example.externalstorage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final static String FILE_NAME="content.txt";
    private static final int REQUEST_PERMISSION_WRITE=1001;
    private boolean permissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private File getExternalPath(){
        return (new File(Environment.getExternalStorageDirectory(), FILE_NAME));

    }
    //сохранение файла
    public void saveText(View view){
        if (!permissionGranted){
            checkPermissions();
            return;
        }
        FileOutputStream fos=null;
        try{
            EditText textBox=findViewById(R.id.save_text);
            String text=textBox.getText().toString();
            fos=new FileOutputStream(getExternalPath());
            fos.write(text.getBytes());
            Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
        }
        catch (IOException ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            try{
                if (fos!=null)
                    fos.close();
            }
            catch (IOException ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Открытие файла
    public void openText(View view){
        if (!permissionGranted){
            checkPermissions();
            return;
        }
        FileInputStream fin=null;
        TextView textView=findViewById(R.id.open_text);
        File file=getExternalPath();
        //если файл не существует, выход из метода
        if (!file.exists()) return;
        try{
            fin=new FileInputStream(file);
            byte[] bytes=new byte[fin.available()];
            fin.read(bytes);
            String text=new String(bytes);
            textView.setText(text);
        }
        catch (IOException ex){
            Toast.makeText(this, ex.getMessage(),  Toast.LENGTH_SHORT).show();
        }
        finally {
            try{
                if (fin!=null)
                    fin.close();
            }
            catch (IOException ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
    //проверяем, доступно ли внешнее хранилище для чтения и записи
    public boolean isExternalStorageWriteable(){
        String state=Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    //проверяем, доступно ли внешнее хранилище хотя бы только для чтения
    public boolean isExternalStorageReadable(){
        String state=Environment.getExternalStorageState();
        return(Environment.MEDIA_MOUNTED.equals(state)||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }
    private boolean checkPermissions(){
        if (!isExternalStorageReadable()||!isExternalStorageWriteable()){
            Toast.makeText(this, "Внешнее хранилище не доступно", Toast.LENGTH_LONG).show();
            return false;
        }
        int permissionCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults){
        switch (requestCode){
            case REQUEST_PERMISSION_WRITE:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    permissionGranted=true;
                    Toast.makeText(this, "Разрешения получены", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "Необходимо дать разрешения", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

}
