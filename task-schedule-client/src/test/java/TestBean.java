import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rocky on 2015/10/19.
 */
public class TestBean {

    private int a = 1;

    private String b = "2";

    private boolean isTest ;

    private List<String> list;

    private transient  String c ;

    private static Collection<String> collection = new ArrayList<String>(1);

    public static Collection<String> getCollection() {
        return collection;
    }

    public static void setCollection(Collection<String> collection) {
        TestBean.collection = collection;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setIsTest(boolean isTest) {
        this.isTest = isTest;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }
}
