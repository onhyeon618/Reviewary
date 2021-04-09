package com.cookandroid.reviewary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.cookandroid.reviewary.R;

import static java.net.Proxy.Type.HTTP;

public class InquiryActivity extends AppCompatActivity {

    Button btnSendEmail;
    Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_inquiry);

        btnSendEmail = (Button)findViewById(R.id.btnEmail);
        btnClose = (Button)findViewById(R.id.btnCloseInquiry);

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","huna0722@gmail.com", null));
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
//                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
