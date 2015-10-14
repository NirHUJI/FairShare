package share.fair.fairshare;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class GroupActivity extends FragmentActivity implements UserNameDialog.UserAddedListener {

    static final int GO_OUT_REQUEST = 1;  // The request code
    TextView groupNameTextView;
    Button addUserButton;
    ArrayList<User> users;
    ListView userListView;
    Group group;
    UserCheckBoxAdapter userCheckBoxAdapter;
    Button goOutAllButton;
    Button goOutCheckedButton;
    Button backToMain;
    Button toActionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        this.group = Group.loadGroupFromStorage(getApplicationContext(), getIntent().getStringExtra("group_key"));
        groupNameTextView = (TextView) findViewById(R.id.tv_grp_name);
        groupNameTextView.setText(group.getName());
        this.users = group.getUsers();

        userListView = (ListView) findViewById(R.id.users_list_view);
        userCheckBoxAdapter = new UserCheckBoxAdapter(this, R.layout.user_check_row, this.users);
        userListView.setAdapter(userCheckBoxAdapter);
        registerForContextMenu(userListView);

        addUserButton = (Button) findViewById(R.id.add_user_button);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserNameDialog dialog = new UserNameDialog();
                dialog.setGroup(group);
                dialog.show(getSupportFragmentManager(), "add_new_user");

            }
        });

        goOutAllButton = (Button) findViewById(R.id.bt_go_out_all);
        goOutAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goOut = new Intent(getApplicationContext(), GoOutActivity.class);
                goOut.putExtra("goOutList", users);
                startActivityForResult(goOut, GO_OUT_REQUEST);
                clearChecked();
            }
        });
        goOutCheckedButton = (Button) findViewById(R.id.bt_go_out_checked);
        goOutCheckedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<User> checkedUsers = userCheckBoxAdapter.getCheckedArray();
                if (checkedUsers.isEmpty()) {
                    //todo: other way to handle error?
                    toastGen(getApplicationContext(), "No user is checked!");
                    return;
                } else {
                    Intent goOut = new Intent(getApplicationContext(), GoOutActivity.class);
                    goOut.putExtra("goOutList", checkedUsers);
                    startActivityForResult(goOut, GO_OUT_REQUEST);
                }
                clearChecked();
            }
        });
        backToMain = (Button) findViewById(R.id.bt_back_to_info);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();
            }
        });
        toActionsButton =(Button) findViewById(R.id.to_actions_button);
        toActionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actions = new Intent(getApplicationContext(),ActionsActivity.class);
                startActivity(actions);
            }
        });
    }

    @Override
    public void notifyUserAdded(String name, String emailAddress) {
       User newUser= new User(name,0);
        newUser.setEmail(emailAddress);
        this.group.addUser(getApplicationContext(), newUser);
        users= group.getUsers();
        userCheckBoxAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GO_OUT_REQUEST) {
            if (resultCode == RESULT_OK) {
                ArrayList<User> resultList = (ArrayList<User>) data.getSerializableExtra("resultUserList");
                Action action = (Action) data.getSerializableExtra("action");
                this.group.getGroupLog().AddAction(action);
                for(User user: resultList){
                    toastGen(getApplicationContext(),"username:"+ user.getName()+" bal: "+user.getBalance());
                }
                //users = resultList; //todo: problem if checked list was sent
                uniteLists(resultList);
                userCheckBoxAdapter.notifyDataSetChanged();
                this.group.saveGroupToStorage(getApplicationContext());
            }
        }
    }
    private void toastGen(Context context,String msg){
        Log.w("user", "in toastGen: " + msg);
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
    private void uniteLists(ArrayList<User> resultList){
        for(int i =0; i< users.size(); i++){
            for(int j =0; j<resultList.size(); j++){
                if ( users.get(i).getId().equals(resultList.get(j).getId()) ){
                    users.set(i, resultList.get(j));
                }
            }
        }
    }

    private void clearChecked(){
       for(int i=0; i< this.userListView.getChildCount(); i++){
           CheckBox checkBox= (CheckBox) this.userListView.getChildAt(i).findViewById(R.id.cb_user_row);
           checkBox.setChecked(false);
       }
        this.userCheckBoxAdapter.clearChecked();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.users_list_view) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle( users.get(info.position).getName() );
            String[] menuItems = {"Notify me by mail","Nir, for real???", "It cant be true"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = {"menu1","menu2", "menu3"};
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = users.get(info.position).getName();

//        TextView text = (TextView)findViewById(R.id.footer);
//        text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
        toastGen(this,String.format("Selected %s for item %s", menuItemName, listItemName));
        return true;
    }


}




