package gmf.com.evan.controller.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import gmf.com.evan.R;
import gmf.com.evan.base.KeepClassProtocol;
import gmf.com.evan.controller.activity.CommonProxyActivity;
import gmf.com.evan.controller.business.NotificationCenter;
import gmf.com.evan.controller.dialog.GMFDialog;
import gmf.com.evan.controller.dialog.ShareDialog.SharePlatform;
import gmf.com.evan.controller.internal.RegexPatternHolder;
import gmf.com.evan.controller.protocol.UMShareHandlerProtocol;
import gmf.com.evan.extension.Optional;
import gmf.com.evan.extension.SignalColorHolder;
import gmf.com.evan.manager.common.CommonManager;
import gmf.com.evan.manager.common.ShareInfo;
import gmf.com.evan.rx.ProxyCompositionSubscription;
import gmf.com.evan.widget.GMFWebView;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

import static gmf.com.evan.extension.SpannableStringExtension.concat;
import static gmf.com.evan.extension.SpannableStringExtension.setFontSize;
import static gmf.com.evan.extension.UIControllerExtension.runOnMain;
import static gmf.com.evan.extension.UIControllerExtension.setStatusBarBackgroundColor;
import static gmf.com.evan.extension.ViewExtension.dp2px;
import static gmf.com.evan.extension.ViewExtension.sp2px;
import static gmf.com.evan.extension.ViewExtension.v_findView;
import static gmf.com.evan.extension.ViewExtension.v_setGone;
import static gmf.com.evan.extension.ViewExtension.v_setVisibility;
import static gmf.com.evan.extension.ViewExtension.v_setVisible;


/**
 * Created by Evan on 16/7/16 下午5:23.
 */
public class WebViewFragments {

    public static PublishSubject<Pair<ShareInfo, SharePlatform[]>> sEditDefalutShareInfoSubject = PublishSubject.create();
    public static PublishSubject<Pair<ShareInfo, SharePlatform[]>> sPerformScreenShotShareSubject = PublishSubject.create();
    public static PublishSubject<Pair<ShareInfo, SharePlatform[]>> sShowShareButtonSubject = PublishSubject.create();
    public static PublishSubject<Pair<ShareInfo, SharePlatform[]>> sHideShareButtonSubject = PublishSubject.create();

    private WebViewFragments() {
    }

    public static class WebViewFragmentDelegate {

        private SimpleFragment mFragment;
        private GMFWebView mWebView;
        private boolean mHasRequestLogin = false;
        private boolean mAllowToInterceptGoBack = true;
        private boolean mResetHeight;
        private boolean mAllowInterceptGoBack = true;
        private ProxyCompositionSubscription mSubscription = ProxyCompositionSubscription.create();

        public WebViewFragmentDelegate(SimpleFragment fragment, GMFWebView webview) {
            this(fragment, webview, false);
        }

        public WebViewFragmentDelegate(SimpleFragment fragment, GMFWebView webView, boolean resetHeight) {
            mFragment = fragment;
            mWebView = webView;
            mResetHeight = resetHeight;
        }

        public void onViewCreated(String requestURL) {
            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    v_setGone(mFragment.mLoadingSection);
                    if (mResetHeight) {
                        view.loadUrl("javascript:window.common._updateHeight(document.body.scrollHeight)");
                    }
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            });

            if (isSecureRequest(requestURL)) {
                mWebView.addJavascriptInterface(new KeepClassProtocol() {

                    @JavascriptInterface
                    public void nativeopen(String link) {
                        getActivity().runOnUiThread(() -> {

                        });
                    }

                    @JavascriptInterface
                    public void nativeLogin() {
                        getActivity().runOnUiThread(() -> {

                        });
                    }

                    @JavascriptInterface
                    public void nativeAlert(String title, String msg) {
                        getActivity().runOnUiThread(() -> {
                            GMFDialog.Builder builder = new GMFDialog.Builder(getActivity());
                            builder.setTitle(title);
                            builder.setMessage(msg);
                            builder.setPositiveButton("确定");
                            builder.create().show();
                        });
                    }

                    @JavascriptInterface
                    public void nativeClose() {
                        getActivity().runOnUiThread(() -> {

                        });
                    }

                    @JavascriptInterface
                    public void riskTestResult() {
                        getActivity().runOnUiThread(() -> {

                        });
                    }

                    @JavascriptInterface
                    public void riskTestFinish() {
                        getActivity().runOnUiThread(() -> {

                        });
                    }

                    @JavascriptInterface
                    public String nativeGetProperty() {
                        return null;
                    }

                    @JavascriptInterface
                    public void updateHeight(int height) {
                        if (mResetHeight) {
                            runOnMain(() -> {
                                if (height > 0) {
                                    mWebView.setMinimumHeight(dp2px(height));
                                }
                            });
                        }
                    }
                }, "common");
            }

            mFragment.consumeEvent(NotificationCenter.loginSubject)
                    .setTag("on_user_login")
                    .onNextFinish(nil -> {
                        if (mHasRequestLogin) {
                            mHasRequestLogin = false;
                            mWebView.callJS("loginFinish", true);
                        }
                    })
                    .done();

            mFragment.consumeEvent(NotificationCenter.cancelLoginSubject)
                    .setTag("on_user_cancel_login")
                    .onNextFinish(user -> {
                        if (mResetHeight) {
                            mHasRequestLogin = true;
                            mWebView.callJS("loginFinish", false);
                        }
                    })
                    .done();

            mFragment.consumeEvent(NotificationCenter.closeEditShippingAddressPageSubject)
                    .setTag("on_close_edit_address_page")
                    .onNextFinish(ignored -> {

                    })
                    .done();

        }

        public void onDestroyView() {
            mSubscription.reset();
        }


        private View getView() {
            return mFragment.getView();
        }

        private Activity getActivity() {
            return mFragment.getActivity();
        }

        public boolean onInterceptGoBack() {
            if (mWebView.canGoBack() && mAllowInterceptGoBack) {
                mWebView.goBack();
                return true;
            }
            mWebView.loadUrl("about:blank");
            return false;
        }

        public void setUserVisibleHint(boolean isVisibleToUser) {

        }

        public static boolean isSecureRequest(String requestURL) {
            if (TextUtils.isEmpty(requestURL)) {
                return true;
            }

            Uri uri = Uri.parse(requestURL);
            if (uri != null) {
                String host = uri.getHost();
                if (!TextUtils.isEmpty(host)) {
                    if (RegexPatternHolder.MATCH_LAN_ADDRESS_ENTIRE.matcher(host).find()) {
                        return true;
                    }

                    List<String> whiteList = Optional.of(CommonManager.getInstance().getWhiteList()).or(Collections.emptyList());
                    for (String rule : whiteList) {
                        if (Pattern.compile("(\\." + rule + "/?$)|(^" + rule + "/?$)").matcher(host).find()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

    }


    public static class WebViewFragment extends SimpleFragment {

        private String mRequestURL;
        private boolean mHasToolBar;
        private boolean mHideShareButton;
        private boolean mPreferCloseButtonAtToolbar;
        private Button mShareButton;
        private TextView mToolbarTitleLabel;
        private GMFWebView mWebView;
        private WebViewFragmentDelegate mDelegate;

        private ProxyCompositionSubscription mSubscription = ProxyCompositionSubscription.create();

        public WebViewFragment init(String requestURL) {
            return this.init(requestURL, true, false);
        }

        public WebViewFragment init(String requestURL, boolean hasToolBar) {
            return this.init(requestURL, hasToolBar, false);
        }

        public WebViewFragment init(String requestURL, boolean hasToolBar, boolean hideShareButton) {
            Bundle arguments = new Bundle();
            arguments.putString(CommonProxyActivity.KEY_URL_STRING, requestURL);
            arguments.putBoolean(CommonProxyActivity.KEY_HAS_TOOLBAR_BOOLEAN, hasToolBar);
            arguments.putBoolean(CommonProxyActivity.KEY_HIDE_SHARE_BUTTON_BOOLEAN, hideShareButton);
            setArguments(arguments);
            return this;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mRequestURL = getArguments().getString(CommonProxyActivity.KEY_URL_STRING);
            mHasToolBar = getArguments().getBoolean(CommonProxyActivity.KEY_HAS_TOOLBAR_BOOLEAN, true);
            mHideShareButton = getArguments().getBoolean(CommonProxyActivity.KEY_HIDE_SHARE_BUTTON_BOOLEAN, false);
            mPreferCloseButtonAtToolbar = getArguments().getBoolean(CommonProxyActivity.KEY_PREFER_CLOSE_BUTTON_AT_TOOLBAR_BOOLEAN, false);
            if (!RegexPatternHolder.MATCH_URL.matcher(mRequestURL).matches()) {
                mRequestURL = "http://" + mRequestURL;
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                WebView.enableSlowWholeDocumentDraw();
            }
            return inflater.inflate(R.layout.frag_webview, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            if (mHasToolBar) {
                setStatusBarBackgroundColor(this, SignalColorHolder.WHITE_COLOR);
                if (mPreferCloseButtonAtToolbar) {

                } else {

                }
            }
            updateTitle("加载中");
            //            v_setVisibility(findToolbar(this), mHasToolBar ? View.VISIBLE : View.GONE);

            mShareButton = v_findView(this, R.id.btn_share);
            mToolbarTitleLabel = v_findView(this, R.id.toolbarTitle);
            v_setVisible(mLoadingSection);

            mWebView = v_findView(this, R.id.webview);
            //            v_setClick(mShareButton, this::performShareButton);
            setOnSwipeFreshListener(() -> mWebView.reload());

            mWebView.setWebChromeClient(new WebChromeClient() {
                boolean isSecure = WebViewFragmentDelegate.isSecureRequest(mRequestURL);

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    if (isSecure) {
                        mToolbarTitleLabel.setSingleLine();
                        if (!title.startsWith("http"))
                            updateTitle(title);
                    } else {
                        mToolbarTitleLabel.setSingleLine(false);
                        mToolbarTitleLabel.setLines(2);
                        concat("操盘侠安全提示", setFontSize("网址未经认证,请谨慎输入账号密码", sp2px(10)));
                        updateTitle(title);
                    }
                }
            });

            boolean isSecureRequest = WebViewFragmentDelegate.isSecureRequest(mRequestURL);
            boolean isShareButtonVisible = isSecureRequest && !mHideShareButton;
            //            mToolbarTitleLabel.setPadding(0, 0, isSecureRequest ? 0 : dp2px(56), 0);
            v_setVisibility(mShareButton, mHideShareButton ? View.VISIBLE : View.GONE);

            mDelegate = new WebViewFragmentDelegate(this, mWebView);
            mDelegate.onViewCreated(mRequestURL);

            mWebView.loadUrl(mRequestURL);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            mSubscription.reset();
        }

        @Override
        public boolean onInterceptGoBack() {
            return Optional.of(mDelegate).let(it -> it.onInterceptGoBack()).or(false) || super.onInterceptGoBack();
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            Optional.of(mDelegate).apply(it -> it.setUserVisibleHint(isVisibleToUser));
            if (getView() != null) {
                mSubscription.reset();
                if (isVisibleToUser) {
                    mSubscription.add(sEditDefalutShareInfoSubject
                            .filter(pair -> pair != null)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(pair -> {
                                ShareInfo info = pair.first;
                                SharePlatform[] platforms = pair.second;
                                resetShareContent(info.title, info.msg, info.url, info.imageUrl, platforms);
                            }));

                    mSubscription.add(sShowShareButtonSubject
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(nil -> {
                                v_setVisible(mShareButton);
                                mToolbarTitleLabel.setPadding(0, 0, 0, 0);
                            }));
                    mSubscription.add(sHideShareButtonSubject
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(nil -> {
                                v_setGone(mShareButton);
                                mToolbarTitleLabel.setPadding(0, 0, dp2px(56), 0);
                            }));
                }
            }
        }

        private ShareInfo mDefaultShareInfo;
        private SharePlatform[] mDefaultPlatforms;

        private void resetShareContent(String title, String msg, String url, String imageUrl, SharePlatform[] platforms) {
            ShareInfo info = new ShareInfo();
            info.title = title;
            info.msg = msg;
            info.url = url;
            info.imageUrl = imageUrl;
            mDefaultShareInfo = info;
            mDefaultPlatforms = platforms;
        }

        private void performShareButton() {
            if (getActivity() instanceof UMShareHandlerProtocol) {
                UMShareHandlerProtocol handler = (UMShareHandlerProtocol) getActivity();
                if (mDefaultShareInfo == null) {
                    ShareInfo info = new ShareInfo();
                    info.title = mWebView.getTitle();
                    info.msg = "来自操盘侠，带你股市翻红";
                    info.imageUrl = "";
                    info.url = mWebView.getUrl();
                    handler.onPerformShare(info, (SharePlatform[]) null);
                } else {
                    handler.onPerformShare(mDefaultShareInfo, mDefaultPlatforms);
                }
            }
        }


    }


}
