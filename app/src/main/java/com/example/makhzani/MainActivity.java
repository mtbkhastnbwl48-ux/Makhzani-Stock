package com.example.makhzani;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout list;
    ArrayList<Item> items = new ArrayList<>();
    SharedPreferences prefs;

    static class Item {
        String name, code;
        int qty;
        Item(String n, String c, int q){name=n; code=c; qty=q;}
    }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("stock", MODE_PRIVATE);
        load();
        buildUi();
    }

    TextView title(String s, float size) {
        TextView t=new TextView(this);
        t.setText(s); t.setTextSize(size); t.setPadding(12,12,12,12);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    Button btn(String s) {
        Button b=new Button(this); b.setText(s); return b;
    }

    void buildUi() {
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(12,12,12,12);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView h=title("مخزني",28);
        h.setGravity(Gravity.CENTER);
        root.addView(h,new LinearLayout.LayoutParams(-1,70));

        LinearLayout actions=new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        Button add=btn("➕ إضافة");
        Button in=btn("📥 إدخال");
        Button out=btn("📤 إخراج");
        actions.addView(add,new LinearLayout.LayoutParams(0,65,1));
        actions.addView(in,new LinearLayout.LayoutParams(0,65,1));
        actions.addView(out,new LinearLayout.LayoutParams(0,65,1));
        root.addView(actions);

        list=new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        ScrollView scroll=new ScrollView(this);
        scroll.addView(list);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        add.setOnClickListener(v->addItem());
        in.setOnClickListener(v->move(false));
        out.setOnClickListener(v->move(true));

        setContentView(root);
        refresh();
    }

    void addItem() {
        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        EditText n=new EditText(this); n.setHint("اسم المادة");
        EditText c=new EditText(this); c.setHint("الكود (اختياري)");
        EditText q=new EditText(this); q.setHint("الكمية"); q.setInputType(2);
        box.addView(n); box.addView(c); box.addView(q);
        new AlertDialog.Builder(this).setTitle("إضافة مادة").setView(box)
            .setPositiveButton("حفظ",(d,w)->{
                String name=n.getText().toString().trim();
                if(name.isEmpty()) return;
                int qty=parse(q.getText().toString());
                items.add(new Item(name,c.getText().toString().trim(),qty));
                save(); refresh();
            }).setNegativeButton("إلغاء",null).show();
    }

    void move(boolean isOut) {
        if(items.isEmpty()){
            Toast.makeText(this,"أضف مادة أولاً",Toast.LENGTH_SHORT).show(); return;
        }
        String[] names=new String[items.size()];
        for(int i=0;i<items.size();i++) names[i]=items.get(i).name;
        Spinner sp=new Spinner(this);
        sp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,names));
        EditText q=new EditText(this); q.setHint("الكمية"); q.setInputType(2);
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL);
        box.addView(sp); box.addView(q);
        new AlertDialog.Builder(this).setTitle(isOut?"إخراج من المخزن":"إدخال إلى المخزن")
            .setView(box).setPositiveButton("تنفيذ",(d,w)->{
                int qty=parse(q.getText().toString());
                int i=sp.getSelectedItemPosition();
                if(qty<=0){ Toast.makeText(this,"أدخل كمية صحيحة",Toast.LENGTH_SHORT).show(); return; }
                Item it=items.get(i);
                if(isOut && qty>it.qty){
                    Toast.makeText(this,"الرصيد غير كافٍ",Toast.LENGTH_SHORT).show(); return;
                }
                it.qty += isOut ? -qty : qty;
                save(); refresh();
            }).setNegativeButton("إلغاء",null).show();
    }

    void refresh() {
        list.removeAllViews();
        if(items.isEmpty()){
            TextView empty=title("لا توجد مواد في المخزن",18);
            empty.setGravity(Gravity.CENTER);
            list.addView(empty);
            return;
        }
        for(int i=0;i<items.size();i++){
            Item it=items.get(i);
            TextView row=new TextView(this);
            String code=it.code.isEmpty() ? "بدون كود" : it.code;
            row.setText("المادة: "+it.name+"\nالكود: "+code+"\nالرصيد: "+it.qty);
            row.setTextSize(18); row.setPadding(18,20,18,20);
            final int index=i;
            row.setOnLongClickListener(v->{
                new AlertDialog.Builder(this).setTitle("حذف المادة؟")
                    .setMessage(it.name)
                    .setPositiveButton("حذف",(d,w)->{items.remove(index);save();refresh();})
                    .setNegativeButton("إلغاء",null).show();
                return true;
            });
            list.addView(row);
        }
    }

    int parse(String s){ try{return Integer.parseInt(s.trim());}catch(Exception e){return 0;} }

    void save(){
        StringBuilder b=new StringBuilder();
        for(Item it:items){
            b.append(it.name.replace("|","/")).append("|")
             .append(it.code.replace("|","/")).append("|")
             .append(it.qty).append("\n");
        }
        prefs.edit().putString("data",b.toString()).apply();
    }

    void load(){
        String data=prefs.getString("data","");
        if(data.isEmpty()) return;
        for(String line:data.split("\n")){
            String[] p=line.split("\\|",-1);
            if(p.length>=3) items.add(new Item(p[0],p[1],parse(p[2])));
        }
    }
}
