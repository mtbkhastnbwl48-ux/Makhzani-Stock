package com.example.makhzani;

import android.app.*;
import android.os.Bundle;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {

    LinearLayout list;
    DatabaseHelper db;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        db = new DatabaseHelper(this);

        buildUi();
    }

    TextView title(String s, float size) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setPadding(12, 12, 12, 12);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    Button btn(String s) {
        Button b = new Button(this);
        b.setText(s);
        return b;
    }

    void buildUi() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(12, 12, 12, 12);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView h = title("مخزني", 28);
        h.setGravity(Gravity.CENTER);

        root.addView(
            h,
            new LinearLayout.LayoutParams(-1, 70)
        );

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button add = btn("➕ إضافة");
        Button in = btn("📥 إدخال");
        Button out = btn("📤 إخراج");

        actions.addView(
            add,
            new LinearLayout.LayoutParams(0, 65, 1)
        );

        actions.addView(
            in,
            new LinearLayout.LayoutParams(0, 65, 1)
        );

        actions.addView(
            out,
            new LinearLayout.LayoutParams(0, 65, 1)
        );

        root.addView(actions);

        // زر البحث
        Button search = btn("🔎 بحث عن مادة");
        root.addView(
            search,
            new LinearLayout.LayoutParams(-1, 65)
        );

        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(list);

        root.addView(
            scroll,
            new LinearLayout.LayoutParams(-1, 0, 1)
        );

        add.setOnClickListener(v -> addItem());
        in.setOnClickListener(v -> move(false));
        out.setOnClickListener(v -> move(true));
        search.setOnClickListener(v -> searchItem());

        setContentView(root);

        refresh();
    }

    // إضافة مادة
    void addItem() {

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);

        EditText n = new EditText(this);
        n.setHint("اسم المادة");

        EditText c = new EditText(this);
        c.setHint("الكود (اختياري)");

        EditText q = new EditText(this);
        q.setHint("الكمية");
        q.setInputType(2);

        box.addView(n);
        box.addView(c);
        box.addView(q);

        new AlertDialog.Builder(this)
            .setTitle("إضافة مادة")
            .setView(box)

            .setPositiveButton("حفظ", (d, w) -> {

                String name =
                    n.getText().toString().trim();

                String code =
                    c.getText().toString().trim();

                int qty =
                    parse(q.getText().toString());

                if (name.isEmpty()) {
                    Toast.makeText(
                        this,
                        "اكتب اسم المادة",
                        Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                if (qty < 0) {
                    Toast.makeText(
                        this,
                        "الكمية غير صحيحة",
                        Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                db.addItem(name, code, qty);

                refresh();

                Toast.makeText(
                    this,
                    "تمت إضافة المادة",
                    Toast.LENGTH_SHORT
                ).show();
            })

            .setNegativeButton("إلغاء", null)
            .show();
    }

    // إدخال أو إخراج
    void move(boolean isOut) {

        Cursor cursor = db.getAllItems();

        if (!cursor.moveToFirst()) {

            cursor.close();

            Toast.makeText(
                this,
                "أضف مادة أولاً",
                Toast.LENGTH_SHORT
            ).show();

            return;
        }

        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        do {

            int id =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
                );

            String name =
                cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
                );

            ids.add(id);
            names.add(name);

        } while (cursor.moveToNext());

        cursor.close();

        Spinner sp = new Spinner(this);

        sp.setAdapter(
            new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                names
            )
        );

        EditText q = new EditText(this);
        q.setHint("الكمية");
        q.setInputType(2);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);

        box.addView(sp);
        box.addView(q);

        new AlertDialog.Builder(this)

            .setTitle(
                isOut
                    ? "إخراج من المخزن"
                    : "إدخال إلى المخزن"
            )

            .setView(box)

            .setPositiveButton("تنفيذ", (d, w) -> {

                int qty =
                    parse(q.getText().toString());

                if (qty <= 0) {

                    Toast.makeText(
                        this,
                        "أدخل كمية صحيحة",
                        Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                int position =
                    sp.getSelectedItemPosition();

                int itemId =
                    ids.get(position);

                Cursor itemCursor =
                    db.getItem(itemId);

                if (!itemCursor.moveToFirst()) {

                    itemCursor.close();

                    return;
                }

                int oldQty =
                    itemCursor.getInt(
                        itemCursor.getColumnIndexOrThrow(
                            "quantity"
                        )
                    );

                itemCursor.close();

                if (isOut && qty > oldQty) {

                    Toast.makeText(
                        this,
                        "الرصيد غير كافٍ",
                        Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                int newQty;

                if (isOut) {
                    newQty = oldQty - qty;
                } else {
                    newQty = oldQty + qty;
                }

                db.updateQuantity(
                    itemId,
                    newQty
                );

                String type =
                    isOut ? "إخراج" : "إدخال";

                String date =
                    new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(new Date());

                String time =
                    new SimpleDateFormat(
                        "HH:mm:ss",
                        Locale.getDefault()
                    ).format(new Date());

                db.addMovement(
                    itemId,
                    type,
                    qty,
                    oldQty,
                    newQty,
                    date,
                    time,
                    "admin"
                );

                refresh();

                Toast.makeText(
                    this,
                    "تم تسجيل " + type,
                    Toast.LENGTH_SHORT
                ).show();
            })

            .setNegativeButton("إلغاء", null)
            .show();
    }

    // البحث
    void searchItem() {

        EditText input = new EditText(this);
        input.setHint("اكتب اسم المادة أو الكود");

        new AlertDialog.Builder(this)

            .setTitle("البحث عن مادة")
            .setView(input)

            .setPositiveButton("بحث", (d, w) -> {

                String text =
                    input.getText().toString().trim();

                showSearchResults(text);
            })

            .setNegativeButton("إلغاء", null)
            .show();
    }

    void showSearchResults(String text) {

        Cursor cursor =
            db.searchItems(text);

        list.removeAllViews();

        if (!cursor.moveToFirst()) {

            cursor.close();

            TextView empty =
                title(
                    "لا توجد نتائج",
                    18
                );

            empty.setGravity(Gravity.CENTER);

            list.addView(empty);

            return;
        }

        do {

            int id =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
                );

            String name =
                cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
                );

            String code =
                cursor.getString(
                    cursor.getColumnIndexOrThrow("code")
                );

            int qty =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow("quantity")
                );

            addItemRow(
                id,
                name,
                code,
                qty
            );

        } while (cursor.moveToNext());

        cursor.close();
    }

    // عرض المواد
    void refresh() {

        list.removeAllViews();

        Cursor cursor =
            db.getAllItems();

        if (!cursor.moveToFirst()) {

            cursor.close();

            TextView empty =
                title(
                    "لا توجد مواد في المخزن",
                    18
                );

            empty.setGravity(Gravity.CENTER);

            list.addView(empty);

            return;
        }

        do {

            int id =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
                );

            String name =
                cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
                );

            String code =
                cursor.getString(
                    cursor.getColumnIndexOrThrow("code")
                );

            int qty =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow("quantity")
                );

            addItemRow(
                id,
                name,
                code,
                qty
            );

        } while (cursor.moveToNext());

        cursor.close();
    }

    void addItemRow(
            int id,
            String name,
            String code,
            int qty) {

        TextView row = new TextView(this);

        if (code == null || code.isEmpty()) {
            code = "بدون كود";
        }

        row.setText(
            "المادة: " + name +
            "\nالكود: " + code +
            "\nالرصيد: " + qty
        );

        row.setTextSize(18);
        row.setPadding(18, 20, 18, 20);

        final int itemId = id;

        row.setOnClickListener(
            v -> showItemDetails(itemId)
        );

        row.setOnLongClickListener(v -> {

            new AlertDialog.Builder(this)

                .setTitle("حذف المادة؟")
                .setMessage(name)

                .setPositiveButton(
                    "حذف",
                    (d, w) -> {

                        db.deleteItem(itemId);

                        refresh();

                        Toast.makeText(
                            this,
                            "تم حذف المادة",
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                )

                .setNegativeButton(
                    "إلغاء",
                    null
                )

                .show();

            return true;
        });

        list.addView(row);
    }

    // تفاصيل المادة
    void showItemDetails(int itemId) {

        Cursor cursor =
            db.getItem(itemId);

        if (!cursor.moveToFirst()) {

            cursor.close();
            return;
        }

        String name =
            cursor.getString(
                cursor.getColumnIndexOrThrow("name")
            );

        String code =
            cursor.getString(
                cursor.getColumnIndexOrThrow("code")
            );

        int qty =
            cursor.getInt(
                cursor.getColumnIndexOrThrow("quantity")
            );

        cursor.close();

        Cursor movements =
            db.getMovements(itemId);

        StringBuilder text =
            new StringBuilder();

        text.append("المادة: ")
            .append(name)
            .append("\n");

        text.append("الكود: ")
            .append(
                code == null || code.isEmpty()
                    ? "بدون كود"
                    : code
            )
            .append("\n");

        text.append("الرصيد الحالي: ")
            .append(qty)
            .append("\n\n");

        int totalIn = 0;
        int totalOut = 0;

        if (movements.moveToFirst()) {

            do {

                String type =
                    movements.getString(
                        movements.getColumnIndexOrThrow(
                            "type"
                        )
                    );

                int amount =
                    movements.getInt(
                        movements.getColumnIndexOrThrow(
                            "quantity"
                        )
                    );

                if (type.equals("إدخال")) {
                    totalIn += amount;
                } else if (type.equals("إخراج")) {
                    totalOut += amount;
                }

            } while (movements.moveToNext());
        }

        movements.close();

        text.append("إجمالي الداخل: ")
            .append(totalIn)
            .append("\n");

        text.append("إجمالي الخارج: ")
            .append(totalOut)
            .append("\n");

        new AlertDialog.Builder(this)
            .setTitle("تفاصيل المادة")
            .setMessage(text.toString())
            .setPositiveButton("إغلاق", null)
            .show();
    }

    int parse(String s) {

        try {
            return Integer.parseInt(
                s.trim()
            );
        } catch (Exception e) {
            return 0;
        }
    }
}
