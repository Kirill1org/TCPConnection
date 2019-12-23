package bonch.dev.tcpconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView infoTV;
    private Button sendBtn;

    private byte[] dataArray;

    private static Socket socket;
    private static OutputStream outputStream;
    private static DataOutputStream dataOutputStream;

    private static final String IP = "192.168.0.100";
    private static final int PORT = 5000;
    private final Random random = new Random();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        setClickListeners();


    }

    private void initData() {

        int length = random.nextInt(100)+1;
        dataArray = new byte[length];
        random.nextBytes(dataArray);


    }

    private void setClickListeners() {

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initData();

                TCPTask tcpTask = new TCPTask();
                tcpTask.execute();

            }
        });


    }

    private void initViews() {
        infoTV = findViewById(R.id.InfoTextView);
        sendBtn = findViewById(R.id.send_btn);
    }

    class TCPTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            infoTV.setText("Start connection with "+IP+":"+PORT);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                socket = new Socket(IP,PORT);
                outputStream = socket.getOutputStream();
                dataOutputStream = new DataOutputStream(outputStream);



                dataOutputStream.writeInt(dataArray.length);
                dataOutputStream.write(dataArray,0,dataArray.length);

                dataOutputStream.close();
                outputStream.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
        }
    }
}
