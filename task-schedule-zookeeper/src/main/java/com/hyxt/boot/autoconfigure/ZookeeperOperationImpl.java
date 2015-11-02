package com.hyxt.boot.autoconfigure;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created by rocky on 15/10/30.
 */
public class ZookeeperOperationImpl implements ZookeeperOperation {

    private final CuratorFramework curatorFramework;

    public ZookeeperOperationImpl(CuratorFramework curatorFramework) {
        this.curatorFramework    = curatorFramework;
    }

    public String createPersistent(String path) {
        return createPersistent(path , null);
    }

    public String createPersistent(String path, byte[] data) {
        try {
            return this.curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(path , data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createEphemeral(String path) {
        return createEphemeral(path , null);
    }

    public String createEphemeral(String path, byte[] data) {
        try {
            return this.curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(path , data);
        } catch (Exception e) {
            throw new RuntimeException(String.format("create path failed , path : %s", path), e);
        }
    }

    public String createPersistentWithValidator(String path) {
        return this.createPersistentWithValidator(path , null);
    }

    public String createPersistentWithValidator(String path, byte[] data) {
        try {
            Stat stat = this.curatorFramework.checkExists().forPath(path);
            if (stat == null) {
                this.createPersistent(path , data);
            } else {
                this.setData(path , data);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("create path failed , path : %s", path), e);
        }
        return null;
    }

    public String createEphemeralWithValidator(String path) {
        return this.createEphemeralWithValidator(path , null);
    }

    public String createEphemeralWithValidator(String path, byte[] data) {
        try {
            Stat stat = this.curatorFramework.checkExists().forPath(path);
            if (stat == null) {
                this.createEphemeral(path, data);
            } else {
                this.setData(path , data);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("create path failed , path : %s", path), e);
        }
        return null;
    }

    public void setData(String path, byte[] data) {
        try {
            this.curatorFramework.setData().forPath(path, data);
        } catch (Exception e) {
            throw new RuntimeException(String.format("set data failed , path : %s", path), e);
        }
    }

    public byte[] getData(String path) {
        try {
            return this.curatorFramework.getData().forPath(path);
        } catch (Exception e) {
            throw new RuntimeException(String.format("get data failed , path : %s", path), e);
        }
    }

    public byte[] getParentData(String path) {
        String []parts = path.split("\\/");
        return getData(parts[parts.length - 2]);
    }

    public CuratorFramework getCuratorFramework() {
        return this.curatorFramework;
    }

    public List<String> getChildren(String path) {
        try {
            return this.curatorFramework.getChildren().forPath(path);
        } catch (Exception e) {
            throw new RuntimeException(String.format("get children failed , path : %s", path), e);
        }
    }

    public void close() {
        this.curatorFramework.close();
    }

}
