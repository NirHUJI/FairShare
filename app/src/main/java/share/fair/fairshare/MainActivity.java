package share.fair.fairshare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends FragmentActivity  {


    ListView groupList;
    GroupsAdapter groupAdapter;
    ArrayList<NameAndKey> groupNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button createNewGroupButton =(Button) findViewById(R.id.create_new_group_button);
        createNewGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddNewGroupDialog().show(getSupportFragmentManager(), "add_new_group");

            }
        });
        groupNames = Group.getSavedGroupNames(getApplicationContext());
        groupNames.add(new NameAndKey("GROUPYAIR","123"));
        groupList = (ListView) findViewById(R.id.groups_list);
        groupAdapter = new GroupsAdapter(this,R.id.info, groupNames);
        groupList.setAdapter(groupAdapter);
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //todo;

            }
        });

    }

    

    public static class AddNewGroupDialog extends DialogFragment {
        public AddNewGroupDialog() {
            // Empty constructor required for DialogFragment
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View dialogLayout = inflater.inflate(R.layout.new_group_dialog_layout, container);
            setCancelable(false);
            getDialog().setContentView(R.layout.new_group_dialog_layout);
            getDialog().setTitle("Choose group name:");

            final EditText groupNameEditText = (EditText) dialogLayout.findViewById(R.id.group_name_edit_text);
            final Button createButton = (Button) dialogLayout.findViewById(R.id.create_button);
            final Button cancelButton = (Button) dialogLayout.findViewById(R.id.cancel_button);
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = groupNameEditText.getText().toString();
                    Group newGroup = new Group(name);
                    newGroup.saveGroupToStorage(getContext());
                    Log.w("custom", name + " as been created");
                    getDialog().dismiss();

                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();

                }
            });
            createButton.setEnabled(false);
            groupNameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (groupNameEditText.getText().toString().length() > 0) {
                        createButton.setEnabled(true);
                    } else {
                        createButton.setEnabled(false);
                    }
                }

            });
            getDialog().show();
            return dialogLayout;

        }
    }

}
