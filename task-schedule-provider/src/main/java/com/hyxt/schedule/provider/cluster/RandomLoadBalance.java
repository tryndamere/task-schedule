package com.hyxt.schedule.provider.cluster;

import java.util.List;
import java.util.Random;

/**
 * Created by rocky on 15/3/8.
 */
public class RandomLoadBalance implements LoadBalance {

    private final Random random = new Random();

    public String selector(List<String> nodes) {
        int size = nodes.size() ;
        if (size == 1) {
            return nodes.get(0);
        }
        return nodes.get(random.nextInt(size));
    }
}
