package share.fair.fairshare;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

import java.util.List;


public class MainActivity extends FragmentActivity implements GroupNameDialog.GroupCreatedListener {


    ListView groupList;
    GroupsAdapter groupAdapter;
    List<FairShareGroup.GroupNameRecord> groupNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initLayoutPreferences();
        SharedPreferences settings = getSharedPreferences("MAIN_PREFERENCES", 0);
        String name = settings.getString("name", "");
        if (name.isEmpty()) {
            new SaveNameDialog().show(getSupportFragmentManager(), "save_name_dialog");
            ;
        }

        Button createNewGroupButton = (Button) findViewById(R.id.create_new_group_button);
        createNewGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GroupNameDialog().show(getSupportFragmentManager(), "add_new_group");
            }
        });
        groupNames = FairShareGroup.getSavedGroupNames();
        groupList = (ListView) findViewById(R.id.groups_list);
        groupAdapter = new GroupsAdapter(this, R.id.group_row_container, groupNames);
        groupList.setAdapter(groupAdapter);
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openGroup = new Intent(getApplicationContext(), GroupActivity.class);
                openGroup.putExtra("groupId", groupNames.get(position).getGroupId());
                startActivity(openGroup);
            }
        });
        if (getIntent() != null) {
            Intent intent = getIntent();
            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                Uri uri = intent.getData();
                String groupName = uri.getQueryParameter("groupName");
                String groupCloudKey = uri.getQueryParameter("groupCloudKey");
                String cloudLogKey = uri.getQueryParameter("cloudLogKey");
                FairShareGroup.joinGroupWithKey(getApplicationContext(), groupName, groupCloudKey, cloudLogKey);
                notifyGroupCreated();
            }
        }
    }

    private void initLayoutPreferences() {
        double titleFactor;
        double buttonFactor;
        int screenSize;
        int configuration = getResources().getConfiguration().orientation;
        if (configuration == Configuration.ORIENTATION_LANDSCAPE) {
            titleFactor = 10;
            buttonFactor = 30;
        } else {
            titleFactor = 10;
            buttonFactor = 40;
        }
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        TextView textView = (TextView) findViewById(R.id.main_activity_title);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (height / titleFactor));
        Button newGroupButton = (Button) findViewById(R.id.create_new_group_button);
        newGroupButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (height / buttonFactor));
    }

    @Override
    public void notifyGroupCreated() {
        groupNames.clear();
        groupNames.addAll(FairShareGroup.getSavedGroupNames());
        groupAdapter.notifyDataSetChanged();
    }
}
