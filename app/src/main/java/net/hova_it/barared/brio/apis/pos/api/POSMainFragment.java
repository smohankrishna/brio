package net.hova_it.barared.brio.apis.pos.api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.pos.POSManager;
import net.hova_it.barared.brio.apis.pos.search.SearchManager;
import net.hova_it.barared.brio.apis.utils.DebugLog;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.views.BrioSearchView;

import java.util.List;

/**
 * Fragment base del POS.
 *
 * Tiene la estructura para contener todos los fragments descritos en el POSManager.
 *
 * http://stackoverflow.com/questions/26993853/viewpager-inside-fragment-how-to-retain-state
 *
 * Created by Herman Peralta on 04/04/2016.
 */
public class POSMainFragment extends Fragment {

    private View rootView;

    //Viewpager
    private ViewPager viewPager;

    private TicketPagerAdapter pagerAdapter;
    public List<POSVenta> pagesVentas;
    private int current_page = 0;

    private FragmentManager fragmentManager;
    public PageSwapListener pageSwapListener = new PageSwapListener();

    private POSManager managerPOS;
    private BrioSearchView posSearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        fragmentManager = getChildFragmentManager();//(getActivity()).getSupportFragmentManager();

        pagerAdapter = new TicketPagerAdapter(fragmentManager);

        if(savedInstanceState != null) {
            (POSMainFragment.this.getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DebugLog.log(getClass(), "POS", "Refresco el viewpager");

                    pagerAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.pos_main_fragment, container, false);

   

        managerPOS = POSManager.getInstance(getActivity());

        return rootView;
    }

    private void pruebaBotonesPistola() {
        //todo: test pistola con boton
        rootView.findViewById(R.id.btnPistola).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugLog.log(POSMainFragment.this.getClass(), "POS", "Fake pistola 1");
                managerPOS.onInputLineMatch("7501001147563");
            }
        });

        rootView.findViewById(R.id.btnPistola2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugLog.log(POSMainFragment.this.getClass(), "POS", "Fake pistola 2");
                managerPOS.onInputLineMatch("7501041201218");
            }
        });
        rootView.findViewById(R.id.btnPistola3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugLog.log(POSMainFragment.this.getClass(), "POS", "Fake pistola 3");
                managerPOS.onInputLineMatch("7502252631115"/*7993661842100063747908611794150"*/);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        initVentasViewPager();

        for (POSVenta venta : pagesVentas) {
            venta.setPageSwapListener(pageSwapListener);
        }

        (POSMainFragment.this.getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DebugLog.log(getClass(), "POS", "ONRESUME Refresco el viewpager");

                pagerAdapter.notifyDataSetChanged();
            }
        });

        SearchManager.getInstance(getActivity()).configure(R.id.pos_search, R.id.pos_container);
    }

    /**
     * http://www.truiton.com/2015/06/android-tabs-example-fragments-viewpager/
     * http://stackoverflow.com/questions/33015652/tablayout-tab-title-text-in-lower-case
     */
    private void initVentasViewPager() {
        DebugLog.log(getClass(), "POSManager", "configura el viewpager");

        final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        for(int t=0 ; t<POSManager.NUM_TICKETS ; t++) {
            tabLayout.addTab(tabLayout.newTab().setText(Utils.getString(R.string.pos_ticket_title, getActivity()).replace("?", "" + (t+1))));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) rootView.findViewById(R.id.pos_pager);
        posSearch = (BrioSearchView) ((BrioBaseActivity) getActivity ()).findViewById(R.id.pos_search);

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager.setOffscreenPageLimit(POSManager.NUM_TICKETS - 1); //El visible + (NUM_TICKETS-1) no visibles = NUM_TICKETS
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current_page = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                current_page = tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public static String getPageTag(int position) {
        return "android:switcher:" + R.id.pos_pager + ":" + position;
    }

    public POSVenta getCurrentVenta() {
        return pagesVentas.get(current_page);
    }

    public class PageSwapListener implements ViewPagerFragmentSwapListener {

        int status;

        @Override
        public void muestraTicket(int position) {
            DebugLog.log(getClass(), "POS", "muestra ticket");

            status = POSVenta.STATUS_TICKET;

            replacePage(position);
        }

        @Override
        public void muestraCliente(int position) {
            DebugLog.log(getClass(), "POS", "muestra cliente");

            status = POSVenta.STATUS_CLIENTE;

            replacePage(position);
        }

        private void replacePage(int position) {
            //Aqui a quito el fragment actual
            fragmentManager.beginTransaction()
                    .remove(pagesVentas.get(position).getFragment())
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();

            //aqui le digo que lo intercambie por el deseado
            pagesVentas.get(position).setStatus(status);

            //aqui refresco el viewpager, que llamara a su adapter, pero como POSVenta ya cambio de estado, regresará el fragment adecuado
            (POSMainFragment.this.getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DebugLog.log(PageSwapListener.this.getClass(), "POS", "Refresco el viewpager");

                    pagerAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * El adapter para el viewpager de las ventas en paralelo
     *
     * http://stackoverflow.com/questions/13664155/dynamically-add-and-remove-view-to-viewpager
     */
    public class TicketPagerAdapter extends FragmentStatePagerAdapter {

        private Fragment mCurrentTicket;

        public TicketPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle (int position) {
            return "title";//fixme //mainActivity.getResources().getString(R.string.pos_ticket_title).replace("?", ""+(position + 1));
        }

        public Fragment getCurrentFragment() {
            return mCurrentTicket;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment ret = pagesVentas.get(position).getFragment();

            DebugLog.log(getClass(), "POS", "el fragment " + position + " es null? " + (ret==null));

            return ret;
        }

        @Override
        public int getCount() {
            return POSManager.NUM_TICKETS;
        }

        /**
         * Mantener la página actual (fragment visible) en mCurrentFragment
         *
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentTicket = ((Fragment) object);
            }

            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            if(!pagesVentas.contains(object)) {
                return POSITION_NONE;
            }

            return POSITION_UNCHANGED;
        }
    }

    public interface ViewPagerFragmentSwapListener {
        void muestraTicket (int position);
        void muestraCliente (int position);
    }
}
