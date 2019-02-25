package com.example.chidiya;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import static android.provider.Telephony.ServiceStateTable.AUTHORITY;

public class MainActivity extends AppCompatActivity {

    EditText e;
    Button b;
    private File pdfFile;
    final private int REQUEST_CODE_ASK_PERMISSIONS=111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        e=findViewById(R.id.editText);
        b=findViewById(R.id.btn12);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(e.getText().toString().isEmpty())
                {
                    e.setError("enter text");
                    e.requestFocus();
                    return;
                }

                try{
                    createPDF();

                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }catch (DocumentException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createPDF() throws FileNotFoundException,DocumentException{
        int hasP= ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(hasP!= PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if(!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)){
                    showMessage("you need to allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        }
                    });
                   // Toast.makeText(this,"give perm",Toast.LENGTH_SHORT).show();
                    //requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
        }
        else{
            File doc=new File(Environment.getExternalStorageDirectory()+"/Documents");
            if(!doc.exists()){
                doc.mkdir();
            }
            pdfFile=new File(doc.getAbsolutePath(),"myPDF.pdf");
            OutputStream output=new FileOutputStream(pdfFile);
            Document document=new Document();
            PdfWriter.getInstance(document,output);
            document.open();
            document.add(new Paragraph(e.getText().toString()));
            document.close();
            showPDF();

        }
    }

    @Override

    public void onRequestPermissionsResult(int requestCode,String[] permission,int[] grantResults){

        switch(requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    try{
                        createPDF();
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }catch (DocumentException e){
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(this,"dedo permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode,permission,grantResults);
        }
    }

    private void showPDF(){
        PackageManager packageManager=getPackageManager();
        Intent testIntent=new Intent(Intent.ACTION_VIEW);

        testIntent.setType("application/pdf");
       // Toast.makeText(this,"dwnload pdf viewer",Toast.LENGTH_SHORT).show();

        List list= packageManager.queryIntentActivities(testIntent,PackageManager.MATCH_DEFAULT_ONLY);
       // Toast.makeText(this,"dwnload pdf viewer",Toast.LENGTH_SHORT).show();
        if(list.size()>0){
            /*Intent i=new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);*/
            Intent i=new Intent(Intent.ACTION_VIEW);

            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",pdfFile);
            i.setDataAndType(uri,"application/pdf");
            startActivity(i);
        }
        else{
            Toast.makeText(this,"dwnload pdf viewer",Toast.LENGTH_SHORT).show();

        }

    }

    private void showMessage(String message, DialogInterface.OnClickListener okListener){
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton("OK",okListener)
                .setNegativeButton("Cancel",null).create().show();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

       // private void showMessage(String message,DailogInterface.onCl)

}
