package com.example.vnexpressrss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleAdapter  extends ArrayAdapter<Article> {

    public ArticleAdapter(Context context, int resource, List<Article> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view =  inflater.inflate(R.layout.article_item, null);
        }
        Article p = getItem(position);
        if (p != null) {
            // Anh xa + Gan gia tri
            TextView title = (TextView) view.findViewById(R.id.tvTitle);
            TextView content = view.findViewById(R.id.tvContent);
            title.setText(p.title);
            content.setText(p.content);

            ImageView imageView = view.findViewById(R.id.imageView);
            Picasso.with(getContext()).load(p.image).into(imageView);

        }
        return view;
    }
}
