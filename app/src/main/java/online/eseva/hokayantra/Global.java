package online.eseva.hokayantra;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

public class Global {

    public static final String APP_PACKAGE_NAME = "online.eseva.hokayantra";
    public static final String GUJ_FONT_PATH = "fonts/HindVadodara-Regular.ttf";
    public static final String NEW_LINE = "\n";

    /**
     * Use this to display the app version name (not code)
     * @param con Context
     * @return returns string containing version name, e.g. "1.1.0"
     */
    public static String getAppDisplayVersion(Context con) {
        PackageInfo pInfo = null;
        try {
            pInfo = con.getPackageManager().getPackageInfo(con.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        return pInfo.versionName;
    }

    /**
     * Opens the dismissible dialog with provided title and message
     *
     * @param con Context
     * @param title Title to be display on dialog
     * @param msg Message to be display beneath the title
     */
    static void showPopup(final Context con, String title, final String msg) {
        if(title == null) {
            title = "";
        }
        Typeface face = Typeface.createFromAsset(con.getAssets(), GUJ_FONT_PATH);
        TextView tvCustomTitle = new TextView(con);
        tvCustomTitle.setText(title);
        tvCustomTitle.setTypeface(face);
        tvCustomTitle.setTextSize(18);
        int padding = 50;
        tvCustomTitle.setPadding(padding, padding, padding, 0);

        tvCustomTitle.setTextColor(con.getResources().getColor(R.color.colorPrimary));

        final String finalTitle = title;
        AlertDialog dialog = new AlertDialog.Builder(con)
                .setMessage(msg)
                .setCustomTitle(tvCustomTitle)
                .setPositiveButton("Share This", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Global.shareTextWithAppLink(con, "*" + finalTitle.trim() + "*\n" + msg, "Share...");
                    }
                })
                .show();

        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTypeface(face);
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Check if app is installed on current device by using package manager
     * @param con Context
     * @param uri String containing package name, e.g. "com.example.app"
     * @return returns true if app is installed otherwise false
     */
    public static boolean isAppInstalled(Context con, String uri) {
        PackageManager pm = con.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    /**
     * Opens the app in play store if play store is installed on the devices otherwise
     * opens the app in web version of play store
     * @param con Context
     * @param packageName Package name, e.g. "com.example.app"
     */
    public static void openAppInPlayStore(Context con, String packageName) {
        if(packageName == null) {
            packageName = con.getPackageName();
        }

        try {
            Intent intentRateUs = new Intent(Intent.ACTION_VIEW);
            intentRateUs.setData(Uri.parse("market://details?id=" + packageName + "&referrer=utm_source%3D" + APP_PACKAGE_NAME));
            con.startActivity(intentRateUs);
            con.startActivity(intentRateUs);
        } catch (Exception e) {
            Intent intentRateUs = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName + "&referrer=utm_source%3D" + APP_PACKAGE_NAME));
            con.startActivity(intentRateUs);
        }
    }

    /**
     * Opens the developer in play store app if installed otherwise open in web version of
     * play store
     * @param con
     */
    public static void openDeveloperPageInStore(Context con) {
        try {
            Intent intentRateUs = new Intent(Intent.ACTION_VIEW);
            intentRateUs.setData(Uri.parse("market://dev?id=6639265479425046477"));
            con.startActivity(intentRateUs);
            con.startActivity(intentRateUs);
        } catch (Exception e) {
            Intent intentRateUs = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/dev?id=6639265479425046477&referrer=utm_source%3D" + APP_PACKAGE_NAME));
            con.startActivity(intentRateUs);
        }
    }

    /**
     * Opens the Share Dialog with provided text with predefined prefixed text
     * @param con Context
     * @param shareText Text to be shared with
     * @param shareTitle Title of open share dialog
     */
    public static void shareTextWithAppLink(Context con, String shareText, String shareTitle) {
        String share_prefix = "http://bit.ly/hokayantra";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, share_prefix + "\n\n" +  shareText);
        con.startActivity(Intent.createChooser(intent, shareTitle));
    }

}
