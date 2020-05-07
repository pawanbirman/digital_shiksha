package quicksolution.digitalshiksha.Student;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAccessorAdapter extends FragmentPagerAdapter
{

    public TabAccessorAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                ChatFragment chatFragment=new ChatFragment();
                return chatFragment;
            case 1:
                GroupFragment groupFragment=new GroupFragment();
                return groupFragment;

            case 2:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;

            case 3:
                FindFriendsFragment findFriendsFragment = new FindFriendsFragment();
                return findFriendsFragment;

            default:

                return null;
        }

    }

    @Override
    public int getCount()
    {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {

        switch (position)
        {
            case 0:

                return "Chats";
            case 1:

                return "Groups";

            case 2:

                return "Requests";

            case 3:
                return "Search";

            default:

                return null;
        }



    }
}
