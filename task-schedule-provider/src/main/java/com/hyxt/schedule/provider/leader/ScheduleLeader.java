package com.hyxt.schedule.provider.leader;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

/**
 * Created by rocky on 2015/10/28.
 */
public class ScheduleLeader {

    public void takeLeadership(CuratorFramework client) throws Exception {

    }

    public ScheduleLeader(CuratorFramework client) throws Exception {
        LeaderLatch leaderLatch = new LeaderLatch(client , "");
        leaderLatch.start();

        leaderLatch.addListener(new LeaderLatchListener() {
            public void isLeader() {

            }

            public void notLeader() {

            }
        });
    }

}
