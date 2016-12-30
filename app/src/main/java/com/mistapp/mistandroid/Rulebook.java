package com.mistapp.mistandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Rulebook extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rulebook);
        copyAssets();
    }

    /**
     * Allows user to download the pdf onto their phone from the link
     */
    private void copyAssets() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/5862bd9aff7c506c99529c04/1482866208834/MISTRulebook2017.pdf")));
    }
}
