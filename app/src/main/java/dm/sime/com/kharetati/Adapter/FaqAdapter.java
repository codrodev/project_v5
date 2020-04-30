package dm.sime.com.kharetati.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dm.sime.com.kharetati.R;
import dm.sime.com.kharetati.fragment.BookmarksFragment;
import dm.sime.com.kharetati.fragment.FaqHelp;
import dm.sime.com.kharetati.pojo.Bookmark;
import dm.sime.com.kharetati.util.FAQ;
import dm.sime.com.kharetati.util.FontChangeCrawler;


/**
 * Created by Hasham on 11/14/2017.
 */

public class FaqAdapter extends BaseAdapter  implements Filterable {

  Context context;
  List<FAQ> data;
  List<FAQ> filteredData;
  private LayoutInflater inflater = null;
  FaqHelp fragment;
  Activity activity;
  private ImageView imageViewExpand;
  private TextView faq_key;
  private TextView faq_value;
  public FaqAdapter(Context context, Activity activity, List<FAQ> data, FaqHelp fragment) {
    this.context = context;
    this.data = data;
    this.filteredData = data;
    this.fragment=fragment;
    this.activity=activity;
    inflater = (LayoutInflater) context
      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

  }

  @Override
  public int getCount() {
    return filteredData.size();
  }
  @Override
  public Object getItem(int i) {
    return filteredData.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }
  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    if (view == null)
      view = inflater.inflate(R.layout.faq_row, null);

    faq_key= (TextView) view.findViewById(R.id.faq_key);
    faq_key.setText(filteredData.get(i).getKey());

    faq_value = (TextView) view.findViewById(R.id.faq_value);
    faq_value.setText(filteredData.get(i).getValue());

    imageViewExpand = (ImageView) view.findViewById(R.id.imageViewExpand);
    FAQ faq=filteredData.get(i);
    if(faq.isExpanded)
    {
      imageViewExpand.setImageResource(R.drawable.minus);
      faq_value.setVisibility(View.VISIBLE);
    }
    else
    {
      imageViewExpand.setImageResource(R.drawable.plus);
      faq_value.setVisibility(View.GONE);
    }

    ImageViewExpandOnClickListener imageViewExpandOnClickListener=new ImageViewExpandOnClickListener(faq,faq_value,imageViewExpand);
    imageViewExpand.setOnClickListener(imageViewExpandOnClickListener);

    view.setOnClickListener(imageViewExpandOnClickListener);

    FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), "Dubai-Regular.ttf");
    fontChanger.replaceFonts((ViewGroup) view);

    return view;
  }

  private class ImageViewExpandOnClickListener implements View.OnClickListener
  {
    FAQ faq;
    TextView faqValue;
    ImageView imageViewExpand;
    public ImageViewExpandOnClickListener(FAQ faq,TextView faqValue,ImageView imageViewExpand){
      this.faq=faq;
      this.faqValue=faqValue;
      this.imageViewExpand=imageViewExpand;
    }

    @Override
    public void onClick(View view) {

      if(faq.isExpanded)
      {
        faq.isExpanded=false;
        notifyDataSetChanged();
      }
      else{
        // set previously expanded row to false
        for(int i=0;i<filteredData.size();i++)
        {
          if(filteredData.get(i).isExpanded)
          {
            filteredData.get(i).isExpanded=false;
          }
        }
        //set current item expanded
        faq.isExpanded=true;
        notifyDataSetChanged();
      }


    }
  }

  @Override
  public Filter getFilter() {
    return new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint == null || constraint.length() == 0) {
          //no constraint given, just return all the data. (no search)
          results.count = data.size();
          results.values = data;
        } else {//do the search
          List<FAQ> resultsData = new ArrayList<>();
          for (FAQ o : data){
            if (o.getValue().contains(constraint)) {
              resultsData.add(o);
            }else if (o.getKey().contains(constraint)){
              resultsData.add(o);
            }
          }
          results.count = resultsData.size();
          results.values = resultsData;
        }
        return results;
      }

      @Override
      protected void publishResults(CharSequence constraint, FilterResults results) {
        filteredData = (ArrayList<FAQ>) results.values;
        notifyDataSetChanged();
      }
    };
  }
}
