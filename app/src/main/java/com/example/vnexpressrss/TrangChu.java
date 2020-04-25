package com.example.vnexpressrss;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrangChu extends AppCompatActivity {

    ListView listView;
    ArticleAdapter articleAdapter;
    ArrayList<Article> articleArrayList;
    View footerView;
    public void init(){
        listView = findViewById(R.id.list_article);
        articleArrayList = new ArrayList<Article>();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        footerView = inflater.inflate(R.layout.footer_view,null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_list);

        Toolbar toolbar =findViewById(R.id.toolbar_list_article);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Trang Chủ");
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new TrangChu.ReadData().execute("https://vnexpress.net/rss/tin-moi-nhat.rss");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TrangChu.this, ArticleView.class);
                intent.putExtra("link", articleArrayList.get(position).link);
                startActivity(intent);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    //  Params: truyền đường link URL -> String,
    //  Progress: quá trình thực hiện -> Integer,
    //  Result: kết quả trả về -> String
    class ReadData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return docNoiDung_Tu_URL(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            XMLDOMParser parser = new XMLDOMParser();
            Document document = parser.getDocument(s);
            NodeList nodeList =document.getElementsByTagName("item");
            NodeList nodeListdescription = document.getElementsByTagName("description");
            String hinhanh = "";
            String title = "";
            String link = "";
            String content = "";
            for (int i = 0; i < nodeList.getLength(); i++){
                String cdata = nodeListdescription.item(i+1).getTextContent();
                Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
                Pattern c = Pattern.compile("br>(.+)");
                Matcher matcher = p.matcher(cdata);
                if (matcher.find()){
                    hinhanh = matcher.group(1);
                    Log.d("hinhanh", hinhanh);
                }
                Matcher matcher1 = c.matcher(cdata);
                if (matcher1.find()){
                    content = matcher1.group(1);
                    Log.d("content", content);
                }
                Log.d("cdata", cdata);
                Element element = (Element) nodeList.item(i);
                title = parser.getValue(element, "title");
                link = parser.getValue(element,"link");
                Log.d("link", link);
                articleArrayList.add(new Article(title,link,hinhanh,content));
            }
            articleAdapter = new ArticleAdapter(TrangChu.this,android.R.layout.simple_list_item_1, articleArrayList);
            listView.setAdapter(articleAdapter);
            super.onPostExecute(s);

        }


    }

    private String docNoiDung_Tu_URL(String theUrl){
        StringBuilder content = new StringBuilder();
        try    {
            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null){
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch(Exception e)    {
            e.printStackTrace();
        }
        return content.toString();
    }


}
