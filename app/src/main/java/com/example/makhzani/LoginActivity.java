package com.example.makhzani;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class LoginActivity extends Activity {

    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildLoginUi();
    }

    TextView title(String text, float size) {
        TextView t = new TextView(this);

        t.setText(text);
        t.setTextSize(size);
        t.setGravity(Gravity.CENTER);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(10, 10, 10, 20);

        return t;
    }

    void buildLoginUi() {

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(
            LinearLayout.VERTICAL
        );

        root.setGravity(Gravity.CENTER);
        root.setPadding(40, 40, 40, 40);

        root.setLayoutDirection(
            View.LAYOUT_DIRECTION_RTL
        );

        TextView title = title(
            "مخزني",
            32
        );

        root.addView(
            title,
            new LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        );

        TextView subtitle = title(
            "تسجيل الدخول",
            20
        );

        root.addView(
            subtitle,
            new LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        );

        username = new EditText(this);

        username.setHint(
            "اسم المستخدم"
        );

        username.setSingleLine(true);

        root.addView(
            username,
            new LinearLayout.LayoutParams(
                -1,
                65
            )
        );

        password = new EditText(this);

        password.setHint(
            "كلمة المرور"
        );

        password.setSingleLine(true);

        password.setInputType(
            0x00000081
        );

        root.addView(
            password,
            new LinearLayout.LayoutParams(
                -1,
                65
            )
        );

        Button login = new Button(this);

        login.setText(
            "🔐 تسجيل الدخول"
        );

        root.addView(
            login,
            new LinearLayout.LayoutParams(
                -1,
                70
            )
        );

        TextView info = new TextView(this);

        info.setText(
            "اسم المستخدم الافتراضي: admin\n" +
            "كلمة المرور الافتراضية: 1234"
        );

        info.setTextSize(14);
        info.setGravity(Gravity.CENTER);
        info.setPadding(10, 30, 10, 10);

        root.addView(
            info,
            new LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        );

        login.setOnClickListener(
            v -> checkLogin()
        );

        setContentView(root);
    }

    void checkLogin() {

        String user =
            username.getText()
                .toString()
                .trim();

        String pass =
            password.getText()
                .toString()
                .trim();

        if (user.equals("admin")
                && pass.equals("1234")) {

            Intent intent =
                new Intent(
                    this,
                    MainActivity.class
                );

            intent.putExtra(
                "username",
                user
            );

            startActivity(intent);

            finish();

        } else {

            Toast.makeText(
                this,
                "اسم المستخدم أو كلمة المرور غير صحيحة",
                Toast.LENGTH_SHORT
            ).show();
        }
    }
}
