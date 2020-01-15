package bonch.dev.tcpconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Arrays;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private TextView infoTV;
    private Button sendBtn;

    private byte[] dataArray;
    private byte[] recivedDataArray;

    private static final String IP = "192.168.0.102";
    private static final int PORT = 5000;
    private SecureRandom secureRandom = new SecureRandom();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        startServer();
    }

    private void startServer() {
        Single.fromCallable(() -> startListening())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::toPrintSuccessMessage, Throwable -> Log.e("Start server error:", Throwable.getLocalizedMessage()));

    }

    private void toPrintSuccessMessage(byte[] data) {
        Toast.makeText(this, Arrays.toString(data), Toast.LENGTH_SHORT).show();
        Log.e("Server recived data:", Arrays.toString(data));
    }

    private byte[] startListening() {
        try (ServerSocket serverSocket = new ServerSocket(9090);
             Socket socket1 = serverSocket.accept();
             InputStream inputStream = socket1.getInputStream();
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {

            int length = dataInputStream.readInt();

            if (length != 0) {

                recivedDataArray = new byte[length];
                dataInputStream.readFully(recivedDataArray, 0, length);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Listening error");
        }
        return recivedDataArray;
    }

    private void initData() {

        int length = secureRandom.nextInt(100) + 1;
        dataArray = new byte[length];
        secureRandom.nextBytes(dataArray);
    }

    private void initViews() {
        infoTV = findViewById(R.id.InfoTextView);
        sendBtn = findViewById(R.id.send_btn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initData();
                TCPSendTask tcpTask = new TCPSendTask();
                tcpTask.execute();

            }
        });
    }

    class TCPSendTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            infoTV.setText("Start connection with " + IP + ":" + PORT);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try (Socket socket = new Socket(IP, PORT);
                 OutputStream outputStream = socket.getOutputStream();
                 DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {

                dataOutputStream.writeInt(dataArray.length);
                dataOutputStream.write(dataArray, 0, dataArray.length);


            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("TCPSend task error");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
        }
    }
}
