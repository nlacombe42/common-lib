package net.nlacombe.commonlib.csv;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanCsvWriterTest {
    public interface MyBeanInterface {
        String getMyBeanPropertyName2();

        String getMyBeanPropertyName1();

        String getA();

        String getC();

        String getB();
    }

    public static class MyBeanClass implements MyBeanInterface {
        private String myBeanPropertyName1;
        private String myBeanPropertyName2;
        private String a;
        private String b;
        private String c;

        public MyBeanClass(String myBeanPropertyName1, String myBeanPropertyName2, String a, String b, String c) {
            this.myBeanPropertyName1 = myBeanPropertyName1;
            this.myBeanPropertyName2 = myBeanPropertyName2;
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        public String getC() {
            return c;
        }

        @Override
        public String getMyBeanPropertyName1() {
            return myBeanPropertyName1;
        }

        @Override
        public String getMyBeanPropertyName2() {
            return myBeanPropertyName2;
        }

        @Override
        public String getA() {
            return a;
        }

        @Override
        public String getB() {
            return b;
        }
    }

    @Test
    public void when_not_providing_order_of_properties_write_csv_with_properties_in_alphanumeric_order_and_with_values_in_right_columns() throws IOException {
        Path csvFilePath = Files.createTempFile("unit-test-", ".csv");
        MyBeanInterface bean = new MyBeanClass("val1", "val2", "va", "vb", "vc");

        try (BeanCsvWriter<MyBeanInterface> beanCsvWriter = new BeanCsvWriter<>(MyBeanInterface.class, csvFilePath)) {
            beanCsvWriter.writeBean(bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String expectedCsvFileContent = "a,b,c,myBeanPropertyName1,myBeanPropertyName2\r\n" +
                bean.getA() + "," + bean.getB() + "," + bean.getC() + "," + bean.getMyBeanPropertyName1() + "," + bean.getMyBeanPropertyName2()  + "\r\n";

        assertThat(Files.readString(csvFilePath)).isEqualTo(expectedCsvFileContent);
    }

    @Test
    public void when_providing_order_of_properties_write_csv_with_properties_in_the_right_order() throws IOException {
        Path csvFilePath = Files.createTempFile("unit-test-", ".csv");
        MyBeanInterface bean = new MyBeanClass("val1", "val2", "va", "vb", "vc");
        List<String> orderOfProperties = Arrays.asList("a", "c", "b", "myBeanPropertyName2", "myBeanPropertyName1");

        try (BeanCsvWriter<MyBeanInterface> beanCsvWriter = new BeanCsvWriter<>(MyBeanInterface.class, csvFilePath, orderOfProperties)) {
            beanCsvWriter.writeBean(bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String expectedCsvFileContent = StringUtils.join(orderOfProperties, ',') + "\r\n" +
                bean.getA() + "," + bean.getC() + "," + bean.getB() + "," + bean.getMyBeanPropertyName2() + "," + bean.getMyBeanPropertyName1()  + "\r\n";

        assertThat(Files.readString(csvFilePath)).isEqualTo(expectedCsvFileContent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_providing_order_of_properties_with_less_properties() throws IOException {
        Path csvFilePath = Files.createTempFile("unit-test-", ".csv");
        MyBeanInterface bean = new MyBeanClass("val1", "val2", "va", "vb", "vc");
        List<String> orderOfProperties = Arrays.asList("a", "c", "b", "myBeanPropertyName2");

        new BeanCsvWriter<>(MyBeanInterface.class, csvFilePath, orderOfProperties);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_when_providing_order_of_properties_with_wrong_property_names() throws IOException {
        Path csvFilePath = Files.createTempFile("unit-test-", ".csv");
        MyBeanInterface bean = new MyBeanClass("val1", "val2", "va", "vb", "vc");
        List<String> orderOfProperties = Arrays.asList("a", "c", "b", "wrongPropertyName", "myBeanPropertyName1");

        new BeanCsvWriter<>(MyBeanInterface.class, csvFilePath, orderOfProperties);
    }
}
