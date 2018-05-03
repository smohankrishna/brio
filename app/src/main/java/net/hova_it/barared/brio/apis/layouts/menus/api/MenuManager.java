package net.hova_it.barared.brio.apis.layouts.menus.api;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.BrioManagerInterface;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.mibrio.MenuMiBrioFragment;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.util.Stack;

/**
 * Maneja lo relacionado a un Menu
 *
 * Cada Menu consta de un MenuManager y un MenuFragment.
 *     * El MenuFragment es el que contiene la lista de opciones del menu (RecyclerView).
 *           Cada opcion debe llevar a un fragment (o a un submenu).
 *     * El MenuManager se encarga de llevar el registro de la opcion abierta actual.
 *           Es necesario que los tags @param{TAG_MENU_FRAGMENT} y @param{TAG_CURRENT_FRAGMENT}
 *               sean distintos entre si y ademas distintos para cada implementacion de MenuManager,
 *               debido a que estos son los que se usan en el fragmentTransaction para agregar los
 *               fragments al FragmentManager.
 *           Cada opcion del menu debe llevar a un fragment, i.e., cuando el usuario presiona una
 *               opcion del menu, y para esto se invoca a onShowOptionFragment(fragment) donde fragment
 *               es la opcion del menu (el mapeo de opcion / logica se hace en el FragmentListButtonListener
 *               que se regresa por el metodo getMenulistener).
 *           Si se requiere un submenu, el layout del MenuFragment debe tener un layout con id "submenuHolder"
 *               y usar la funcion onShowSubMenu(submenuManager) con el manager del submenu.
 *     * Las opciones que muestra el MenuFragment se relacionan con un string-array en res/arrays, i.e.,
 *           el MenuFragment llena el RecyclerView con los strings de un array de los xml.
 *     * El mapeo (switch) de la opcion elegida y la logica por detras se hace en una instancia
 *           FragmentListButtonListener la cual se regresa por el metodo abstracto getMenulistener.
 *
 *
 * http://stackoverflow.com/questions/6987334/separate-back-stack-for-each-tab-in-android-using-fragments
 * Created by Herman Peralta on 09/03/2016.
 */
public abstract class MenuManager implements BrioManagerInterface, MenuFragmentController {
    private String TAG_MENU_FRAGMENT;

    protected Context context;
    private BrioActivityMain mainActivity;

    protected OptionMenuFragment menuFragment;
    protected FragmentManager fragmentManager;
    protected String[] opciones;

    private Stack<Fragment> daStack;

    private MenuManager subMenuManager;
    private boolean isSubMenu;

    protected MenuManager(Context context, String tag_menu_fragment, int resid_array_string_menu, boolean isSubMenu) {
        this.context = context;
        this.mainActivity = (BrioActivityMain)context;

        this.TAG_MENU_FRAGMENT = tag_menu_fragment;
        this.opciones = Utils.getStringArray(resid_array_string_menu, context);

        this.daStack = new Stack<>();
        this.isSubMenu = isSubMenu;
    }

    /**
     * Recupera todos los fragments de la pila y los agrega al activity
     *
     * @param fragmentManager
     * @param containerId Id de la vista
     * @return
     */
    @Override
    public Fragment createFragments(FragmentManager fragmentManager, int containerId) {
        DebugLog.log(getClass(), "MENUS", "");
        this.fragmentManager = fragmentManager;

        //(1) Recuperar el fragment del menu
        DebugLog.log(getClass(), "MENUS", "(1) Recuperar el fragment del menu");
        Fragment tmpMenu = fragmentManager.findFragmentByTag(TAG_MENU_FRAGMENT);
        if (tmpMenu == null) {
            DebugLog.log(getClass(), "MENUS", "tmpMenu era null, instanciando");
            menuFragment = getMenuFragmentInstance();
            ((MenuFragment) menuFragment).setMenuListener(getMenulistener());

            daStack.push(menuFragment); //Solo lo ingreso a pila si es nuevo
            DebugLog.log(getClass(), "TEST", "instanciado y en pila");
        } else {
            DebugLog.log(getClass(), "MENUS", "tmpMenu no era null, recuperando");
            menuFragment = (MenuFragment) tmpMenu;

            //if(isSubMenu) { DebugLog.log(getClass(), "MENUS", "recuperado y en pila"); daStack.push(menuFragment); }

            //daStack.push(menuFragment);
            if(daStack.size()>0) {
                daStack.set(0, menuFragment);
            } else {
                daStack.push(menuFragment);
            }
        }

        this.fragmentManager.beginTransaction().add(containerId, menuFragment, TAG_MENU_FRAGMENT).addToBackStack(null).commit();

        //(2) Revierto el estado del menu
        DebugLog.log(getClass(), "MENUS", "(2) Revierto el estado del menu");
        for(int i=1 ; i<daStack.size() ; i++) {
            OptionMenuFragment top = (OptionMenuFragment) daStack.get(i);
            top.beforeRemove();
            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                fragmentTransaction.add(containerId, top);
                fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        //(3) Restaurar el submenu (si lo hay)
        DebugLog.log(getClass(), "MENUS", "(3) Restaurar el submenu (si lo hay)");
        if(subMenuManager != null) {
            subMenuManager.createFragments(fragmentManager, R.id.fragmentHolder);
        }

        if( (!isSubMenu && daStack.size()>1) || (isSubMenu && daStack.size()>0) ) {
            mainActivity.enableBackNavigation();
        }

        DebugLog.log(getClass(), "PILA", "Hay " + daStack.size() + " fragments en pila local");

        return menuFragment;
    }

    /**
     * Quita todos los fragments del activity (pero no de la pila).
     */
    @Override
    public void removeFragments() {
        DebugLog.log(getClass(), "MENUS", "");
        mainActivity.disableBackNavigation();

        if(subMenuManager != null) {
            subMenuManager.removeFragments();

            if( subMenuManager.daStack.size() == 0 ) {
                DebugLog.log(getClass(), "MENU", "la pila del submenu es 0");

                subMenuManager = null;
            }
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            for(int i=daStack.size() - 1 ; i>=0 ; i--) {
                Fragment top = daStack.get(i);
                fragmentTransaction.remove(top);
            }
        fragmentTransaction.commit();

        mainActivity.managerTeclado.closeKeyboard();

        DebugLog.log(getClass(), "PILA", "Hay " + daStack.size() + " fragments en pila local");
    }

    /**
     * Muestra un fragment de opción de menú.
     *
     * @param topFragment
     */
    public void onShowOptionFragment(OptionMenuFragment topFragment) {
        DebugLog.log(getClass(), "MENUS", "");
        topFragment.setMenuFragmentController(MenuManager.this);
        topFragment.setMenuManager(MenuManager.this);

        fragmentManager
            .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                //Todo podria servir meterle tag
                .add(R.id.fragmentHolder, topFragment)
                .addToBackStack(null)
            .commit();

        daStack.push(topFragment);
        DebugLog.log(getClass(), "PILA", "Hay " + daStack.size() + " fragments en pila local");

        mainActivity.enableBackNavigation();
    }

    /**
     * Quita un fragment de opcion de menú de la pila y del activity.
     */
    @Override
    public void onRemoveOptionFragment() {
        DebugLog.log(getClass(), "MENUS", "");
        if(subMenuManager != null) {
            subMenuManager.onRemoveOptionFragment();

            if( subMenuManager.daStack.size() == 0 ) {
                DebugLog.log(getClass(), "MENU", "la pila del submenu es 0");

                subMenuManager = null;
            }

            return;
        }

        if( (!isSubMenu && daStack.size()>1) || (isSubMenu && daStack.size()>0) ) {
            OptionMenuFragment top = (OptionMenuFragment) daStack.pop();
            top.beforeRemove();
            fragmentManager.beginTransaction().remove(top).commit();

            mainActivity.enableBackNavigation();
        }

        if( !isSubMenu && daStack.size()==1 ) {
            mainActivity.disableBackNavigation();
        }

        if( isSubMenu && daStack.size()==0 ) {
            mainActivity.enableBackNavigation();
        }

        mainActivity.managerTeclado.closeKeyboard();

        DebugLog.log(getClass(), "PILA", "Hay " + daStack.size() + " fragments en pila local");
    
        for ( Fragment fgmt:fragmentManager.getFragments ()) {
            if(fgmt instanceof MenuMiBrioFragment){
                if(((MenuMiBrioFragment)     fgmt).mibrio){
                    MenuMiBrioFragment.updateItems ();
                }
            }
        }
        
    }

    /**
     * Muestra un submenu
     *
     * @param subMenuManager
     * @param resid
     */
    public void onShowSubMenu(MenuManager subMenuManager, int resid) {
        DebugLog.log(getClass(), "MENUS", "Voy a submenu");

        this.subMenuManager = subMenuManager;

        this.subMenuManager.createFragments(fragmentManager, resid);

        mainActivity.managerTeclado.closeKeyboard();
    }

    protected abstract FragmentListButtonListener getMenulistener();

    /**
     * Obtener la instancia del fragment a mostrar en el MenuManager.
     *
     * @return
     */
    protected abstract MenuFragment getMenuFragmentInstance();
}
