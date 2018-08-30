package lightning.cyborg.activity;

/**
 * This class creates a grid which displays all of the avatars for the users to pick
 * once clicked it will change the profile avatar.
 *
 * Created by Team Cyborg Lightning
 */
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import java.util.ArrayList;

import lightning.cyborg.R;
import lightning.cyborg.avator.GridViewAdapter;
import lightning.cyborg.fragment.UserProfileFragment;

public class Avator_Logo extends Activity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;


    /**
     * Default method that is ran by app
     * @param savedInstanceState  where user previously left off
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avator_background);

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Bitmap item = (Bitmap) parent.getItemAtPosition(position);
                Bitmap x = item;
                UserProfileFragment.imageview.setImageBitmap(x);
                finish();
            }
        });
    }

    /**
     * Preparing the data for gridview
     */
    private ArrayList<Bitmap> getData() {
        final ArrayList<Bitmap> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            imageItems.add(bitmap);
        }
        return imageItems;
    }
}