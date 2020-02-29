package net.nlacombe.commonlib.csv;

import net.nlacombe.commonlib.util.GenericUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanCsvWriter<BeanType> implements AutoCloseable {

    private static final char DEFAULT_DELIMITER = ',';

    private FileWriter csvFileWriter;
    private CSVPrinter csvPrinter;
    private Class<? extends BeanType> classOfBean;
    private List<String> propertiesInColumnOrder;

    public BeanCsvWriter(Class<? extends BeanType> classOfBean, Path csvFilePath) throws IOException {
        this(classOfBean, csvFilePath, null);
    }

    public BeanCsvWriter(Class<? extends BeanType> classOfBean, Path csvFilePath, List<String> orderOfProperties) throws IOException {
        this(classOfBean, csvFilePath, DEFAULT_DELIMITER, orderOfProperties);
    }

    public BeanCsvWriter(Class<? extends BeanType> classOfBean, Path csvFilePath, char delimiter, List<String> orderOfProperties) throws IOException {
        this.csvFileWriter = new FileWriter(csvFilePath.toFile());
        this.csvPrinter = getCsvPrinter(csvFileWriter, delimiter);
        this.propertiesInColumnOrder = getPropertiesInColumnOrder(classOfBean, orderOfProperties);

        writeHeaders();
    }

    public BeanCsvWriter(Class<? extends BeanType> classOfBean, Appendable appendable, char delimiter, List<String> orderOfProperties) throws IOException {
        this.csvPrinter = getCsvPrinter(appendable, delimiter);
        this.propertiesInColumnOrder = getPropertiesInColumnOrder(classOfBean, orderOfProperties);

        writeHeaders();
    }

    public void writeBean(BeanType bean) throws IOException {
        writeForEveryPropertyName(propertyName -> {
            try {
                return BeanUtils.getProperty(bean, propertyName);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void flush() throws IOException {
        csvPrinter.flush();
    }

    @Override
    public void close() {
        try {
            GenericUtil.mergeCloseable(csvFileWriter, csvPrinter).close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeHeaders() throws IOException {
        writeForEveryPropertyName(propertyName -> propertyName);
    }

    private void writeForEveryPropertyName(Function<String, Object> mapFunction) throws IOException {
        List<Object> propertyValuesInOrder = propertiesInColumnOrder.stream()
                .map(mapFunction)
                .collect(Collectors.toList());

        csvPrinter.printRecord(propertyValuesInOrder);
    }

    private CSVPrinter getCsvPrinter(Appendable appendable, char delimiter) throws IOException {
        CSVFormat csvFormat = CSVFormat.RFC4180;

        if (delimiter != DEFAULT_DELIMITER)
            csvFormat = csvFormat.withDelimiter(delimiter);

        return csvFormat.print(appendable);
    }

    private List<String> getPropertiesInColumnOrder(Class<? extends BeanType> classOfBean, List<String> orderOfProperties) {
        try {
            var propertyDescriptors = Introspector.getBeanInfo(classOfBean).getPropertyDescriptors();
            var beanClassProperties = Arrays.stream(propertyDescriptors)
                    .map(FeatureDescriptor::getName)
                    .collect(Collectors.toList());

            if (orderOfProperties == null)
                return beanClassProperties;

            if (!CollectionUtils.isEqualCollection(orderOfProperties, beanClassProperties))
                throw new IllegalArgumentException("orderOfProperties does not contain exactly the same properties as the bean class provided");

            return orderOfProperties;
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
}
