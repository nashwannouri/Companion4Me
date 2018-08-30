package lightning.cyborg.avator;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import lightning.cyborg.R;

/**
 *
 */
public class Avator_Logo extends ActionBarActivity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avator);

        //Images are put into gridView Adapter
        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        //onClickListener for GridView
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //retrieves the item that was clicked
                Bitmap item = (Bitmap) parent.getItemAtPosition(position);
                android.graphics.Bitmap x = item;

                //image is compressed
                Intent intent = new Intent();
                ByteArrayOutputStream bs  = new ByteArrayOutputStream();
                x.compress(android.graphics.Bitmap.CompressFormat.PNG,50,bs );

                intent.putExtra("Bitmap", bs.toByteArray());
                intent.putExtra("imageID",position);

                //return return to activity
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });
    }

    /**
     * Gets the avatar data
     * @return an ArrayList of Avatars in Bitmap format
     */
    private ArrayList<Bitmap> getData() {
        final ArrayList<Bitmap> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, 0));
            imageItems.add(bitmap);
        }
        return imageItems;
    }
}