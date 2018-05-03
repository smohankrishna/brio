package lat.brio.api.botones;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.AnimationUtils2;
import net.hova_it.barared.brio.apis.utils.Utils;


/**
 * Created by Delgadillo on 11/04/17.
 */
public class Cerrar extends RelativeLayout {

    private Context context;
    private View.OnClickListener onClickListener;

    private View vroot, vicon;

    public Cerrar(Context context) {
        super(context);

        init(context, null);
    }

    public Cerrar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public Cerrar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;

        LayoutInflater.from(this.context).inflate(R.layout.view_brio_close_button, this);

        vroot = findViewById(R.id.view_briobutton_root);
        vicon = vroot.findViewById(R.id.view_briobutton_icon);
    }

    @Override
    public void setOnClickListener(OnClickListener l){
        onClickListener = l;

        vroot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (onClickListener != null && Utils.shouldPerformClick()) {

                    if (vroot.isEnabled()) {

                        vroot.setEnabled(false);

                        AnimationUtils2.animateButtonPush(view, new Runnable() {
                            @Override
                            public void run() {

                                onClickListener.onClick(Cerrar.this);

                                vroot.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        vroot.setEnabled(true);
                                    }
                                });
                            }

                        });
                    }
                }
            }
        });
    }
}