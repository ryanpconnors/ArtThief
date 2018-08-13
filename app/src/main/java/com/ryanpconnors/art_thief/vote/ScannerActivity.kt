package com.ryanpconnors.art_thief.vote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.ryanpconnors.art_thief.MainActivity

import java.util.ArrayList

import me.dm7.barcodescanner.zxing.ZXingScannerView

/**
 * Created by rpc80 on 12/3/16.
 */

class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var mScannerView: ZXingScannerView? = null // source: https://github.com/dm77/barcodescanner

    private val barcodeFormats = object : ArrayList<BarcodeFormat>() {
        init {
            add(BarcodeFormat.QR_CODE)
        }
    }

    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        mScannerView = ZXingScannerView(this)   // Programmatically initialize the scanner view
        mScannerView!!.setFormats(barcodeFormats)
        setContentView(mScannerView)                // Set the scanner view as the content view
    }

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera()          // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()
    }

    override fun handleResult(rawResult: Result) {

        Log.v(TAG, rawResult.text)
        Log.v(TAG, rawResult.barcodeFormat.toString())

        mScannerView!!.stopCamera()
        val scanData = rawResult.text

        val resultIntent = Intent()
        resultIntent.putExtra(MainActivity.TICKET_CODE, scanData)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    companion object {
        private val TAG = "ScannerActivity"
    }

}
