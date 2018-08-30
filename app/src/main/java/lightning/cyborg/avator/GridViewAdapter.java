package lightning.cyborg.avator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import lightning.cyborg.R;

public class GridViewAdapter extends ArrayAdapter<Bitmap> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Bitmap> data = new ArrayList<Bitmap>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<Bitmap> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        Bitmap item = data.get(position);
        holder.image.setImageBitmap(item);
        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}