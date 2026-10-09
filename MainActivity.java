package com.pcore.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {
    private static final int PICK_WALLPAPER = 101;
    private static final int PICK_GAME = 102;
    private static final int PICK_FILE = 103;
    private final int bg = Color.rgb(11, 16, 32);
    private final int panel = Color.rgb(22, 30, 49);
    private final int panel2 = Color.rgb(29, 39, 62);
    private final int accent = Color.rgb(140, 168, 255);
    private final int white = Color.rgb(240, 244, 255);
    private final int muted = Color.rgb(163, 174, 198);
    private LinearLayout root;
    private LinearLayout content;
    private SharedPreferences prefs;
    private final ArrayList<String> games = new ArrayList<>();
    private String page = "home";
    private Uri wallpaperUri;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("pcore", MODE_PRIVATE);
        wallpaperUri = parseUri(prefs.getString("wallpaper", ""));
        Set<String> saved = prefs.getStringSet("games", new HashSet<String>());
        if (saved != null) games.addAll(saved);
        showPage("home");
    }

    private Uri parseUri(String value) {
        try { return value == null || value.isEmpty() ? null : Uri.parse(value); }
        catch (Exception ignored) { return null; }
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius * getResources().getDisplayMetrics().density);
        d.setStroke((int)(1 * getResources().getDisplayMetrics().density), Color.rgb(48, 60, 86));
        return d;
    }

    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + 0.5f); }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        t.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        t.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_RTL);
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l;
    }

    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); l.setGravity(Gravity.CENTER_VERTICAL); return l;
    }

    private void pad(View v, int all) { v.setPadding(dp(all), dp(all), dp(all), dp(all)); }

    private Button button(String label, Runnable action, boolean primary) {
        Button b = new Button(this); b.setText(label); b.setTextColor(primary ? Color.rgb(10, 16, 32) : white);
        b.setAllCaps(false); b.setTextSize(14); b.setTypeface(null, Typeface.BOLD);
        b.setBackground(shape(primary ? accent : panel2, 14));
        b.setPadding(dp(10), dp(7), dp(10), dp(7));
        b.setOnClickListener(v -> action.run());
        return b;
    }

    private void showPage(String target) {
        page = target;
        root = column(); root.setBackgroundColor(bg);
        setContentView(root);
        LinearLayout header = row(); header.setPadding(dp(18), dp(16), dp(18), dp(12));
        LinearLayout brand = column();
        TextView logo = text("PCore", 25, white, true); logo.setGravity(Gravity.RIGHT);
        TextView tagline = text("كمبيوتر وألعابك في جيبك", 12, muted, false);
        brand.addView(logo); brand.addView(tagline);
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));
        TextView version = text("نسخة تجريبية 0.1", 11, accent, true); version.setGravity(Gravity.CENTER);
        header.addView(version, new LinearLayout.LayoutParams(-2, -2));
        root.addView(header);
        View line = new View(this); line.setBackgroundColor(Color.rgb(42, 52, 76)); root.addView(line, new LinearLayout.LayoutParams(-1, dp(1)));
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true);
        content = column(); content.setPadding(dp(16), dp(16), dp(16), dp(20));
        scroll.addView(content); root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        if ("home".equals(target)) home();
        else if ("games".equals(target)) gamesPage();
        else if ("desktop".equals(target)) desktopPage();
        else if ("files".equals(target)) filesPage();
        else settingsPage();
        LinearLayout nav = row(); nav.setPadding(dp(5), dp(7), dp(5), dp(7)); nav.setBackgroundColor(Color.rgb(14, 20, 36));
        String[][] items = {{"home", "الرئيسية"}, {"games", "ألعابي"}, {"desktop", "سطح المكتب"}, {"files", "ملفاتي"}, {"settings", "الإعدادات"}};
        for (String[] item : items) {
            TextView tab = text(item[1], 11, target.equals(item[0]) ? accent : muted, target.equals(item[0]));
            tab.setGravity(Gravity.CENTER); tab.setPadding(dp(3), dp(10), dp(3), dp(10));
            tab.setOnClickListener(v -> showPage(item[0]));
            nav.addView(tab, new LinearLayout.LayoutParams(0, -2, 1));
        }
        root.addView(nav);
    }

    private void addCard(String title, String description, String actionLabel, Runnable action) {
        LinearLayout card = column(); card.setBackground(shape(panel, 18)); card.setPadding(dp(15), dp(14), dp(15), dp(14));
        TextView h = text(title, 17, white, true); h.setGravity(Gravity.RIGHT);
        TextView d = text(description, 13, muted, false); d.setGravity(Gravity.RIGHT | Gravity.TOP); d.setLineSpacing(dp(3), 1f);
        card.addView(h); LinearLayout.LayoutParams dpms = new LinearLayout.LayoutParams(-1, -2); dpms.topMargin = dp(6); card.addView(d, dpms);
        Button b = button(actionLabel, action, true); LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, -2); bp.topMargin = dp(12); card.addView(b, bp);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2); cp.bottomMargin = dp(12); content.addView(card, cp);
    }

    private void home() {
        TextView hello = text("أهلًا بك في PCore", 24, white, true); content.addView(hello);
        TextView sub = text("بيئة ألعاب وكمبيوتر محمول — نسخة أولية", 13, muted, false); content.addView(sub);
        View gap = new View(this); content.addView(gap, new LinearLayout.LayoutParams(1, dp(16)));
        addCard("🎮 وضع الألعاب", "استورد ملفات ألعابك واعرضها في مكتبة واحدة. تشغيل ألعاب Windows الحقيقي لم يُدمج بعد.", "فتح مكتبة الألعاب", () -> showPage("games"));
        addCard("🖥️ وضع سطح المكتب", "واجهة تجريبية مع متصفح داخل التطبيق وخيارات الخلفية المحفوظة.", "فتح سطح المكتب", () -> showPage("desktop"));
        addCard("📁 ملفاتي", "اختر ملفات من الهاتف باستخدام منتقي ملفات أندرويد.", "فتح مدير الملفات", () -> showPage("files"));
        TextView note = text("المرحلة الحالية: أساس تطبيق أندرويد. محرك تشغيل ألعاب Windows ما زال غير مدمج.", 12, accent, false);
        note.setPadding(dp(4), dp(4), dp(4), dp(8)); content.addView(note);
    }

    private void gamesPage() {
        content.addView(text("مكتبة الألعاب", 23, white, true));
        TextView intro = text("استورد ملف تشغيل أو ملفًا متعلقًا بلعبتك. هذه النسخة تحفظ اختياراتك فقط ولا تشغّل ألعاب PC بعد.", 13, muted, false); content.addView(intro);
        Button add = button("＋ استيراد ملف لعبة", () -> pick(PICK_GAME), true);
        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(-1, -2); ap.topMargin = dp(14); content.addView(add, ap);
        if (games.isEmpty()) {
            LinearLayout empty = column(); empty.setGravity(Gravity.CENTER); empty.setPadding(dp(15), dp(30), dp(15), dp(30));
            TextView e = text("لسه مفيش ألعاب مستوردة", 16, white, true); e.setGravity(Gravity.CENTER); empty.addView(e);
            TextView e2 = text("اضغط على استيراد ملف لعبة للبدء", 12, muted, false); e2.setGravity(Gravity.CENTER); empty.addView(e2);
            LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(-1, -2); ep.topMargin = dp(16); content.addView(empty, ep);
        } else {
            for (String game : new ArrayList<>(games)) {
                LinearLayout card = column(); card.setBackground(shape(panel, 14)); card.setPadding(dp(12), dp(12), dp(12), dp(12));
                card.addView(text("🟡  " + game, 15, white, true));
                card.addView(text("غير مختبرة — لا توجد نتيجة توافق بعد", 12, muted, false));
                LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2); cp.topMargin = dp(10); content.addView(card, cp);
            }
        }
    }

    private void desktopPage() {
        content.addView(text("سطح المكتب", 23, white, true));
        TextView info = text("واجهة أولية داخل PCore. الخلفية المختارة تُحفظ محليًا، ويمكنك فتح المتصفح داخل التطبيق.", 13, muted, false); content.addView(info);
        FrameLayout desk = new FrameLayout(this); desk.setBackground(shape(Color.rgb(25, 43, 69), 18));
        if (wallpaperUri != null) {
            try { desk.setBackgroundColor(Color.rgb(25,43,69)); ImageView img = new ImageView(this); img.setImageURI(wallpaperUri); img.setScaleType(ImageView.ScaleType.CENTER_CROP); desk.addView(img, new FrameLayout.LayoutParams(-1, -1)); }
            catch (Exception ignored) { }
        }
        LinearLayout icons = column(); icons.setPadding(dp(12), dp(12), dp(12), dp(12));
        TextView pc = text("🖥️ الكمبيوتر", 13, white, true); pc.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL); icons.addView(pc);
        TextView files = text("📁 ملفاتي", 13, white, true); files.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL); icons.addView(files);
        TextView browser = text("🌐 المتصفح", 13, white, true); browser.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL); browser.setOnClickListener(v -> openBrowser()); icons.addView(browser);
        desk.addView(icons);
        LinearLayout.LayoutParams deskp = new LinearLayout.LayoutParams(-1, dp(270)); deskp.topMargin = dp(12); content.addView(desk, deskp);
        LinearLayout actions = row();
        Button wallpaper = button("اختيار خلفية", () -> pick(PICK_WALLPAPER), true);
        Button web = button("فتح المتصفح", this::openBrowser, false);
        actions.addView(wallpaper, new LinearLayout.LayoutParams(0, -2, 1)); LinearLayout.LayoutParams w = new LinearLayout.LayoutParams(0, -2, 1); w.leftMargin = dp(8); actions.addView(web, w);
        LinearLayout.LayoutParams actp = new LinearLayout.LayoutParams(-1, -2); actp.topMargin = dp(12); content.addView(actions, actp);
        TextView saved = text(wallpaperUri == null ? "الخلفية الحالية: الافتراضية" : "تم حفظ اختيار الخلفية على الجهاز", 12, muted, false); content.addView(saved);
        TextView caveat = text("ملاحظة: هذا سطح مكتب تجريبي، وليس نظام Windows كاملًا. الخلفيات المتحركة غير مدعومة في هذه النسخة الأولية.", 12, accent, false); caveat.setPadding(0, dp(12), 0, 0); content.addView(caveat);
    }

    private void openBrowser() {
        content.removeAllViews();
        LinearLayout top = row();
        EditText url = new EditText(this); url.setSingleLine(true); url.setText("https://www.google.com"); url.setHint("اكتب عنوان الموقع"); url.setTextColor(white); url.setHintTextColor(muted); url.setBackground(shape(panel2, 12)); url.setPadding(dp(10), dp(8), dp(10), dp(8));
        Button go = button("اذهب", () -> { String address = url.getText().toString().trim(); if (!address.startsWith("http://") && !address.startsWith("https://")) address = "https://" + address; browserLoad(address); }, true);
        top.addView(url, new LinearLayout.LayoutParams(0, -2, 1)); LinearLayout.LayoutParams gp = new LinearLayout.LayoutParams(-2, -2); gp.leftMargin = dp(6); top.addView(go, gp); content.addView(top);
        WebView web = new WebView(this); web.setBackgroundColor(Color.WHITE); web.getSettings().setJavaScriptEnabled(true); web.getSettings().setDomStorageEnabled(true); web.setWebViewClient(new WebViewClient()); web.setWebChromeClient(new WebChromeClient()); web.loadUrl("https://www.google.com");
        LinearLayout.LayoutParams wp = new LinearLayout.LayoutParams(-1, dp(500)); wp.topMargin = dp(10); content.addView(web, wp);
        // The URL entry's button uses the active WebView below.
        go.setOnClickListener(v -> { String address = url.getText().toString().trim(); if (!address.startsWith("http://") && !address.startsWith("https://")) address = "https://" + address; web.loadUrl(address); });
        Button back = button("رجوع لسطح المكتب", () -> showPage("desktop"), false); LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, -2); bp.topMargin = dp(10); content.addView(back, bp);
    }

    private void browserLoad(String address) { /* URL is loaded by the active browser screen button. */ }

    private void filesPage() {
        content.addView(text("ملفاتي", 23, white, true));
        content.addView(text("اختر ملفًا من الهاتف دون منح التطبيق وصولًا شاملًا للتخزين.", 13, muted, false));
        addCard("اختيار ملف", "فتح منتقي الملفات الرسمي في أندرويد.", "اختيار ملف", () -> pick(PICK_FILE));
        addCard("مجلد التنزيلات", "يمكنك فتح مجلد أو ملف من خلال منتقي النظام؛ إدارة الملفات المتقدمة ستُضاف لاحقًا.", "اختيار ملف للتنزيلات", () -> pick(PICK_FILE));
    }

    private void settingsPage() {
        content.addView(text("الإعدادات", 23, white, true));
        addCard("المظهر", "الواجهة الحالية تستخدم ثيمًا داكنًا.", "إعادة تحميل الواجهة", () -> showPage("settings"));
        addCard("البيانات", "إعدادات الألعاب والخلفية محفوظة محليًا داخل التطبيق.", "إعادة ضبط البيانات", () -> new android.app.AlertDialog.Builder(this).setTitle("إعادة ضبط PCore؟").setMessage("سيتم حذف قائمة الألعاب المحفوظة والخلفية المختارة من إعدادات التطبيق.").setNegativeButton("إلغاء", null).setPositiveButton("إعادة ضبط", (d,w) -> { prefs.edit().clear().apply(); games.clear(); wallpaperUri = null; showPage("settings"); }).show());
        content.addView(text("PCore 0.1.0 • نموذج أندرويد أولي", 12, muted, false));
    }

    private void pick(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try { startActivityForResult(intent, requestCode); }
        catch (Exception e) { Toast.makeText(this, "تعذر فتح منتقي الملفات على هذا الجهاز", Toast.LENGTH_LONG).show(); }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        try { getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch (Exception ignored) { }
        if (requestCode == PICK_WALLPAPER) {
            wallpaperUri = uri; prefs.edit().putString("wallpaper", uri.toString()).apply(); Toast.makeText(this, "تم حفظ اختيار الخلفية", Toast.LENGTH_SHORT).show(); showPage("desktop");
        } else if (requestCode == PICK_GAME) {
            String name = displayName(uri);
            if (!games.contains(name)) games.add(name);
            prefs.edit().putStringSet("games", new HashSet<>(games)).apply(); Toast.makeText(this, "تمت إضافة الملف للقائمة التجريبية", Toast.LENGTH_SHORT).show(); showPage("games");
        } else if (requestCode == PICK_FILE) {
            Toast.makeText(this, "تم اختيار: " + displayName(uri), Toast.LENGTH_LONG).show();
        }
    }

    private String displayName(Uri uri) {
        String last = uri.getLastPathSegment();
        if (last == null || last.trim().isEmpty()) last = "ملف مستورد";
        int slash = last.lastIndexOf('/'); if (slash >= 0 && slash + 1 < last.length()) last = last.substring(slash + 1);
        try { last = java.net.URLDecoder.decode(last, "UTF-8"); } catch (Exception ignored) { }
        return last;
    }
}
