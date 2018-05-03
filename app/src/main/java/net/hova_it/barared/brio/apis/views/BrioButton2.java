package net.hova_it.barared.brio.apis.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import net.hova_it.barared.brio.apis.utils.AnimationUtils2;
import net.hova_it.barared.brio.apis.utils.Utils;

/**
 * Botón personalizado para la aplicación.
 * Es necesario especificar el atributo style
 * para que el botón quede en color rosa, naranja o gris
 * (tema de Brio).
 *
 * res/values/styles/
 *     BrioButton.Gris
 *     BrioButton.Naranja
 *     BrioButton.Rosa
 *
 * Created by Herman Peralta on 30/03/2016.
 */
public class BrioButton2 extends Button implements View.OnClickListener {

    private OnClickListener onClickListener;

    public BrioButton2(Context context) {
        super(context);
    }

    public BrioButton2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BrioButton2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BrioButton2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;

        super.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        if (onClickListener != null && Utils.shouldPerformClick()) {
            if (view.isEnabled()) {

                view.setEnabled(false);

                AnimationUtils2.animateButtonPush(view, new Runnable() {
                    @Override
                    public void run() {
                        onClickListener.onClick(view);

                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                view.setEnabled(true);
                            }
                        });
                    }
                });

            }
        }
    }
}
