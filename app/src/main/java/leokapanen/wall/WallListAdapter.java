package leokapanen.wall;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import leokapanen.datamodel.WallData;
import leokapanen.testvkwall.R;

/**
 * Created by Leonid Kabanen on 15.07.15.
 * <p/>
 * Adapter for user wall ListView
 */
public class WallListAdapter extends ArrayAdapter {

    public WallListAdapter(Context context, List wallRecords) {
        super(context, 0, wallRecords);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Inflating row
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(
                    getContext()).inflate(R.layout.wall_list_item,
                    parent,
                    false
            );
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        // Setting data

        WallData wallData = (WallData) getItem(position);

        if (wallData.getUserID() != 0) {
            holder.userRow.setVisibility(View.VISIBLE);
            holder.userValue.setText(Integer.toString(wallData.getUserID()));
        } else {
            holder.userRow.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(wallData.getText())) {
            holder.textRow.setVisibility(View.VISIBLE);
            holder.textValue.setText(wallData.getText());
        } else {
            holder.textRow.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(wallData.getPhotoURL())) {
            holder.photoRow.setVisibility(View.VISIBLE);
            Picasso
                    .with(getContext())
                    .load(wallData.getPhotoURL())
                    .placeholder(R.drawable.ic_access_time_black_36dp)
                    .into(holder.photoValue);
        } else {
            holder.photoRow.setVisibility(View.GONE);
            holder.photoValue.setImageBitmap(null);
        }

        return view;
    }

    static class ViewHolder {

        @Bind(R.id.user_row)
        ViewGroup userRow;

        @Bind(R.id.user_value)
        TextView userValue;

        @Bind(R.id.text_row)
        ViewGroup textRow;

        @Bind(R.id.text_value)
        TextView textValue;

        @Bind(R.id.photo_row)
        ViewGroup photoRow;

        @Bind(R.id.photo_value)
        ImageView photoValue;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
