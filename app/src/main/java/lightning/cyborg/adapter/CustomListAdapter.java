package lightning.cyborg.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import lightning.cyborg.R;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final Bitmap[] images;
    private final Integer[] avatar;

    public CustomListAdapter(Activity context, String[] itemname, Bitmap[] images, Integer[] avatar) {
        super(context, R.layout.mylist, itemname);
        this.context=context;
        this.itemname=itemname;
        this.avatar=avatar;
        this.images=images;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);

        txtTitle.setText(itemname[position]);
        imageView.setImageBitmap(images[avatar[position]]);
        return rowView;
    }
}