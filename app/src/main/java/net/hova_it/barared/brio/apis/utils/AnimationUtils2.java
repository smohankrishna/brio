package net.hova_it.barared.brio.apis.utils;

import android.app.Activity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

/**
 * Clase de utilieria de animaciones de Views para el uso general en
 *
 * Created by Herman Peralta on 27/11/2015.
 */
public class AnimationUtils2 {

    /**
     * Animación para rotar un TextView y ponerle un texto al terminar la animación.
     *
     * @param tv - El TextView a rotar
     * @param newText - El texto a ponerle al tv al finalizar la animación
     * @param deg - Los grados a rotar
     * @param millis - El tiempo de la animación en milisegundos
     */
    public static void rotateTextView (final TextView tv, final String newText, float deg, int millis) {
        tv.animate().rotation(deg).setDuration(millis).withEndAction(new Runnable() {
            @Override
            public void run() {
                tv.setRotation(0);
                tv.setText(newText);
            }
        });
    }

    /**
     * Metodo que se ocupa para enfatizar el cmbio de texto de un textview con una animacion
     *
     * @param tv - el textview a animar
     * @param newText - el texto a poner cuando termine la animación
     * @param millis - El tiempo de la animación en milisegundos
     */
    public static void fadeTextView (final TextView tv, final String newText, int millis) {
        tv.animate().alpha(0f).setDuration(millis).withEndAction(new Runnable() {
            @Override
            public void run() {
                tv.setAlpha(1f);
                tv.setText(newText);
            }
        });
    }

    /**
     * animar un BrioSearchView
     *
     * @param parent
     * @param listItem
     * @param animresid
     * @param duration - El tiempo de la animación en milisegundos
     */
    public static void animateRecyclerViewItem (Activity parent, View listItem, int animresid, int duration) {
        Animation anim = android.view.animation.AnimationUtils.loadAnimation(parent, animresid);
        anim.setDuration(duration);
        listItem.startAnimation(anim);
    }

    /**
     * Realizar una animación escalado sobre un view, para dar la idea de que fue presionado.
     * @param view - El view a animar
     * @param scaleTo - La escala a la
     * @param millis - El tiempo de la animación en milisegundos
     */
    public static void animateViewPush(final View view, float scaleTo, final int millis) {
        animateViewPush(view, scaleTo, millis, null);
    }

    /**
     * Realizar una animación escalado sobre un view, para dar la idea de que fue presionado.
     * Automaticamente hrá la animación inversa (regresar la escala a 1) para enfatizar el efecto de presionar
     * un botón.
     *
     * @param view - El view a animar
     * @param scaleTo - La escala a la
     * @param millis - El tiempo de la animación en milisegundos
     * @param doOnAnimationEnd - Acción a relizar cuando termine la animación
     */
    public static void animateViewPush(final View view, float scaleTo, final int millis, final Runnable doOnAnimationEnd) {
        view.animate().scaleX(scaleTo).scaleY(scaleTo).setDuration(millis)
                .withEndAction(
                        new Runnable() {
                            @Override
                            public void run() {
                                //todo: declarar objeto animation para no repetir codigo
                                if (doOnAnimationEnd != null) {
                                    view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(millis / 2).withEndAction(doOnAnimationEnd);
                                } else {
                                    view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(millis / 2);
                                }
                            }
                        }
                );
    }

    /**
     * Lo msimo que animateViewPush, pero personalizado para todos los botones de la aplicación
     * @param btn - el view a animar
     * @param doOnAnimationEnd - Acción a relizar cuando termine la animación
     */
    public static void animateButtonPush(final View btn, final Runnable doOnAnimationEnd) {
        animateViewPush(btn, 0.80f, 100, doOnAnimationEnd);
    }

    /**
     * Animar la una "limpieza" del view
     *
     * @param view - el view a animar
     * @param scaleTo - el factor de escala
     * @param millis - El tiempo de la animación en milisegundos
     */
    public static void animateViewClear(final View view, float scaleTo, final int millis) {
        view.animate().rotation(scaleTo).setDuration(millis)
                .withEndAction(
                        new Runnable() {
                            @Override
                            public void run() {
                                view.animate().rotation(0f).setDuration(millis / 2);
                            }
                        }
                );
    }

    /**
     * Animar un view con un parpadeo o blink
     * @param view - El view a animar
     * @param millis - El tiempo de la animación en milisegundos
     */
    public static void animateBlink(final View view, long millis) {
        Animation mAnimation = new AlphaAnimation(1, 0);
        mAnimation.setDuration(millis);
        mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());//LinearInterpolator
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);

        view.startAnimation(mAnimation);
    }
}
