package net.nlacombe.commonlib.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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

    public static <ElementType> Stream<ElementType> createStream(PageIterator<ElementType> pageIterator) {
        Iterator<ElementType> it = new PageElementIterator<>(pageIterator);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, Spliterator.DISTINCT), false);
    }

    public static <ElementType> Stream<ElementType> createStream(PageSource<ElementType> pageSource) {
        return createStream(new PageSourcePageIterator<>(pageSource));
    }

    public static <T> Stream<List<T>> batch(Stream<T> stream, int batchSize) {
        return batchSize <= 0
                ? Stream.of(stream.collect(Collectors.toList()))
                : StreamSupport.stream(new BatchSpliterator<>(stream.spliterator(), batchSize), stream.isParallel());
    }

}
