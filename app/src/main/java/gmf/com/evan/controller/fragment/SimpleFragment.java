package gmf.com.evan.controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.annimon.stream.Stream;

import java.lang.ref.WeakReference;
import java.util.List;

import gmf.com.evan.R;
import gmf.com.evan.base.MResults;
import gmf.com.evan.extension.common.function.SafeAction0;
import gmf.com.evan.extension.common.function.SafeAction1;
import gmf.com.evan.rx.ConsumeEventChainMR;
import gmf.com.evan.rx.ConsumeEventChainMRList;
import rx.Observable;
import rx.functions.Action1;

import static gmf.com.evan.extension.MResultExtension.getErrorMessage;
import static gmf.com.evan.extension.ObjectExtension.safeCall;
import static gmf.com.evan.extension.UIControllerExtension.showToast;
import static gmf.com.evan.extension.ViewExtension.v_setOnFreshListener;
import static gmf.com.evan.extension.ViewExtension.v_setText;

/**
 * Created by Evan on 16/7/14 上午10:02.
 */
public class SimpleFragment extends BaseFragment {
    public static final int TYPE_LOADING = 1;
    public static final int TYPE_RELOAD = 1 << 1;
    public static final int TYPE_CONTENT = 1 << 2;
    public static final int TYPE_EMPTY = 1 << 3;
    public static final int FLAG_DISABLE_FORCE_SHOW_SWIPE_REFRESH_LAYOUT = 1;
    protected SwipeRefreshLayout mRefreshLayout;
    protected View mContentSection;
    protected View mLoadingSection;
    protected View mReloadSection;
    protected View mEmptySection;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRefreshLayout = safeFindView(view, SwipeRefreshLayout.class, getRefreshLayoutID(), getContentSectionID());
        mContentSection = safeFindView(view, View.class, getContentSectionID());
        mLoadingSection = safeFindView(view, View.class, getLoadingSectionID());
        mReloadSection = safeFindView(view, View.class, getReloadSectionID());
        mEmptySection = safeFindView(view, View.class, getEmptySectionID());
    }

    public void setEmptySectionTips(CharSequence title, CharSequence subtitle) {
        v_setText(mEmptySection, R.id.label_title, title);
        v_setText(mEmptySection, R.id.label_subtitle, subtitle);
    }

    public void setReloadSectionTips(CharSequence text) {
        if (mReloadSection != null)
            v_setText(mReloadSection, R.id.label_title, text);
    }

    public void setLoadingSectionTips(CharSequence text) {
        if (mLoadingSection != null)
            v_setText(mLoadingSection, R.id.label_title, text);
    }

    public void setSwipeRefreshable(boolean isRefreshable) {
        if (mRefreshLayout != null) {
            mRefreshLayout.setEnabled(isRefreshable);
            mRefreshLayout.requestDisallowInterceptTouchEvent(!isRefreshable);
        }
    }

    public void setOnSwipeFreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        if (mRefreshLayout != null && listener != null) {
            mRefreshLayout.setEnabled(true);
            v_setOnFreshListener(mRefreshLayout, listener);
        }
    }

    public void setOnSwipeFreshListener(Action1<SwipeRefreshLayout> callback) {
        if (mRefreshLayout != null && callback != null) {
            mRefreshLayout.setEnabled(true);
            v_setOnFreshListener(mRefreshLayout, callback);
        }
    }

    public void setSwipeFreshing(boolean value) {
        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(value);
        }
    }

    protected int getRefreshLayoutID() {
        return R.id.refreshlayout;
    }

    protected int getLoadingSectionID() {
        return R.id.section_loading;
    }

    protected int getReloadSectionID() {
        return R.id.section_reload;
    }

    protected int getContentSectionID() {
        return R.id.section_content;
    }

    protected int getEmptySectionID() {
        return R.id.section_empty;
    }

    public void changeVisibleSection(int type) {
        View[] sections = {mContentSection, mLoadingSection, mReloadSection, mEmptySection};
        Integer[] types = {TYPE_CONTENT, TYPE_LOADING, TYPE_RELOAD, TYPE_EMPTY};
        Stream.zip(Stream.of(sections), Stream.of(types), (first, second) -> Pair.create(first, second))
                .filter(it -> it.first != null)
                .forEach(it -> {
                    int relatedType = it.second;
                    boolean isVisible = (relatedType & type) == relatedType;
                    it.first.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                });
    }

    public <T extends MResults.MResultsInfo> ConsumeEventChainMR<T> consumeEventMRUpdateUI(Observable<T> observable, boolean isReload) {
        return consumeEventMRUpdateUI(observable, isReload, 0);
    }

    public <T extends MResults.MResultsInfo> ConsumeEventChainMR<T> consumeEventMRUpdateUI(Observable<T> observable, boolean isReload, int flags) {
        return consumeEventMRUpdateUI(new WeakReference<SimpleFragment>(this), observable, isReload, flags);
    }

    private static <T extends MResults.MResultsInfo> ConsumeEventChainMR<T> consumeEventMRUpdateUI(WeakReference<SimpleFragment> vcRef, Observable<T> observable, boolean isReload, int flags) {
        return new ConsumeEventChainMR<T>(vcRef.get()) {

            @Override
            public SafeAction0 getOnConsumed() {
                SafeAction0 operation = super.getOnConsumed();
                return () -> {
                    if (vcRef.get() != null) {
                        SimpleFragment vc = vcRef.get();
                        if (isReload) {
                            vc.changeVisibleSection(TYPE_LOADING);
                        } else {
                            boolean isDisableForceShowSwipeRefreshLayout = (flags & FLAG_DISABLE_FORCE_SHOW_SWIPE_REFRESH_LAYOUT) != 0;
                            if (!isDisableForceShowSwipeRefreshLayout) {
                                vc.setSwipeFreshing(true);
                            }
                        }
                    }

                    if (operation != null) {
                        safeCall(() -> operation.call());
                    }
                };
            }

            @Override
            public SafeAction1<T> getOnNextSuccess() {
                SafeAction1<T> operation = super.getOnNextSuccess();
                return response -> {
                    if (vcRef.get() != null) {
                        SimpleFragment vc = vcRef.get();
                        vc.changeVisibleSection(TYPE_CONTENT);
                    }

                    if (operation != null) {
                        safeCall(() -> operation.call(response));
                    }
                };
            }

            @Override
            public SafeAction1<T> getOnNextFail() {
                SafeAction1<T> operation = super.getOnNextFail();
                return response -> {
                    if (vcRef.get() != null) {
                        SimpleFragment vc = vcRef.get();
                        if (isReload) {
                            vc.setReloadSectionTips(response.msg);
                            vc.changeVisibleSection(TYPE_RELOAD);
                        } else {
                            vc.changeVisibleSection(TYPE_CONTENT);
                            showToast(vc, response.msg);
                        }
                    }

                    if (operation != null) {
                        safeCall(() -> operation.call(response));
                    }
                };
            }

            @Override
            public SafeAction1<T> getOnNextFinish() {
                SafeAction1<T> operation = super.getOnNextFinish();
                return response -> {
                    if (vcRef.get() != null) {
                        SimpleFragment vc = vcRef.get();
                        vc.setSwipeFreshing(false);
                    }

                    if (operation != null) {
                        safeCall(() -> operation.call(response));
                    }
                };
            }
        }.setObservable(observable);
    }

    public <T extends List<MResults.MResultsInfo>> ConsumeEventChainMRList<T> consumeEventMRListUpdateUI(Observable<T> observable, boolean isReload) {
        return consumeEventMRListUpdateUI(observable, isReload, 0);
    }

    public <T extends List<MResults.MResultsInfo>> ConsumeEventChainMRList<T> consumeEventMRListUpdateUI(Observable<T> observable, boolean isReload, int flags) {
        return consumeEventMRListUpdateUI(new WeakReference<SimpleFragment>(this), observable, isReload, flags);
    }

    private static <T extends List<MResults.MResultsInfo>> ConsumeEventChainMRList<T> consumeEventMRListUpdateUI
            (WeakReference<SimpleFragment> vcRef, Observable<T> observable, boolean isReload,
             int flags) {
        return new ConsumeEventChainMRList<T>(vcRef.get()) {

            @Override
            public SafeAction0 getOnConsumed() {
                SafeAction0 operation = super.getOnConsumed();
                return () -> {
                    if (vcRef.get() != null) {
                        SimpleFragment vc = vcRef.get();
                        if (isReload) {
                            vc.changeVisibleSection(TYPE_RELOAD);
                        } else {
                            boolean isDisableForceShowSwipeRefreshLayout = (flags & FLAG_DISABLE_FORCE_SHOW_SWIPE_REFRESH_LAYOUT) != 0;
                            if (!isDisableForceShowSwipeRefreshLayout) {
                                vc.setSwipeFreshing(true);
                            }
                        }
                    }

                    if (operation != null) {
                        safeCall(() -> operation.call());
                    }
                };
            }

            @Override
            public SafeAction1<T> getOnNextSuccess() {
                SafeAction1<T> operation = super.getOnNextSuccess();
                return response -> {
                    if (vcRef.get() != null) {
                        SimpleFragment vc = vcRef.get();
                        vc.changeVisibleSection(TYPE_CONTENT);
                    }

                    if (operation != null) {
                        safeCall(() -> operation.call(response));
                    }
                };
            }

            @Override
            public SafeAction1<T> getOnNextFail() {
                SafeAction1<T> operation = super.getOnNextFail();
                return response -> {
                    if (vcRef.get() != null) {
                        SimpleFragment vc = vcRef.get();
                        if (isReload) {
                            vc.changeVisibleSection(TYPE_RELOAD);
                            vc.setReloadSectionTips(getErrorMessage(response));
                        } else {
                            vc.changeVisibleSection(TYPE_CONTENT);
                            showToast(vc, getErrorMessage(response));
                        }
                    }

                    if (operation != null) {
                        safeCall(() -> operation.call(response));
                    }
                };
            }

            @Override
            public SafeAction1<T> getOnNextFinish() {
                SafeAction1<T> operation = super.getOnNextFinish();
                return response -> {
                    if (vcRef.get() != null) {
                        SimpleFragment vc = vcRef.get();
                        vc.setSwipeFreshing(false);
                    }

                    if (operation != null) {
                        safeCall(() -> operation.call(response));
                    }
                };
            }
        };
    }

    protected <T extends View> T safeFindView(View view, Class<T> clazz, int... candidates) {

        if (view == null)
            return null;

        for (int viewID : candidates) {

            View child = view.findViewById(viewID);
            if (child != null && clazz.isInstance(child)) {
                return (T) child;
            }
        }
        return null;
    }


}
