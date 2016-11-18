package gmf.com.evan.controller.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import gmf.com.evan.R;
import gmf.com.evan.controller.fragment.MainFragments;
import gmf.com.evan.extension.Optional;
import rx.functions.Func0;

import static gmf.com.evan.extension.ObjectExtension.opt;

/**
 * Created by Evan on 16/7/9 下午5:31.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
    }


    private interface CreateFragmentFunc extends Func0<Fragment> {
    }

    Class[] sClazzList = {
            MainFragments.ADHomeFragment.class,
            MainFragments.StockHomeFragment.class,
            MainFragments.InvestHomeFragment.class,
            MainFragments.ConversationFragment.class,
            MainFragments.MineFragment.class
    };

    CreateFragmentFunc[] sActionList = {
            MainFragments.ADHomeFragment::new,
            MainFragments.StockHomeFragment::new,
            MainFragments.InvestHomeFragment::new,
            MainFragments.ConversationFragment::new,
            MainFragments.MineFragment::new
    };

    private void onTabClick(int index) {

    }

    private Optional<Fragment> mCurrentFragment = Optional.empty();

    private void replaceCurrentFragment(int index, Class clazz, CreateFragmentFunc func) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment cacheFragment = manager.findFragmentByTag(clazz.getSimpleName());
        Fragment newFragment = cacheFragment == null ? func.call() : null;
        FragmentTransaction transaction = manager.beginTransaction();
        if (cacheFragment != null) {
            transaction.show(cacheFragment);
            cacheFragment.setUserVisibleHint(true);
        } else {
            transaction.add(R.id.fragment_container, newFragment, clazz.getSimpleName());
        }

        if (mCurrentFragment.isPresent()) {
            mCurrentFragment.get().setUserVisibleHint(false);
            transaction.hide(mCurrentFragment.get());
        }
        transaction.commit();
        mCurrentFragment = opt(cacheFragment != null ? cacheFragment : newFragment);
    }


}
