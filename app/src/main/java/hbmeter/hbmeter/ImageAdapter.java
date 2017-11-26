package hbmeter.hbmeter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

/**
 * Created by Giuseppe on 25/10/2017.
 */

public class ImageAdapter extends PagerAdapter {
    private Context context;
    private int[] GalImages = new int[] {R.drawable.step21, R.drawable.step22,
            R.drawable.step23, R.drawable.step24};

    private static final String[] TEXTS = { "Come primo passo l'utente deve montare la lente macro in corrispondenza della fotocamera posteriore del proprio cellulare.", "Come secondo passo l'utente deve scattare una foto da analizzare, o sceglierne una tra quelle già presenti, cliccando sul bottone SCEGLI/SCATTA FOTO.", "Dopo aver scelto/scattato la foto, l'utente potrà effettuare la selezione dei superpixel relativi all'area interessata, tracciando una linea o selezionando i singoli superpixel.", "Dopo aver selezionato i superpixel, cliccando sul bottone analizza l'utente potrà analizzare la parte selezionata e verificarne il risultato." };

    LayoutInflater inflater;

    public ImageAdapter(Context context)
    {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == ((RelativeLayout) object);
    }

    @Override
    public int getCount()
    {
        return TEXTS.length;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        ((ViewPager) container).removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {

        View itemView;
        itemView = inflater.inflate(R.layout.fragment_analizza, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.viewPager);
        imageView.setImageResource(GalImages[position]);
        TextView topTextItem = (TextView) itemView.findViewById(R.id.txtsw);
        topTextItem.setText(TEXTS[position]);

        ((ViewPager) container).addView(itemView);

        return itemView;
    }
}