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
        /**String path = Environment.getExternalStorageDirectory()+"/Download/MISTRulebook2017.pdf";
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch(ActivityNotFoundException e) {
            Toast.makeText(Rulebook.this, "Install a pdf reader", Toast.LENGTH_SHORT).show();

        }
    **/
    }

    /**
     * Not the best solution, but definitely the easiest, allows user to download the pdf onto their phone
     */
    private void copyAssets() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/5862bd9aff7c506c99529c04/1482866208834/MISTRulebook2017.pdf")));
    }
}
