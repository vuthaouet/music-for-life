package com.example.musicplayerapp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    public Button btnLogin = null;
    public TextView btnRegister = null;
    public EditText fieldName = null;
    public EditText fieldPassword = null;
    Context context = this;

    private int usableHeightPrevious;
    private int defaultHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private View mChildOfContent;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        btnLogin = findViewById(R.id.login_button);
//        btnRegister = findViewById(R.id.register_button);
//        fieldName = findViewById(R.id.name_field);
//        fieldPassword = findViewById(R.id.password_field);
//        userService = APIUtils.getUserService();
//
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//
//        OnBackPressedCallback backWithoutLogin = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                setResult(RESULT_CANCELED, intent);
//                finish();
//            }
//        };
//        this.getOnBackPressedDispatcher().addCallback(this, backWithoutLogin);
//
//        FrameLayout content = findViewById(android.R.id.content);
//        defaultHeightPrevious = content.getLayoutParams().height;
//
//        mChildOfContent = content.getChildAt(0);
//        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            public void onGlobalLayout() {
//                possiblyResizeChildOfContent();
//            }
//        });
//        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();

//    }
//
//
//    private void possiblyResizeChildOfContent() {
//        int usableHeightNow = computeUsableHeight();
//        if (usableHeightNow != usableHeightPrevious) {
//            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
//            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
//            if (heightDifference > (usableHeightSansKeyboard / 4)) {
//                // keyboard probably just became visible
//                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
//            } else {
//                // keyboard probably just became hidden
//                frameLayoutParams.height = defaultHeightPrevious;
//            }
//            mChildOfContent.requestLayout();
//            usableHeightPrevious = usableHeightNow;
//        }
//    }
//
//    private int computeUsableHeight() {
//        Rect r = new Rect();
//        mChildOfContent.getWindowVisibleDisplayFrame(r);
//        return (r.bottom - r.top + getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android")));
//    }
}