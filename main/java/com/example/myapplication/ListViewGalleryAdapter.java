package com.example.myapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewGalleryAdapter extends BaseAdapter {
    private ArrayList<ListViewGalleryItem> ListViewGalleryItemList = new ArrayList<ListViewGalleryItem>();

    public ListViewGalleryAdapter() {

    }

    @Override
    public int getCount() {
        return ListViewGalleryItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "activity_listview_gallery" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_listview_gallery, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.listView_gallery_imageView);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.listView_title_textView);
        TextView artistTextView = (TextView) convertView.findViewById(R.id.listView_artist_textView);

        // Data Set(ListViewGalleryItemList)에서 position에 위치한 데이터 참조 획득
        ListViewGalleryItem ListViewGalleryItem = ListViewGalleryItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(ListViewGalleryItem.getImage());
        titleTextView.setText(ListViewGalleryItem.getTitle());
        artistTextView.setText(ListViewGalleryItem.getArtist());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return ListViewGalleryItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(Drawable icon, String title, String artist) {
        ListViewGalleryItem item = new ListViewGalleryItem();

        item.setImage(icon);
        item.setTitle(title);
        item.setArtist(artist);

        ListViewGalleryItemList.add(item);
    }
}