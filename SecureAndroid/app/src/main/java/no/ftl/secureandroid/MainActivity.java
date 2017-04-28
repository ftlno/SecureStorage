package no.ftl.secureandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView resultLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultLabel = (TextView) findViewById(R.id.resultLabel);
        demonstration();
    }

    private void demonstration() {
        String message = "abc1234";

        Storage storage = new Storage(getApplicationContext());
        storage.set("result", message);
        String decrypted = storage.get("result");

        resultLabel.setText(message.equals(decrypted) ? "Success! " + decrypted : "Failed! " + decrypted);
    }
}
