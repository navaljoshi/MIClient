package com.example.naval.miclient;

import android.os.Environment;
import android.os.FileObserver;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.path;

public class SendfileActivity extends Activity {
    /** Called when the activity is first created. */

    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;


    private ImageView img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendfile);
        Button save = (Button) findViewById(R.id.btn_Save);
        final EditText port = (EditText)findViewById(R.id.et_port);
        final  EditText ip = (EditText)findViewById(R.id.et_ip);
        final  EditText clientport = (EditText)findViewById(R.id.et_clientport);

        System.out.println("34");
       // img = (ImageView) findViewById(R.id.ivPic);
        System.out.println("36");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /* Calling File observer */
        final String m_path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath()+"/Camera/";
             Log.d("MI 00","Path selected : "+m_path);
        Toast toast = Toast.makeText(getApplicationContext(),
                "Path--->"+m_path,
                Toast.LENGTH_LONG);

        toast.show();



        save.setOnClickListener(new OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                //Create the intent to start another activity
                while(true) {
                    File file = null;

                    try {
                        if(lastFileModified() == null)
                        {
                            Log.d("MI 01","Sleeping for 10 seconds :");
                            Toast.makeText(getApplicationContext(), "This is my Toast message!",
                                    Toast.LENGTH_SHORT).show();
                            TimeUnit.SECONDS.sleep(10);
                            continue;
                        }
                        else {


                            file = lastFileModified();
                            selectedImagePath = file.getAbsolutePath().toLowerCase().toString();
                            Log.d("MI 02","Last Modified file Path > "+selectedImagePath);
                            String ip_str = ip.getText().toString();
                            String port_str = port.getText().toString();
                            String client_port = clientport.getText().toString();
                            Toast.makeText(getApplicationContext(), "This is my Toast message!",
                                    Toast.LENGTH_SHORT).show();

                            sendData(selectedImagePath,ip_str,port_str,client_port);
                        }
                    }catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }


                }

            }
        });


    }





    public void sendData(String path, String ip_str, String port_str, String client_port)
    {
        Socket sock;
        try {
            //sock = new Socket("192.168.0.103",8180);


            Log.d("MI 04","Connecting to Server IP"+ip_str);

            Log.d("MI 04","Connecting to Server PORT"+port_str);

            sock = new Socket();
            sock.setReuseAddress(true);
            sock.bind(new InetSocketAddress(Integer.parseInt(client_port)));
            sock.connect(new InetSocketAddress(ip_str,Integer.parseInt(port_str)));
            //sock = new Socket(ip_str,Integer.parseInt(port_str));
          //  sock.bind(new InetSocketAddress("", 1000));

            Log.d("MI007","Printing Client sock"+sock);
            Toast.makeText(getApplicationContext(), "TClient Sock",
                    Toast.LENGTH_SHORT).show();

            // sendfile
            File myFile = new File (path);
            byte [] mybytearray  = new byte [(int)myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray,0,mybytearray.length);
            OutputStream os = sock.getOutputStream();
            Log.d("MI 05","Sending Data to Server PC ");
            Toast.makeText(getApplicationContext(), "Sending Data",
                    Toast.LENGTH_SHORT).show();
            os.write(mybytearray,0,mybytearray.length);
            os.flush();

            sock.close();

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        File file = new File(path);
        file.delete();
        Log.d("MI 07","Deleted File >  "+path.toString());
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
               // TextView path = (TextView) findViewById(R.id.tvPath);
               // path.setText("Image Path : " + selectedImagePath);
                img.setImageURI(selectedImageUri);
            }
        }
    }
    public  File lastFileModified() throws IOException {
        String dir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath()+"/Camera/";

        File fl = new File(dir);
        File[] files = fl.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        });
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                Log.d("MI 06","Inside lastFileModified(), Choice found > "+file.getAbsolutePath());
                Toast.makeText(getApplicationContext(), "Last modified file",
                        Toast.LENGTH_SHORT).show();

                choice = file;
                lastMod = file.lastModified();

            }
        }
        return choice;
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}