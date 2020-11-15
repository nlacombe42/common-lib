package net.nlacombe.commonlib.mapping;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractBeanMapper<DtoType, DomainType> implements BeanMapper<DtoType, DomainType> {

    @Override
    public List<DtoType> mapToDtos(List<DomainType> domainObjects) {
        return domainObjects.stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<DomainType> mapToDomainObjects(List<DtoType> dtos) {
        return dtos.stream()
            .map(this::mapToDomainObject)
            .collect(Collectors.toList());
    }
}
