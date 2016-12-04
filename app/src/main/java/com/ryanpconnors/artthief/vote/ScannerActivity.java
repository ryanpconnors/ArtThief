package com.ryanpconnors.artthief.vote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.ryanpconnors.artthief.MainActivity;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by rpc80 on 12/3/16.
 */

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView; // source: https://github.com/dm77/barcodescanner
    private static final String TAG = "ScannerActivity";

    private final ArrayList<BarcodeFormat> barcodeFormats = new ArrayList<BarcodeFormat>() {{
        add(BarcodeFormat.QR_CODE);
    }};

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        mScannerView.setFormats(barcodeFormats);
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        Log.v(TAG, rawResult.getText());
        Log.v(TAG, rawResult.getBarcodeFormat().toString());

        mScannerView.stopCamera();
        String scanData = rawResult.getText();

        Intent resultIntent = new Intent();
        resultIntent.putExtra(MainActivity.TICKET_CODE, scanData);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}
