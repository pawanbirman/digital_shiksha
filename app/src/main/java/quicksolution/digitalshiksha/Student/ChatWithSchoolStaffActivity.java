package quicksolution.digitalshiksha.Student;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import quicksolution.digitalshiksha.R;

public class ChatWithSchoolStaffActivity extends AppCompatActivity implements ChatFragment.OnFragmentInteractionListener,GroupFragment.OnFragmentInteractionListener,ContactFragment.OnFragmentInteractionListener
{

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabAccessorAdapter myTabAccessorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_school_staff);

        mToolbar=(Toolbar)findViewById(R.id.chat_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Quality Chat");

        myViewPager=(ViewPager)findViewById(R.id.chat_tab_pager);
        myTabAccessorAdapter=new TabAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);

        myTabLayout=(TabLayout)findViewById(R.id.chat_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.chat_option_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.chat_menu_find_friends)
        {

        }

        if(item.getItemId() == R.id.chat_menu_settings)
        {

        }

        return true;



    }
}

