package lk.sanoj.cyberyakku.automaticmr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import com.sdsmdg.tastytoast.TastyToast;
import com.zanjou.http.debug.Logger;
import com.zanjou.http.request.Request;
import com.zanjou.http.request.RequestListener;
import com.zanjou.http.response.XmlResponseListener;

import org.jsoup.nodes.Document;

import java.text.DateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class dashbord extends AppCompatActivity {
    private static final String TAG = "dashbord";
    private Button setting,service,stopservce,loggercopy,loggerclear;
    private TextView Simmodeview,maintimer,simonetimercount,simtowtimercount;
    private Date date = new Date();
    private String currunttime = "["  +DateFormat.getTimeInstance(DateFormat.MEDIUM).format(date)+"]"+" ";
    private TextView log,showstatus;
    private String getsimmod;
    /////////////////Storage/////////////////
    private String STsim1auth = "";
    private String STsim2auth = "";
    private String STsim1url = "";
    private String STsim2url = "";
    private String STDevices = "";
    private int STtime = 200;
    /////////////////////////////////////////
    /////////////////////deactivenote/////////////
    private String note = "";
    /////////////////////////////////////////////

    /////////////////logscroldown/////////////
    private ScrollView mScrollView;
    ///////////////////////////////////////////

    //////////////////counter///////////////////
    private int sim1coun = 1;
    private int sim2coun = 1;
    ///////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbord);

        getmessage();
        statesget();

        //////////////////button_head/////////////////
        setting = findViewById(R.id.setting);
        log = findViewById(R.id.editTextlogcrat);
        Simmodeview = findViewById(R.id.simmod);
        maintimer = findViewById(R.id.maintimer);
        simonetimercount = findViewById(R.id.simonecounter);
        simtowtimercount = findViewById(R.id.simtowcounter);
        service = findViewById(R.id.startservice);
        mScrollView = findViewById(R.id.scrollView);
        mScrollView.fullScroll(View.FOCUS_DOWN);
        stopservce = findViewById(R.id.stopservice);
        loggercopy = findViewById(R.id.loggcopy);
        loggerclear = findViewById(R.id.clearlog);
        showstatus = findViewById(R.id.statsview);
        //////////////////////////////////////////////
        //
            String ua = System.getProperty("http.agent");

        /////////////////Restorepref///////////////////
        try{
            SharedPreferences prefs = dashbord.this.getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
            String simmodeset = prefs.getString("SIMMODE", "Sim Mode: Not config");
            String getsim1auth = prefs.getString("SIM1AU", "");
            String getsim2auth = prefs.getString("SIM2AU", "");
            String getsim1url = prefs.getString("SIM1UR", "");
            String getsim2url = prefs.getString("SIM2UR", "");
            String getdevices = prefs.getString("DEVICES", ua);
            int gettime = prefs.getInt("TIME", 200);

            STsim1auth = getsim1auth;
            STsim2auth = getsim2auth;
            STsim1url = getsim1url;
            STsim2url = getsim2url;
            STDevices = getdevices;
            STtime = gettime;
            Simmodeview.setText(simmodeset);
        }catch (Exception NML){

        }
        ///////////////////////////////////////////////

        /////////////GetSim Mode//////////////////////
        getsimmod = Simmodeview.getText().toString();
        /////////////////////////////////////////////

        ///////////////mainTextAppend////////////
        log.setText(currunttime+"Start AutomaticMR"+"\n");
        log.append(currunttime+"Build 2.0 beta "+"\n");
        log.append(currunttime+ "Status: Waiting for network"+"\n");
        if(getsimmod.equals("Sim Mode: Not config")){
            log.append(currunttime+"Need Setup , Press Setting"+"\n");
            TastyToast.makeText(getApplicationContext(), "Need Setup", TastyToast.LENGTH_LONG, TastyToast.WARNING);
        }else if (getsimmod.equals("Sim Mode: Single")){
            simtowtimercount.setText("Disable");
            TastyToast.makeText(getApplicationContext(), "Success Import Single mode", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
            log.append(currunttime+"Import Single mode"+"\n");
            log.append(currunttime+"Authorization -- OK"+"\n");
            log.append(currunttime+"URL -- OK"+"\n");
            log.append(currunttime+"Device --OK"+"\n");
            log.append(currunttime+"Time "+ STtime +"--OK"+"\n");
            log.append(currunttime+"Press Play Start Service ☺"+"\n");
        }else if(getsimmod.equals("Sim Mode: Dual")){
            TastyToast.makeText(getApplicationContext(), "Success Import Dual mode", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
            log.append(currunttime+"Import Dual mode"+"\n");
            log.append(currunttime+"[S1]Authorization -- OK"+"\n");
            log.append(currunttime+"[S2]Authorization -- OK"+"\n");
            log.append(currunttime+"[S1]URL -- OK"+"\n");
            log.append(currunttime+"[S2]URL -- OK"+"\n");
            log.append(currunttime+"Device --OK"+"\n");
            log.append(currunttime+"Time "+ STtime +"--OK"+"\n");
            log.append(currunttime+"Press Play Start Service ☺"+"\n");
        }else{
            TastyToast.makeText(getApplicationContext(), "Need Setup", TastyToast.LENGTH_LONG, TastyToast.WARNING);
            log.append(currunttime+"Need Setup , Press Setting"+"\n");
        }

        /////////////////////////////////////////
        maintimer.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                int wochingtimer = Integer.parseInt(maintimer.getText().toString());
                if(wochingtimer==STtime){
                    Date dates = new Date();
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                    if(getsimmod.equals("Sim Mode: Not config")){
                        log.append("["  +DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates)+"]"+" "+"Need Setup , Press Setting"+"\n");
                        TastyToast.makeText(getApplicationContext(), "Need Setup", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                    }else if (getsimmod.equals("Sim Mode: Single")){
                        simonetimercount.setText(String.valueOf(sim1coun++));
                        increase_splash_bar(1,STtime);
                    }else if(getsimmod.equals("Sim Mode: Dual")){
                        simonetimercount.setText(String.valueOf(sim1coun++));
                        simtowtimercount.setText(String.valueOf(sim2coun++));
                        increase_splash_bar(1,STtime);
                    }
                }
                /////////////////statusget///////////////////

                String checkstatus = showstatus.getText().toString();
                if(checkstatus.equals("Status: Waiting for network")){
                   try{
                       getmessage();
                       statesget();
                   }catch (Exception L){

                   }

                }else{

                }

                /////////////////statusget///////////////////


            }
        });

        showstatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                String chekstatus = showstatus.getText().toString();
                final Date dates = new Date();

                if(chekstatus.equals("Status:Deactivate")){
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                    log.append("["  +DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates)+"]"+" "+"Status:Deactivate"+"\n");
                    statusdeactive();
                    TastyToast.makeText(getApplicationContext(), "Status:Deactivate", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    TastyToast.makeText(getApplicationContext(), "Automatic Close App 10 sec", TastyToast.LENGTH_LONG, TastyToast.CONFUSING);
                }else if (chekstatus.equals("Status:Update")){
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                    log.append("["  +DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates)+"]"+" "+"Status:Update"+"\n");
                    updateapp();
                    TastyToast.makeText(getApplicationContext(), "New update available", TastyToast.LENGTH_LONG, TastyToast.CONFUSING);

                }else if(chekstatus.equals("Status:Active")){
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                    log.append("["  +DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates)+"]"+" "+"Status:Active"+"\n");
                    TastyToast.makeText(getApplicationContext(), "Status:Active", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);

                }


            }
        });


        simonetimercount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                Date dates = new Date();
                mScrollView.fullScroll(View.FOCUS_DOWN);
                log.append("["  +DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates)+"]"+" "+"Start New requests ♻ [SIM 1]"+"\n");
                int simonedisplay = Integer.parseInt(simonetimercount.getText().toString());

                if (simonedisplay>=31){
                    log.append("["  +DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates)+"]"+" "+"Danger,30+ req, have been sent ☠[SIM 1]"+"\n");
                }
                //////////////////////////SIMONE REQAST/////////////////////////////
                       getdatasimone();
                ////////////////////////////////////////////////////////////////////
            }
        });
        simtowtimercount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                final Date dates = new Date();
                mScrollView.fullScroll(View.FOCUS_DOWN);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        log.append("["  +DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates)+"]"+" "+"Start New requests ♻ [SIM 2]"+"\n");
                        int simtowdisplay = Integer.parseInt(simtowtimercount.getText().toString());
                        if (simtowdisplay>=31) {
                            log.append("[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " " + "Danger,30+ req, have been sent ☠[SIM 2]" + "\n");
                        }
                        //////////////////////////SIMTOW REQAST/////////////////////////////
                        getdatasimtow();
                        ////////////////////////////////////////////////////////////////////
                    }
                }, 5000);



            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wochingtimer = maintimer.getText().toString();
                if (wochingtimer.equals("")){
                    btn_setting(v);
                }else{
                    TastyToast.makeText(getApplicationContext(), "Stop the service first", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }

            }
        });
        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date dates = new Date();
                mScrollView.fullScroll(View.FOCUS_DOWN);
                String checkstatus = showstatus.getText().toString();

                if(checkstatus.equals("Status:Deactivate")){
                    TastyToast.makeText(getApplicationContext(), "Status:Deactivate", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }else if (checkstatus.equals("Status:Update")){
                    updateapp();
                    TastyToast.makeText(getApplicationContext(), "New update available", TastyToast.LENGTH_LONG, TastyToast.CONFUSING);
                }else{
                    if(getsimmod.equals("Sim Mode: Not config")){
                        log.append("["  +DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates)+"]"+ " Need Setup , Press Setting"+"\n");
                        TastyToast.makeText(getApplicationContext(), "Need Setup", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                    }else{
                        log.append("["  +DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates)+"]"+ " Start Service ☺"+"\n");
                        service.setVisibility(View.GONE);
                        stopservce.setVisibility(View.VISIBLE);
                        increase_splash_bar(1,STtime);
                    }
                }

            }
        });

        stopservce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase_splash_bar(0,0);
                int id= android.os.Process.myPid();
                android.os.Process.killProcess(id);
                startActivity(getIntent());
            }
        });

        loggerclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.setText("");
                TastyToast.makeText(getApplicationContext(), "Clear log!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
            }
        });

        loggercopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText(log.getText().toString());
                clipboard.getText();
                TastyToast.makeText(getApplicationContext(), "Add log Clipboard", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
            }
        });
    }
    public void increase_splash_bar (int from, int to)
    {
        final Handler handler1 = new Handler();
        class Task implements Runnable {
            int start,end;
            Task(int a,int b) { start = a; end = b;}
            @Override
            public void run() {
                for (int i =start ; i <= end; i++) {
                    final int value = i;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                           // maintarget.setProgress(value);
                          maintimer.setText(String.valueOf(value));
                        }
                    });
                }
            }
        }
        Thread t = new Thread(new Task(from, to));   //call it
        t.start();
    }
    public void btn_setting(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(dashbord.this,R.style.CustomDialog);
        View mView = getLayoutInflater().inflate(R.layout.setting,null);
        Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
        final EditText sim1auth = (EditText)mView.findViewById(R.id.editText1);
        final EditText sim1url = (EditText)mView.findViewById(R.id.editText2);
        final EditText sim2auth = (EditText)mView.findViewById(R.id.editText1SIM2);//////////////viewgoneID
        final EditText sim2url = (EditText)mView.findViewById(R.id.editText2SIM2);//////////////viewgoneID
        final EditText recaptime = (EditText)mView.findViewById(R.id.editText3);
        final EditText payingdevice = (EditText)mView.findViewById(R.id.editText4);

        //////////////////setprefdata//////////////////
        sim1auth.setText(STsim1auth);
        sim2auth.setText(STsim2auth);
        sim1url.setText(STsim1url);
        sim2url.setText(STsim2url);
        payingdevice.setText(STDevices);
        recaptime.setText(String.valueOf(STtime));
        //////////////////////////////////////////////


        //////////////viewgoneID////////////////////////
        final TextView xone = (TextView)mView.findViewById(R.id.yone);
        final TextView xtow = (TextView)mView.findViewById(R.id.ytow);
        final TextView xtree = (TextView)mView.findViewById(R.id.ytree);
        ////////////////////////////////////////////////
        final CheckBox visiblechange = (CheckBox)mView.findViewById(R.id.checkBox);
        ////////////////checksimmode////////////////////////
        if(getsimmod.equals("Sim Mode: Single")){
            visiblechange.setChecked(true);
        }else if(getsimmod.equals("Sim Mode: Dual")){
            visiblechange.setChecked(false);
        }else{
            visiblechange.setChecked(true);
        }
        ////////////////////////////////////////////////////
        if(visiblechange.isChecked()){
            visiblechange.setText("Dual Sim Service Enable");
            sim2auth.setVisibility(View.GONE);
            sim2url.setVisibility(View.GONE);
            xone.setVisibility(View.GONE);
            xtow.setVisibility(View.GONE);
            xtree.setVisibility(View.GONE);
        }else{
            sim2auth.setVisibility(View.VISIBLE);
            sim2url.setVisibility(View.VISIBLE);
            xone.setVisibility(View.VISIBLE);
            xtow.setVisibility(View.VISIBLE);
            xtree.setVisibility(View.VISIBLE);
            visiblechange.setText("Dual Sim Service Disable");
        }
        visiblechange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    visiblechange.setText("Dual Sim Service Enable");
                    sim2auth.setVisibility(View.GONE);
                    sim2url.setVisibility(View.GONE);
                    xone.setVisibility(View.GONE);
                    xtow.setVisibility(View.GONE);
                    xtree.setVisibility(View.GONE);
                }else {
                    sim2auth.setVisibility(View.VISIBLE);
                    sim2url.setVisibility(View.VISIBLE);
                    xone.setVisibility(View.VISIBLE);
                    xtow.setVisibility(View.VISIBLE);
                    xtree.setVisibility(View.VISIBLE);
                    visiblechange.setText("Dual Sim Service Disable");
                }
            }
        });
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sim1authcation = sim1auth.getText().toString();
                String sim2authcation = sim2auth.getText().toString();
                String sim1urlmr = sim1url.getText().toString();
                String sim2urlmr = sim2url.getText().toString();
                String time = recaptime.getText().toString();
                String devices = payingdevice.getText().toString();

                if(visiblechange.isChecked()){
                    if(sim1authcation.equals("")||sim1urlmr.equals("")||time.equals("")||devices.equals("")){
                        TastyToast.makeText(getApplicationContext(), "Fill in the blank", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    }else{
                        try{
                            SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                            editor.putString("SIM1AU", sim1authcation);
                            editor.putString("SIM1UR", sim1urlmr);
                            editor.putInt("TIME", Integer.parseInt(time));
                            editor.putString("DEVICES", devices);
                            editor.putString("SIMMODE", "Sim Mode: Single");
                            editor.apply();
                            TastyToast.makeText(getApplicationContext(), "Save Success", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                            alertDialog.dismiss();
                            finish();
                            startActivity(getIntent());
                        }catch (Exception OI){
                            TastyToast.makeText(getApplicationContext(), "Error plz contect Developer", TastyToast.LENGTH_LONG, TastyToast.CONFUSING);
                        }
                    }
                }else {
                    try{
                        if(sim1authcation.equals("")||sim2authcation.equals("")||sim1urlmr.equals("")||sim2urlmr.equals("")||time.equals("")||devices.equals("")){
                            TastyToast.makeText(getApplicationContext(), "Fill in the blank", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                        }else{
                            SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                            editor.putString("SIM1AU", sim1authcation);
                            editor.putString("SIM2AU", sim2authcation);
                            editor.putString("SIM1UR", sim1urlmr);
                            editor.putString("SIM2UR", sim2urlmr);
                            editor.putInt("TIME", Integer.parseInt(time));
                            editor.putString("DEVICES", devices);
                            editor.putString("SIMMODE", "Sim Mode: Dual");
                            editor.apply();
                            TastyToast.makeText(getApplicationContext(), "Save Success", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                            alertDialog.dismiss();
                            finish();
                            startActivity(getIntent());
                        }
                    }catch (Exception O){
                        TastyToast.makeText(getApplicationContext(), "Error plz contect Developer", TastyToast.LENGTH_LONG, TastyToast.CONFUSING);
                    }
                }
                // alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    public void getdatasimone(){

        final Date dates = new Date();
        mScrollView.fullScroll(View.FOCUS_DOWN);
        log.append("["  +DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates)+"]"+" "+"Send New requests \uD83D\uDCE9 [SIM 1]"+"\n");

        Request request = Request.create(STsim1url);
        request.setMethod(Request.GET)
                .addHeader("Authorization", STsim1auth)
                .addHeader("X-Unity-Version", "2018.3.0f2")
                .addHeader("User-Agent", STDevices)
                .addHeader("Host", "megarun.dialog.lk")
                .addHeader("Connection","Keep-Alive")
                .addHeader("Accept-Encoding","gzip")
                .setLogger(new Logger(Logger.ERROR))
                .setRequestListener(new RequestListener() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "onStart");
                    }

                    @Override
                    public void onFinish() {
                        Log.d(TAG, "onFinish");
                    }

                    @Override
                    public void onUploadProgress(float progress) {
                        Log.d(TAG, "onProgress " + progress);
                    }

                    @Override
                    public void onConnectionError(Exception e) {
                        e.printStackTrace();
                    }
                })
                .setResponseListener(new XmlResponseListener() {
                    @Override
                    public void onOkResponse(Document document) {

                        try{
                            log.append(Html.fromHtml("<font color='#800080' size='18'>[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " " + document.text() + "\uD83D\uDCE1 [SIM1]</font>"));
                            log.append("\n");
                        }catch (Exception d){
                            log.append("[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " " + document.text() + "\uD83D\uDCE1 [SIM1]");
                            log.append("\n");
                        }
                        mScrollView.fullScroll(View.FOCUS_DOWN);

                    }

                    @Override
                    public void onErrorResponse(Document document) {

                        try{
                            log.append(Html.fromHtml("<font color='#800080' size='18'>[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " " + document.text() + " ⚠ "+ "[SIM1]</font>"));
                            log.append("\n");
                            log.append(Html.fromHtml("<font color='#228B22' size='18'>[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " May be Coin , Magnet or shield" +  " \uD83C\uDF3C "+ "[SIM1]</font>"));
                            log.append("\n");
                            log.append(Html.fromHtml("<font color='#228B22' size='18'>[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " Plz Check Network Connection" +" \uD83C\uDF3C "+ "[SIM1]</font>"));
                            log.append("\n");
                            log.append(Html.fromHtml("<font color='#228B22' size='18'>[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " May be User Block" +" \uD83C\uDF3C "+ "[SIM1]</font>"));
                            log.append("\n");
                        }catch (Exception R){
                            log.append("[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " " + document.text() + " ⚠ "+ "[SIM1]");
                            log.append("\n");
                            log.append("[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " May be Coin , Magnet or shield" +  " \uD83C\uDF3C "+ "[SIM1]");
                            log.append("\n");
                            log.append("[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " Plz Check Network Connection" +" \uD83C\uDF3C "+ "[SIM1]");
                            log.append("\n");
                            log.append("[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " May be User Block" +" \uD83C\uDF3C "+ "[SIM1]");
                            log.append("\n");
                        }
                        mScrollView.fullScroll(View.FOCUS_DOWN);


                    }

                    @Override
                    public void onParseError(Exception e) {
                        e.printStackTrace();
                    }
                }).execute();

    }
    public void getdatasimtow(){

        final Date dates = new Date();
        mScrollView.fullScroll(View.FOCUS_DOWN);
        log.append("["  +DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates)+"]"+" "+"Send New requests \uD83D\uDCE9 [SIM 2]"+"\n");

        Request request = Request.create(STsim2url);
        request.setMethod(Request.GET)
                .addHeader("Authorization", STsim2auth)
                .addHeader("X-Unity-Version", "2018.3.0f2")
                .addHeader("User-Agent", STDevices)
                .addHeader("Host", "megarun.dialog.lk")
                .addHeader("Connection","Keep-Alive")
                .addHeader("Accept-Encoding","gzip")
                .setLogger(new Logger(Logger.ERROR))
                .setRequestListener(new RequestListener() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "onStart");
                    }

                    @Override
                    public void onFinish() {
                        Log.d(TAG, "onFinish");
                    }

                    @Override
                    public void onUploadProgress(float progress) {
                        Log.d(TAG, "onProgress " + progress);
                    }

                    @Override
                    public void onConnectionError(Exception e) {
                        e.printStackTrace();
                    }
                })
                .setResponseListener(new XmlResponseListener() {
                    @Override
                    public void onOkResponse(Document document) {

                        try{
                            log.append(Html.fromHtml("<font color='#800080' size='18'>[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " " + document.text() + "\uD83D\uDCE1 [SIM2]</font>"));
                            log.append("\n");
                        }catch (Exception d){
                            log.append("[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " " + document.text() + "\uD83D\uDCE1 [SIM2]");
                            log.append("\n");
                        }
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    }

                    @Override
                    public void onErrorResponse(Document document) {

                        try{
                            log.append(Html.fromHtml("<font color='#800080' size='18'>[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " " + document.text() + " ⚠ "+ "[SIM2]</font>"));
                            log.append("\n");
                            log.append(Html.fromHtml("<font color='#228B22' size='18'>[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " May be Coin , Magnet or shield" +  " \uD83C\uDF3C "+ "[SIM2]</font>"));
                            log.append("\n");
                            log.append(Html.fromHtml("<font color='#228B22' size='18'>[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " Plz Check Network Connection" +" \uD83C\uDF3C "+ "[SIM2]</font>"));
                            log.append("\n");
                            log.append(Html.fromHtml("<font color='#228B22' size='18'>[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " May be User Block" +" \uD83C\uDF3C "+ "[SIM2]</font>"));
                            log.append("\n");
                        }catch (Exception R){
                            log.append("[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " " + document.text() + " ⚠ "+ "[SIM1]");
                            log.append("\n");
                            log.append("[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " May be Coin , Magnet or shield" +  " \uD83C\uDF3C "+ "[SIM2]");
                            log.append("\n");
                            log.append("[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " Plz Check Network Connection" +" \uD83C\uDF3C "+ "[SIM2]");
                            log.append("\n");
                            log.append("[" + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(dates) + "]" + " May be User Block" +" \uD83C\uDF3C "+ "[SIM2]");
                            log.append("\n");
                            mScrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    }

                    @Override
                    public void onParseError(Exception e) {
                        e.printStackTrace();
                    }
                }).execute();

    }
    private void statesget() {

        Request request = Request.create("https://raw.githubusercontent.com/00sanoj00/00sanoj00.github.io/master/automaicmrv2/appstatus.json");
        request.setMethod(Request.GET)
                .setLogger(new Logger(Logger.ERROR))
                .setRequestListener(new RequestListener() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "onStart");
                    }

                    @Override
                    public void onFinish() {
                        Log.d(TAG, "onFinish");
                    }

                    @Override
                    public void onUploadProgress(float progress) {
                        Log.d(TAG, "onProgress " + progress);
                    }

                    @Override
                    public void onConnectionError(Exception e) {
                        e.printStackTrace();
                    }
                })
                .setResponseListener(new XmlResponseListener() {
                    @Override
                    public void onOkResponse(Document document) {
                        showstatus.setText(document.text());
                    }

                    @Override
                    public void onErrorResponse(Document document) {
                        showstatus.setText(document.text());
                    }

                    @Override
                    public void onParseError(Exception e) {
                        e.printStackTrace();
                    }
                }).execute();

    }
    private void getmessage() {

        Request request = Request.create("https://raw.githubusercontent.com/00sanoj00/00sanoj00.github.io/master/automaicmrv2/deactivemessage.json");
        request.setMethod(Request.GET)
                .setLogger(new Logger(Logger.ERROR))
                .setRequestListener(new RequestListener() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "onStart");
                    }

                    @Override
                    public void onFinish() {
                        Log.d(TAG, "onFinish");
                    }

                    @Override
                    public void onUploadProgress(float progress) {
                        Log.d(TAG, "onProgress " + progress);
                    }

                    @Override
                    public void onConnectionError(Exception e) {
                        e.printStackTrace();
                    }
                })
                .setResponseListener(new XmlResponseListener() {
                    @Override
                    public void onOkResponse(Document document) {
                        note=(document.text());
                    }

                    @Override
                    public void onErrorResponse(Document document) {
                        note=(document.text());
                    }

                    @Override
                    public void onParseError(Exception e) {
                        e.printStackTrace();
                    }
                }).execute();

    }
    public void statusdeactive(){

        final AlertDialog.Builder alert = new AlertDialog.Builder(dashbord.this,R.style.CustomDialog);
        View mView = getLayoutInflater().inflate(R.layout.error,null);
        Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
        TextView noteview = (TextView)mView.findViewById(R.id.viewrmsg);
        noteview.append(note);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                increase_splash_bar(0,0);
                int id= android.os.Process.myPid();
                android.os.Process.killProcess(id);
                startActivity(getIntent());
            }
        }, 10000);



        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                increase_splash_bar(0,0);
                int id= android.os.Process.myPid();
                android.os.Process.killProcess(id);
                startActivity(getIntent());

            }
        });
        alertDialog.show();
    }
    public void updateapp(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("New update available")
                .setContentText("Update this app as soon as possible \ndownload the app by clicking the button below")
                .setConfirmText("Yes,Update")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        String url = "https://raw.githubusercontent.com/00sanoj00/00sanoj00.github.io/master/updatemr/app.apk";//https://raw.githubusercontent.com/00sanoj00/00sanoj00.github.io/master/updatemr/app.apk

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        sDialog.dismissWithAnimation();
                        STtime = 0;
                        int id= android.os.Process.myPid();
                        android.os.Process.killProcess(id);
                        startActivity(getIntent());
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        STtime = 0;
                        int id= android.os.Process.myPid();
                        android.os.Process.killProcess(id);
                        startActivity(getIntent());
                    }
                })
                .show();

    }
}
