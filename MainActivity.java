package com.example.campusfind;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private final int navy = Color.rgb(23, 59, 135);
    private final int blue = Color.rgb(36, 87, 197);
    private final int pale = Color.rgb(241, 245, 255);
    private DBHelper dbHelper;
    private LinearLayout root;
    private LinearLayout listContainer;
    private EditText searchBox;
    private String currentFilter = "All";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);
        showHome();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(blue));
        return b;
    }

    private LinearLayout vertical() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private LinearLayout.LayoutParams params(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    private void setup(String subtitle) {
        root = vertical();
        root.setBackgroundColor(Color.rgb(248, 250, 255));
        setContentView(root);

        LinearLayout header = vertical();
        header.setPadding(dp(20), dp(20), dp(20), dp(18));
        header.setBackgroundColor(navy);
        TextView title = text("CampusFind", 27, Color.WHITE, true);
        header.addView(title);
        TextView sub = text(subtitle, 14, Color.rgb(225, 233, 255), false);
        LinearLayout.LayoutParams sp = params(-1, -2);
        sp.topMargin = dp(4);
        header.addView(sub, sp);
        root.addView(header);
    }

    private void showHome() {
        setup("College Lost & Found Management");
        LinearLayout content = vertical();
        content.setPadding(dp(16), dp(16), dp(16), dp(12));
        root.addView(content, params(-1, -1));

        TextView intro = text("Lost something on campus? Found an item? Report it here and help it get back to its owner.", 16, Color.rgb(55, 65, 81), false);
        intro.setPadding(dp(4), dp(4), dp(4), dp(14));
        content.addView(intro);

        Button lost = button("＋  Report Lost Item");
        Button found = button("＋  Report Found Item");
        Button browse = button("⌕  Browse All Items");
        content.addView(lost, buttonParams());
        content.addView(found, buttonParams());
        content.addView(browse, buttonParams());
        lost.setOnClickListener(v -> showForm("Lost"));
        found.setOnClickListener(v -> showForm("Found"));
        browse.setOnClickListener(v -> showListings());

        TextView note = text("How it works", 19, navy, true);
        LinearLayout.LayoutParams np = params(-1, -2); np.topMargin = dp(20); np.bottomMargin = dp(8);
        content.addView(note, np);
        content.addView(infoRow("1", "Report", "Add the item name, category and location."));
        content.addView(infoRow("2", "Search", "Browse listings and look for a matching item."));
        content.addView(infoRow("3", "Return", "Contact the reporter and verify ownership."));

        TextView footer = text("CampusFind • Student CEP Project", 12, Color.GRAY, false);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams fp = params(-1, -2); fp.topMargin = dp(22);
        content.addView(footer, fp);
    }

    private LinearLayout.LayoutParams buttonParams() {
        LinearLayout.LayoutParams p = params(-1, -2);
        p.bottomMargin = dp(9);
        return p;
    }

    private View infoRow(String number, String heading, String description) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), dp(10), dp(12), dp(10));
        row.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.WHITE));
        TextView n = text(number, 18, blue, true);
        n.setGravity(Gravity.CENTER);
        n.setBackgroundColor(pale);
        row.addView(n, params(dp(36), dp(36)));
        LinearLayout detail = vertical();
        detail.setPadding(dp(12), 0, 0, 0);
        detail.addView(text(heading, 15, Color.BLACK, true));
        detail.addView(text(description, 13, Color.DKGRAY, false));
        row.addView(detail, new LinearLayout.LayoutParams(0, -2, 1));
        LinearLayout.LayoutParams p = params(-1, -2); p.bottomMargin = dp(7);
        row.setElevation(dp(1));
        return wrap(row, p);
    }

    private View wrap(View v, LinearLayout.LayoutParams p) {
        LinearLayout holder = vertical();
        holder.addView(v, p);
        return holder;
    }

    private EditText edit(String hint) {
        EditText e = new EditText(this);
        e.setSingleLine(false);
        e.setHint(hint);
        e.setTextSize(15);
        e.setPadding(dp(12), dp(10), dp(12), dp(10));
        e.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.LTGRAY));
        return e;
    }

    private void showForm(String type) {
        setup("Report a " + type.toLowerCase() + " item");
        LinearLayout form = vertical();
        form.setPadding(dp(18), dp(16), dp(18), dp(16));
        ScrollView scroll = new ScrollView(this);
        scroll.addView(form);
        root.addView(scroll, params(-1, -1));

        form.addView(text("Please provide as much detail as possible.", 15, Color.DKGRAY, false));
        EditText name = edit("Item name (e.g. blue water bottle)");
        EditText description = edit("Description / identifying details");
        EditText location = edit(type.equals("Lost") ? "Last seen location in college" : "Location where item was found");
        EditText date = edit("Date (e.g. 25 Oct 2026)");
        EditText contact = edit("Contact detail (college email or phone)");
        addField(form, "Item name *", name);
        addField(form, "Category", null);
        Spinner category = new Spinner(this);
        String[] categories = {"ID Card", "Books / Notes", "Bag", "Bottle", "Keys", "Electronics", "Clothing", "Other"};
        category.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories));
        form.addView(category, params(-1, dp(48)));
        addField(form, "Description", description);
        addField(form, "Location *", location);
        addField(form, "Date", date);
        addField(form, "Contact information *", contact);

        Button save = button("Submit " + type + " Report");
        LinearLayout.LayoutParams bp = buttonParams(); bp.topMargin = dp(12);
        form.addView(save, bp);
        Button back = button("Back to Home"); form.addView(back, buttonParams());
        back.setOnClickListener(v -> showHome());
        save.setOnClickListener(v -> {
            String itemName = name.getText().toString().trim();
            String itemLocation = location.getText().toString().trim();
            String itemContact = contact.getText().toString().trim();
            if (itemName.isEmpty() || itemLocation.isEmpty() || itemContact.isEmpty()) {
                Toast.makeText(this, "Please fill in item name, location and contact.", Toast.LENGTH_LONG).show();
                return;
            }
            ContentValues values = new ContentValues();
            values.put("type", type);
            values.put("name", itemName);
            values.put("category", category.getSelectedItem().toString());
            values.put("description", description.getText().toString().trim());
            values.put("location", itemLocation);
            values.put("date", date.getText().toString().trim());
            values.put("contact", itemContact);
            values.put("status", "Open");
            long id = dbHelper.getWritableDatabase().insert("items", null, values);
            if (id > 0) {
                Toast.makeText(this, type + " item reported successfully.", Toast.LENGTH_LONG).show();
                showListings();
            } else {
                Toast.makeText(this, "Could not save report. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addField(LinearLayout parent, String label, EditText field) {
        TextView t = text(label, 14, Color.rgb(45, 55, 72), true);
        LinearLayout.LayoutParams lp = params(-1, -2); lp.topMargin = dp(12);
        parent.addView(t, lp);
        if (field != null) {
            parent.addView(field, params(-1, -2));
        }
    }

    private void showListings() {
        setup("Browse campus lost and found reports");
        LinearLayout body = vertical();
        body.setPadding(dp(14), dp(12), dp(14), dp(10));
        root.addView(body, params(-1, -1));
        searchBox = edit("Search by item name, category or location");
        body.addView(searchBox, params(-1, -2));
        Spinner filter = new Spinner(this);
        String[] options = {"All", "Lost", "Found", "Open", "Returned"};
        filter.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options));
        LinearLayout.LayoutParams flp = params(-1, dp(48)); flp.topMargin = dp(5);
        body.addView(filter, flp);
        ScrollView scroll = new ScrollView(this);
        listContainer = vertical();
        scroll.addView(listContainer);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(-1, 0, 1);
        slp.topMargin = dp(8);
        body.addView(scroll, slp);
        Button home = button("Back to Home"); body.addView(home, buttonParams());
        home.setOnClickListener(v -> showHome());
        filter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            public void onItemSelected(android.widget.AdapterView<?> p, View v, int pos, long id) {
                currentFilter = options[pos]; refreshListings();
            }
            public void onNothingSelected(android.widget.AdapterView<?> p) { }
        });
        searchBox.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { refreshListings(); }
            public void afterTextChanged(Editable e) { }
        });
        refreshListings();
    }

    private void refreshListings() {
        if (listContainer == null) return;
        listContainer.removeAllViews();
        String query = searchBox == null ? "" : searchBox.getText().toString().trim();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("items", null, null, null, null, null, "id DESC");
        int shown = 0;
        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndexOrThrow("id"));
            String type = c.getString(c.getColumnIndexOrThrow("type"));
            String name = c.getString(c.getColumnIndexOrThrow("name"));
            String category = c.getString(c.getColumnIndexOrThrow("category"));
            String description = c.getString(c.getColumnIndexOrThrow("description"));
            String location = c.getString(c.getColumnIndexOrThrow("location"));
            String date = c.getString(c.getColumnIndexOrThrow("date"));
            String contact = c.getString(c.getColumnIndexOrThrow("contact"));
            String status = c.getString(c.getColumnIndexOrThrow("status"));
            if (!currentFilter.equals("All") && !currentFilter.equals(type) && !currentFilter.equals(status)) continue;
            String haystack = (name + " " + category + " " + description + " " + location).toLowerCase();
            if (!haystack.contains(query.toLowerCase())) continue;
            shown++;
            listContainer.addView(itemCard(id, type, name, category, description, location, date, contact, status));
        }
        c.close();
        if (shown == 0) {
            TextView empty = text("No matching items yet. You can add a report from the home screen.", 15, Color.DKGRAY, false);
            empty.setPadding(dp(12), dp(22), dp(12), dp(22));
            listContainer.addView(empty);
        }
    }

    private View itemCard(long id, String type, String name, String category, String description,
                          String location, String date, String contact, String status) {
        LinearLayout card = vertical();
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        card.setBackgroundColor(Color.WHITE);
        card.setElevation(dp(2));
        TextView title = text((type.equals("Lost") ? "LOST • " : "FOUND • ") + name, 17, navy, true);
        card.addView(title);
        TextView statusText = text("Status: " + status + "   |   " + category, 12, status.equals("Returned") ? Color.rgb(20, 125, 65) : blue, true);
        LinearLayout.LayoutParams stp = params(-1, -2); stp.topMargin = dp(4);
        card.addView(statusText, stp);
        String details = (description.isEmpty() ? "" : description + "\n") + "Location: " + location +
                (date.isEmpty() ? "" : "\nDate: " + date) + "\nContact: " + contact;
        TextView detail = text(details, 14, Color.DKGRAY, false);
        LinearLayout.LayoutParams dp1 = params(-1, -2); dp1.topMargin = dp(7);
        card.addView(detail, dp1);
        if (!status.equals("Returned")) {
            Button returned = button("Mark as Returned");
            LinearLayout.LayoutParams rp = params(-1, -2); rp.topMargin = dp(8);
            card.addView(returned, rp);
            returned.setOnClickListener(v -> new AlertDialog.Builder(this)
                    .setTitle("Mark item as returned?")
                    .setMessage("Only do this after verifying the item's rightful owner.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        ContentValues values = new ContentValues(); values.put("status", "Returned");
                        dbHelper.getWritableDatabase().update("items", values, "id=?", new String[]{String.valueOf(id)});
                        refreshListings();
                        Toast.makeText(this, "Item status updated.", Toast.LENGTH_SHORT).show();
                    }).show());
        }
        LinearLayout.LayoutParams cp = params(-1, -2); cp.bottomMargin = dp(10);
        return wrap(card, cp);
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) dbHelper.close();
        super.onDestroy();
    }

    private static class DBHelper extends SQLiteOpenHelper {
        DBHelper(Context context) { super(context, "campusfind.db", null, 1); }
        @Override public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE items (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT NOT NULL, name TEXT NOT NULL, category TEXT, description TEXT, location TEXT NOT NULL, date TEXT, contact TEXT NOT NULL, status TEXT NOT NULL)");
        }
        @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    }
}
