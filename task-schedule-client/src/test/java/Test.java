import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by rocky on 2015/10/16.
 */
public class Test {

    public static void main(String[] args) {
        System.out.println(false || true || false || false);
        System.out.println(true || true || true || false);

        StringBuilder sb = new StringBuilder(20);
        System.out.println(sb.length());

        final TestBean testBean = new TestBean();
        String a = "20";
//        ReflectionUtils.doWithFields(a.getClass(), new ReflectionUtils.FieldCallback() {
//            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
//                ReflectionUtils.makeAccessible(field);
////                System.out.println(field.get(testBean) + "===" + field.getGenericType().getTypeName());
////                System.out.println(field.isSynthetic() + "===" + ReflectionUtils.isPublicStaticFinal(field));
//                System.out.println("field class equals : " + (field.getType() == String.class));
//            }
//        });

        System.out.println(File.separator);
    }
}
