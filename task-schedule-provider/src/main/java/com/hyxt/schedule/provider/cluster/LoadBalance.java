package com.hyxt.schedule.provider.cluster;

import java.util.List;

/**
 * Created by rocky on 15/3/8.
 */
public interface LoadBalance {

    String selector(List<String> nodes);

}
