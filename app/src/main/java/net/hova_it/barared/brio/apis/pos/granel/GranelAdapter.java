package net.hova_it.barared.brio.apis.pos.granel;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.utils.MediaUtils;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador del grid de artículos a granel del POS.
 *
 * Created by Herman Peralta on 17/12/2015.
 */
public class GranelAdapter extends RecyclerView.Adapter<GranelAdapter.Holder> {
    private final static String KEY_LOG = GranelAdapter.class.getSimpleName();

    public final static String ITEM_CODBARRAS_VARIOS = "ITEM_CODBARRAS_VARIOS";

    //private final static String BRIO_IMG_DIR = android.os.Environment.getExternalStorageDirectory() + "/BrioTest/Images/";

    private List<GranelItem> items;
    private Context context;
    private int mPreviousPosition = 0;

    private FragmentListButtonListener fragmentListButtonListener;

    //private Bitmap imgtest;
    private static final String iconname = "granel_?.jpg";
    private static final int nicons = 47;

    public GranelAdapter(Context context, FragmentListButtonListener listener) {
        this.context = context;
        this.items = new ArrayList<>();
        fragmentListButtonListener = listener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.pos_granel_item, null);
        return new Holder(item);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        GranelItem item = items.get(position);

        holder.itemView.setTag(item);
        holder.tvNombre.setText(item.getName());

        //Integer imgn = r.nextInt(nicons - 1) + 1;
        //Bitmap img = MediaUtils.getImageFromFS(Utils.getBrioInternalPath(context) + iconname.replace("?", imgn.toString()));

        int id = item.getViewArticulo().getIdArticulo();// % nicons;
        Bitmap img = MediaUtils.getImageFromFS(Utils.getBrioInternalPath(context) + iconname.replace("?", ""+id));

        if(img != null) {
            holder.ivFoto.setImageBitmap(img);
        } else {
            holder.ivFoto.setImageResource(R.drawable.pos_granel_ic_notfound);
        }

        /*
        //todo animar
        if (position > mPreviousPosition) {
            //Todo: animar salida
        } else {
            //Todo: animar entrada
        }
        */

        mPreviousPosition = position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(List<GranelItem> data) {
        if (data != null) {
            items = new ArrayList<>();
            items.addAll(data);
        }
        this.notifyDataSetChanged();
    }

    public void setItemClickListener(FragmentListButtonListener listener) {
        this.fragmentListButtonListener = listener;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvNombre;
        public ImageView ivFoto;

        public Holder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            tvNombre = (TextView)itemView.findViewById(R.id.granel_tv_nombre);
            ivFoto = (ImageView)itemView.findViewById(R.id.granel_iv_foto);
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(context, "Clicked granel position = " + getPosition(), Toast.LENGTH_SHORT).show();

            //items.add(new GranelItem("Hola", R.drawable.test_granel));

            //notifyItemInserted(items.size() - 1);
            if(fragmentListButtonListener != null) {
                fragmentListButtonListener.onListButtonClicked(view, view.getTag());
            } else {
                Log.e(KEY_LOG, "El listener es null");
            }
        }
    }
}
