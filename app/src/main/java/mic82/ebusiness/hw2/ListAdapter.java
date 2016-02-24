package mic82.ebusiness.hw2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.LinkedList;
/**
 * Created by chiming on 16/2/23.
 */
public class ListAdapter extends BaseAdapter {
    private Context mContext;
    private LinkedList<Data> mData;

    public ListAdapter() {
    }

    public ListAdapter(LinkedList<Data> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        Object x=null;
        if(mData != null) {
            x=mData.get(position);
        }
        return x;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false);
            holder = new ViewHolder();
            holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);
            holder.txt_content = (TextView) convertView.findViewById(R.id.txt_content);
            holder.txt_datetime = (TextView) convertView.findViewById(R.id.txt_date_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_title.setText(mData.get(position).getTitle());
        holder.txt_content.setText(mData.get(position).getContent());
        holder.txt_datetime.setText(mData.get(position).getDate()+" "+mData.get(position).getTime());
        return convertView;
    }

    public void add(Data data) {
        if (mData == null) {
            mData = new LinkedList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }

    public void add(int position,Data data){
        if (mData == null) {
            mData = new LinkedList<>();
        }
        mData.add(position, data);
        notifyDataSetChanged();
    }

    public void remove(Data data) {
        if(mData != null) {
            mData.remove(data);
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if(mData != null) {
            mData.remove(position);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        if(mData != null) {
            mData.clear();
        }
        notifyDataSetChanged();
    }
    public void requestUpdate()
    {
        notifyDataSetChanged();
    }

// view holder
    private class ViewHolder {
        TextView txt_title;
        TextView txt_content;
        TextView txt_datetime;
    }

}
