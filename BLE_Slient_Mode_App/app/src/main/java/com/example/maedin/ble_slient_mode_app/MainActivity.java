package com.example.maedin.ble_slient_mode_app;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_setting;     //설정 버튼
    private Button btn_set_mode;    //모드 설정 버튼
    private Button btn_beacon_info; //비콘 정보 확인 버튼
    private Button btn_exit;        //종료 버튼
    private TextView txt_data;

    private Button btn_switch_info;
    boolean switch_info;

    private TextView txt_on_off;    //변경 확인 임시 위젯
    private TextView txt_rssi;      //rssi확인 임시 위젯

    private BeaconManager beaconManager;
    private BeaconRegion region;
    Beacon nearestBeacon;

    private int soundstate = 0;     //현재 음량 상태
    boolean isConnected = false;   //false 변경X true 변경

    AudioManager mAudiomanger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //설정 버튼 연결
        btn_setting = (Button) findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(this);
        txt_data = (TextView) findViewById(R.id.txt_data);

        txt_on_off = (TextView) findViewById(R.id.txt_on_off);  //삭제예정
        txt_rssi = (TextView) findViewById(R.id.txt_rssi);

        beaconManager = new BeaconManager(this);

        chagneState();

        region = new BeaconRegion("ranged region",
                UUID.fromString("74278BDA-B644-4520-8F0C-720EAF059935"), null, null);

        mAudiomanger = (AudioManager)getSystemService(getApplicationContext().AUDIO_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //런타임 권한 (Android 6.0이상) - ACCESS_COARSE_LOCATION 권한 요청
        //Bluetooth on, Location on 등등을 고려되었는지 확인
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_setting:
                setDialog();
                break;
        }
        //셋팅 버튼 클릭시

        //Toast.makeText(MainActivity.this, "설정 버튼 클릭", Toast.LENGTH_SHORT).show();
    }

    //설정 다이얼로그
    private void setDialog()
    {
        final LinearLayout setting_layout = (LinearLayout) View.inflate(this, R.layout.setting, null);

        btn_beacon_info = (Button) setting_layout.findViewById(R.id.btn_beacon);
        btn_set_mode = (Button) setting_layout.findViewById(R.id.btn_mode);
        btn_exit = (Button) setting_layout.findViewById(R.id.btn_exit);

        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("설정");
        dlg.setView(setting_layout);
        //dlg.setIcon(R.drawable);
        dlg.setCancelable(false);
        dlg.setPositiveButton("확인", null);
        dlg.show();


        btn_set_mode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setModeDialog();
            }
        });
        btn_beacon_info.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               beaconInfoDialog();
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //수정할 부분
                Toast.makeText(MainActivity.this, "어플 종료", Toast.LENGTH_SHORT).show();
            }
        });

     }

     //모드 설정 다이얼로그
     private void setModeDialog()
     {
         final LinearLayout set_mode_layout = (LinearLayout) View.inflate(this, R.layout.set_mode, null);
         btn_switch_info = (Button) set_mode_layout.findViewById(R.id.btn_switch);

         AlertDialog.Builder dlg = new AlertDialog.Builder(this);
         dlg.setTitle("모드 설정");
         dlg.setView(set_mode_layout);
         //dlg.setIcon(R.drawable);
         dlg.setCancelable(false);
         dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                  switch_info = btn_switch_info.isSelected();
                 //수정할 부분
                 //설정 시 모드 변환 코드 추가
             }
         });
         dlg.show();
     }

     //비콘 정보 다이얼로그
     private void beaconInfoDialog()
     {
         final LinearLayout beacon_info_layout = (LinearLayout) View.inflate(this, R.layout.beacon_info, null);

         AlertDialog.Builder dlg = new AlertDialog.Builder(this);
         dlg.setTitle("비콘 정보");
         dlg.setView(beacon_info_layout);
         //dlg.setIcon(R.drawable);
         dlg.setCancelable(false);
         dlg.setPositiveButton("확인", null);
         dlg.show();

         //수정할 부분
         //비콘 연결 O - 정보 출력
         //비콘 연결 X - 연결되어있지 않습니다 문구 출력
         //설정의 비콘 정보부분
         //txt_data.setText("Rssi: "+nearestBeacon.getRssi() + "\n"
          //       +"UUID: " + nearestBeacon.getProximityUUID() + "\n"
           //      +"Major, Minor: " +nearestBeacon.getMajor() + ", " + nearestBeacon.getMinor());
         //txt_data.setText("비콘이 연결되어 있지 않습니다.");
         //Toast.makeText(MainActivity.this, "비콘 연결 해제", Toast.LENGTH_SHORT);
     }

     private void chagneState()
     {
         final LinearLayout set_mode_layout = (LinearLayout) View.inflate(this, R.layout.setting, null);
         btn_switch_info = (Button) set_mode_layout.findViewById(R.id.btn_switch);
         beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener()
         {
             @Override
             public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> beacons) {
                 if (!beacons.isEmpty()){
                     nearestBeacon = beacons.get(0);

                     txt_rssi.setText("Rssi: "+nearestBeacon.getRssi());
                     //수정할 예정
                     //진동 변경 코드 추가
                     if (nearestBeacon.getRssi() > -70 && !isConnected)   //앱이 작동되지않은 상태로 비콘감지
                     {
                         txt_on_off.setText("ON");
                         isConnected = true;
                         soundstate = mAudiomanger.getRingerMode();
                         mAudiomanger.setRingerMode(AudioManager.RINGER_MODE_VIBRATE); // RINGER_MODE_VIBRATE = 1
                         sound_mode_check();

                     }
                     else if (nearestBeacon.getRssi() > -70 && isConnected) //앱이 작동된 상태로 비콘감지
                     {
                         txt_on_off.setText("OFF");
                         isConnected = false;
                         if(switch_info)
                         {
                         mAudiomanger.setRingerMode(soundstate);
                         sound_mode_check();
                         }else if(soundstate==0){
                             mAudiomanger.setRingerMode(AudioManager.RINGER_MODE_VIBRATE); // RINGER_MODE_NORMAL= 2
                         }
                     }

                 }
             }
         });
     }

    private void sound_mode_check(){
        switch (mAudiomanger.getRingerMode()){
            case AudioManager.RINGER_MODE_NORMAL: // 소리모드
                Toast.makeText(this, "소리 모드",Toast.LENGTH_SHORT).show();
                break;
            case AudioManager.RINGER_MODE_SILENT: // 무음모드
                Toast.makeText(this, "무음 모드",Toast.LENGTH_SHORT).show();
                break;
            case AudioManager.RINGER_MODE_VIBRATE: // 진동모드
                Toast.makeText(this, "진동 모드",Toast.LENGTH_SHORT).show();
                break;
        }
    }

}