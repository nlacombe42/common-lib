package net.nlacombe.commonlib.stream;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil {

    public static <ElementType> void processInBatch(Stream<ElementType> stream, int batchSize, Consumer<Collection<ElementType>> batchProcessor) {
        List<ElementType> newBatch = new ArrayList<>(batchSize);

        stream.forEach(element -> {
            List<ElementType> fullBatch;

            synchronized (newBatch) {
                if (newBatch.size() < batchSize) {
                    newBatch.add(element);
                    return;
                } else {
                    fullBatch = new ArrayList<>(newBatch);
                    newBatch.clear();
                    newBatch.add(element);
                }
            }

            batchProcessor.accept(fullBatch);
        });

        if (newBatch.size() > 0)
            batchProcessor.accept(new ArrayList<>(newBatch));
    }

    public static <ElementType> Stream<ElementType> createStreamFromPageIterator(PageIterator<ElementType> pageIterator) {
        Iterator<ElementType> it = new PageElementIterator<>(pageIterator);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, Spliterator.DISTINCT), false);
    }

}
