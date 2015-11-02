package com.hyxt.boot.autoconfigure;

import org.apache.curator.framework.CuratorFramework;

import java.util.List;

/**
 * Created by rocky on 15/10/30.
 */
public interface ZookeeperOperation {

    String createPersistent(String path);

    String createPersistent(String path , byte[] data);

    String createEphemeral(String path);

    String createEphemeral(String path , byte[] data);

    String createPersistentWithValidator(String path);

    String createPersistentWithValidator(String path , byte[] data);

    String createEphemeralWithValidator(String path);

    String createEphemeralWithValidator(String path , byte[] data);

    void setData(String path , byte[] data);

    byte[] getData(String path);

    byte[] getParentData(String path);

    CuratorFramework getCuratorFramework();

    List<String> getChildren(String path);

    void close();

}
