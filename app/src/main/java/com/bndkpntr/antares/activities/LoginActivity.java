package com.bndkpntr.antares.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bndkpntr.antares.Antares;
import com.bndkpntr.antares.R;
import com.bndkpntr.antares.events.GetOAuthTokenSuccessfulEvent;
import com.bndkpntr.antares.network.SoundCloudAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        webView.setWebViewClient(new WebViewClient() {
            private final Pattern pattern = Pattern.compile("(?<=antares://auth\\?code=)[a-z0-9]+[^&#]");

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Matcher matcher = pattern.matcher(url);
                if (matcher.find()) {
                    String code = matcher.group();
                    Antares.getSoundCloudInteractor().getTokenFromCode(code);
                } else {
                    webView.loadUrl(url);
                }
                return true;
            }
        });

        webView.loadUrl(SoundCloudAPI.LOGIN_URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetOAuthTokenSuccessfulEvent(GetOAuthTokenSuccessfulEvent event) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
