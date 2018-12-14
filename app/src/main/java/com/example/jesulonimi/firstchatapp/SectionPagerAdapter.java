package com.example.jesulonimi.firstchatapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
   switch (position){
       case 0:return "requests" ;
       case 1:return "chats";
       case 2:return "friends";

       default:return null;
   }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:RequestFragment rf=new RequestFragment();
            return rf;
            case 1:ChatsFragment cf=new ChatsFragment();
            return  cf;
            case 2:FriendsFragment ff=new FriendsFragment();
            return ff;
            default:return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }
}
